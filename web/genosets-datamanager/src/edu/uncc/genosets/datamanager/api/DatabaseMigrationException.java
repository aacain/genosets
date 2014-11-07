
package edu.uncc.genosets.datamanager.api;

/**
 *
 * @author aacain
 */
public class DatabaseMigrationException extends Exception{

    public DatabaseMigrationException() {
    }

    public DatabaseMigrationException(String message) {
        super(message);
    }

    public DatabaseMigrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseMigrationException(Throwable cause) {
        super(cause);
    }

}
