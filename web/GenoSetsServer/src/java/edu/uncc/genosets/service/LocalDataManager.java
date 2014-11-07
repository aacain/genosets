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

package edu.uncc.genosets.service;

import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.hibernate.HibernateUtil_spring;
import edu.uncc.genosets.datamanager.persister.Persister;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aacain
 */
public class LocalDataManager extends DataManager{
    private HibernateUtil_spring hibUtil;

    @Override
    public List createQuery(String query) {
        return hibUtil.createQuery(query);
    }

    @Override
    public <T> List<? extends T> createQuery(String query, Class<T> type) {
        return hibUtil.createQuery(query);
    }

    @Override
    public Object get(String entityName, Serializable id) {
        return hibUtil.get(entityName, id);
    }

    @Override
    public void save(CustomizableEntity object) {
        hibUtil.save(object);
    }

    @Override
    public List<Organism> getOrganisms() {
        return hibUtil.createQuery("select org from Organism as org order by org.strain");
    }

    @Override
    public String getDatabaseColumnName(String entityName, String propertyName) {
        return hibUtil.getColumnName(entityName, propertyName);
    }

    @Override
    public String getDatabaseTableName(String entityName) {
        return hibUtil.getTableName(entityName);
    }

    @Override
    public List<Object[]> createNativeQuery(String query) {
        return hibUtil.createNativeQuery(query);
    }

    @Override
    public void createNativeStatement(ArrayList<String> statements) {
        hibUtil.createNativeStatement(statements);
    }

    @Override
    public void persist(List<? extends Persister> persisters) {
        try {
            hibUtil.persist(persisters);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getDatabaseName() {
        return "local_db";
    }

    @Override
    public String getConnectionId() {
        return "local_db";
    }

    @Override
    public boolean isDatabaseSet() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        
    }

    @Override
    public void connect() throws InvalidConnectionException {
        
    }
    
    public void setHibUtil(HibernateUtil_spring hibUtil){
        this.hibUtil = hibUtil;
    }
    
    @Override
    public List<Object[]> createNativeSQLQuery(String query) {
        return hibUtil.createNativeQuery(query);
    }

    @Override
    public Object initializeLazy(CustomizableEntity obj, String property) {
        return hibUtil.get(obj, property);
    }
}
