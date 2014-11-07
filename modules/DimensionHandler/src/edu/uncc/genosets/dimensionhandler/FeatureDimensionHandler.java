/*
 * 
 * 
 */
package edu.uncc.genosets.dimensionhandler;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.dimensionhandler.CombinedProperty.PropertyCriteria;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.openide.util.Lookup;

/**
 *
 * @author aacain
 */
public abstract class FeatureDimensionHandler implements DimensionHandler{

    public static FeatureDimensionHandler instantiate() {
        return new FeatureDimensionHandlerImpl();
    }

    public abstract void createTempTable();


    /** create the hierarchy of dimensions
     *
     */
    public abstract void createNewProperties();

    private static class FeatureDimensionHandlerImpl extends FeatureDimensionHandler {

        List<Property> properties = new LinkedList<Property>();
        DataManager mgr;

        public FeatureDimensionHandlerImpl() {
            mgr = DataManager.getDefault();
        }

        @Override
        public List<Property> getProperties(String tableName) {
            if (properties.isEmpty()) {
                loadProperties();
            }
            return properties;
        }

        @Override
        public List<Object[]> createQuery(List<Property> selectProps, List<Property> groupByProps, boolean idAsCount) {
            StringBuilder queryString = new StringBuilder("SELECT ");
            if (idAsCount) {
                queryString.append("COUNT(featureId)");
            } else {
                queryString.append("featureId");
            }
            for (Property prop : selectProps) {
                if (!prop.getName().equals("featureId")) {
                    queryString.append(", ");
                    if (prop instanceof CombinedProperty) {
                        StringBuilder temp = getSelect((CombinedProperty) prop);
                        System.out.println(prop.getAlias() + "\t" + prop.getDisplayName() + "\t" + temp);
                        queryString.append(temp).append(" as ");
                    }
                    System.out.println(prop.getAlias() + "\t" + prop.getDisplayName());
                    queryString.append(prop.getAlias());
                }
            }
            queryString.append(" FROM temp_ps_v2 ");
            if (groupByProps != null && !groupByProps.isEmpty()) {
                queryString.append(" GROUP BY ");
                int i = 0;
                for (Property gProp : groupByProps) {
                    if (i > 0) {
                        queryString.append(",");
                    }
                    queryString.append(gProp.getAlias());
                    i++;
                }
            }

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
                b.append(crit.getProperty().getAlias()).append(" = ").append(crit.getValue()).append(" ");
                i++;
            }
            b.append(") THEN 'True' ELSE 'False' END ");

            return b;
        }

        @Override
        public void createTempTable() {
            Property featureId = new Property("featureId", "arg_" + properties.size(), "Feature Id", "ontology");
            properties.add(featureId);
            StringBuilder fClusterTable = new StringBuilder("FeatureExistsInCluster"); //fact_location_ortho_fact
            StringBuilder subTable = new StringBuilder("MemberInOrg"); //subquery
            StringBuilder subOrg = new StringBuilder("Org"); //Organism
            StringBuilder subFact = new StringBuilder("SubFact"); //fact_location_ortho_fact

            StringBuilder fClusterSelect = new StringBuilder("SELECT ").append(fClusterTable).append(".Feature as featureId ");
            StringBuilder orgClusterSelect = new StringBuilder("SELECT ").append(subFact).append(".FeatureCluster as FeatureCluster ");
            List<Organism> orgs = mgr.getOrganisms();
            int i = 1;
            HashMap<String, HashMap<String, CombinedProperty>> hierMap = new HashMap<String, HashMap<String, CombinedProperty>>(10);
            for (Organism organism : orgs) {
                Property p = new Property(organism.getStrain(), "arg_" + properties.size(), organism.getStrain(), "ontology");
                properties.add(p);
                addHier(hierMap, organism, p);
                fClusterSelect.append(", ").
                        append("CASE WHEN SUM(").append(subTable).append(".").
                        append(p.getAlias()).append(" > 0) THEN 'True' ELSE 'False' END as ").
                        append(p.getAlias());
                orgClusterSelect.append(",").append("SUM(CASE WHEN ").append(subOrg).append(".OrganismId = ").
                        append(organism.getOrganismId().toString()).append(" THEN 1 ELSE 0 END) as ").append(p.getAlias());
                i++;
            }

            StringBuilder stmt = new StringBuilder();
            stmt.append(fClusterSelect).append(" FROM fact_location_ortho_fact as ").append(fClusterTable).
                    append(" , (").append(orgClusterSelect).append(" FROM fact_location_ortho_fact as ").
                    append(subFact).append(" , ").append("Organism as ").
                    append(subOrg).append(" WHERE ").append(subFact).
                    append(".Organism = ").append(subOrg).append(".OrganismId").append(" GROUP BY ").append(subFact).append(".FeatureCluster) as ").append(subTable).append(" WHERE ").append(fClusterTable).append(".FeatureCluster = ").append(subTable).append(".FeatureCluster").
                    append(" GROUP BY ").append(fClusterTable).append(".Feature");

            System.out.println(stmt.toString());
        }

        private void loadProperties() {
            if (properties.isEmpty()) {
                DimensionWriter writer = new DimensionWriter();
                properties = writer.loadAll();
            }
        }

        @Override
        public void createNewProperties() {
            //set up hiermap
            HashMap<String, HashMap<String, CombinedProperty>> hierMap = new HashMap<String, HashMap<String, CombinedProperty>>(10);
//            Property featureId = new Property("featureId", "featureId", "Feature Id");
//            properties.add(featureId);
            List<Organism> orgs = mgr.getOrganisms();
            int i = 1;
            for (Organism organism : orgs) {
                Property p = new Property(organism.getStrain(), "arg_" + i, organism.getStrain(), "ontology");
                properties.add(p);
                i++;
                addHier(hierMap, organism, p);
            }
        }

        private void addHier(HashMap<String, HashMap<String, CombinedProperty>> hierList,
                Organism organism, Property property) {
            addHier(hierList, "kingdom", organism.getKingdom(), property);
            addHier(hierList, "phylum", organism.getPhylum(), property);
            addHier(hierList, "class", organism.getTaxClass(), property);
            addHier(hierList, "taxOrder", organism.getTaxOrder(), property);
            addHier(hierList, "family", organism.getFamily(), property);
            addHier(hierList, "genus", organism.getGenus(), property);
            addHier(hierList, "species", organism.getSpecies(), property);
        }

        private void addHier(HashMap<String, HashMap<String, CombinedProperty>> map,
                String hierLevel, String value, Property property) {

            HashMap<String, CombinedProperty> level = map.get(hierLevel);
            if (level == null) {
                level = new HashMap<String, CombinedProperty>();
                map.put(hierLevel, level);
            }
            CombinedProperty combined = level.get(value);
            if (combined == null) {
                combined = new CombinedProperty(hierLevel + ": " + value, "arg_hier" + properties.size(), "In All " + hierLevel + ": " + value, CombinedProperty.OPERATOR_AND, "ontology");
                level.put(value, combined);
                properties.add(combined);
            }
            combined.addProperty(property, "'True'");
        }

        @Override
        public void writeProperties() {
            DimensionWriter writer = new DimensionWriter();
            for (Property property : getProperties(null)) {
                writer.save(property);
            }
        }

        @Override
        public void readProperties() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
