package edu.uncc.genosets.connections;

/**
 *
 * @author aacain
 */
public class NoSuchDatabaseException extends InvalidConnectionException{

    public NoSuchDatabaseException() {
    }

    public NoSuchDatabaseException(String message) {
        super(message);
    }

    public NoSuchDatabaseException(Throwable cause) {
        super(cause);
    }

    public NoSuchDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

}
