/*
 * 
 * 
 */

package edu.uncc.genosets.studyset;

import java.io.Serializable;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class GenoSetsCondition implements Condition, Serializable{
    private Property property;
    private Object value;
    private String operator;
    private Set<Integer> lines;
    private String displayName;

    public GenoSetsCondition() {
        
    }

    public GenoSetsCondition(Property property, Object value, String operator, String displayName) {
        this.property = property;
        this.value = value;
        this.operator = operator;
        this.displayName = displayName;
    }

    public Set<Integer> getLines() {
        return lines;
    }

    public void setLines(Set<Integer> lines) {
        this.lines = lines;
    }

    @Override
    public Property getProperty() {
        return this.property;
    }

    @Override
    public void setProperty(Property property) {
        this.property = property;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String getOperator() {
        return this.operator;
    }

    @Override
    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
