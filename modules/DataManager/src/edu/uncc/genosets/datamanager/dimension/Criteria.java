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
public class Criteria {
    public static final String TYPE_LEAF = "TYPE_LEAF";
    public static final String TYPE_AND = "TYPE_AND";
    public static final String TYPE_OR = "TYPE_OR";
    public static final String TYPE_NOT = "TYPE_NOT";
    public static final String OPERATOR_EQUALS = "OPERATOR_EQUALS";
    public static final String OPERATOR_LESSTHAN = "OPERATOR_LESSTHAN";
    public static final String OPERATOR_GREATERTHAN = "OPERATOR_GREATERTHAN";

    private String type;
    private Criteria parent;
    private List<Criteria> children;
    private String property;
    private String value;
    private String operator;

    public List<Criteria> getChildren() {
        return children;
    }

    public void setChildren(List<Criteria> children) {
        this.children = children;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Criteria getParent() {
        return parent;
    }

    public void setParent(Criteria parent) {
        this.parent = parent;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}
