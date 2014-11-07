/*
 * 
 * 
 */

package edu.uncc.genosets.dimensionhandler;

import java.io.Serializable;

/**
 *
 * @author aacain
 */
public class Property implements Serializable{

    private String name;
    private String displayName;
    private String alias;
    private String propertyType;

    public Property(String name, String alias, String displayName, String propertyType) {
        this.name = name;
        this.alias = alias;
        this.displayName = displayName;
        this.propertyType = propertyType;
    }

    public String getAlias() {
        return alias;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getName() {
        return name;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    

}
