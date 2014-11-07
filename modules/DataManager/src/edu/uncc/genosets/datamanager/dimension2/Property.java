/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.dimension2;

import java.util.List;

/**
 *
 * @author aacain
 */
public class Property {

    private final Table table;
    private final String propertyName;
    private final String alias;
    private final String column;
    private final String dbColumn;
    private final String select;
    private final List<String[]> criteria;
    private String displayName;

    public Property(Table table, String propertyName, String alias, String displayName,
            String column, String dbColumn, String select, List<String[]> criteria) {
        this.table = table;
        this.propertyName = propertyName;
        this.alias = alias;
        this.column = column;
        this.dbColumn = dbColumn;
        this.select = select;
        this.criteria = criteria;
        this.displayName = displayName;
    }

    public Property(Table table, String propertyName, String alias, String displayName,
            String column, String dbColumn) {
        this(table, propertyName, alias, displayName, column, dbColumn, null, null);
    }

    public Property(Table table, String propertyName, String alias, String displayName,
            String select, List<String[]> criteria) {
        this(table, propertyName, alias, displayName, null, null, select, criteria);
    }

    public String getColumn() {
        return column;
    }

    public String getDbColumn(){
        return dbColumn;
    }

    public List<String[]> getCriteria() {
        return criteria;
    }

    public String getSelect() {
        return select;
    }

    public String getAlias() {
        return alias;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Table getTable() {
        return table;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Property other = (Property) obj;
        if (this.table != other.table && (this.table == null || !this.table.equals(other.table))) {
            return false;
        }
        if ((this.propertyName == null) ? (other.propertyName != null) : !this.propertyName.equals(other.propertyName)) {
            return false;
        }
        if ((this.alias == null) ? (other.alias != null) : !this.alias.equals(other.alias)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.table != null ? this.table.hashCode() : 0);
        hash = 89 * hash + (this.propertyName != null ? this.propertyName.hashCode() : 0);
        hash = 89 * hash + (this.alias != null ? this.alias.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Property{" + "table=" + table + "propertyName=" + propertyName + "alias=" + alias + "column=" + column + "dbColumn=" + dbColumn + "select=" + select + "criteria=" + criteria + "displayName=" + displayName + '}';
    }
}
