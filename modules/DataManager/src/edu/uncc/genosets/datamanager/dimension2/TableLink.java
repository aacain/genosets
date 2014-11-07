/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.dimension2;

/**
 *
 * @author aacain
 */
public class TableLink {

    private final String propertyName;
    private final Table parentTable;
    private final Table childTable;
    private final Property childLinkProperty;
    private final Property parentLinkProperty;
    private final String selectStatement;
    private final String linkType;

    public TableLink(String propertyName, Table parentTable, Table childTable, Property childLinkProperty, Property parentLinkProperty, String linkType, String selectStatement) {
        this.propertyName = propertyName;
        this.parentTable = parentTable;
        this.childTable = childTable;
        this.childLinkProperty = childLinkProperty;
        this.parentLinkProperty = parentLinkProperty;
        this.linkType = linkType;
        this.selectStatement = selectStatement;
    }

    public Property getChildLinkProperty() {
        return childLinkProperty;
    }

    public Table getChildTable() {
        return childTable;
    }

    public Property getParentLinkProperty() {
        return parentLinkProperty;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getSelectStatement() {
        return selectStatement;
    }

    public String getLinkType() {
        return linkType;
    }

    public Table getParentTable() {
        return parentTable;
    }



}
