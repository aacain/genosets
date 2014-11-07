/*
 * 
 * 
 */
package edu.uncc.genosets.treemap.view;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.geneontology.api.GeneOntology;
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
public class TreeMapLoader1 {

    //private final GenoSetsDataSet ds;
    private final DAGGraph graph;
    private final GOSchema schema;
    private static int id = -1;
    private Node obsoleteNode;
    private Obo obo;

    public TreeMapLoader1() {
        this.schema = new GOSchema();
        this.graph = new DAGGraph(schema.instantiate(), (new GOEdgeSchema()).instantiate(), true, GOSchema.ID, GOEdgeSchema.SOURCE, GOEdgeSchema.TARGET, GOSchema.NAME);
    }

    public GenoSetsTreeMap createTreeMap(final GoEnrichment enrichment, final OboDataObject obodao) {
        ProgressRunnable<GenoSetsTreeMap> runnable = new ProgressRunnable<GenoSetsTreeMap>() {
            @Override
            public GenoSetsTreeMap run(ProgressHandle handle) {
                handle.setDisplayName("Reading Obo");
                obo = obodao.getObo();
                handle.setDisplayName("Querying");
                DAGGraph loadedGraph = getAnnotations(enrichment, obo);
                handle.setDisplayName("Initializing TreeMap");
                //StudySet studySet = mgr.getStudySets().get(0);
                GenoSetsTreeMap treeMap = new GenoSetsTreeMap(loadedGraph, GOSchema.TOTAL_COUNT, "GO:0000000");
                //treeMap.createVisualization(900, 600);

                return treeMap;
            }
        };

        GenoSetsTreeMap treeMap = ProgressUtils.showProgressDialogAndRun(runnable, "Querying", true);
        return treeMap;
    }

    private DAGGraph getAnnotations(GoEnrichment enrichment, Obo obo) {
        Set<Integer> populationIds = new HashSet<Integer>();
        FocusEntity focus = null;
        if (enrichment.getOntologizerParameters().getPopulationSets() != null) {
            for (StudySet ss : enrichment.getOntologizerParameters().getPopulationSets()) {
                populationIds.addAll(ss.getIdSet());
                focus = ss.getFocusEntity();
            }
        }

//        //get the annotations
//        StringBuilder annoQuery = new StringBuilder("SELECT count(*), g.ClusterName ");
//        annoQuery.append(" FROM temp_go_ortholog_summary inner join fact_location_ortho_fact on temp_go_ortholog_summary.OrthoCluster = fact_location_ortho_fact.FeatureCluster inner join cluster_go_term as g on g.FeatureClusterId = temp_go_ortholog_summary.GoCluster ");
//        annoQuery.append(" WHERE fact_location_ortho_fact.Feature IN (");
//        for (Integer fId : populationFeatureIds) {
//            annoQuery.append(fId).append(", ");
//        }
//        //delete the last comma
//        annoQuery.deleteCharAt(annoQuery.length() - 2);
//        annoQuery.append(") ");
//        annoQuery.append(" GROUP BY temp_go_ortholog_summary.GoCluster");
//        List<Object[]> result = DataManager.getDefault().createNativeQuery(annoQuery.toString());


        StringBuilder bldr = new StringBuilder();
        bldr.append("SELECT COUNT(fact_location_ortho_fact.").append(focus.getEntityName()).append("), cluster_go_term.ClusterName, SubQuery.GOTermCluster")
                .append(" FROM fact_location_ortho_fact ")
                .append(" INNER JOIN ")
                .append("(SELECT fact_location_ortho_fact.FeatureCluster AS OrthologCluster, fact_feature_go_anno.FeatureCluster AS GOTermCluster FROM fact_feature_go_anno INNER JOIN fact_location_ortho_fact ON fact_feature_go_anno.Feature = fact_location_ortho_fact.Feature ")
                .append(" WHERE fact_location_ortho_fact.Feature IN (");
        int i = 0;
        for (Integer popId : populationIds) {
            if (i > 0) {
                bldr.append(", ");
            }
            bldr.append(popId);
            i++;
        }
        bldr.append(")");
        bldr.append(" GROUP BY OrthologCluster, GOTermCluster) SubQuery");
        bldr.append(" ON fact_location_ortho_fact.FeatureCluster = SubQuery.OrthologCluster ");
        bldr.append(" INNER JOIN cluster_go_term ON SubQuery.GOTermCluster = cluster_go_term.FeatureClusterId");
        bldr.append(" GROUP BY SubQuery.GOTermCluster");

        List<Object[]> result = DataManager.getDefault().createNativeQuery(bldr.toString());

        //add the root
        Node node = graph.addNode();
        node.setInt(GOSchema.ID, -1);
        id++;
        node.setString(GOSchema.GO_TERM_ID, GeneOntology.ALL_TERMID);
        node.setString(GOSchema.NAME, "All");
        node.setInt(GOSchema.TOTAL_COUNT, 0);
        node.setInt(GOSchema.STUDY_COUNT, 0);
        graph.setRoot(node);

        //add obsolete node
        obsoleteNode = graph.addNode();
        obsoleteNode.setInt(GOSchema.ID, id++);
        obsoleteNode.setString(GOSchema.GO_TERM_ID, GeneOntology.OBSOLETE_TERMID);
        obsoleteNode.setString(GOSchema.NAME, "Obsolete");
        obsoleteNode.setInt(GOSchema.TOTAL_COUNT, 0);
        obsoleteNode.setInt(GOSchema.STUDY_COUNT, 0);
        graph.addEdge(node, obsoleteNode);

        HashMap<String, TermWrapper> wrapperLookup = new HashMap<String, TermWrapper>(result.size());
        //now lookup the term for each annotation
        for (Object[] data : result) {
            String id = (String) data[1];
            //lookup term in the wrapper
            TermWrapper wrapper = wrapperLookup.get(id);
            if (wrapper == null) {
                wrapper = new TermWrapper();
                wrapper.term = obo.getTerm(id);
                wrapperLookup.put(id, wrapper);
            }
            if (wrapper.graphRow < 0) { //need to add
                Number count = (Number) data[0];
                addNode(wrapper, count.intValue(), wrapperLookup, obo);
            }
        }
        result = null;
        return graph;
    }

    /**
     *
     * @param term
     * @param count
     * @return the graph row number for this node or
     */
    private int addNode(TermWrapper term, int count, HashMap<String, TermWrapper> goIdWrapperLookup, Obo obo) {
        if (term.graphRow < 0) {
            Node node = graph.addNode();
            node.setInt(GOSchema.ID, id++);
            node.setString(GOSchema.GO_TERM_ID, term.term.getGoId());
            if (term.term.getName() == null) {
                int x = 5;
            }
            node.setString(GOSchema.NAME, term.term.getName());
            node.setInt(GOSchema.TOTAL_COUNT, count);
            node.setInt(GOSchema.STUDY_COUNT, 0);
            term.graphRow = node.getRow();
            if (term.term == null || term.term.getParentsEdges() == null) {
                System.out.println("Here is the problem");
            }
            if (term.term.getParentsEdges().isEmpty()) {
                //see if this term is obsolete, then make it a child of the obsolete term
                if (term.term.getIsObsolete().equals(Boolean.TRUE)) {
                    int eId = graph.addEdge(obsoleteNode.getRow(), term.graphRow);
                    Edge edge = graph.getEdge(eId);
                    edge.set(GOEdgeSchema.RELATIONSHIP_TYPE, "is_a");
                } else {
                    //add to root node
                    Node root = graph.getRoot();
                    //add edge
                    int eId = graph.addEdge(root.getRow(), term.graphRow);
                    Edge edge = graph.getEdge(eId);
                    edge.set(GOEdgeSchema.RELATIONSHIP_TYPE, "is_a");
                }
            } else {
                for (TermEdge termEdge : term.term.getParentsEdges()) {
                    TermWrapper parent = goIdWrapperLookup.get(termEdge.getParent().getGoId());
                    if (parent == null) {
                        parent = new TermWrapper();
                        parent.term = obo.getTerm(termEdge.getParent().getGoId());
                        goIdWrapperLookup.put(termEdge.getParent().getGoId(), parent);
                    }
                    int parentId = addNode(parent, 0, goIdWrapperLookup, obo);
                    //add edge
                    int eId = graph.addEdge(parentId, term.graphRow);
                    Edge edge = graph.getEdge(eId);
                    edge.set(GOEdgeSchema.RELATIONSHIP_TYPE, termEdge.getRelationshipType());
                }
            }
        }
        return term.graphRow;
    }

    private static class TermWrapper {

        Term term;
        int graphRow = -1;
    }
}
