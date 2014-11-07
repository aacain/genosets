/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.connections;

import edu.uncc.genosets.connections.Connection;
import edu.uncc.genosets.connections.InvalidConnectionException;
import java.beans.PropertyChangeListener;
import java.util.Collection;

/**
 *
 * @author aacain
 */
public interface ConnectionManager {

    public static final String CONNECTION__DETAILS_CHANGED = "CONNECTION_CHANGED";

    /**
     * Get all saved connections
     *
     * @return a collection of all the saved connections
     */
    public Collection<Connection> getSavedConnections();

    /**
     * Test the connection. (Just calls DataManager testConnection).
     *
     * @param url
     * @param userName
     * @param password
     * @param connectionType
     * @throws InvalidConnectionException - thrown if the collection could not
     * be created because the url, username, or password is invalid.
     */
    public void testConnection(String url, String userName, String password, String connectionType) throws InvalidConnectionException;

    /**
     * Creates a new connection and saves the details. The returned connections
     * has a unique id assigned as the connectionId.
     *
     * @param connectionName
     * @param url
     * @param userName
     * @param password
     * @param connectionType
     * @param savePassword - should the password be saved
     * @param isDefault
     * @return a newly created connection
     */
    public Connection createConnection(String connectionName, String url, String userName, String password, String connectionType, boolean savePassword, boolean isDefault);

    /**
     * Updates and saves the connection name
     *
     * @param connection to update
     * @param newName
     */
    public void updateConnectionName(Connection conn, String newName);
    

    /**
     * Updates and saves the username
     *
     * @param connection
     * @param username
     */
    public void updateUserName(Connection connection, String username);

    /**
     * Updates the password. Saves if parameter save is true.
     *
     * @param connection
     * @param password
     * @param save - save the password
     */
    public void updatePassword(Connection connection, String password, boolean save);

    /**
     * Removes the connection and all associated data that is stored locally.
     *
     * @param connection
     */
    public void removeConnection(Connection connection);

    /**
     * Sets the default connection. The default connection will be automatically
     * opened when GenoSets is started.
     *
     * @param connection
     */
    public void setDefaultConnection(Connection connection);

    /**
     * Reads all saved connections and returns the default connection if it
     * exits.
     *
     * @return - the default connection or null if none exists.
     */
    public Connection getDefaultConnection();

    /**
     * Add a property change listener to listen for changes to the collection of
     * details of saved connections.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove property change listener
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener);
}
