/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.connections;

import edu.uncc.genosets.connections.Connection;
import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.datamanager.api.DataManager;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author aacain
 */
public class ConnectionManagerImpl implements ConnectionManager {

    private HashMap<Connection, FileObject> connectionToFile;
    private PropertyChangeSupport pcs;

    private synchronized HashMap<Connection, FileObject> getConnectionMap() {
        if (connectionToFile == null) {
            getSavedConnections();
        }
        return connectionToFile;
    }

    @Override
    public synchronized Collection<Connection> getSavedConnections() {
        if (connectionToFile == null) {
            connectionToFile = new HashMap<Connection, FileObject>();
            FileObject configRoot = FileUtil.getConfigRoot();
            FileObject connectionRoot = configRoot.getFileObject("dbconnections");
            if (connectionRoot == null) {
                try {
                    connectionRoot = configRoot.createFolder("dbconnections");
                } catch (IOException ex) {
                    Logger.getLogger("edu.uncc.genosets.datamanager.connections.ConnectionManager.class").log(Level.SEVERE, "Could not open the database connections");
                }
            }
            JAXBContext jaxbContext;
            Unmarshaller unmarshaller;
            try {
                jaxbContext = JAXBContext.newInstance(edu.uncc.genosets.connections.Connection.class);
                unmarshaller = jaxbContext.createUnmarshaller();
            } catch (JAXBException ex) {
                Exceptions.printStackTrace(ex);
                return null;
            }
            for (FileObject fo : connectionRoot.getChildren()) {
                FileLock lock = null;
                InputStream in = null;
                try {
                    lock = fo.lock();
                    in = fo.getInputStream();
                    Connection conn = (Connection) unmarshaller.unmarshal(in);
                    connectionToFile.put(conn, fo);
                } catch (FileAlreadyLockedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (JAXBException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        lock.releaseLock();
                    }
                }
            }
        }

        return connectionToFile.keySet();
    }

    @Override
    public void testConnection(String url, String userName, String password, String connectionType) throws InvalidConnectionException {
        Connection tempConn = new Connection("temporary", "temp", url, userName, password, false, connectionType, false);
        DataManager.testConnection(tempConn);
    }

    @Override
    public synchronized Connection createConnection(String connectionName, String url, String userName, String password, String connectionType, boolean savePassword, boolean isDefault) {
        FileObject configRoot = FileUtil.getConfigRoot();
        FileObject connectionRoot = configRoot.getFileObject("dbconnections");
        try {
            FileObject connFo = createConnectionDecriptionFile(connectionRoot, 0);
            if (connectionName == null) {
                connectionName = connFo.getName();
            }
            Connection conn = new Connection(connFo.getName(), connectionName, url, userName, password, isDefault, connectionType, savePassword);
            //add it to the map
            HashMap<Connection, FileObject> map = getConnectionMap();
            map.put(conn, connFo);
            //create the main folder for this connection
            FileObject fo = FileUtil.getConfigFile(conn.getConnectionId());
            if (fo == null) {
                FileObject root = FileUtil.getConfigRoot();
                root.createFolder(conn.getConnectionId());
            }
            updateConnection(conn);
            if (isDefault) {
                setDefaultConnection(conn);
            }
            return conn;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    /**
     * Creates a unique file to store the details of the connection. Should be
     * called with the count parameter set to 0 (recursive).
     *
     * @param connectionRoot
     * @param count - set this to 0
     * @return
     * @throws IOException
     */
    private FileObject createConnectionDecriptionFile(FileObject connectionRoot, int count) throws IOException {
        count++;
        String fileName = FileUtil.findFreeFileName(connectionRoot, "connection", "db");
        try {
            return connectionRoot.createData(fileName, "db");
        } catch (IOException ex) {
            if (count < 10) {
                return createConnectionDecriptionFile(connectionRoot, count);
            } else {
                throw ex;
            }
        }
    }

    @Override
    public synchronized void removeConnection(Connection connection) {
        if (DataManager.getDefault().getConnectionId() != null && DataManager.getDefault().getConnectionId().equals(connection.getConnectionId())) {
            NotifyDescriptor d = new NotifyDescriptor.Message("Connect delete the current connection.");
            DialogDisplayer.getDefault().notify(d);
        } else {
            //delete the connection file
            HashMap<Connection, FileObject> map = getConnectionMap();
            FileObject connFile = map.remove(connection);
            try {
                //delete the connection file
                connFile.delete();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                //delete the database folder
                FileObject db = FileUtil.getConfigFile(connection.getConnectionId());
                if (db != null) {
                    try {
                        db.delete();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
        PropertyChangeSupport myPcs = getPcs();
        myPcs.firePropertyChange("CONNECTION_CHANGED", null, connection);
    }

    @Override
    public synchronized void setDefaultConnection(Connection connection) {
        HashMap<Connection, FileObject> map = getConnectionMap();
        //set all others as false
        for (Entry<Connection, FileObject> entry : map.entrySet()) {
            Connection otherConnection = entry.getKey();
            if (otherConnection.isIsDefault() && otherConnection != connection) {
                otherConnection.setIsDefault(false);
                updateConnection(otherConnection);
            }
        }
        //now set connection as default
        if (connection != null) {
            connection.setIsDefault(Boolean.TRUE);
            updateConnection(connection);
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        PropertyChangeSupport mine = getPcs();
        mine.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        PropertyChangeSupport mine = getPcs();
        mine.removePropertyChangeListener(listener);
    }

    private synchronized PropertyChangeSupport getPcs() {
        if (this.pcs == null) {
            this.pcs = new PropertyChangeSupport(this);
        }
        return this.pcs;
    }

    @Override
    public void updateConnectionName(Connection conn, String newName) {
        if (conn.getConnectionName().equals(newName)) {
            return;
        }
        conn.setConnectionName(newName);
        updateConnection(conn);
    }

    @Override
    public void updateUserName(Connection connection, String username) {
        if (connection.getUserName().equals(username)) {
            return;
        }
        connection.setUserName(username);
        updateConnection(connection);
    }

    @Override
    public void updatePassword(Connection connection, String password, boolean save) {
        boolean oldSave = connection.isSavePassword();
        String oldPass = connection.getPassword();
        connection.setSavePassword(save);
        connection.setPassword(password);
        if (oldPass != null && (!oldPass.equals(password) || !oldSave == save)) {
            updateConnection(connection);
            //notify listeners
            PropertyChangeSupport pcs1 = getPcs();
            pcs1.firePropertyChange(ConnectionManager.CONNECTION__DETAILS_CHANGED, null, connection);
        }
    }

    /**
     * Saves the changes to the connection description file and notifies
     * listeners of changes.
     *
     * @param conn - with the updates
     */
    private synchronized void updateConnection(Connection conn) {
        //save connection
        HashMap<Connection, FileObject> connMap = getConnectionMap();
        FileObject fo = connMap.get(conn);
        JAXBContext jaxbContext;
        Marshaller marshaller;
        FileLock lock = null;
        OutputStream out = null;

        try {
            jaxbContext = JAXBContext.newInstance(edu.uncc.genosets.connections.Connection.class);
            marshaller = jaxbContext.createMarshaller();


            try {
                lock = fo.lock();
            } catch (FileAlreadyLockedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            out = fo.getOutputStream(lock);

            String password = conn.getPassword();
            if (!conn.isSavePassword()) {
                conn.setPassword(null);
            }
            marshaller.marshal(conn, out);
            conn.setPassword(password);
        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                lock.releaseLock();
            }
        }

        //notify listeners
        PropertyChangeSupport pcs1 = getPcs();
        pcs1.firePropertyChange(ConnectionManager.CONNECTION__DETAILS_CHANGED, null, conn);
    }

    @Override
    public Connection getDefaultConnection() {
        HashMap<Connection, FileObject> connectionMap = getConnectionMap();
        for (Connection connection : connectionMap.keySet()) {
            if (connection.isIsDefault()) {
                return connection;
            }
        }
        return null;
    }
}
