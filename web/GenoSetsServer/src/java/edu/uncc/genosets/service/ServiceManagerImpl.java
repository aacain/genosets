/*
 * ServiceManager implementation
 */
package edu.uncc.genosets.service;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.GenericDataManager;
import edu.uncc.genosets.datamanager.api.ServiceManager;
import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import edu.uncc.genosets.datamanager.hibernate.HibernateUtil_spring;
import edu.uncc.genosets.datamanager.persister.Persister;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author aacain  
 */
public class ServiceManagerImpl implements ServiceManager{
    @Autowired
    private LocalDataManager localDataManager;

    @Override
    public List createQuery(String query) {
        return DataManager.getDefault().createQuery(query);
    }

    @Override
    public <T> List<? extends T> createQuery(String query, Class<T> type) {
        return DataManager.getDefault().createQuery(query, type);
    }

    @Override
    public Object get(String entityName, Serializable id) {
        return DataManager.getDefault().get(entityName, id);
    }

    @Override
    public void save(CustomizableEntity object) {
        DataManager.getDefault().save(object);
    }

    @Override
    public String getDatabaseColumnName(String entityName, String propertyName) {
        return DataManager.getDefault().getDatabaseColumnName(entityName, propertyName);
    }

    @Override
    public String getDatabaseTableName(String entityName) {
        return DataManager.getDefault().getDatabaseTableName(entityName);
    }

    @Override
    public List createNativeSQLQuery(String query) {
        return DataManager.getDefault().createNativeSQLQuery(query);
    }

    @Override
    public void createNativeStatement(ArrayList<String> statements) {
        DataManager.getDefault().createNativeStatement(statements);
    }
    
    public void initDataManager(){
        DataManager dm = DataManager.getDefault();
        if(dm instanceof GenericDataManager){
            ((GenericDataManager)dm).setCurrentDataManager(this.localDataManager);
        }
    }

    @Override
    public void persist(List<? extends Persister> persisters) {
        DataManager.getDefault().persist(persisters);
    }

    @Override
    public Object initializeLazy(CustomizableEntity obj, String property) {
        return DataManager.getDefault().initializeLazy(obj, property);
    }
}
