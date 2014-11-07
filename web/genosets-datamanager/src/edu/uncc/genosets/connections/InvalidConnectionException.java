/*
 * Exception when the connection is invalid
 */
package edu.uncc.genosets.connections;

/**
 *
 * @author aacain
 */
public class InvalidConnectionException extends Exception{

    public InvalidConnectionException() {
    }

    public InvalidConnectionException(String message) {
        super(message);
    }

    public InvalidConnectionException(Throwable cause) {
        super(cause);
    }

    public InvalidConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
