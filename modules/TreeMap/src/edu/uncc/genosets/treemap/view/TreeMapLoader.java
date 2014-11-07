/*
 * 
 * 
 */
package edu.uncc.genosets.treemap.view;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.geneontology.obo.Obo;
import edu.uncc.genosets.geneontology.obo.OboDataObject;
import edu.uncc.genosets.geneontology.obo.Term;
import edu.uncc.genosets.geneontology.obo.TermEdge;
import edu.uncc.genosets.ontologizer.GoEnrichment;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.treemap.DAGGraph;
import edu.uncc.genosets.treemap.GOEdgeSchema;
import edu.uncc.genosets.treemap.GOSchema;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressRunnable;
import org.netbeans.api.progress.ProgressUtils;
import prefuse.data.Edge;
import prefuse.data.Node;

/**
 *
 * @author aacain
 */
@Deprecated
public class TreeMapLoader {

    //private final GenoSetsDataSet ds;
    private final DAGGraph graph;
    private final GOSchema schema;

    public TreeMapLoader() {
        this.schema = new GOSchema();
        this.graph = new DAGGraph(schema.instantiate(), (new GOEdgeSchema()).instantiate(), true, GOSchema.ID, GOEdgeSchema.SOURCE, GOEdgeSchema.TARGET, GOSchema.NAME);
    }

    public DAGGraph load(Set<Integer> populationFeatureIds, OboDataObject obodao) {
        HashMap<String, TermWrapper> goIdWrapperLookup = new HashMap<String, TermWrapper>();
        getAnnotations(populationFeatureIds, loadTerms(obodao, goIdWrapperLookup), goIdWrapperLookup);
        return graph;
    }

    @Deprecated
    public GenoSetsTreeMap createTreeMap(final Set<Integer> featureIds, final OboDataObject obodao) {
        ProgressRunnable<GenoSetsTreeMap> runnable = new ProgressRunnable<GenoSetsTreeMap>() {

            @Override
            public GenoSetsTreeMap run(ProgressHandle handle) {
//                handle.setDisplayName("Querying");
//                DAGGraph loadedGraph = load(featureIds);
//                handle.setDisplayName("Initializing TreeMap");
//                StudySet studySet = mgr.getStudySets().get(0);
//                TreeMap map = new TreeMap(loadedGraph, GOSchema.TOTAL_COUNT, "GO:0008150");
//                //map.setStudySet(studySet);
//                map.createVisualiztion(900, 600);
//                return map;
                handle.setDisplayName("Querying");
                DAGGraph loadedGraph = load(featureIds, obodao);
                handle.setDisplayName("Initializing TreeMap");
                //StudySet studySet = mgr.getStudySets().get(0);
                GenoSetsTreeMap treeMap = new GenoSetsTreeMap(graph, GOSchema.TOTAL_COUNT, "GO:0000000");
                treeMap.createVisualization(900, 600);

                return treeMap;
            }
        };

        GenoSetsTreeMap treeMap = ProgressUtils.showProgressDialogAndRun(runnable, "Querying", true);
        return treeMap;
    }
    
    public GenoSetsTreeMap createTreeMap(final GoEnrichment enrichment, final OboDataObject obodao) {
        ProgressRunnable<GenoSetsTreeMap> runnable = new ProgressRunnable<GenoSetsTreeMap>() {

            @Override
            public GenoSetsTreeMap run(ProgressHandle handle) {
//                handle.setDisplayName("Querying");
//                DAGGraph loadedGraph = load(featureIds);
//                handle.setDisplayName("Initializing TreeMap");
//                StudySet studySet = mgr.getStudySets().get(0);
//                TreeMap map = new TreeMap(loadedGraph, GOSchema.TOTAL_COUNT, "GO:0008150");
//                //map.setStudySet(studySet);
//                map.createVisualiztion(900, 600);
//                return map;
                handle.setDisplayName("Querying");
                Set<Integer> featureIds = new HashSet<Integer>();
                List<StudySet> populationSets = enrichment.getOntologizerParameters().getPopulationSets();
                if(populationSets != null){
                    for (StudySet ss : populationSets) {
                        featureIds.addAll(ss.getIdSet());
                    }
                }
                DAGGraph loadedGraph = load(featureIds, obodao);
                handle.setDisplayName("Initializing TreeMap");
                //StudySet studySet = mgr.getStudySets().get(0);
                GenoSetsTreeMap treeMap = new GenoSetsTreeMap(loadedGraph, GOSchema.TOTAL_COUNT, "GO:0000000");
                treeMap.createVisualization(900, 600);

                return treeMap;
            }
        };

        GenoSetsTreeMap treeMap = ProgressUtils.showProgressDialogAndRun(runnable, "Querying", true);
        return treeMap;
    }

    private void getAnnotations(Set<Integer> populationFeatureIds, HashMap<Integer, TermWrapper> termLookup, HashMap<String, TermWrapper> goIdWrapperLookup) {
        //now get annotations
        StringBuilder annoQuery = new StringBuilder("SELECT count(*), temp_go_ortholog_summary.GoCluster ");
        annoQuery.append(" FROM temp_go_ortholog_summary, fact_location_ortho_fact ");
        annoQuery.append(" WHERE temp_go_ortholog_summary.OrthoCluster = fact_location_ortho_fact.FeatureCluster ");
        annoQuery.append(" AND fact_location_ortho_fact.Feature IN (");
        for (Integer fId : populationFeatureIds) {
            annoQuery.append(fId).append(", ");
        }
        //delete the last comma
        annoQuery.deleteCharAt(annoQuery.length() - 2);
        annoQuery.append(") ");
        annoQuery.append(" GROUP BY temp_go_ortholog_summary.GoCluster");
        List<Object[]> result = DataManager.getDefault().createNativeQuery(annoQuery.toString());


        for (Object[] data : result) {
            int id = ((Long) data[1]).intValue();
            //lookup term
            TermWrapper term = termLookup.get(id);
            if (term.graphRow < 0) { //need to add
                addNode(term, ((Long) data[0]).intValue(), goIdWrapperLookup);
            }
        }
        result = null;
    }

    /**
     *
     * @param term
     * @param count
     * @return the graph row number for this node or
     */
    private int addNode(TermWrapper term, int count, HashMap<String, TermWrapper> goIdWrapperLookup) {
        if(term == null){
            int x = 5;
        }
        if (term.graphRow < 0) {
            Node node = graph.addNode();
            node.setInt(GOSchema.ID, term.fc.getFeatureClusterId());
            node.setString(GOSchema.GO_TERM_ID, term.fc.getClusterName());
            node.setString(GOSchema.NAME, (String) term.fc.getValueOfCustomField("goName"));
            node.setInt(GOSchema.TOTAL_COUNT, count);
            node.setInt(GOSchema.STUDY_COUNT, 0);
            term.graphRow = node.getRow();
            if(term.term == null || term.term.getParentsEdges() == null){
                System.out.println("Here is the problem");
            }
            if (term.term.getParentsEdges().isEmpty()) {
                //add to root node
                Node root = graph.getRoot();
                //add edge
                int eId = graph.addEdge(root.getRow(), term.graphRow);
                Edge edge = graph.getEdge(eId);
                edge.set(GOEdgeSchema.RELATIONSHIP_TYPE, "is_a");
            } else {
                for (TermEdge termEdge : term.term.getParentsEdges()) {
                    TermWrapper parent = goIdWrapperLookup.get(termEdge.getParent().getGoId());
                    int parentId = addNode(parent, 0, goIdWrapperLookup);
                    //add edge
                    int eId = graph.addEdge(parentId, term.graphRow);
                    Edge edge = graph.getEdge(eId);
                    edge.set(GOEdgeSchema.RELATIONSHIP_TYPE, termEdge.getRelationshipType());
                }
            }
        }
        return term.graphRow;
    }

    @SuppressWarnings("unchecked")
    private HashMap<Integer, TermWrapper> loadTerms(OboDataObject obodao, HashMap<String, TermWrapper> goIdWrapperLookup) {
        //get terms
        StringBuilder termQuery = new StringBuilder("SELECT t from Cluster_GoTerm as t");
        List<FeatureCluster> terms = DataManager.getDefault().createQuery(termQuery.toString());
        //create lookup
        HashMap<Integer, TermWrapper> termLookup = new HashMap<Integer, TermWrapper>();
        for (FeatureCluster term : terms) {
            TermWrapper w = new TermWrapper();
            w.fc = term;
            termLookup.put(term.getFeatureClusterId(), w);
            goIdWrapperLookup.put(term.getClusterName(), w);
        }
        //add the root
        Node node = graph.addNode();
        node.setInt(GOSchema.ID, -1);
        node.setString(GOSchema.GO_TERM_ID, "GO:0000000");
        node.setString(GOSchema.NAME, "All");
        node.setInt(GOSchema.TOTAL_COUNT, 0);
        node.setInt(GOSchema.STUDY_COUNT, 0);
        graph.setRoot(node);

        Obo obo = obodao.getObo();
        for (TermWrapper termWrapper : termLookup.values()) {
            Term term = obo.getTerm(termWrapper.fc.getClusterName());
            termWrapper.term = term;
        }
        return termLookup;
    }

    private static class TermWrapper {

        FeatureCluster fc;
        Term term;
        int graphRow = -1;
    }
}
