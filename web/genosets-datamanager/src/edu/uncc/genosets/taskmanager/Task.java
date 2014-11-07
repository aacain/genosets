/*
 * 
 * 
 */
package edu.uncc.genosets.taskmanager;

import edu.uncc.genosets.datamanager.entity.Organism;
import java.beans.PropertyChangeListener;
import java.util.Date;

/**
 *
 * @author aacain
 */
public interface Task {

    public final static String PROP_NAME = "PROP_NAME";
    public final static String PROP_ID = "PROP_ID";
    public final static String PROP_START_TIME = "PROP_START_TIME";
    public final static String PROP_END_TIME = "PROP_END_TIME";
    public final static String PROP_RANK = "PROP_RANK";
    public final static String PROP_PROGRESS = "PROP_PROGRESS";
    public final static String PROP_DESC = "PROP_DESC";
    public final static String PROP_COMPLETE = "PROP_COMPLETE";
    public final static String PROP_ERROR = "PROP_ERROR";

    public void performTask() throws TaskException;

    public void uninitialize();

    public void logErrors();

    public Organism getOrganismDependency();

    public void setOrganismDependency(Organism org);

    public boolean isComplete();

    public void setComplete(boolean complete);

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    public String getDescr();

    public void setDescr(String descr);

    public Date getEndTime();

    public void setEndTime(Date endTime);

    public String getId();

    public void setId(String id);

    public String getName();

    public void setName(String name);

    public int getProgress();

    public void setProgress(int progress);

    public int getRank();

    public void setRank(int rank);

    public Date getStartTime();

    public void setStartTime(Date startTime);
}
