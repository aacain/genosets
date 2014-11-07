
package edu.uncc.genosets.connections;

/**
 *
 * @author aacain
 */
public class SchemaInvalidException extends InvalidConnectionException{

    public SchemaInvalidException() {
    }

    public SchemaInvalidException(String message) {
        super(message);
    }

    public SchemaInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchemaInvalidException(Throwable cause) {
        super(cause);
    }

}
