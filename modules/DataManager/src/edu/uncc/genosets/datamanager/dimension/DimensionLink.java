/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.dimension;

/**
 *
 * @author aacain
 */
public class DimensionLink {

    private final Dimension parentDimension;
    private final Dimension childDimension;
    private final String selectString;
    private final String joinType;
    private final TableLink linkTable;

    public DimensionLink(Dimension parentDimension, Dimension childDimension,
            String selectString, String column, String joinType, TableLink linkTable) {
        this.parentDimension = parentDimension;
        this.childDimension = childDimension;
        this.selectString = selectString;
        this.joinType = joinType;
        this.linkTable = linkTable;
    }

    public TableLink getLinkTable() {
        return linkTable;
    }

    public Dimension getChildDimension() {
        return childDimension;
    }

    public String getJoinType() {
        return joinType;
    }

    public Dimension getParentDimension() {
        return parentDimension;
    }

    public String getSelectString() {
        return selectString;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DimensionLink other = (DimensionLink) obj;
        if (this.parentDimension != other.parentDimension && (this.parentDimension == null || !this.parentDimension.equals(other.parentDimension))) {
            return false;
        }
        if (this.childDimension != other.childDimension && (this.childDimension == null || !this.childDimension.equals(other.childDimension))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.parentDimension != null ? this.parentDimension.hashCode() : 0);
        hash = 13 * hash + (this.childDimension != null ? this.childDimension.hashCode() : 0);
        return hash;
    }
}
