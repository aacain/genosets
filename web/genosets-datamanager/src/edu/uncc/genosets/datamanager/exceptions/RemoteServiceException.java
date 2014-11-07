
package edu.uncc.genosets.datamanager.exceptions;

/**
 *
 * @author aacain
 */
public class RemoteServiceException extends Exception{

    public RemoteServiceException() {
    }

    public RemoteServiceException(String message) {
        super(message);
    }

    public RemoteServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteServiceException(Throwable cause) {
        super(cause);
    }

}
