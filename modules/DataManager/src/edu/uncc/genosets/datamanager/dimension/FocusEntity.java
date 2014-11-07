/*
 * Copyright (C) 2013 Aurora Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.uncc.genosets.datamanager.dimension;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author aacain
 */
public class FocusEntity {

    private static HashMap<String, FocusEntity> entityMap;
    private static HashMap<String, Integer> hiermap;
    private String name;
    private String displayName;
    private String entityName;
    private String idProperty;
    private String factTable;
    private String factTableId;
    private String lowestGranularity;
    private Class<? extends CustomizableEntity> entity;

    public FocusEntity(String name, String displayName, String entityName, String idProperty, String factTable, String factTableId, String lowestGranularity, Class<? extends CustomizableEntity> entity) {
        this.name = name;
        this.displayName = displayName;
        this.entityName = entityName;
        this.idProperty = idProperty;
        this.factTable = factTable;
        this.factTableId = factTableId;
        this.lowestGranularity = lowestGranularity;
        this.entity = entity;
    }
    
    
    /**
     * This is the unique name for this focus entity.  This should not be
     * used in a query.
     * @return name of this FocusEntity
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        DataManager.getDefault().getDatabaseColumnName(name, name);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Class<? extends CustomizableEntity> getEntityClass() {
        return entity;
    }

    /**
     * Get the unique entity name for this focus entity. This is the dimension
     * table for this focus entity.
     * @return 
     */
    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getIdProperty() {
        return idProperty;
    }

    public void setIdProperty(String idProperty) {
        this.idProperty = idProperty;
    }

    public String getFactTable() {
        return factTable;
    }

    public void setFactTable(String factTable) {
        this.factTable = factTable;
    }

    public String getFactTableId() {
        return factTableId;
    }

    public void setFactTableId(String factTableId) {
        this.factTableId = factTableId;
    }

    public String getLowestGranularity() {
        return lowestGranularity;
    }
    
    public String getIdProperty(String table){
        if(this.getEntityName().equals(table)){
            return this.getIdProperty();
        }
        return this.getFactTableId();
    }
    
    
    @Override
    public String toString() {
        return this.displayName;
    }

    public static FocusEntity getEntity(String entityName) {
        return getMap().get(entityName);
    }
    
    private static HashMap<String, FocusEntity> getMap(){
        synchronized (FocusEntity.class) {
            if (entityMap == null) {
                initializeMap();
            }
        }
        return entityMap;
    }

    private static void initializeMap() {
        //initalize the hierarchy
        hiermap = new HashMap<String, Integer>();
        hiermap.put("organismId", 100);
        hiermap.put("assembledUnitId", 200);
        hiermap.put("featureId", 300);
        hiermap.put("locationId", 400);

        
        entityMap = new HashMap<String, FocusEntity>();
        FocusEntity entity = new FocusEntity("feature", "Feature", "Feature", "featureId", "AnnoFact", "featureId", "featureId", Feature.class);
        entityMap.put(entity.entityName, entity);
        entity = new FocusEntity("organism", "Organism", "Organism", "organismId", "AnnoFact", "organismId", "organismId", Organism.class);
        entityMap.put(entity.entityName, entity);
        entity = new FocusEntity("location", "Location", "Location", "locationId", "AnnoFact", "locationId", "locationId", Location.class);
        entityMap.put(entity.entityName, entity);
        entity = new FocusEntity("assembledUnit", "Assembled Unit", "AssembledUnit", "assembledUnitId", "AnnoFact", "assembledUnitId", "assembledUnitId", AssembledUnit.class);
        entityMap.put(entity.entityName, entity);
        entity = new FocusEntity("orthologCluster", "Ortholog Cluster", "FeatureCluster", "featureClusterId", "OrthoFact", "featureClusterId", "locationId", FeatureCluster.class);
        entityMap.put(entity.entityName, entity);
    }
    
    public static Collection<FocusEntity> getEntities(){
        return getMap().values();
    }
    
    /**
     * Finds the lowest granularity of all the focusEntities. Compares each
     * focus entity and finds the most common granularity among them.
     * 
     * @param focusEntities
     * @return the FocusEntity with the granularity.
     */
    public static FocusEntity getLowestGranularity(List<FocusEntity> focusEntities){
        FocusEntity lowest = null;
        for (FocusEntity focusEntity : focusEntities) {
            lowest = getLowestGranularity(lowest, focusEntity);
        }
        if(lowest == null){
            return lowest;
        }
        return lowest;
    }
    
    public static FocusEntity getLowestGranularity(FocusEntity e1, FocusEntity e2){
        if(e1 == null){
            return e2;
        }
        if(e2 == null){
            return e1;
        }
        Integer h1 = hiermap.get(e1.getLowestGranularity());
        Integer h2 = hiermap.get(e2.getLowestGranularity());
        if(h1.intValue() <= h2.intValue()){
            return e1;
        }
        return e2;
    }
}
