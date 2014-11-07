/*
 * 
 * 
 */

package edu.uncc.genosets.studyset;

import java.io.Serializable;
import java.util.HashMap;

/**
 *
 * @author aacain
 */
public class GenoSetsProperty implements Property, Serializable{
    private String alias;
    private String column;
    private String tableName;
    private String displayName;
    private String propertyType;
    private HashMap<String, Condition> conditionMap = new HashMap<String, Condition>();
    private static int nextAliasIndex = 0;

    public static final String TYPE_BOOLEAN = "TYPE_BOOLEAN";
    public static final String TYPE_STRING = "TYPE_STRING";

    public GenoSetsProperty(String aliasPrefix, String column, String tableName){
        this.alias = aliasPrefix + "_" + getNextAliasIndex();
        this.column = column;
        this.tableName = tableName;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String getColumn() {
        return column;
    }

    @Override
    public void setColumn(String column) {
       this.column = column;
    }

    @Override
    public String getTableName() {
       return this.tableName;
    }

    @Override
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public static synchronized int getNextAliasIndex() {
        nextAliasIndex = nextAliasIndex++;
        return nextAliasIndex;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GenoSetsProperty other = (GenoSetsProperty) obj;
        if ((this.alias == null) ? (other.alias != null) : !this.alias.equals(other.alias)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.alias != null ? this.alias.hashCode() : 0);
        return hash;
    }

    @Override
    public Condition lookupCondition(String conditionValue) {
        return conditionMap.get(conditionValue);
    }

    @Override
    public void addCondition(Condition condition) {
        conditionMap.put(condition.getValue().toString(), condition);
    }
}
