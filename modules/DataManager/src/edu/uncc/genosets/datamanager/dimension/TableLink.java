/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.dimension;

/**
 *
 * @author aacain
 */
public class TableLink {
    private final String propertyName;
    private final Table parentTable;
    private final Table childTable;
    private Dimension parentDimension;
    private final String joinType;
    private final String column;
    private final String select;
    private String columnAlias;

    public TableLink(String propertyName, Table parentTable, Table childTable, String joinType, String column, String select) {
        this.propertyName = propertyName;
        this.parentTable = parentTable;
        this.childTable = childTable;
        this.joinType = joinType;
        this.column = column;
        this.select = select;
    }

    public TableLink(String propertyName, Table parentTable, Table childTable, String joinType, String column) {
        this.propertyName = propertyName;
        this.parentTable = parentTable;
        this.childTable = childTable;
        this.joinType = joinType;
        this.column = column;
        this.select = null;
    }

    public Dimension getParentDimension() {
        return parentDimension;
    }

    public void setParentDimension(Dimension parentDimension) {
        this.parentDimension = parentDimension;
    }



    public String getColumnAlias() {
        return columnAlias;
    }

    public void setColumnAlias(String columnAlias) {
        this.columnAlias = columnAlias;
    }



    public Table getChildTable() {
        return childTable;
    }

    public String getColumn() {
        return column;
    }

    public String getJoinType() {
        return joinType;
    }

    public Table getParentTable() {
        return parentTable;
    }

    public String getSelect() {
        return select;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TableLink other = (TableLink) obj;
        if ((this.propertyName == null) ? (other.propertyName != null) : !this.propertyName.equals(other.propertyName)) {
            return false;
        }
        if (this.parentTable != other.parentTable && (this.parentTable == null || !this.parentTable.equals(other.parentTable))) {
            return false;
        }
        if (this.childTable != other.childTable && (this.childTable == null || !this.childTable.equals(other.childTable))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.propertyName != null ? this.propertyName.hashCode() : 0);
        hash = 29 * hash + (this.parentTable != null ? this.parentTable.hashCode() : 0);
        hash = 29 * hash + (this.childTable != null ? this.childTable.hashCode() : 0);
        return hash;
    }



}
