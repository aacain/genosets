/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class OrthologTableCreator {

    public static void initialize() {
        if (needsUpdate()) {
            update();
        }
    }

    public static void listenToDataManager(DataManager mgr) {
        mgr.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if(DataManager.PROP_ORGANISM_ADD.equals(evt.getPropertyName())){
                    dropTables();
                }
            }
        });
    }

    public static boolean isClusterPerformed() {
        List<? extends Long> result = DataManager.getDefault().createQuery("SELECT count(*) FROM OrthoFact", Long.class);
        if (result != null && !result.isEmpty()) {
            Long i = result.get(0);
            if (i.longValue() > 0) {
                return true;
            }
        }
        return false;
    }

    public static void dropTables() {
        ArrayList<String> statements = new ArrayList(3);
        statements.add("DROP TABLE IF EXISTS temp_ortholog_summary; ");
        statements.add("DROP TABLE IF EXISTS temp_ortholog_feature_summary; ");
        statements.add("DROP TABLE IF EXISTS temp_go_ortholog_summary; ");
        DataManager.getDefault().createNativeStatement(statements);
    }

    private static boolean needsUpdate() {
        String query = "SHOW TABLES";
        //the below lines no longer work with hibernate 4
//        List<Object[]> rs = DataManager.getDefault().createNativeQuery(query);
//        Set<String> tables = new HashSet<String>(rs.size());
//        
//        for (Object[] r : rs) {
//            tables.add((String) r[0]);
//        }
        
        List<String> rs = (List)DataManager.getDefault().createNativeQuery(query);//TODO: this is a hack
        Set<String> tables = new HashSet<String>(rs.size());
        for (String r : rs) {
            tables.add(r);
        }
        
        if (!tables.contains("temp_ortholog_summary")) {
            return true;
        }
        if (!tables.contains("temp_go_ortholog_summary")) {
            return true;
        }
        if (!tables.contains("temp_ortholog_feature_summary")) {
            return true;
        }

        return false;
    }

    private static void update() {
        //get the organisms
        DataManager mgr = DataManager.getDefault();
        List<Organism> orgs = mgr.getOrganisms();
        //get ortholog methods
        //get all the ortholog clustering methods
        List<AnnotationMethod> orthoMethods = DataManager.getDefault().createQuery("SELECT m FROM OrthoFact as f, AnnotationMethod as m WHERE f.annotationMethodId = m.annotationMethodId GROUP BY f.annotationMethodId");
        if (!orgs.isEmpty() && !orthoMethods.isEmpty()) {
            StringBuilder paralogQuery = new StringBuilder();
            StringBuilder featureQuery = new StringBuilder();
            StringBuilder featureGroupBy = new StringBuilder();
            StringBuilder clusterQuery = new StringBuilder();
            for (AnnotationMethod method : orthoMethods) {
                for (Organism org : orgs) {
                    paralogQuery.append("SUM(CASE WHEN Organism = ").append(org.getOrganismId()).
                            append(" AND AnnotationMethod = ").append(method.getAnnotationMethodId()).
                            append(" THEN 1 ELSE 0 END) ").
                            append("as org_").append(org.getOrganismId()).append("method_").append(method.getAnnotationMethodId()).append(", ");
                    clusterQuery.append("CASE WHEN SUM(CASE WHEN Organism = ").append(org.getOrganismId()).
                            append(" AND AnnotationMethod = ").append(method.getAnnotationMethodId()).
                            append(" THEN 1 ELSE 0 END) > 0 THEN TRUE ELSE FALSE END ").
                            append("as org_").append(org.getOrganismId()).append("method_").append(method.getAnnotationMethodId()).append(", ");
                    featureQuery.append("CASE WHEN SUM(subquery.org_").append(org.getOrganismId()).append("method_").append(method.getAnnotationMethodId()).append(") > 0 THEN 1 ELSE 0 END ");
                    featureQuery.append("as org_").append(org.getOrganismId()).append("method_").append(method.getAnnotationMethodId()).append(", ");
                    featureGroupBy.append("subquery.org_").append(org.getOrganismId()).append("method_").append(method.getAnnotationMethodId()).append(", ");
                }
            }

            paralogQuery.delete(paralogQuery.length() - 2, paralogQuery.length());
            clusterQuery.delete(clusterQuery.length() - 2, clusterQuery.length());
            featureQuery.delete(featureQuery.length() - 2, featureQuery.length());
            featureGroupBy.delete(featureGroupBy.length() - 2, featureGroupBy.length());

            paralogQuery.insert(0, "(SELECT FeatureCluster, ").append(" FROM fact_location_ortho_fact GROUP BY FeatureCluster)");
            clusterQuery.insert(0, "(SELECT FeatureCluster, ").append(" FROM fact_location_ortho_fact GROUP BY FeatureCluster)");
            featureQuery.insert(0, "SELECT fact_location_ortho_fact.Feature, ");
            featureQuery.append(" FROM fact_location_ortho_fact, temp_ortholog_summary as subquery ");
            featureQuery.append("WHERE subquery.FeatureCluster = fact_location_ortho_fact.FeatureCluster GROUP BY fact_location_ortho_fact.Feature");

            //create temp_ortholog_summary table
            ArrayList<String> statements = new ArrayList(2);
            StringBuilder bldr = new StringBuilder("DROP TABLE IF EXISTS temp_ortholog_summary; ");
            statements.add(bldr.toString());
            bldr = new StringBuilder();
            bldr.append("CREATE TABLE temp_ortholog_summary (INDEX FEATUREINDEX USING BTREE (FeatureCluster) )");
            bldr.append(clusterQuery);
            statements.add(bldr.toString());
            mgr.createNativeStatement(statements);
            
            statements = new ArrayList(2);
            bldr = new StringBuilder("DROP TABLE IF EXISTS temp_ortholog_total; ");
            statements.add(bldr.toString());
            bldr = new StringBuilder();
            bldr.append("CREATE TABLE temp_ortholog_total (INDEX FEATUREINDEX USING BTREE (FeatureCluster) )");
            bldr.append(paralogQuery);
            statements.add(bldr.toString());
            mgr.createNativeStatement(statements);

            //create temp_ortholog_feature_summary
            statements = new ArrayList(2);
            statements.add("DROP TABLE IF EXISTS temp_ortholog_feature_summary; ");
            featureQuery.insert(0, "CREATE TABLE temp_ortholog_feature_summary (INDEX FEATUREINDEX USING BTREE (Feature) ) (").append("); ");
            statements.add(featureQuery.toString());
            mgr.createNativeStatement(statements);

            //create temp_go_ortholog_summary
            statements = new ArrayList(2);
            bldr = new StringBuilder();
            bldr.append("DROP TABLE IF EXISTS temp_go_ortholog_summary; ");
            statements.add(bldr.toString());
            bldr = new StringBuilder();
            bldr.append(" CREATE TABLE temp_go_ortholog_summary (INDEX OrthoClusterIndex USING BTREE (OrthoCluster), INDEX GoClusterIndex USING BTREE (GoCluster), INDEX OrthoMethodIndex USING BTREE (OrthologMethod)) ");
            bldr.append(" SELECT  fact_location_ortho_fact.FeatureCluster AS OrthoCluster, fact_feature_go_anno.FeatureCluster AS GoCluster, fact_location_ortho_fact.AnnotationMethod AS OrthologMethod, fact_feature_go_anno.AnnotationMethod AS GoMethod FROM fact_location_ortho_fact INNER JOIN fact_feature_go_anno ON fact_location_ortho_fact.Feature = fact_feature_go_anno.Feature GROUP BY OrthoCluster, GoCluster, OrthologMethod ;");
            statements.add(bldr.toString());
            mgr.createNativeStatement(statements);

            //get unclassified
            bldr = new StringBuilder("SELECT t FROM Cluster_GoTerm as t WHERE clusterName = 'GS:0000000'");
            List<FeatureCluster> result = mgr.createQuery(bldr.toString());
            if (result.size() > 0) {
                Integer goIt = result.get(0).getFeatureClusterId();
                //get all the clusters that have no annotation
                bldr = new StringBuilder("INSERT INTO temp_go_ortholog_summary ");
                bldr.append("SELECT fact_location_ortho_fact.FeatureCluster,");
                bldr.append(goIt.intValue()).append(", fact_location_ortho_fact.AnnotationMethod, temp_go_ortholog_summary.OrthoCluster as GoMethod ");
                bldr.append("FROM fact_location_ortho_fact LEFT OUTER JOIN temp_go_ortholog_summary ON fact_location_ortho_fact.FeatureCluster = temp_go_ortholog_summary.OrthoCluster GROUP BY fact_location_ortho_fact.FeatureCluster HAVING GoMethod IS NULL;");
                statements = new ArrayList(1);
                statements.add(bldr.toString());
                mgr.createNativeStatement(statements);
            }
        }
    }
}
