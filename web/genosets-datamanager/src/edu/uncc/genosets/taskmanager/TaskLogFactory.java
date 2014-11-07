package edu.uncc.genosets.taskmanager;

import edu.uncc.genosets.datamanager.api.DataManager;
import java.beans.PropertyChangeSupport;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author aacain
 */
public class TaskLogFactory {

    private static TaskLog instance;

    /**
     * Get the default TaskLog that is defined in the taskContext.xml file.
     * @return the default taskLog
     */
    public static TaskLog getDefault() {
        synchronized (TaskLog.class) {
            if (instance == null) {
                ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("taskContext.xml");
                instance = (TaskLog) context.getBean("taskLog");
                if (instance == null) {
                    instance = new TaskLog();
                }
                DataManager.getDefault().addPropertyChangeListener(instance.dbListener);
                instance.cs = new PropertyChangeSupport(instance);
                instance.dbChanged(null);
            }
        }
        return instance;
    }
}
