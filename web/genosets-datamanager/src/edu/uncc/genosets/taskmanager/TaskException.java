package edu.uncc.genosets.taskmanager;

/**
 *
 * @author aacain
 */
public class TaskException extends Exception{

    public TaskException() {
    }

    public TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskException(Throwable cause) {
        super(cause);
    }

}
