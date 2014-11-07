/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.dimension2;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.dimension.Constants;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openide.util.Lookup;

/**
 *
 * @author aacain
 */
public class DimensionImpl4 {

    private Map<String, Table> tableByName = new HashMap<String, Table>();
    private DataManager dataManager;
    private static int nextVarIndex = 0;

    public DimensionImpl4() {
        dataManager = DataManager.getDefault();
        loadDimension();
    }

    public Set<Property> getTableProperties(String rootTableName) {
        Table table = tableByName.get(rootTableName);
        return table.getAllProperties();
    }

    public Table addTable(String tableName, String entityName) {
        String databaseTableName = dataManager.getDatabaseTableName(entityName);
        Table table = new Table(tableName, entityName, databaseTableName);
        tableByName.put(tableName, table);
        return table;
    }

    public void addTableId(String tableName, String propertyName, String displayName, String column) {
        Property property = addTableProperty(tableName, propertyName, displayName, column, null, null);
        property.getTable().setIdProperty(property);
    }

    public Property addTableProperty(String tableName, String propertyName,
            String displayName, String column, String select, List<String[]> criteria) {
        Table table = tableByName.get(tableName);
        Property property;
        if (column == null) {
            property = new Property(table, propertyName, propertyName, displayName, select, criteria);
        } else {
            String dbColumn = dataManager.getDatabaseColumnName(table.getEntityName(), column);
            property = new Property(table, propertyName, getNewPropertyAlias(table), displayName, column, dbColumn);
        }
        table.addSimpleProperty(property);
        return property;
    }

    private LinkProperty addLinkProperty(Table table, Property childProperty,
            String alias, String displayName, TableLink tableLink,
            String selectStatement, List<String[]> criteria) {

        LinkProperty property = new LinkProperty(table, childProperty, alias, displayName, tableLink, selectStatement, criteria);
        table.addSimpleProperty(property);

        return property;
    }

    public void addTableGroupByColumn(String tableName, String column) {
        Table table = tableByName.get(tableName);
        Property property = table.getProperty(column);
        if (property == null) {
            property = addTableProperty(tableName, column, column, column, column, null);
        }
        table.addGroupBy(property);
    }

    public void addLinkTable(String parentTableName,
            String childTableName, String propertyName,
            String linkType, String parentPropertyName,
            String selectStatement, List<String> parameters) {

        Table parentTable = tableByName.get(parentTableName);
        Table childTable = tableByName.get(childTableName);

        Property parentProperty = parentTable.getProperty(parentPropertyName);
        if (parentProperty == null) {
            parentProperty = addTableProperty(parentTableName, parentPropertyName, parentPropertyName, parentPropertyName, null, null);
        }
        Property childProperty = childTable.getIdProperty();

        TableLink tableLink = new TableLink(propertyName, parentTable, childTable, childProperty, parentProperty, linkType, selectStatement);
        parentTable.addChildTableLinksByProperty(propertyName, tableLink);
        childTable.addParentLinkTable(tableLink);
    }

    public void addQuery(String tableName, String displayName, String parentPropertyName, String selectStatement, List<String[]> criteria) {
        Table parentTable = tableByName.get(tableName);
        String alias = getNewPropertyAlias(parentTable);
        Property property = null;
        TableLink tableLinkToChild = parentTable.getChildTableLinksByProperty(parentPropertyName);
        if (tableLinkToChild != null) {
            property = addLinkProperty(parentTable, null, alias, displayName, tableLinkToChild, selectStatement, criteria);
        } else {
            property = addTableProperty(parentTable.getTableName(), alias, displayName, null, selectStatement, criteria);

        }
        addParentQuery(property);
    }

    private void addParentQuery(Property property) {
        //get all parent link tables
        Table childTable = property.getTable();
        Set<TableLink> allParentLinkTables = childTable.getAllParentLinkTables();
        for (TableLink parentLinkTable : allParentLinkTables) {
            Property parentProperty = addLinkProperty(parentLinkTable.getParentTable(), property, property.getAlias(), property.getDisplayName(), parentLinkTable, null, null);
            addParentQuery(parentProperty);
        }
    }

    public String createQuery(String rootTableName, List<Property> selectedProperties, List<Property> groupBy) {
        Table table = tableByName.get(rootTableName);
        Query query = new Query(rootTableName, table);
        for (Property property : selectedProperties) {
            createQuery(query, property);
        }

        String s = queryToString(query, groupBy).toString();

        return s;
    }

    private StringBuilder queryToString(Query query, List<Property> rootGroupBys) {
        List<StringBuilder> selects = new LinkedList<StringBuilder>();
        Set<Property> selectProperties = query.getSelectDimensions();
        for (Property property : selectProperties) {
            if (property instanceof LinkProperty) {
                LinkProperty linkProperty = (LinkProperty) property;
                selects.add(updateSelect(query, linkProperty));
            } else {
                selects.add(updateSelect(query, property));
            }
        }

        List<StringBuilder> wheres = new LinkedList<StringBuilder>();
        Set<LinkProperty> linkProperties = query.getLinkProperties();
        HashSet<TableLink> joinLinks = new HashSet<TableLink>();
        HashSet<TableLink> subLinks = new HashSet<TableLink>();
        if (linkProperties != null) {
            for (LinkProperty propLink : linkProperties) {
                TableLink tLink = propLink.getTableLink();
                StringBuilder w = new StringBuilder();
                String childTableAlias = query.getTableName(tLink);
                w.append(query.getName()).append(".").append(tLink.getParentLinkProperty().getDbColumn());
                w.append(" = ");
                w.append(childTableAlias).append('.');

                if (tLink.getLinkType().equals(Constants.LINK_JOIN)) {
                    joinLinks.add(tLink);
                    w.append(tLink.getChildLinkProperty().getDbColumn());
                }
                if (tLink.getLinkType().equals(Constants.LINK_SUBSELECT)) {
                    subLinks.add(tLink);
                    w.append(tLink.getChildLinkProperty().getAlias());
                }
                wheres.add(w);
            }
        }

        List<StringBuilder> froms = new LinkedList<StringBuilder>();
        //add root table
        StringBuilder rootFrom = new StringBuilder();
        rootFrom.append(query.getRootTable().getDatabaseTableName()).append(" as ").append(query.getName());
        froms.add(rootFrom);
        List<StringBuilder> groupBys = new LinkedList<StringBuilder>();
        for (TableLink tLink : joinLinks) {
            StringBuilder f = new StringBuilder();
            f.append(tLink.getChildTable().getDatabaseTableName()).append(" as ");
            f.append(query.getTableName(tLink));
            froms.add(f);

            StringBuilder childTableName = new StringBuilder(query.getTableName(tLink));
            for (Property p : tLink.getChildTable().getGroupBy()) {
                StringBuilder gb = new StringBuilder();
                gb.append(childTableName).append('.');
                gb.append(p.getDbColumn());
                groupBys.add(gb);
            }
        }

        //add root query groupby
        for (Property p : query.getRootTable().getGroupBy()) {
            StringBuilder gb = new StringBuilder();
            gb.append(query.getName()).append('.');
            gb.append(p.getDbColumn());
            groupBys.add(gb);
        }

        for (Property p : rootGroupBys) {
            StringBuilder gb = new StringBuilder();
            gb.append(p.getAlias());
            groupBys.add(gb);
        }

        //get subselects
        List<StringBuilder> subStrings = new LinkedList<StringBuilder>();
        for (TableLink tLink : subLinks) {
            Query subQuery = query.getSubQueryByChildTable(tLink.getChildTable());
            StringBuilder subString = new StringBuilder('(');
            subString.append(queryToString(subQuery, Collections.EMPTY_LIST));
            subString.append(") as ").append(query.getTableName(tLink));
            subStrings.add(subString);
        }

        //add Selects
        StringBuilder finalBuilder = new StringBuilder();
        finalBuilder.append(" SELECT ");
        int i = 0;
        for (StringBuilder b : selects) {
            if (i != 0) {
                finalBuilder.append(", ");
            }
            finalBuilder.append(b).append(' ');
            i++;
        }

        //add from
        finalBuilder.append(System.getProperty("line.separator")).append(" FROM ");
        i = 0;
        for (StringBuilder b : froms) {
            if (i != 0) {
                finalBuilder.append(", ");
            }
            finalBuilder.append(b).append(' ');
            i++;
        }

        //add subselects
        for (StringBuilder s : subStrings) {
            if (i != 0) {
                finalBuilder.append(", (");
            }
            finalBuilder.append(s).append(' ');
            i++;
        }

        //add where
        finalBuilder.append(System.getProperty("line.separator")).append(" WHERE ");
        i = 0;
        for (StringBuilder b : wheres) {
            if (i != 0) {
                finalBuilder.append(" AND ");
            }
            finalBuilder.append(b).append(' ');
            i++;
        }

        if (!groupBys.isEmpty()) {
            finalBuilder.append(System.getProperty("line.separator")).append(" GROUP BY ");
            i = 0;
            for (StringBuilder g : groupBys) {
                if (i != 0) {
                    finalBuilder.append(", ");
                }
                finalBuilder.append(g);
                i++;
            }
        }

        return finalBuilder;
    }

    private StringBuilder updateSelect(Query query, LinkProperty property) {
        String tableName = query.getTableName(property.getTableLink());

        String mySelect = property.getSelect();
        if (mySelect != null) {
            for (String[] crits : property.getCriteria()) {
                //get the column
                String columnName = dataManager.getDatabaseColumnName(property.getTableLink().getChildTable().getEntityName(), crits[1]);
                mySelect = mySelect.replace(crits[0], tableName + "." + columnName);
            }
        } else {
            mySelect = tableName + "." + property.getPropertyName();
        }

        String linkSelect = property.getTableLink().getSelectStatement();
        if (linkSelect != null) {
            mySelect = linkSelect.replaceAll(":parameter", mySelect);
        }

        return new StringBuilder(mySelect).append(" as ").append(property.getAlias());
    }

    private StringBuilder updateSelect(Query query, Property property) {
        String tableName = query.getName();
        String mySelect = property.getSelect();
        if (mySelect != null) {
            for (String[] crits : property.getCriteria()) {
                String columnName = dataManager.getDatabaseColumnName(property.getTable().getEntityName(), crits[1]);
                mySelect = mySelect.replace(crits[0], tableName + "." + columnName);
            }
        } else {
            mySelect = tableName + "." + property.getDbColumn();
        }

        return new StringBuilder(mySelect).append(" as ").append(property.getAlias());
    }

    public String createQuery(String rootTableName) {
        Set<Property> props = getTableProperties(rootTableName);
        LinkedList<Property> list = new LinkedList<Property>();
        LinkedList<Property> groupBys = new LinkedList<Property>();
        for (Property property : props) {
            list.add(property);
            if (!property.getPropertyName().equals("myCount")
                    && !property.getPropertyName().equals("featureId")) {
                groupBys.add(property);
            }
        }

        list.addAll(props);

        return createQuery(rootTableName, list, groupBys);
    }

    private void createQuery(Query query, Property property) {
        query.addSelect(property);
        if (property instanceof LinkProperty) {
            LinkProperty linkProperty = (LinkProperty) property;
            query.addLinkProperty(linkProperty);
            if (linkProperty.getTableLink().getLinkType().equals(Constants.LINK_SUBSELECT)) {
                Table childTable = linkProperty.getTableLink().getChildTable();
                Query subQuery = query.getSubQueryByChildTable(childTable);
                if (subQuery == null) {
                    subQuery = new Query(childTable.getTableName(), childTable);
                    Property idProperty = linkProperty.getTableLink().getChildLinkProperty();
                    subQuery.addSelect(idProperty);
                    query.addSubquery(subQuery);
                }
                Property childProperty = linkProperty.getChildProperty();
                createQuery(subQuery, childProperty);
            }
        }
    }

    private synchronized String getNewPropertyAlias(Table table) {
        return (new StringBuilder().append("arg_").append(nextVarIndex++)).toString();
    }

    private void loadDimension() {
        addTable("MyFeature", "Feature");
        addTableId("MyFeature", "featureId", "featureId", "featureId");
        addTableProperty("MyFeature", "featureType", "Feature Type", "featureType", null, null);


        addTable("FeatureExistsInCluster", "OrthoFact");
        addTableId("FeatureExistsInCluster", "featureId", "Feature Id", "featureId");
        addTableGroupByColumn("FeatureExistsInCluster", "featureId");

        addTable("MyMethod", "AnnotationMethod");
        addTableId("MyMethod", "methodId", "Method Id", "annotationMethodId");

        addTable("MemberInOrg", "OrthoFact");
        addTableId("MemberInOrg", "featureClusterId", "Feature Cluster Id", "featureClusterId");
        addTableGroupByColumn("MemberInOrg", "featureClusterId");

        addTable("MyOrganism", "Organism");
        addTableId("MyOrganism", "organismId", "Organism Id", "organismId");

        addLinkTable("MyFeature", "FeatureExistsInCluster", "iExist", Constants.LINK_SUBSELECT, "featureId", null, null);
        addLinkTable("FeatureExistsInCluster", "MyMethod", "byMethod", Constants.LINK_JOIN, "annotationMethodId", null, null);
        addLinkTable("FeatureExistsInCluster", "MemberInOrg", "inOrg", Constants.LINK_SUBSELECT, "featureClusterId", "CASE WHEN SUM(:parameter) > 0 THEN 'True' ELSE 'False' END", null);
        addLinkTable("MemberInOrg", "MyOrganism", "org", Constants.LINK_JOIN, "organismId", null, null);

        List<String[]> critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "methodCategory"});
//        addQuery("FeatureExistsInCluster", "Feature Annotations", "byMethod", "CASE WHEN SUM(CASE WHEN :criteria1 = 'Feature Annotation' THEN 1 ELSE 0 END) > 0 THEN 'True' ELSE 'False' END", critList);


//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "Carsonella ruddii (strain PV)", "org", "SUM(CASE WHEN :criteria1 = '17977' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "Candidatus Blochmannia vafer str. BVAF", "org", "SUM(CASE WHEN :criteria1 = '50045' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "Blochmannia pennsylvanicus (strain BPEN)", "org", "SUM(CASE WHEN :criteria1 = '13875' THEN 1 ELSE 0 END)", critList);




        critList = new LinkedList<String[]>();
        critList.add(new String[]{":criteria1", "projectId"});
        addQuery("MemberInOrg", "B. abortus S19", "org", "SUM(CASE WHEN :criteria1 = '18999' THEN 1 ELSE 0 END)", critList);

        critList = new LinkedList<String[]>();
        critList.add(new String[]{":criteria1", "projectId"});
        addQuery("MemberInOrg", "B.abortus 9-941", "org", "SUM(CASE WHEN :criteria1 = '9619' THEN 1 ELSE 0 END)", critList);

        critList = new LinkedList<String[]>();
        critList.add(new String[]{":criteria1", "projectId"});
        addQuery("MemberInOrg", "B. suis1330", "org", "SUM(CASE WHEN :criteria1 = '320' THEN 1 ELSE 0 END)", critList);

        critList = new LinkedList<String[]>();
        critList.add(new String[]{":criteria1", "projectId"});
        addQuery("MemberInOrg", "B suisATCC", "org", "SUM(CASE WHEN :criteria1 = '20371' THEN 1 ELSE 0 END)", critList);

        critList = new LinkedList<String[]>();
        critList.add(new String[]{":criteria1", "projectId"});
        addQuery("MemberInOrg", "B ovis25840", "org", "SUM(CASE WHEN :criteria1 = '12514' THEN 1 ELSE 0 END)", critList);

        critList = new LinkedList<String[]>();
        critList.add(new String[]{":criteria1", "projectId"});
        addQuery("MemberInOrg", "B microtiCCM4915", "org", "SUM(CASE WHEN :criteria1 = '32233' THEN 1 ELSE 0 END)", critList);

        critList = new LinkedList<String[]>();
        critList.add(new String[]{":criteria1", "projectId"});
        addQuery("MemberInOrg", "B. melitensis16M", "org", "SUM(CASE WHEN :criteria1 = '180' THEN 1 ELSE 0 END)", critList);

        critList = new LinkedList<String[]>();
        critList.add(new String[]{":criteria1", "projectId"});
        addQuery("MemberInOrg", "B. abortus2308", "org", "SUM(CASE WHEN :criteria1 = '16203' THEN 1 ELSE 0 END)", critList);

        critList = new LinkedList<String[]>();
        critList.add(new String[]{":criteria1", "projectId"});
        addQuery("MemberInOrg", "B. melitensisATCC", "org", "SUM(CASE WHEN :criteria1 = '30561' THEN 1 ELSE 0 END)", critList);

        critList = new LinkedList<String[]>();
        critList.add(new String[]{":criteria1", "projectId"});
        addQuery("MemberInOrg", "B. canisATCC", "org", "SUM(CASE WHEN :criteria1 = '20243' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "A. tumefaciens C58", "org", "SUM(CASE WHEN :criteria1 = '283' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "A. radiobacter K84", "org", "SUM(CASE WHEN :criteria1 = '13402' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "S. medicae WSM419", "org", "SUM(CASE WHEN :criteria1 = '16304' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "S. meliloti 1021", "org", "SUM(CASE WHEN :criteria1 = '19' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "R. leguminosarum vs. viciae 3841", "org", "SUM(CASE WHEN :criteria1 = '344' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "R. sp. NGR234", "org", "SUM(CASE WHEN :criteria1 = '21101' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "R. etli CIAT 652", "org", "SUM(CASE WHEN :criteria1 = '28021' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "O. anthropi ATCC", "org", "SUM(CASE WHEN :criteria1 = '20097' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "O. anthropi ATCC", "org", "SUM(CASE WHEN :criteria1 = '13932' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "O. anthropi ATCC", "org", "SUM(CASE WHEN :criteria1 = '19485' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "O. anthropi ATCC", "org", "SUM(CASE WHEN :criteria1 = '29273' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "O. anthropi ATCC", "org", "SUM(CASE WHEN :criteria1 = '29835' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "O. anthropi ATCC", "org", "SUM(CASE WHEN :criteria1 = '13372' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "O. anthropi ATCC", "org", "SUM(CASE WHEN :criteria1 = '20179' THEN 1 ELSE 0 END)", critList);
//
//        critList = new LinkedList<String[]>();
//        critList.add(new String[]{":criteria1", "projectId"});
//        addQuery("MemberInOrg", "O. anthropi ATCC", "org", "SUM(CASE WHEN :criteria1 = '19485' THEN 1 ELSE 0 END)", critList);

        critList = new LinkedList<String[]>();
        critList.add(new String[]{":criteria1", "featureId"});
        //addQuery("MyFeature", "myCount", "myCount", "COUNT(:criteria)", critList);
        addTableProperty("MyFeature", "myCount", "Count", null, "COUNT(:criteria1)", critList);
    }

    private static class Query {

        private final String name;
        private final Table rootTable;
        private final Set<Property> selectDimensions = new LinkedHashSet<Property>();
        private final Set<LinkProperty> linkProperties = new HashSet<LinkProperty>();
        private Map<Table, Query> subquery;
        private Map<TableLink, String> tableName = new HashMap<TableLink, String>();
        private static int nextTableIndex = 0;

        public Query(String name, Table rootTable) {
            this.name = name;
            this.rootTable = rootTable;
        }

        public void addSelect(Property property) {
            selectDimensions.add(property);
        }

        public void addLinkProperty(LinkProperty linkProperty) {
            linkProperties.add(linkProperty);
        }

        public Set<LinkProperty> getLinkProperties() {
            return linkProperties;
        }

        public String getName() {
            return name;
        }

        public Table getRootTable() {
            return rootTable;
        }

        public Set<Property> getSelectDimensions() {
            return selectDimensions;
        }

        public Map<Table, Query> getSubquery() {
            return subquery;
        }

        public Query getSubQueryByChildTable(Table table) {
            if (subquery == null) {
                return null;
            }
            return this.subquery.get(table);
        }

        public void addSubquery(Query subQuery) {
            if (this.subquery == null) {
                this.subquery = new HashMap<Table, Query>();
            }
            this.subquery.put(subQuery.rootTable, subQuery);
        }

        private synchronized String getNextTableName() {
            StringBuilder builder = new StringBuilder();
            return builder.append("tbl_").append(nextTableIndex++).toString();
        }

        public String getTableName(TableLink linkTable) {
            String myName = tableName.get(linkTable);
            if (myName == null) {
                myName = getNextTableName();
                tableName.put(linkTable, myName);
            }

            return myName;
        }
    }

    public void createQueryFromView() {
        String viewTable = "temp_ps_v1";
        DataManager mgr = DataManager.getDefault();
        mgr.createNativeQuery("select * from " + viewTable);
    }
//    public String createQueryFromView(){
//
//    }
}
