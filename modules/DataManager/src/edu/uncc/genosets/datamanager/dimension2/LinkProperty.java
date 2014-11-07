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
public class LinkProperty extends Property{

    private final Property childProperty;
    private final TableLink tableLink;

    public LinkProperty(Table table, Property childProperty,
            String alias, String displayName, TableLink tableLink,
            String selectStatement, List<String[]> criteria) {
        super(table, alias, alias, displayName, selectStatement, criteria);
        this.childProperty = childProperty;
        this.tableLink = tableLink;
    }

    public Property getChildProperty() {
        return childProperty;
    }

    public TableLink getTableLink() {
        return tableLink;
    }

}
