package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.persister.Persister;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author aacain
 */
public class GenericDataManager extends DataManager {

    private DataManager currentDataManager;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public GenericDataManager() {
        currentDataManager = new DataManagerNull();
    }

    @Override
    public List createQuery(String query) {
        return currentDataManager.createQuery(query);
    }

    @Override
    public <T> List<? extends T> createQuery(String query, Class<T> type) {
        return currentDataManager.createQuery(query);
    }

    @Override
    public Object get(String entityName, Serializable id) {
        return currentDataManager.get(entityName, id);
    }

    @Override
    public void save(CustomizableEntity object) {
        currentDataManager.save(object);
        if (object instanceof Organism) {
            this.propertyChange(new PropertyChangeEvent(this, PROP_ORGANISM_ADD, null, getOrganisms()));
        }
    }

    @Override
    public List<Organism> getOrganisms() {
        return currentDataManager.getOrganisms();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        this.pcs.firePropertyChange(evt);
    }

    @Override
    public String getDatabaseColumnName(String entityName, String propertyName) {
        return currentDataManager.getDatabaseColumnName(entityName, propertyName);
    }

    @Override
    public String getDatabaseTableName(String entityName) {
        return currentDataManager.getDatabaseTableName(entityName);
    }

    @Override
    public List<Object[]> createNativeQuery(String query) {
        return currentDataManager.createNativeQuery(query);
    }

    @Override
    public void createNativeStatement(ArrayList<String> statements) {
        currentDataManager.createNativeStatement(statements);
    }

    @Override
    public void createNativeStatement(ArrayList<String> statements, boolean notify) {
        createNativeStatement(statements);
        this.pcs.firePropertyChange(PROP_ORGANISM_ADD, null, getOrganisms());
    }

    @Override
    public void persist(List<? extends Persister> persisters) {
        currentDataManager.persist(persisters);
        this.pcs.firePropertyChange(DataManager.PROP_ORGANISM_ADD, null, getOrganisms());
    }

    @Override
    public String getDatabaseName() {
        return currentDataManager.getDatabaseName();
    }

    @Override
    public String getConnectionId() {
        return this.currentDataManager.getConnectionId();
    }

    @Override
    public boolean isDatabaseSet() {
        return currentDataManager.isDatabaseSet();
    }

    public DataManager setCurrentDataManager(DataManager dataManager) {
        DataManager old = this.currentDataManager;
        this.currentDataManager = dataManager;
        this.pcs.firePropertyChange(PROP_DB_CHANGED, old.getConnectionId(), dataManager.getConnectionId());
        return this.currentDataManager;
    }

    @Override
    public void connect() throws InvalidConnectionException {
        this.currentDataManager.connect();
    }

    @Override
    public List createNativeSQLQuery(String query) {
        return currentDataManager.createNativeSQLQuery(query);
    }

    @Override
    public Object initializeLazy(CustomizableEntity obj, String property) {
        return currentDataManager.initializeLazy(obj, property);
    }

    @Override
    public <T> List<? extends T> createQuery(String query, Class<T> type, int firstResult, int maxResult) {
        return currentDataManager.createQuery(query, type, firstResult, maxResult);
    }
}
