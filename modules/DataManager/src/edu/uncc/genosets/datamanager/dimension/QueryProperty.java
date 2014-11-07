/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.dimension;

import java.util.List;

/**
 *
 * @author aacain
 */
public class QueryProperty {

    private Table table;
    private String displayName;
    private String selectStatement;
    private String property;
    private List<Parameter> parameters;
    //private Map<Table, List<Criteria>> criteriaMap;

    public QueryProperty(String property, Table table, String displayName, String selectStatement, List<Parameter> parameters) {
        this.table = table;
        this.displayName = displayName;
        this.selectStatement = selectStatement;
        this.property = property;
        this.parameters = parameters;
    }

//    public void addCriteria(String name, Table table, String column){
//        if(criteriaMap == null){
//            criteriaMap = new HashMap<Table, List<Criteria>>();
//        }
//        List<Criteria> crits = criteriaMap.get(table);
//        if(crits == null){
//            crits = new LinkedList<Criteria>();
//            criteriaMap.put(table, crits);
//        }
//        Criteria c = new Criteria();
//        c.name = name;
//        c.table = table;
//        c.column = column;
//        crits.add(null);
//    }

    public String getProperty() {
        return property;
    }



    public String getDisplayName() {
        return displayName;
    }

    public String getSelectStatement() {
        return selectStatement;
    }

    public Table getTable() {
        return table;
    }

    protected static class Criteria{
        String name;
        Table table;
        String column;
    }

    protected static class Parameter{
        String stringIdentifier;
        String column;

        public Parameter(String stringIdentifier, String column) {
            this.stringIdentifier = stringIdentifier;
            this.column = column;
        }
    }
}
