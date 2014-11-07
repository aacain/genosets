
package edu.uncc.genosets.taskmanager;

/**
 *
 * @author aacain
 */
public class TaskManagerFactory {
    private static TaskManager taskManager = new TaskManagerImpl2();
    public static TaskManager getDefault(){
        return taskManager;
    }
}
