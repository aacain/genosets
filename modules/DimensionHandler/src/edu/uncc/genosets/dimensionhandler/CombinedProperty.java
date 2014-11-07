/*
 * 
 * 
 */

package edu.uncc.genosets.dimensionhandler;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author aacain
 */
public class CombinedProperty extends Property{
    public static String OPERATOR_AND = "AND";
    public static String OPERATOR_OR = "OR";
    private String operator;
    private List<PropertyCriteria> propertyList = new LinkedList<PropertyCriteria>();

    public CombinedProperty(String name, String alias, String displayName, String operator, String propertyType) {
        super(name, alias, displayName, propertyType);
        this.operator = operator;
    }

    public void addProperty(Property property, String value){
        propertyList.add(new PropertyCriteria(property, value));
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<PropertyCriteria> getPropertyList() {
        return propertyList;
    }

    public void setPropertyList(List<PropertyCriteria> propertyList) {
        this.propertyList = propertyList;
    }

    public static class PropertyCriteria implements Serializable{
        private Property property;
        private String value;

        public PropertyCriteria(Property property, String value) {
            this.property = property;
            this.value = value;
        }

        public Property getProperty() {
            return property;
        }

        public void setProperty(Property property) {
            this.property = property;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        
    }
}
