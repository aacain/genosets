/*
 * 
 * 
 */

package edu.uncc.genosets.studyset;

import java.io.Serializable;

/**
 *
 * @author aacain
 */
public interface Property extends Serializable{

    public String getAlias();

    public void setAlias(String alias);

    public String getColumn();

    public void setColumn(String column);

    public String getTableName();

    public void setTableName(String tableName);
    
    public Condition lookupCondition(String value);
    
    public void addCondition(Condition condition);
    
}
