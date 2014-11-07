
package edu.uncc.genosets.datamanager.api;

/**
 *
 * @author aacain
 */
public class DataLoadException extends RuntimeException{

    public DataLoadException() {
    }

    public DataLoadException(String message) {
        super(message);
    }

    public DataLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataLoadException(Throwable cause) {
        super(cause);
    }

}
