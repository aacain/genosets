/*
 */
package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import edu.uncc.genosets.datamanager.persister.Persister;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aacain
 */
public interface ServiceManager {

    public List createQuery(String query);

    public <T> List<? extends T> createQuery(String query, Class<T> type);
    
    public <T> List<? extends T> createQuery(String query, Class<T> type, int firstResult, int maxResult);

    public Object get(String entityName, Serializable id);

    public void save(CustomizableEntity object);

    public String getDatabaseColumnName(String entityName, String propertyName);

    public String getDatabaseTableName(String entityName);

    public List createNativeSQLQuery(String query);

    public void createNativeStatement(ArrayList<String> statements);
    
    public void persist(List<? extends Persister> persisters);
    
    public Object initializeLazy(CustomizableEntity obj, String property);
}
