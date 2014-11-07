/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.connections.Connection;
import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.connections.NoSuchDatabaseException;
import edu.uncc.genosets.connections.SchemaInvalidException;
import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author aacain
 */
public abstract class DataManager {

    private static volatile GenericDataManager instance;
    public final static String PROP_ORGANISM_ADD = "PROP_ORGANISM_ADD";
    public final static String PROP_DB_CHANGED = "PROP_DB_CHANGED";

    /**
     * Query the database using Hibernate Query Language (HQL).
     *
     * @param query in HQL
     * @return the result list or an empty list
     */
    public abstract List createQuery(String query);

    /**
     * Query the database using Hibernate Query Language (HQL)
     *
     * @param query - the query string
     * @param type the result object type
     * @return the result list or an empty list
     */
    public abstract <T> List<? extends T> createQuery(String query, Class<T> type);

    /**
     * Query the database using Hibernate Query Language (HQL)
     *
     * @param query - the query string
     * @param type the result object type
     * @return the result list or an empty list
     */
    public abstract <T> List<? extends T> createQuery(String query, Class<T> type, int firstResult, int maxResult);

    /**
     * Get the entity with the given id.
     *
     * @param entityName
     * @param id
     * @return the entity
     */
    public abstract Object get(String entityName, Serializable id);

    public abstract Object initializeLazy(CustomizableEntity obj, String property);

    /**
     * Save the entity
     *
     * @param the entity object
     */
    public abstract void save(CustomizableEntity object);

    /**
     * Gets a list of all the organisms in the database, otherwise returns an
     * empty list
     *
     * @return list of organisms
     */
    public abstract List<Organism> getOrganisms();
    //public List<AnnotationMethod> getAnnotationMethods();

    /**
     * Get the database column name by the entity's property name
     *
     * @param entityName
     * @param propertyName
     * @return the database column name
     */
    public abstract String getDatabaseColumnName(String entityName, String propertyName);

    /**
     * Get the database table name for the given entity
     *
     * @param entityName
     * @return the database table name
     */
    public abstract String getDatabaseTableName(String entityName);

    /**
     * Query the database using native sql.
     *
     * Deprecated. Use {@link DataManager#createNativeSQLQuery(java.lang.String)
     * }
     *
     * @param query - the sql query string
     * @return the result list
     *
     */
    @Deprecated
    public abstract List<Object[]> createNativeQuery(String query);

    /**
     * Query the database using native sql
     *
     * @param query - the sql query string
     * @return the result list
     */
    public abstract List createNativeSQLQuery(String query);

    /**
     * Execute native statements.
     *
     * @param list of statements to execute
     */
    public abstract void createNativeStatement(ArrayList<String> statements);

    /**
     * Execute native statements.
     *
     * @param list of statements to execute
     * @param notify listeners of database change
     */
    public abstract void createNativeStatement(ArrayList<String> statements, boolean notify);

    /**
     * Persist to the database.
     *
     * @param list of persisters
     */
    public abstract void persist(List<? extends edu.uncc.genosets.datamanager.persister.Persister> persisters);

    /**
     * Get the name of the current connection's database
     *
     * @return database name
     */
    public abstract String getDatabaseName();

    /**
     * Get the connection's unique id
     *
     * @return unique connection id or null if not connected
     */
    public abstract String getConnectionId();

    /**
     * Test to see if the database is set.
     *
     * @return is the database set?
     */
    public abstract boolean isDatabaseSet();

    public abstract void addPropertyChangeListener(PropertyChangeListener listener);

    public abstract void removePropertyChangeListener(PropertyChangeListener listener);

    public abstract void connect() throws InvalidConnectionException, SchemaInvalidException, NoSuchDatabaseException;

    public static DataManager getDefault() {
        synchronized (DataManager.class) {
            if (instance == null) {
                if (instance == null) {
                    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
                    instance = (GenericDataManager) context.getBean("dataManager");
                    try {
                        instance.connect();
                    } catch (InvalidConnectionException ex) {
                        Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, "DataManager could not connect to datasource.", ex);
                    }
                }
            }
        }
        return instance;
    }

    public static void testConnection(Connection connection) throws InvalidConnectionException {
        try {
            DataManager mgr = null;
            if (connection.getConnectionType().equals(Connection.TYPE_DIRECT_DB)) {
                mgr = new DataManagerJDBC(connection);
            } else if (connection.getConnectionType().equals(Connection.TYPE_WEB_SERVICE)) {
                mgr = new DataManagerWeb(connection);
            }
            mgr.connect();
            mgr.createQuery("SELECT COUNT(o) FROM Organism as o");
        } catch (InvalidConnectionException ex) {
            throw ex;
        } catch (Exception ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.INFO, null, ex);
            throw new InvalidConnectionException("Connection invalid", ex);
        }
    }

    public static void testConnection(String url, String userName, String password, String connectionType) throws InvalidConnectionException {
        Connection tempConn = new Connection("temporary", "temp", url, userName, password, false, connectionType, false);
        testConnection(tempConn);
    }

    /**
     * Tests and opens the connection. Set the default datamanager to a new
     * datamanger with the connection. If the connection is not established, the
     * DataManager and connection prior to this call will remain the same.
     *
     * @param connection - the connection
     * @throws InvalidConnectionException - connection could not be established
     * @throws SchemaInvalidException - the database must be updated to this
     * schema
     */
    public static void openConnection(Connection connection) throws InvalidConnectionException, SchemaInvalidException {
        try {
            DataManager mgr = null;
            if (connection.getConnectionType().equals(Connection.TYPE_DIRECT_DB)) {
                mgr = new DataManagerJDBC(connection);
            } else if (connection.getConnectionType().equals(Connection.TYPE_WEB_SERVICE)) {
                mgr = new DataManagerWeb(connection);
            }
            try {
                mgr.connect();
            } catch (InvalidConnectionException exx) {
                throw exx;
            }
            mgr.createQuery("SELECT COUNT(o) FROM Organism as o");
            synchronized (DataManager.class) {
                ((GenericDataManager) getDefault()).setCurrentDataManager(mgr);
            }
        } catch (Exception ex) {
            Logger.getLogger(DataManager.class.getName()).log(Level.SEVERE, "DataManager could not connect to datasource.", ex);
            throw new InvalidConnectionException("Connection invalid", ex);
        }
    }
}
