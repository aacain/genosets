/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.dimension;

/**
 *
 * @author aacain
 */
public class Dimension {

    private final Table table;
    private final String name;
    private final String select;
    private String dbColumn;
    private String alias;
    private String displayName;

    public Dimension(Table table, String name, String displayName, String select) {
        this.table = table;
        this.name = name;
        this.displayName = displayName;
        this.select = select;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDbColumn() {
        return dbColumn;
    }

    public void setDbColumn(String dbColumn) {
        this.dbColumn = dbColumn;
    }




    public String getSelect() {
        return select;
    }


    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
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
        final Dimension other = (Dimension) obj;
        if (this.table != other.table && (this.table == null || !this.table.equals(other.table))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.table != null ? this.table.hashCode() : 0);
        hash = 97 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

}
