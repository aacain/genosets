/*
 * 
 * 
 */
package edu.uncc.genosets.taskmanager;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author aacain
 */
public abstract class AbstractTask implements Task{

    protected String id = "";
    protected String name = "";
    protected Date startTime = new Date();
    protected Date endTime = new Date();
    protected int rank = 0;
    protected int progress = 0;
    protected String descr = "";
    protected boolean complete = false;
    protected PropertyChangeSupport pcs;
    protected static Random random;

    public AbstractTask() {
        this.id = getAndIncrementId();
        this.pcs = new PropertyChangeSupport(this);
    }

    public AbstractTask(String name) {
        this.name = name;
        this.id = getAndIncrementId();
        this.pcs = new PropertyChangeSupport(this);
    }

    private static synchronized String getAndIncrementId() {
        if (random == null) {
            random = new Random();
        }
        return "0000" + random.nextInt();
    }




    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        String old = this.descr;
        this.descr = descr;
        this.pcs.firePropertyChange(PROP_DESC, old, this.descr);
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        Date old = this.endTime;
        this.endTime = endTime;
        this.pcs.firePropertyChange(PROP_END_TIME, old, this.endTime);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        String old = this.id;
        this.id = id;
        this.pcs.firePropertyChange(PROP_ID, old, this.id);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        String old = this.name;
        this.name = name;
        this.pcs.firePropertyChange(PROP_NAME, old, this.name);
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        int old = this.progress;
        this.progress = progress;
        this.pcs.firePropertyChange(PROP_PROGRESS, old, this.progress);
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        int old = this.rank;
        this.rank = rank;
        this.pcs.firePropertyChange(PROP_RANK, old, this.rank);
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        Date old = this.startTime;
        this.startTime = startTime;
        this.pcs.firePropertyChange(PROP_START_TIME, old, this.startTime);
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        boolean old = this.complete;
        this.complete = complete;
        this.pcs.firePropertyChange(PROP_COMPLETE, old, this.complete);
    }

    @Override
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    @Override
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Task other = (Task) obj;
        return this.id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.getId() + " - " +  " = " + this.getName();
    }
}
