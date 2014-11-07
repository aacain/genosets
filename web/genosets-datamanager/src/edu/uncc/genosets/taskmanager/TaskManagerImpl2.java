/*
 * 
 * 
 */
package edu.uncc.genosets.taskmanager;

import edu.uncc.genosets.datamanager.entity.Organism;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import javax.swing.SwingWorker;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author aacain
 */
public class TaskManagerImpl2 implements TaskManager {

    private final Map<String, Organism> orgDependMap;
    //private final ConcurrentLinkedQueue<Task> waitingTasks;
    private final LinkedList<Task> waitingTasks;
    private final PriorityQueue<Task> priorityTasks;
    private int numRunning = 0;
    private int totalToRun = 1;
    private PropertyChangeSupport pcs;
    private List<Task> runningTasks = new ArrayList(10);
    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String DB_ID = "DB_ID";

    public TaskManagerImpl2() {
        this.orgDependMap = new HashMap<String, Organism>();
        this.waitingTasks = new LinkedList<Task>();
        this.priorityTasks = new PriorityQueue<Task>(10, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return ((Integer) o1.getRank()).compareTo((Integer) o2.getRank());
            }
        });
        this.pcs = new PropertyChangeSupport(this);
    }

    @Override
    public void addPendingTask(Task task) {
        addTask(task);
        tryToSubmit();
    }

    public synchronized void addTask(Task task) {
        if (task.getRank() == 0) {
            this.waitingTasks.add(task);
        } else {
            this.priorityTasks.add(task);
        }
    }

    private void tryToSubmit() {
        Task task = getNextTask();
        if (task != null) {
            TaskWorker worker = new TaskWorker(task);
            worker.execute();
        }
    }

    private synchronized void removeDependency(Organism org) {
        if (org != null) {
            if (org.getOrganismId() != null) {
                orgDependMap.remove(org.getOrganismId().toString() + DB_ID);
            }
            if (org.getProjectId() != null) {
                orgDependMap.remove(org.getProjectId().toString() + PROJECT_ID);
            }
        }
    }

    protected synchronized void addTaskToList(Task task) {
        runningTasks.add(task);
        this.pcs.firePropertyChange(TaskManager.PROP_RUNNING_LIST_ADD, null, task);
    }

    protected synchronized void removeTaskFromList(Task task) {
        runningTasks.remove(task);
        this.pcs.firePropertyChange(TaskManager.PROP_RUNNING_LIST_REMOVE, null, task);
    }

    private synchronized void decrementRunning() {
        numRunning = numRunning - 1;
    }

    private synchronized boolean canContinue(Task task) {
        Organism org = task.getOrganismDependency();
        if (org == null) {
            return true;
        }
        //lookup
        if (org.getOrganismId() == null) {
            if (org.getProjectId() == null) {
                return true; //not enough information to lookup
            } else { //has projectid but not orgId
                //lookup by projectId
                Organism mapOrg = orgDependMap.get(org.getProjectId() + PROJECT_ID);
                if (mapOrg != null) {//set task organism to the lookup one, this one will be persisted
                    task.setOrganismDependency(mapOrg);
                    return false;
                } else {

                    Collection<Organism> lookup = OrganismLookup.lookup(org);
                    for (Organism dbOrg : lookup) {
                        org.setOrganismId(dbOrg.getOrganismId());
                        org.setSpecies(dbOrg.getSpecies());
                        org.setStrain(dbOrg.getStrain());
                    }

                    if (org.getOrganismId() != null) {
                        orgDependMap.put(org.getOrganismId() + DB_ID, org);
                    }
                    orgDependMap.put(org.getProjectId() + PROJECT_ID, org);
                    return true;
                }
            }
        } else {//has orgid
            Organism mapOrg = orgDependMap.get(org.getOrganismId() + DB_ID);
            if (mapOrg != null) {
                task.setOrganismDependency(mapOrg);
                return false;
            } else {//not currently running
                orgDependMap.put(org.getOrganismId() + DB_ID, org);
                return true;
            }
        }
    }

    private synchronized Task getNextTask() {
        if (numRunning < totalToRun) {
            System.out.println("Priority: " + priorityTasks.size() + ", Waiting: " + waitingTasks.size());
            Task task = priorityTasks.poll();
            if (task == null) {
                task = waitingTasks.poll();
            }
            if (task != null && canContinue(task)) {
                numRunning = numRunning + 1;
                return task;
            } else {
                this.addPendingTask(task);
                return null;
            }
        }
        return null;
    }

    @Override
    public List<Task> getRunningTasks() {
        return runningTasks;
    }

    private class TaskWorker extends SwingWorker<Task, Object> {

        private final Task task;
        private final Organism orgDepend;

        TaskWorker(Task task) {
            this.task = task;
            this.orgDepend = task.getOrganismDependency();
        }

        @Override
        protected Task doInBackground() throws Exception {
            try {
                addTaskToList(task);
                task.performTask();
            } catch (Exception e) {
                LogFactory.getLog(TaskManagerImpl2.class).error("Could not run task" + task.getName(), e);
                throw new TaskException(e);
            } finally {
                removeTaskFromList(task);
                task.setComplete(true);
                removeDependency(orgDepend);
                decrementRunning();
                tryToSubmit();
            }
            return task;
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
}
