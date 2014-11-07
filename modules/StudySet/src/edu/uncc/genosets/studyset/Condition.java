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
public interface Condition<T> extends Serializable{
    public static final String OPER_EQUALS = "OPER_EQUALS";
    public <X extends Property> X getProperty();
    public <X extends Property> void setProperty(X property);
    public T getValue();
    public void setValue(T value);
    public String getOperator();
    public void setOperator(String operator);
    public void setDisplayName(String displayName);
    public String getDisplayName();
}
