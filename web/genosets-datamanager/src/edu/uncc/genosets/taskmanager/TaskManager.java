/*
 * 
 * 
 */
package edu.uncc.genosets.taskmanager;

import java.beans.PropertyChangeListener;
import java.util.List;

/**
 *
 * @author aacain
 */
public interface TaskManager {

    public void addPendingTask(Task task);

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);
    
    public List<Task> getRunningTasks();
    public static final String PROP_WAITING_LIST_ADD = "PROP_WAITING_LIST_ADD";
    public static final String PROP_WAITING_LIST_REMOVE = "PROP_WAITING_LIST_REMOVE";
    public static final String PROP_RUNNING_LIST_ADD = "PROP_RUNNING_LIST_ADD";
    public static final String PROP_RUNNING_LIST_REMOVE = "PROP_RUNNING_LIST_REMOVE";
}
