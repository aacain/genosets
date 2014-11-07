/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.dimension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class Query {

    private final String name;
    private final Table queryRootTable;
    private Set<Dimension> selectDimensions;
    private Set<DimensionLink> dimensionLinks;

    private Map<Table, Query> subquery;

    public Query(String name, Table queryRootTable) {
        this.name = name;
        this.queryRootTable = queryRootTable;
    }

    public void addSelectDimension(Dimension dimension) {
        if(selectDimensions == null){
            selectDimensions = new LinkedHashSet<Dimension>();
        }
        this.selectDimensions.add(dimension);
    }

    public void addDimensionLink(DimensionLink dimLink) {
        if(this.dimensionLinks == null){
            dimensionLinks = new HashSet<DimensionLink>();
        }
        this.dimensionLinks.add(dimLink);
    }

    public String getName() {
        return name;
    }

    public Table getQueryRootTable() {
        return queryRootTable;
    }

    public Set<Dimension> getSelectDimensions() {
        return selectDimensions;
    }

    public Map<Table, Query> getSubquery() {
        return subquery;
    }

    public Set<DimensionLink> getDimensionLinks() {
        return this.dimensionLinks;
    }

    public void addSubquery(Query subQuery, DimensionLink dimLink) {
        if(this.subquery == null){
            this.subquery = new HashMap<Table, Query>();
        }
        this.subquery.put(subQuery.queryRootTable, subQuery);
    }

    public Query getSubQuery(Table table){
        if(subquery == null){
            return null;
        }
        return subquery.get(table);
    }
    
}
