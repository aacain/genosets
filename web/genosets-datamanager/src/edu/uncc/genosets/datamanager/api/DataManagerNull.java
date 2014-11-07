/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.connections.Connection;
import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.persister.Persister;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author lucy
 */
public class DataManagerNull extends DataManager {

    @Override
    public List createQuery(String query) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public <T> List<? extends T> createQuery(String query, Class<T> type) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Object get(String entityName, Serializable id) {
        return null;
    }

    @Override
    public void save(CustomizableEntity object) {
    }

    @Override
    public List<Organism> getOrganisms() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public String getDatabaseColumnName(String entityName, String propertyName) {
        return null;
    }

    @Override
    public String getDatabaseTableName(String entityName) {
        return null;
    }

    @Override
    public List<Object[]> createNativeQuery(String query) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void createNativeStatement(ArrayList<String> statements) {
    }
    
    @Override
    public void createNativeStatement(ArrayList<String> statements, boolean notify) {
    }

    @Override
    public void persist(List<? extends Persister> persisters) {
    }

    @Override
    public String getDatabaseName() {
        return "NOT Connected";
    }

    @Override
    public boolean isDatabaseSet() {
        return Boolean.FALSE;
    }

    @Override
    public String getConnectionId() {
        return null;
    }

    @Override
    public void connect() throws InvalidConnectionException {
    }

    @Override
    public List createNativeSQLQuery(String query) {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Object initializeLazy(CustomizableEntity obj, String property) {
        return null;
    }

    @Override
    public <T> List<? extends T> createQuery(String query, Class<T> type, int firstResult, int maxResult) {
        return Collections.EMPTY_LIST;
    }
}
