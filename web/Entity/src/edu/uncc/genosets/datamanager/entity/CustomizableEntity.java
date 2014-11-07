/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author aacain
 */
public abstract class CustomizableEntity implements Serializable{
    private String entityName;
    private Map<String, Object> customProperties;

    public abstract String getDefaultName();
    

    public abstract Integer getId();
   public String getEntityName() {
       if(entityName == null){
           entityName = getDefaultName();
       }
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Map<String, Object> getCustomProperties() {
        if(customProperties == null)
            customProperties = new HashMap();
        return customProperties;
    }

    public void setCustomProperties(Map customProperties) {
        this.customProperties = customProperties;
    }

    public Object getValueOfCustomField(String name){
        return getCustomProperties().get(name);
    }

    public void setValueOfCustomField(String name, Object value){
        getCustomProperties().put(name, value);
    }
}
