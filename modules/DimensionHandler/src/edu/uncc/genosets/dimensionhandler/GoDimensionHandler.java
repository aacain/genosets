/*
 * 
 * 
 */
package edu.uncc.genosets.dimensionhandler;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.ClusterGraph;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.dimensionhandler.CombinedProperty.PropertyCriteria;
import java.util.List;
import org.openide.util.Lookup;

/**
 *
 * @author aacain
 */
public class GoDimensionHandler {

    public final static int OFFSET = 4;
    public final static int INDEX_COUNT_OR_ID = 0;
    public final static int INDEX_CLUSTER_ID = 1;
    public final static int INDEX_GO_ID = 2;
    public final static int INDEX_GO_NAME = 3;
    private FeatureDimensionHandler fHandler;
    public static final String GO_NAME = "goName";
    private DataManager mgr;

    public GoDimensionHandler() {
        fHandler = FeatureDimensionHandler.instantiate();
        fHandler.getProperties(null);
        mgr = DataManager.getDefault();
    }

    public static GoDimensionHandler instantiate() {
        return new GoDimensionHandler();
    }

    public List<Object[]> createQuery(List<Property> selectProps, List<Property> groupByProps, boolean idAsCount) {
        StringBuilder queryString = new StringBuilder("SELECT ");
        if (idAsCount) {
            queryString.append("COUNT(f.featureId)");
        } else {
            queryString.append("f.featureId");
        }
        queryString.append(", t.FeatureClusterId, t.ClusterName, t.goName ");
        for (Property prop : selectProps) {
            queryString.append(", ");
            if (prop instanceof CombinedProperty) {
                StringBuilder temp = getSelect((CombinedProperty) prop);
                //System.out.println(prop.getAlias() + "\t" + prop.getDisplayName() + "\t" + temp);
                queryString.append(temp).append(" as ");
            } else {
                queryString.append("f.").append(prop.getAlias()).append(" as ");
            }
            queryString.append(prop.getAlias());
        }
        queryString.append(" FROM temp_ps_v2 as f, temp_go as gf,  cluster_go_term as t");
        queryString.append(" WHERE f.featureId = gf.Feature AND t.FeatureClusterId = gf.FeatureCluster ");
        //add group by
        StringBuilder group = null;
        if (idAsCount) {
            group = new StringBuilder(" GROUP BY gf.FeatureCluster ");
        }
        if (groupByProps != null && !groupByProps.isEmpty()) {
            int i = 0;
            for (Property gProp : groupByProps) {
                if (group == null) {
                    group = new StringBuilder("GROUP BY ");
                }
                group.append(gProp.getAlias());
                if (i < groupByProps.size() - 1) {
                    group.append(", ");
                }
                i++;
            }
        }
        if (group != null) {
            queryString.append(group);
        }

        System.out.println(queryString.toString());
        return mgr.createNativeQuery(queryString.toString());
    }

    public List<Object[]> createAllQuery(List<Property> selectProps, boolean idAsCount) {
        StringBuilder queryString = new StringBuilder("SELECT ");
        if (idAsCount) {
            queryString.append("COUNT(f.featureId)");
        } else {
            queryString.append("f.featureId");
        }
        queryString.append(", t.FeatureClusterId, t.ClusterName, t.goName ");
        for (Property prop : selectProps) {
            queryString.append(", ");
            if (prop instanceof CombinedProperty) {
                StringBuilder temp = getSelect((CombinedProperty) prop);
                //System.out.println(prop.getAlias() + "\t" + prop.getDisplayName() + "\t" + temp);
                queryString.append(temp).append(" as ");
            } else {
                queryString.append("f.").append(prop.getAlias()).append(" as ");
            }
            queryString.append(prop.getAlias());
        }
        queryString.append(" FROM temp_ps_v2 as f, temp_go as gf,  cluster_go_term as t");
        queryString.append(" WHERE f.featureId = gf.Feature AND t.FeatureClusterId = gf.FeatureCluster ");


        System.out.println(queryString.toString());
        return mgr.createNativeQuery(queryString.toString());

    }

    private StringBuilder getSelect(CombinedProperty prop) {
        StringBuilder b = new StringBuilder("CASE WHEN ( ");
        List<PropertyCriteria> propertyList = prop.getPropertyList();
        int i = 0;
        for (PropertyCriteria crit : propertyList) {
            if (i > 0) {//not first so add operator
                b.append(" ").append(prop.getOperator()).append(" ");
            }
            b.append("f.").append(crit.getProperty().getAlias()).append(" = ").append(crit.getValue()).append(" ");
            i++;
        }
        b.append(") THEN 'True' ELSE 'False' END ");

        return b;
    }

    public static List<ClusterGraph> getGraphEdges() {
        String query = "from Graph_Go";
        return DataManager.getDefault().createQuery(query);
    }

    public static List<FeatureCluster> getNodes() {
        String query = "from Cluster_GoTerm";
        return DataManager.getDefault().createQuery(query);
    }
}
