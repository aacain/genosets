/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.dimension;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aacain
 */
public class Table {
    private final String name;
    private final String idColumn;
    private Dimension idDimension;
    private final String entityName;
    private String databaseTableName;
    private Map<String /*propertyName*/, TableLink> childLinkTables;

    private List<String> groupBy;

    public Table(String name, String id, String entityName) {
        this.name = name;
        this.idColumn = id;
        this.entityName = entityName;
    }

    public Dimension getIdDimension() {
        return idDimension;
    }

    public void setIdDimension(Dimension idDimension) {
        this.idDimension = idDimension;
    }


    public void addGroupBy(String columnName){
        if(groupBy == null){
            groupBy = new LinkedList<String>();
        }
        groupBy.add(columnName);
    }

    public void addGroupBy(List<String> columnNames){
        if(groupBy == null){
            groupBy = new LinkedList<String>();
        }
        groupBy.addAll(columnNames);
    }

    public String getEntityName() {
        return entityName;
    }

    public List<String> getGroupBy() {
        return groupBy;
    }

    public String getIdColumn() {
        return idColumn;
    }

    public String getName() {
        return name;
    }

    public void addChildLink(String propertyName, TableLink tableLink){
        if(childLinkTables == null){
            childLinkTables = new HashMap<String/* propertyName */, TableLink>();
        }
        childLinkTables.put(propertyName, tableLink);
    }

    public TableLink getChildLinkByPropertyName(String propertyName){
        if(childLinkTables == null){
            return null;
        }else{
            return childLinkTables.get(propertyName);
        }
    }

    public String getDatabaseTableName() {
        return databaseTableName;
    }

    public void setDatabaseTableName(String databaseTableName) {
        this.databaseTableName = databaseTableName;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Table other = (Table) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
