/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.dimension;

import edu.uncc.genosets.datamanager.api.DataManager;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openide.util.Lookup;

/**
 *
 * @author aacain
 */
public class DimensionControllerImpl3 {

    Map<Dimension, DimensionLink> parentToChildDimensions = new HashMap<Dimension, DimensionLink>();
    Map<Table, Set<Dimension>> dimensionsByTable = new HashMap<Table, Set<Dimension>>();
    Map<Table, Set<TableLink>> childToParentTables = new HashMap<Table, Set<TableLink>>();
    Map<String, Table> tableByName = new HashMap<String, Table>();
    Map<Table, Map<String, Dimension>> dimensionsByName = new HashMap<Table, Map<String, Dimension>>();
    private static int nextVarIndex = 0;
    DataManager dataManager;

    public DimensionControllerImpl3() {
        dataManager = DataManager.getDefault();
    }

    public String createQuery(Table rootTable, List<Dimension> dimensions) {
        Query query = new Query(rootTable.getName(), rootTable);
        for (Dimension dimension : dimensions) {
            createQuery(query, dimension);
        }
        String toString = queryToString(query).toString();
        return toString;
    }

    private StringBuilder queryToString(Query query) {
        List<StringBuilder> selects = new LinkedList<StringBuilder>();
        Set<Dimension> selectDimensions = query.getSelectDimensions();
        if (selectDimensions != null) {
            for (Dimension dimension : selectDimensions) {
                StringBuilder s = new StringBuilder(dimension.getSelect()).append(" as ").append(dimension.getName());
                selects.add(s);
            }
        }

        List<StringBuilder> wheres = new LinkedList<StringBuilder>();
        Set<DimensionLink> dimensionLinks = query.getDimensionLinks();
        HashSet<TableLink> joinLinks = new HashSet<TableLink>();
        HashSet<TableLink> subLinks = new HashSet<TableLink>();
        if (dimensionLinks != null) {
            for (DimensionLink dimensionLink : dimensionLinks) {
                TableLink tLink = dimensionLink.getLinkTable();
                StringBuilder w = new StringBuilder();
                w.append(tLink.getParentTable().getName()).append('.');
                w.append(tLink.getParentDimension().getSelect());
                w.append(" = ");
                w.append(tLink.getPropertyName()).append('.');
                w.append(tLink.getColumnAlias());
                wheres.add(w);

                if (tLink.getJoinType().equals(Constants.LINK_JOIN)) {
                    joinLinks.add(tLink);
                }
                if (tLink.getJoinType().equals(Constants.LINK_SUBSELECT)) {
                    subLinks.add(tLink);
                }
            }
        }

        List<StringBuilder> froms = new LinkedList<StringBuilder>();
        //add root table
        StringBuilder rootFrom = new StringBuilder();
        rootFrom.append(query.getQueryRootTable().getDatabaseTableName()).append(" as ").append(query.getQueryRootTable().getName());
        froms.add(rootFrom);
        for (TableLink tLink : joinLinks) {
            StringBuilder f = new StringBuilder();
            f.append(tLink.getChildTable().getDatabaseTableName()).append(" as ").append(tLink.getPropertyName());
            froms.add(f);
        }


        //get subselects
        List<StringBuilder> subStrings = new LinkedList<StringBuilder>();
        for (TableLink tLink : subLinks) {
            Query subQuery = query.getSubQuery(tLink.getChildTable());
            StringBuilder subString = new StringBuilder('(');
            subString.append(queryToString(subQuery));
            subString.append(") as ").append(tLink.getPropertyName());
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

        return finalBuilder;
    }

    public String createQuery(String tableName, List<Dimension> dimensions) {
        return createQuery(tableByName.get(tableName), dimensions);
    }

    public Set<Dimension> getDimensionsByTable(String tableName) {
        Table table = tableByName.get(tableName);
        return dimensionsByTable.get(table);
    }

    private void createQuery(Query query, Dimension parentDimension) {
        //get child links
        query.addSelectDimension(parentDimension);
        DimensionLink parentToChildDimLink = parentToChildDimensions.get(parentDimension);
        if (parentToChildDimLink != null) {
            query.addDimensionLink(parentToChildDimLink);
            if (parentToChildDimLink.getJoinType().equals(Constants.LINK_SUBSELECT)) {
                Query subQuery = query.getSubQuery(parentToChildDimLink.getLinkTable().getChildTable());
                if (subQuery == null) {
                    subQuery = new Query(parentToChildDimLink.getLinkTable().getChildTable().getName(), parentToChildDimLink.getLinkTable().getChildTable());
                    Dimension childIdDimension = parentToChildDimLink.getChildDimension().getTable().getIdDimension();
                    subQuery.addSelectDimension(childIdDimension);
                    query.addSubquery(subQuery, parentToChildDimLink);
                }
                createQuery(subQuery, parentToChildDimLink.getChildDimension());
            }
        }
    }

    private void addLink(DimensionLink dimLink) {
        parentToChildDimensions.put(dimLink.getParentDimension(), dimLink);
    }

    public void loadDimension() {
        //add tables
        Table fTable = addTable("MyFeature", "featureId", "Feature");
        String fType = updateColumnToDb(fTable, "featureType");
        addDimension("MyFeature", "featureType", "Feature Type", fType);

        Table fexists = addTable("FeatureExistsInCluster", "featureId", "FeatureClusterClassification");
        String fId = updateColumnToDb(fexists, "featureId");
        addGroupBy("FeatureExistsInCluster", Collections.singletonList(fId));

        addTable("MyMethod", "annotationMethodId", "AnnotationMethod");

        Table memTable = addTable("MemberInOrg", "featureClusterId", "FeatureClusterClassification");
        String fClust = updateColumnToDb(memTable, "featureClusterId");
        addGroupBy("MemberInOrg", Collections.singletonList(fClust));

        addTable("MyOrganism", "organismId", "Organism");

        //add link tables
        addLinkTable("MyFeature", "iExist", "FeatureExistsInCluster", Constants.LINK_SUBSELECT, "featureId", null);
        addLinkTable("FeatureExistsInCluster", "byMethod", "MyMethod", Constants.LINK_JOIN, "annotationMethodId", null);
        addLinkTable("FeatureExistsInCluster", "inOrg", "MemberInOrg", Constants.LINK_SUBSELECT, "featureClusterId", "CASE WHEN SUM(:parameter) > 0 THEN 'True' ELSE 'False' END");
        addLinkTable("MemberInOrg", "org", "MyOrganism", Constants.LINK_JOIN, "organismId", null);


        List<String[]> paramList = new LinkedList<String[]>();
        paramList.add(new String[]{":criteria1", "methodCategory"});
        addQueryProperty("FeatureExistsInCluster", "byMethod", "Feature Annotations", "CASE WHEN SUM(CASE WHEN byMethod.:criteria1 = 'Feature Annotation' THEN 1 ELSE 0 END) > 0 THEN 'True' ELSE 'False' END", paramList);

        paramList = new LinkedList<String[]>();
        paramList.add(new String[]{":criteria1", "projectId"});
        addQueryProperty("MemberInOrg", "org", "InOrgX", "SUM(CASE WHEN org.:criteria1 = '18999' THEN 1 ELSE 0 END)", paramList);

        paramList = new LinkedList<String[]>();
        paramList.add(new String[]{"criteria1", "featureId"});
        addQueryProperty("MyFeature", null, "MyCount", "COUNT(MyFeature.:criteria1)", paramList);
    }

    private QueryProperty addQueryProperty(String tableName, String property, String displayName, String select, List<String[]> parameters) {
        Table table = tableByName.get(tableName);
        Dimension dimension = null;
        if (property == null) {
            select = updateSelectWithCriteria(table, select, parameters);
            dimension = addDimension(table, getNewVariableName(), displayName, select);
        } else {
            //get child link
            TableLink childToParentLinkTable = table.getChildLinkByPropertyName(property);
            if (childToParentLinkTable != null) {
                select = updateSelectWithCriteria(childToParentLinkTable.getChildTable(), select, parameters);
                dimension = addDimension(childToParentLinkTable.getChildTable(), getNewVariableName(), displayName, select);
                addParentDimensions(dimension);
            }
        }

        return null;
    }

    private String updateSelectWithCriteria(Table parentTable, String select, List<String[]> paramList) {
        if (paramList != null) {
            if (select != null) {
                for (String[] strings : paramList) {
                    String name = strings[0];
                    String property = strings[1];
                    String nativeColumnName = dataManager.getDatabaseColumnName(parentTable.getEntityName(), property);
                    select = select.replaceAll(name, nativeColumnName);
                }
            }
        }
        return select;
    }

    private String updateColumnToDb(Table table, String column) {
        return dataManager.getDatabaseColumnName(table.getEntityName(), column);
    }

    private void addParentDimensions(Dimension childDimension) {
        //get parent tables
        Set<TableLink> parentTableLinks = childToParentTables.get(childDimension.getTable());
        if (parentTableLinks == null) {
            return;
        }

        String selectStatement = null;
        for (TableLink tableLink : parentTableLinks) {
            if (Constants.LINK_JOIN.equals(tableLink.getJoinType())) {
                selectStatement = childDimension.getSelect();
            } else if (Constants.LINK_SUBSELECT.equals(tableLink.getJoinType())) {
                selectStatement = getSubSelect(childDimension, tableLink);
            }
            Dimension parentDimension = addDimension(tableLink.getParentTable(), childDimension.getName(), childDimension.getDisplayName(), selectStatement);
            DimensionLink dimLink = new DimensionLink(parentDimension, childDimension, selectStatement.toString(), null, tableLink.getJoinType(), tableLink);
            addLink(dimLink);
            addParentDimensions(parentDimension);
        }
    }

    private String getSubSelect(Dimension childDimension, TableLink tableLink) {
        StringBuilder builder = new StringBuilder();
        builder.append(tableLink.getPropertyName()).append('.').append(childDimension.getName());
        if (tableLink.getSelect() != null) {
            builder = new StringBuilder(tableLink.getSelect().replace(":parameter", builder));
        }

        return builder.toString();
    }

    private synchronized String getNewVariableName() {
        return (new StringBuilder().append("arg_").append(nextVarIndex++)).toString();
    }

    private Table addTable(String name, String id, String entityName) {
        //get database column name
        String select = dataManager.getDatabaseColumnName(entityName, id);
        String databaseTableName = dataManager.getDatabaseTableName(entityName);

        Table table = new Table(name, id, entityName);
        table.setDatabaseTableName(databaseTableName);
        Dimension idDimension = addDimension(table, id, id, select);
        table.setIdDimension(idDimension);

        //add dimensions by table
        Set<Dimension> dimSet = new HashSet<Dimension>();
        dimensionsByTable.put(table, dimSet);

        //add to table by name
        tableByName.put(table.getName(), table);

        return table;
    }

    private Dimension addDimension(String tableName, String name, String displayName, String select) {
        Table table = tableByName.get(tableName);
        return addDimension(table, name, displayName, select);
    }

    private Dimension addDimension(Table table, String name, String displayName, String select) {
        Dimension dimension = new Dimension(table, name, displayName, select);
        //add to dimensions by table
        addDimension(dimension, table);
        return dimension;
    }

    private void addDimension(Dimension dimension, Table table) {
        Set<Dimension> dims = dimensionsByTable.get(table);
        if (dims == null) {
            dims = new HashSet<Dimension>();
            dimensionsByTable.put(table, dims);
        }
        dims.add(dimension);
    }

    private void addGroupBy(String tableName, List<String> columns) {
        addGroupBy(tableByName.get(tableName), columns);
    }

    private void addGroupBy(Table table, List<String> columns) {
        table.addGroupBy(columns);
    }

    private TableLink addLinkTable(String parentTableName, String propertyName, String childTableName, String joinType, String column, String selectStatement) {
        Table parentTable = tableByName.get(parentTableName);
        return addLinkTable(parentTable, propertyName, childTableName, joinType, column, selectStatement);
    }

    private TableLink addLinkTable(Table parentTable, String propertyName, String childTableName, String joinType, String column, String selectStatement) {
        //lookup child table
        Table childTable = tableByName.get(childTableName);
        return addLinkTable(parentTable, propertyName, childTable, joinType, column, selectStatement);
    }

    private TableLink addLinkTable(Table parentTable, String propertyName, Table childTable, String joinType, String column, String selectStatement) {
        //lookup identity look dimension
        Dimension parentDimension = null;
        Map<String, Dimension> parentDims = dimensionsByName.get(parentTable);
        if (parentDims == null) {
            parentDims = new HashMap<String, Dimension>();
            dimensionsByName.put(parentTable, parentDims);
        }
        parentDimension = parentDims.get(column);
        if (parentDimension == null) {
            String newColumnSelect = updateColumnToDb(parentTable, column);
            parentDimension = addDimension(parentTable, column, column, newColumnSelect);
        }

        TableLink link = new TableLink(propertyName, parentTable, childTable, joinType, column, selectStatement);
        link.setColumnAlias(this.getNewVariableName());
        link.setParentDimension(parentDimension);

        //add child link to table
        parentTable.addChildLink(propertyName, link);
        //add child to parent link
        addChildToParentTable(link);

        return link;
    }

    private void addChildToParentTable(TableLink linkTable) {
        Set<TableLink> set = childToParentTables.get(linkTable.getChildTable());
        if (set == null) {
            set = new HashSet<TableLink>();
            childToParentTables.put(linkTable.getChildTable(), set);
        }
        set.add(linkTable);
    }
}
