/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.connections.Connection;
import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.connections.NoSuchDatabaseException;
import edu.uncc.genosets.connections.SchemaInvalidException;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.hibernate.HibernateUtil;
import edu.uncc.genosets.datamanager.persister.OrganismPersister;
import edu.uncc.genosets.datamanager.persister.Persister;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.net.SocketException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Query;
import org.hibernate.StatelessSession;
import org.hibernate.exception.JDBCConnectionException;

/**
 *
 * @author aacain
 */
public class DataManagerJDBC extends DataManager implements PropertyChangeListener {

    private List<? extends Organism> orgList;
    private List<? extends AnnotationMethod> methodList;
    private boolean dbSet = false;
    private final Connection connection;

    public DataManagerJDBC(Connection connection) {
        this.connection = connection;
        OrganismPersister.addPropertyChangeListener(this);
        OrthologTableCreator.listenToDataManager(this);
    }
    
    @Override
    public Object initializeLazy(CustomizableEntity obj, String property){
        return HibernateUtil.get(obj, property);
    }

    @Override
    public List createQuery(final String query) {
        return createQuery(query, 0);
    }

    private List createQuery(final String query, int count) {
        count++;
        try {
            if (dbSet) {
                StatelessSession session = HibernateUtil.currentSession();
                List list = session.createQuery(query).list();
                if (session.getTransaction() == null || !session.getTransaction().isActive()) {
                    HibernateUtil.closeSession();
                }
                if (list == null) {
                    return Collections.EMPTY_LIST;
                }
                return list;
            }
        } catch (Exception ex) {
            if (count < 2 && (ex instanceof SocketException || ex instanceof JDBCConnectionException)) {
                //openConnection(connection);
                return createQuery(query, count);
            } else {
                Logger.getLogger("edu.uncc.getnosets.DataManager.JDBC").log(Level.SEVERE, "Could not execute query: " + query, ex);
            }
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public <T> List<? extends T> createQuery(String query, Class<T> type) {
        return createQuery(query, type, -1, -1, 0);
    }
    
    @Override
    public <T> List<? extends T> createQuery(String query, Class<T> type, int firstResult, int maxResult) {
        return createQuery(query, type, firstResult, maxResult, 0);
    }

    private <T> List<? extends T> createQuery(String query, Class<T> type, int firstResult, int maxResult, int count) {
        count++;
        try {
            if (dbSet) {
                StatelessSession session = HibernateUtil.currentSession();
                Query q = session.createQuery(query);
                if(firstResult > 0){
                    q.setFirstResult(firstResult);
                }
                if(maxResult > 0){
                    q.setMaxResults(maxResult);
                }
                List list = q.list();
                if (session.getTransaction() == null || !session.getTransaction().isActive()) {
                    HibernateUtil.closeSession();
                }
                if (list == null) {
                    return Collections.EMPTY_LIST;
                }
                return list;
            }
        } catch (Exception ex) {
            if (count < 2 && (ex instanceof SocketException || ex instanceof JDBCConnectionException)) {
                //openConnection(connection);
                return createQuery(query, type, firstResult, maxResult, count);
            } else {
                Logger.getLogger("edu.uncc.getnosets.DataManager.JDBC").log(Level.SEVERE, "Could not execute query: " + query, ex);
            }
        }
        return new LinkedList<T>();
    }

    @Override
    public synchronized List<Organism> getOrganisms() {
        if (dbSet) {
            orgList = createQuery("select org from Organism as org order by org.strain", Organism.class);
            return Collections.unmodifiableList(orgList);
        }
        return Collections.EMPTY_LIST;
    }

    //@Override
    public synchronized List<? extends AnnotationMethod> getAnnotationMethods() {
        if (dbSet) {

            methodList = createQuery("select method from AnnotationMethod as method", AnnotationMethod.class);
            return Collections.unmodifiableList(methodList);
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //TODO: change property type
        orgList = getOrganisms();
        methodList = createQuery("select method from AnnotationMethod as method", AnnotationMethod.class);
    }

    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
    }

    @Override
    public String getDatabaseColumnName(String entityName, String propertyName) {
        return HibernateUtil.getColumnName(entityName, propertyName);
    }

    @Override
    public String getDatabaseTableName(String entityName) {
        return HibernateUtil.getTableName(entityName);
    }

    @Override
    public Object get(String entityName, Serializable id) {
        return get(entityName, id, 0);
    }

    private Object get(String entityName, Serializable id, int count) {
        count++;
        Object obj = null;
        try {
            obj = HibernateUtil.currentSession().get(entityName, id);
        } catch (Exception ex) {
            if (count < 2 && (ex instanceof SocketException || ex instanceof JDBCConnectionException)) {
                //openConnection(connection);
                obj = get(entityName, id, count);
            } else {
                Logger.getLogger("edu.uncc.getnosets.DataManager.JDBC").log(Level.SEVERE, "Could not find entity: " + entityName + " " + id.toString(), ex);
            }
        } finally {
            HibernateUtil.closeSession();
        }
        return obj;
    }

//    @Override
//    public synchronized void persistClassic(List<? extends OldPersister> persisters) {
//        HibernateUtil.persistClassic(persisters);
//        propertyChange(new PropertyChangeEvent(this, Organism.DEFAULT_NAME, null, null));
//    }
    @Override
    public List<Object[]> createNativeQuery(String query) {
        return createNativeQuery(query, 0);
    }

    private List<Object[]> createNativeQuery(String query, int count) {
        List<Object[]> list = new LinkedList<Object[]>();
        if (dbSet) {
            count++;
            try {
                StatelessSession session = HibernateUtil.currentSession();
                return session.createSQLQuery(query).list();
            } catch (Exception ex) {
                if (count < 2 && (ex instanceof SocketException || ex instanceof JDBCConnectionException)) {
                    //openConnection(connection);
                    list = createNativeQuery(query, count);
                } else {
                    Logger.getLogger("edu.uncc.getnosets.DataManager.JDBC").log(Level.SEVERE, "Could not execute native query: " + query, ex);
                }
            } finally {
                HibernateUtil.closeSession();
            }
        }
        return list;
    }

    @Override
    public void createNativeStatement(ArrayList<String> statements) {
        createNativeStatement(statements, false);
    }

    public void createNativeStatement(ArrayList<String> statements, int count) {
        count++;
        if (dbSet) {
            try {
                StatelessSession session = HibernateUtil.currentSession();
                session.beginTransaction();
                for (String statement : statements) {
                    HibernateUtil.currentSession().createSQLQuery(statement).executeUpdate();
                }
                session.getTransaction().commit();
            } catch (Exception ex) {
                if (count < 2 && (ex instanceof SocketException || ex instanceof JDBCConnectionException)) {
                    //openConnection(connection);
                    createNativeStatement(statements, count);
                } else {
                    Logger.getLogger("edu.uncc.getnosets.DataManager.JDBC").log(Level.SEVERE, "Could not execute native statements", ex);
                }
            } finally {
                HibernateUtil.closeSession();
            }
        }
    }

    @Override
    public void persist(List<? extends Persister> persisters) {
        persist(persisters, 0);
    }

    private void persist(List<? extends Persister> persisters, int count) {
        count++;
        if (dbSet) {
            try {
                HibernateUtil.persist(persisters);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            getOrganisms();
            OrthologTableCreator.dropTables();
            this.propertyChange(new PropertyChangeEvent(this, PROP_ORGANISM_ADD, null, orgList));
        }
    }

    @Override
    public synchronized String getDatabaseName() {
        return connection.getConnectionName();
    }

    @Override
    public String getConnectionId() {
        return connection.getConnectionId();
    }

    @Override
    public synchronized void save(CustomizableEntity object) {
        HibernateUtil.save(object, 0);
        if (object instanceof Organism) {
            this.propertyChange(new PropertyChangeEvent(this, PROP_ORGANISM_ADD, null, orgList));
        }
    }

    @Override
    public boolean isDatabaseSet() {
        return dbSet;
    }

    @Override
    public void connect() throws InvalidConnectionException, SchemaInvalidException, NoSuchDatabaseException {
        boolean connected = false;
        try {
            boolean checkDatabaseCurrent = HibernateUtil.checkDatabaseCurrent(connection.getUrl(), connection.getUserName(), connection.getPassword());
            connected = HibernateUtil.openConnection(connection.getUrl(), connection.getUserName(), connection.getPassword());
        } catch (InvalidConnectionException ex) {
            throw ex;
        } finally {
            if (connected) {
                Logger.getLogger("edu.uncc.getnosets.DataManager.JDBC").log(Level.INFO, "Sucessfully connected to database {0}", connection.getUrl());
                dbSet = true;
                getOrganisms();
            } else {
                Logger.getLogger("edu.uncc.getnosets.DataManager.JDBC").log(Level.INFO, "Could not connect to {0}", connection.getUrl());
            }
        }
    }

    @Override
    public List createNativeSQLQuery(String query) {
        return createNativeQuery(query, 0);
    }

    private List createNativeSQLQuery(String query, int count) {
        List list = new LinkedList();
        if (dbSet) {
            count++;
            try {
                StatelessSession session = HibernateUtil.currentSession();
                return session.createSQLQuery(query).list();
            } catch (Exception ex) {
                if (count < 2 && (ex instanceof SocketException || ex instanceof JDBCConnectionException)) {
                    //openConnection(connection);
                    list = createNativeQuery(query, count);
                }
            } finally {
                HibernateUtil.closeSession();
            }
        }
        return list;
    }

    public static void updateSchema(String url, String username, String password) throws DatabaseMigrationException, InvalidConnectionException {
        HibernateUtil.migrateDatabase(url, username, password);
    }

    public static void createDatabase(String url, String username, String password) throws DatabaseMigrationException, InvalidConnectionException {
        HibernateUtil.createDb(url, username, password);
    }

    @Override
    public void createNativeStatement(ArrayList<String> statements, boolean notify) {
        createNativeStatement(statements, 0);
        
    }
}
