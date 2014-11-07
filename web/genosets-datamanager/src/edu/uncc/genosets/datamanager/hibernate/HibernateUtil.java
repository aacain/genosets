/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.hibernate;

import edu.uncc.genosets.connections.InvalidConnectionException;
import com.mchange.v2.c3p0.DataSources;
import edu.uncc.genosets.connections.NoSuchDatabaseException;
import edu.uncc.genosets.connections.SchemaInvalidException;
import edu.uncc.genosets.datamanager.api.DataLoadException;
import edu.uncc.genosets.datamanager.api.DatabaseMigrationException;
import edu.uncc.genosets.datamanager.api.DeleteException;
import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import edu.uncc.genosets.datamanager.persister.Persister;
import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author aacain
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static Configuration ac;
    public static final ThreadLocal<StatelessSession> sessionThread = new ThreadLocal<StatelessSession>();
    private final static int MAX_ALLOWED_PACKET_MIN = 16777216;

    public static synchronized StatelessSession currentSession() {
        StatelessSession s = (StatelessSession) sessionThread.get();
        if (s == null) {
            s = sessionFactory.openStatelessSession();
            sessionThread.set(s);
        }
        return s;
    }

    public static synchronized Object get(CustomizableEntity obj, String property) {
        Session session = sessionFactory.openSession();
        //Object merge = session.merge(obj.getEntityName(), obj);
        Object merge = obj;
        if (obj.getId() != null) {
            merge = session.load(obj.getEntityName(), obj.getId());
        }
        try {
            BeanInfo info = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = info.getPropertyDescriptors();
            boolean found = false;
            for (PropertyDescriptor desc : propertyDescriptors) {
                if (desc.getName().equals(property)) {
                    Method readMethod = desc.getReadMethod();
                    Hibernate.initialize(readMethod.invoke(merge, (Object[]) null));
                    found = true;
                    break;
                }
            }
            if (found = false) {
                Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, new IntrospectionException("Could not find method."));
            }
        } catch (IntrospectionException ex) {
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            session.close();
        }
        obj = (CustomizableEntity) merge;
        return merge;
    }

    public static synchronized void closeSession() {
        StatelessSession s = sessionThread.get();
        if (s != null) {
            s.close();
        }
        sessionThread.set(null);
    }

    public static String getColumnName(String entityName, String propertyName) {
        PersistentClass classMapping = ac.getClassMapping(entityName);
        Property property = classMapping.getProperty(propertyName);
        for (Iterator<Column> it = property.getColumnIterator(); it.hasNext();) {
            Column c = it.next();
            return c.getName();
        }

        return null;
    }

    public static String getTableName(String entityName) {
        PersistentClass classMapping = ac.getClassMapping(entityName);
        if (classMapping != null) {
            return classMapping.getTable().getName();
        }
        return null;
    }

//    public static boolean persistClassic(List<? extends OldPersister> persisters) {
//        StatelessSession session = currentSession();
//        session.beginTransaction();
//        boolean inserted = false;
//        try {
//            for (OldPersister persister : persisters) {
//                inserted = persister.persist(session, !inserted);
//            }
//        } catch (Exception e) {
//            session.getTransaction().rollback();
//            closeSession();
//            Exceptions.printStackTrace(e);
//            return false;
//        }
//        session.getTransaction().commit();
//        closeSession();
//        return inserted;
//    }
    public static boolean persist(List<? extends Persister> persisters) throws Exception {
        return persist(persisters, 0);
    }

    private static boolean persist(List<? extends Persister> persisters, int tryNumber) throws Exception {
        tryNumber++;
        StatelessSession session = currentSession();
        session.beginTransaction();
        boolean inserted = false;
        if (persisters != null) {
            try {
                for (Persister persister : persisters) {
                    persister.persist(session);
                }
                session.getTransaction().commit();
                inserted = true;
            } catch (Exception e) {
                LogFactory.getLog(HibernateUtil.class).warn("Error persisting data.", e);
                if (tryNumber < 0) {
                    System.out.println("Restarted in Hibernate Util " + tryNumber);
                    session.getTransaction().rollback();
                    inserted = persist(persisters, tryNumber);
                } else {
                    session.getTransaction().rollback();
                    throw e;
                }
            } finally {
                closeSession();
            }
        }
        return inserted;
    }

    public static void delete(Collection<? extends CustomizableEntity> entities) throws DeleteException {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            for (CustomizableEntity entity : entities) {
                session.delete(entity);
            }
        } catch (Exception ex) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            throw new DeleteException(ex);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public static boolean save(CustomizableEntity obj, int num) throws DataLoadException {
        num++;
        boolean retVal = false;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.saveOrUpdate(obj.getEntityName(), obj);
            session.getTransaction().commit();
            retVal = true;
        } catch (Exception ex) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            throw new DataLoadException("Could not save entity " + obj.getEntityName(), ex);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return retVal;
    }

    public static void delete(Object obj) {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.delete(obj);
            session.getTransaction().commit();
        } catch (Exception ex) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            throw new DataLoadException("Could not delete objects ", ex);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public static Set<String> getDatabases(String host, String port, String username, String password) {
        java.sql.Connection sqlConn = null;
        Statement stmt = null;
        Set<String> dbs = null;
        try {
            final String driver = "com.mysql.jdbc.Driver";
            final String url = "jdbc:mysql://" + host + "/";
            Class.forName(driver);
            sqlConn = DriverManager.getConnection(url, username, password);
            stmt = sqlConn.createStatement();
            final String databaseString = "SHOW DATABASES";
            ResultSet rs = stmt.executeQuery(databaseString);
            dbs = new HashSet<String>();
            while (rs.next()) {
                dbs.add(rs.getString("Database"));
            }
        } catch (SQLException se) {
        } catch (ClassNotFoundException ex1) {
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
            }
            try {
                if (sqlConn != null) {
                    sqlConn.close();
                }
            } catch (SQLException se) {
            }
            return dbs;
        }
    }

    public static synchronized boolean openConnection(String url, String username, String password) throws InvalidConnectionException, SchemaInvalidException {
        //close session
        closeSession();

        final String fullUrl = "jdbc:mysql://" + url;
        //see if already connected
        if (sessionFactory != null && ac != null) {
            String oldUrl = ac.getProperty("hibernate.connection.url");
            String oldUsername = ac.getProperty("hibernate.connection.username");
            String oldPassword = ac.getProperty("hibernate.connection.password");
            if (oldUrl.equals(url) && oldUsername.equals(username) && oldPassword.equals(password)) {
                return true;
            }
        }

        //not connected, so we will create connection
        ac = new Configuration();
        ac.configure("edu/uncc/genosets/datamanager/entity/hibernate.cfg.xml");
        ac.setProperty("hibernate.connection.url", fullUrl);
        ac.setProperty("hibernate.connection.username", username);
        if (password != null) {
            ac.setProperty("hibernate.connection.password", password);
        }
        ac.setProperty("hibernate.c3p0.idle_test_period", "300");
        ac.setProperty("hibernate.c3p0.testConnectionOnCheckin", "true");

        //test the connection
        String driverClass = ac.getProperty("hibernate.connection.driver_class");
        Connection sqlConnection = null;
        try {
            Class.forName(driverClass);

            try {
                DataSource ds = DataSources.unpooledDataSource(fullUrl, username, password == null ? "" : password);
                sqlConnection = ds.getConnection();
                testMaxAllowedPacketSize(sqlConnection);
            } catch (Exception ex) {
                throw new InvalidConnectionException("Invalid connection", ex);
            } finally {
                if (sqlConnection != null) {
                    try {
                        sqlConnection.close();
                    } catch (SQLException ex) {
                    }
                }
            }

        } catch (ClassNotFoundException ex) {
            throw new InvalidConnectionException("Invalid connection", ex);
        }

        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(ac.getProperties()).buildServiceRegistry();
        //close the previous session factory
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        sessionFactory = ac.buildSessionFactory(serviceRegistry);
        return true;
    }

    public static void createDb(String url, String username, String password) throws InvalidConnectionException, DatabaseMigrationException {
        int colonIndex = url.indexOf(":");
        int fSlashIndex = url.lastIndexOf("/");
        String host = url.substring(0, colonIndex);
        String port = url.substring(colonIndex + 1, fSlashIndex);
        String dbName = url.substring(fSlashIndex + 1, url.length());

        createDb(host, port, dbName, username, password);
    }

    public static void createDb(String host, String port, String dbName, String username, String password) throws InvalidConnectionException, DatabaseMigrationException {
        LogFactory.getLog(HibernateUtil.class).info("Creating database " + dbName + " on " + host + ".");
        //Create the connection
        String url = "jdbc:mysql://" + host + ":" + port + "/";
        String fullUrl = url + dbName;

        //create the database
        String driverClass = "com.mysql.jdbc.Driver";
        Connection sqlConnection = null;
        try {
            Class.forName(driverClass);
            try {
                DataSource ds = DataSources.unpooledDataSource(url, username, password == null ? "" : password);
                sqlConnection = ds.getConnection();
                PreparedStatement statement = sqlConnection.prepareStatement("CREATE DATABASE " + dbName);
                try {
                    statement.execute();
                } catch (SQLException ex) {
                    throw new InvalidConnectionException("Could not create database " + dbName, ex);
                } finally {
                    statement.close();
                }
                testMaxAllowedPacketSize(sqlConnection);

                ac = new Configuration();
                ac.configure("edu/uncc/genosets/datamanager/entity/hibernate.cfg.xml");
                ac.setProperty("hibernate.connection.url", fullUrl);
                ac.setProperty("hibernate.connection.username", username);
                if (password != null) {
                    ac.setProperty("hibernate.connection.password", password);
                }
                ac.setProperty("hibernate.c3p0.idle_test_period", "300");
                ac.setProperty("hibernate.c3p0.testConnectionOnCheckin", "true");
            } catch (SQLException ex) {
                throw new InvalidConnectionException("Invalid connection", ex);
            } finally {
                if (sqlConnection != null) {
                    try {
                        sqlConnection.close();
                    } catch (SQLException ex) {
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            throw new InvalidConnectionException("Invalid connection", ex);
        }
        TaskLogFactory.getDefault().log("Created database: " + dbName, "HibernateUtil", "Sucessfully created database " + dbName + " on host " + host, TaskLog.INFO, new java.util.Date());
        migrateDatabase(host, port, dbName, username, password);
        TaskLogFactory.getDefault().log("Created necessary tables for: " + dbName, "HibernateUtil", "Sucessfully created tables for " + dbName + " on host " + host, TaskLog.INFO, new java.util.Date());
//        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(ac.getProperties()).buildServiceRegistry();
//        SessionFactory buildSessionFactory = ac.buildSessionFactory(serviceRegistry);
    }

    public static boolean checkDatabaseCurrent(String url, String username, String password) throws InvalidConnectionException, SchemaInvalidException, NoSuchDatabaseException {
        int colonIndex = url.indexOf(":");
        int fSlashIndex = url.lastIndexOf("/");
        String host = url.substring(0, colonIndex);
        String port = url.substring(colonIndex + 1, fSlashIndex);
        String dbName = url.substring(fSlashIndex + 1, url.length());

        return checkDatabaseCurrent(host, port, dbName, username, password);
    }

    public static boolean checkDatabaseCurrent(String host, String port, String dbName, String username, String password) throws InvalidConnectionException, SchemaInvalidException, NoSuchDatabaseException {
        String generalUrl = "jdbc:mysql://" + host + ":" + port;
        String fullUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
        //set the driver class
        String driverClass = "com.mysql.jdbc.Driver";
        //Create the connection
        Connection sqlConnection = null;
        try {
            Class.forName(driverClass);
            //see if database exists
            try {
                DataSource ds = DataSources.unpooledDataSource(generalUrl, username, password == null ? "" : password);
                sqlConnection = ds.getConnection();
                //test to see if liquibase tables exist
                Statement stmt = sqlConnection.createStatement();
                ResultSet databases = stmt.executeQuery("SHOW DATABASES LIKE '" + dbName + "';");
                if (!databases.next()) {
                    throw new NoSuchDatabaseException("Database does not exist: " + dbName);
                }
            } catch (SQLException ex) {
                throw new InvalidConnectionException("Could not connect.");
            } finally {
                if (sqlConnection != null) {
                    try {
                        sqlConnection.close();
                    } catch (SQLException ex) {
                    }
                }
            }
            try {
                DataSource ds = DataSources.unpooledDataSource(fullUrl, username, password == null ? "" : password);
                sqlConnection = ds.getConnection();
                Liquibase liquibase = null;
                try {
                    Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(sqlConnection));
                    liquibase = new Liquibase("edu/uncc/genosets/datamanager/resources/db.changelog-master.xml", new ClassLoaderResourceAccessor(), database);
                    List<ChangeSet> listUnrunChangeSets = liquibase.listUnrunChangeSets("");
                    if (listUnrunChangeSets.size() > 0) {
                        throw new SchemaInvalidException("Schema is invalid. Please update.");
                    }
                } catch (DatabaseException ex) {
                    Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LiquibaseException ex) {
                    Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (SQLException ex) {
                throw new InvalidConnectionException("Could not connect to database.", ex);
            } finally {
                if (sqlConnection != null) {
                    try {
                        sqlConnection.close();
                    } catch (SQLException ex) {
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            throw new InvalidConnectionException("No suitable driver found for connection. Could not connect to database.", ex);
        }

        return true;
    }

    public static void migrateDatabase(String url, String username, String password) throws DatabaseMigrationException, InvalidConnectionException {
        LogFactory.getLog(HibernateUtil.class).info("Checking if database schema is up to date." + url);
        int colonIndex = url.indexOf(":");
        int fSlashIndex = url.lastIndexOf("/");
        String host = url.substring(0, colonIndex);
        String port = url.substring(colonIndex + 1, fSlashIndex);
        String dbName = url.substring(fSlashIndex + 1, url.length());

        migrateDatabase(host, port, dbName, username, password);
    }

    public static void migrateDatabase(String host, String port, String dbName, String username, String password) throws DatabaseMigrationException, InvalidConnectionException {
        LogFactory.getLog(HibernateUtil.class).info("Migrating database " + dbName);
        String fullUrl = "jdbc:mysql://" + host + ":" + port + "/" + dbName;
        //set the driver class
        String driverClass = "com.mysql.jdbc.Driver";
        //Create the connection
        Connection sqlConnection = null;
        try {
            Class.forName(driverClass);
            try {
                DataSource ds = DataSources.unpooledDataSource(fullUrl, username, password == null ? "" : password);
                sqlConnection = ds.getConnection();
                //test to see if liquibase tables exist
                Statement stmt = sqlConnection.createStatement();
                ResultSet result = stmt.executeQuery("SHOW TABLES LIKE 'DATABASECHANGELOG'");
                boolean isLiquibase = false;
                if (result.next()) {
                    isLiquibase = true;
                }
                result = stmt.executeQuery("SHOW TABLES LIKE 'databasechangelog'");
                if (result.next()) {
                    isLiquibase = true;
                    try {
                        stmt.execute("RENAME TABLE databasechangelog TO DATABASECHANGELOG");
                    } catch (SQLException ex) {
                    }
                }
                stmt = sqlConnection.createStatement();
                result = stmt.executeQuery("SHOW TABLES LIKE 'organism'");
                boolean hasTables = false;
                if (result.next()) {
                    hasTables = true;
                }
                if (hasTables && !isLiquibase) {
                    //Read the script file
                    List<String> commandList = new LinkedList<String>();
                    BufferedReader br = null;
                    try {
                        String str;
                        br = new BufferedReader(new InputStreamReader(HibernateUtil.class.getResourceAsStream("/edu/uncc/genosets/datamanager/resources/databasechangelog.sql")));
                        while ((str = br.readLine()) != null) {
                            if (!str.startsWith("--") && str.length() > 0) {
                                commandList.add(str);
                            }
                        }
                    } catch (IOException ex) {
                        throw new InvalidConnectionException("Database creation script could not be read. ", ex);
                    } finally {
                        if (br != null) {
                            try {
                                br.close();
                            } catch (IOException ex) {
                            }
                        }
                    }
                    //execute statements
                    Statement statement = sqlConnection.createStatement();
                    for (String command : commandList) {
                        statement.addBatch(command);
                    }
                    try {
                        statement.executeBatch();
                    } catch (SQLException ex) {
                        throw new DatabaseMigrationException("Could not update database.", ex);
                    } finally {
                        sqlConnection.close();
                        sqlConnection = null;
                    }
                }
                if (sqlConnection == null) {
                    sqlConnection = ds.getConnection();
                }
                Liquibase liquibase = null;
                try {
                    Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(sqlConnection));
                    liquibase = new Liquibase("edu/uncc/genosets/datamanager/resources/db.changelog-master.xml", new ClassLoaderResourceAccessor(), database);
                    liquibase.update("");
                } catch (DatabaseException ex) {
                    Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LiquibaseException ex) {
                    Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
                testMaxAllowedPacketSize(sqlConnection);
            } catch (SQLException ex) {
                throw new DatabaseMigrationException("Invalid connection", ex);
            } finally {
                if (sqlConnection != null) {
                    try {
                        sqlConnection.close();
                    } catch (SQLException ex) {
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            throw new DatabaseMigrationException("Invalid connection", ex);
        }
    }

    private static int testMaxAllowedPacketSize(Connection sqlConnection) {
        int value = -1;
        Statement stmt = null;
        String query = "SHOW VARIABLES LIKE 'max_allowed_packet';";
        try {
            stmt = sqlConnection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                value = rs.getInt("VARIABLES.Value");
            }
            LogFactory.getLog(HibernateUtil.class).debug("Before update: max_allowed_packet=" + value);
            if (value < MAX_ALLOWED_PACKET_MIN) {
                // increase max_allowed_packet to 16M 
                Statement stmtSet = null;
                try {
                    stmtSet = sqlConnection.createStatement();
                    stmtSet.execute("SET GLOBAL max_allowed_packet = " + MAX_ALLOWED_PACKET_MIN);
                } catch (SQLException ex) {
                    LogFactory.getLog(HibernateUtil.class).warn("Could not set max_allowed_packet");
                } finally {
                    stmtSet.close();
                }
                stmt = sqlConnection.createStatement();
                rs = stmt.executeQuery(query);
                while (rs.next()) {
                    value = rs.getInt("VARIABLES.Value");
                }
                LogFactory.getLog(HibernateUtil.class).debug("After update: max_allowed_packet=" + value);
                LogFactory.getLog(HibernateUtil.class).warn("The max allowed packet size could be set too low (" + value + ").  See documentation for more information.");
            }
        } catch (SQLException e) {
            LogFactory.getLog(HibernateUtil.class).warn("Could not test max allowed packet size.", e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException ex) {
                    LogFactory.getLog(HibernateUtil.class).debug(ex);
                }
            }
        }

        return value;
    }
}
