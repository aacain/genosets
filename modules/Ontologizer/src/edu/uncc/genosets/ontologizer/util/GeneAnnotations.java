/*
 * 
 * 
 */
package edu.uncc.genosets.ontologizer.util;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.OrthologTableCreator;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.ontologizer.OntologizerParameters;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author aacain
 */
public class GeneAnnotations {

    public static final String header = "!gaf-version: 2.0";

    public static FileObject createAnnotationsFile(FileObject dbRoot, FocusEntity focus, OntologizerParameters params) {
        //check to see if necessary tables have been created
        OrthologTableCreator.initialize();
        FileObject fo = null;
        try {
            fo = dbRoot.getFileObject("geneassociations", "gaf");
            if (fo == null) {
                fo = dbRoot.createData("geneassociations", "gaf");
                final int fIndex = 0;
                final int tIndex = 1;
                final int oIndex = 2;
//                StringBuilder query = new StringBuilder("SELECT fact_location_ortho_fact.Feature, cluster_go_term.ClusterName, fact_location_ortho_fact.Organism ");
//                query.append(" FROM temp_go_ortholog_summary, fact_location_ortho_fact, cluster_go_term ");
//                query.append(" WHERE temp_go_ortholog_summary.GoCluster = cluster_go_term.FeatureClusterId AND temp_go_ortholog_summary.OrthoCluster = fact_location_ortho_fact.FeatureCluster ");
//                //query.append(" AND cluster_go_term.ClusterName != 'GS:0000000' ");
//                
//                List<Object[]> result = DataManager.getDefault().createNativeQuery(query.toString());

//                List<Object[]> result = null;
//                if(params.getTermsByOrthologMethod() == null){
//                    result = Query.queryByEntity(focus);
//                }else{
//                    result = Query.queryTermsByOrtholog(focus, params.getTermsByOrthologMethod());
//                }  
                List<Object[]> result = Query.queryTermsByOrtholog(focus, null);
                
                BufferedWriter wr = null;
                try {
                    wr = new BufferedWriter(new OutputStreamWriter(fo.getOutputStream()));
                    //write file
                    for (Object[] objs : result) {
                        StringBuilder line = new StringBuilder("GenoSets\t"); //db
                        line.append(objs[fIndex]).append("\t"); //db object id
                        line.append(objs[fIndex]).append("\t"); //db object symbol
                        line.append("X\t");//qualifier
                        line.append(objs[tIndex]).append("\t"); //go id
                        line.append("X\t");//db reference
                        line.append("X\t"); //evidence code
                        line.append("X\t");//with or from
                        line.append("X\t"); //aspect
                        line.append(objs[fIndex]).append("\t"); //db object name
                        line.append("X\t"); //db object synonym
                        line.append("protein\t");  //db object type
                        line.append(objs[oIndex]).append("\t"); //taxon
                        line.append("20110101\t"); //date
                        line.append("GenoSets\t"); //assigned by
                        line.append("X\t"); //annotation extension
                        line.append("X"); //gene product form id
                        wr.write(line.toString());
                        wr.newLine();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    try {
                        if (wr != null) {
                            wr.close();
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return fo;
    }

    private static class Query implements QueryCreator {

        private static List<Object[]> queryByEntity(FocusEntity focusEntity) {
            StringBuilder q = new StringBuilder();
            q.append("SELECT fact_feature_go_anno.").append(focusEntity.getEntityName()).append(", cluster_go_term.ClusterName, fact_feature_go_anno.Organism")
                    .append(" FROM fact_feature_go_anno INNER JOIN cluster_go_term ON fact_feature_go_anno.FeatureCluster = cluster_go_term.FeatureClusterId");
            return DataManager.getDefault().createNativeQuery(q.toString());
        }

        private static List<Object[]> queryTermsByOrtholog(FocusEntity focusEntity, Integer orthologMethodId) {
            StringBuilder q = new StringBuilder();
            q.append("SELECT fact_location_ortho_fact.").append(focusEntity.getEntityName()).append(", SubQuery.ClusterName, fact_location_ortho_fact.Organism ")
                    .append(" FROM fact_location_ortho_fact ")
                    .append(" INNER JOIN (SELECT fact_location_ortho_fact.FeatureCluster AS OrthologCluster, fact_feature_go_anno.FeatureCluster AS GoCluster, cluster_go_term.ClusterName")
                    .append(" FROM fact_location_ortho_fact INNER JOIN fact_feature_go_anno ON fact_location_ortho_fact.Feature = fact_feature_go_anno.Feature ")
                    .append(" INNER JOIN cluster_go_term ON fact_feature_go_anno.FeatureCluster = cluster_go_term.FeatureClusterId");
                    if(orthologMethodId != null){
                        q.append(" WHERE  fact_location_ortho_fact.AnnotationMethod = ").append(orthologMethodId.toString());
                    }
                    q.append(" GROUP BY fact_feature_go_anno.FeatureCluster, OrthologCluster) SubQuery ON fact_location_ortho_fact.FeatureCluster = SubQuery.OrthologCluster");
            
            return DataManager.getDefault().createNativeQuery(q.toString());
        }
    }
}
