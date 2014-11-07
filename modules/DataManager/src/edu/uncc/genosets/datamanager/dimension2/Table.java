/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.dimension2;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class Table {
    private final String tableName;
    private final String entityName;
    private Property idProperty;
    private final String databaseTableName;
    private final Set<Property> groupBy = new HashSet<Property>();
    private final Map<String, Property> properties = new HashMap<String, Property>();
    private final Map<String, TableLink> childTableLinksByProperty = new HashMap<String, TableLink>();
    private final Map<Table, Map<String/**ParentPropertyName**/, TableLink>> parentLinkTables = new HashMap<Table, Map<String, TableLink>>();

    public Table(String tableName, String entityName, String databaseTableName) {
        this.tableName = tableName;
        this.entityName = entityName;
        this.databaseTableName = databaseTableName;
    }

    public void addGroupBy(Property property){
        groupBy.add(property);
    }

    public Set<Property> getGroupBy(){
        return groupBy;
    }


    public void addParentLinkTable(TableLink tableLinkThisIsChild){
        Map<String, TableLink> parentMap = parentLinkTables.get(tableLinkThisIsChild.getParentTable());
        if(parentMap == null){
            parentMap = new HashMap<String, TableLink>();
            parentLinkTables.put(tableLinkThisIsChild.getChildTable(), parentMap);
        }
        parentMap.put(tableLinkThisIsChild.getPropertyName(), tableLinkThisIsChild);
    }

    public Set<TableLink> getAllParentLinkTables(){
        Set<TableLink> tableLinks = null;
        Collection<Map<String, TableLink>> values = parentLinkTables.values();
        if(values != null){
            tableLinks = new HashSet<TableLink>();
            for (Map<String, TableLink> map : values) {
                Collection<TableLink> values1 = map.values();
                tableLinks.addAll(values1);
            }
        }
        return tableLinks;
    }

    public Set<Property> getAllProperties(){
        Set<Property> props = new LinkedHashSet<Property>();
        props.addAll(properties.values());
        
        return props;
    }
    public TableLink getChildTableLinksByProperty(String propertyName){
        return childTableLinksByProperty.get(propertyName);
    }

    public void addChildTableLinksByProperty(String propertyName, TableLink tableLink){
        this.childTableLinksByProperty.put(propertyName, tableLink);
    }
    
    public void setIdProperty(Property idProperty) {
        this.idProperty = idProperty;
    }

    public void addSimpleProperty(Property property){
        properties.put(property.getPropertyName(), property);
    }

    public Property getProperty(String propertyName){
        return properties.get(propertyName);
    }

    public String getDatabaseTableName() {
        return databaseTableName;
    }

    public String getEntityName() {
        return entityName;
    }

    public Property getIdProperty() {
        return idProperty;
    }

    public Map<String, Property> getSimpleProperties() {
        return properties;
    }

    public String getTableName() {
        return tableName;
    }


}
