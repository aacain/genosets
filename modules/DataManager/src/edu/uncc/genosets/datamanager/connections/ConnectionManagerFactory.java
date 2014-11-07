/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.connections;

/**
 *
 * @author aacain
 */
public class ConnectionManagerFactory {

    private static ConnectionManager instance;
    
    public static ConnectionManager getConnectionManager() {
        if(instance == null){
            instance = new ConnectionManagerImpl();
        }
        return instance;
    }
}
