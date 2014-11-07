/*
 * 
 * 
 */
package edu.uncc.genosets.studyset;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.datamanager.entity.StudySetEntity;
import edu.uncc.genosets.studyset.StudySetManager.StudySetManagerFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author aacain
 */
public class StudySet implements Serializable {

    public static final String PROP_ADDED_FEATURES = "PROP_ADDED_FEATURES";
    public static final String PROP_DELETED = "PROP_DELETED";
    private transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private StudySetEntity ssEntity;
    private boolean block = false;
    //private Date lastUpdated;
    private transient Lookup lookup;
    private transient InstanceContent ic;
    private final Integer uniqueName;
    private Set<Integer> tempIdSet;

    public StudySet() {
        this(new StudySetEntity());
    }

    public StudySet(StudySetEntity ssEntity) {
        this.ssEntity = ssEntity;
        this.uniqueName = StudySetManagerFactory.getDefault().getNextUniqueId();
        this.ic = new InstanceContent();
        lookup = new AbstractLookup(ic);
        this.ic.add(this);
        Collection<? extends StudySetServiceProvider> providers = Lookup.getDefault().lookupAll(StudySetServiceProvider.class);
        for (StudySetServiceProvider service : providers) {
            service.initialize(DataManager.getDefault().getConnectionId(), this);
        }
    }

    private boolean isSaved() {
        //return Lookup.getDefault().lookup(StudySetManager.class).getStudySet(uniqueName) != null;
        return ssEntity.getId() != null;
    }

    /**
     * Gets the date that this study set was last updated.
     *
     * @return date last updated
     */
    public synchronized Date getLastUpdated() {
        Date modifiedDate = this.getStudySetEntity().getModifiedDate();
        return modifiedDate;
    }

    /**
     * Getter for the lookup that is created for this object.
     *
     * @return the lookup for this object
     */
    public Lookup getLookup() {
        return this.lookup;
    }

    /**
     * Loads the data for this object. This method is used to create all
     * necessary objects if this object has been serialized. Overriden methods
     * should never call StudySetManager.class
     *
     */
    public void load() {
        this.ic = new InstanceContent();
        lookup = new AbstractLookup(ic);
        this.ic.add(this);
        pcs = new PropertyChangeSupport(this);
        Collection<? extends StudySetServiceProvider> providers = Lookup.getDefault().lookupAll(StudySetServiceProvider.class);
        for (StudySetServiceProvider service : providers) {
            service.initialize(DataManager.getDefault().getConnectionId(), this);
        }
    }

    /**
     * Adds a instance to the lookup of this object.
     *
     * @param instance to add to lookup
     */
    public void addInstanceContent(Object instance) {
        this.ic.add(instance);
    }

    /**
     * Removes the instance from this objects lookup.
     *
     * @param instance to remove from lookup
     */
    public void removeInstanceContent(Object instance) {
        this.ic.remove(instance);
    }

    public String getName() {
        return ssEntity.getStudySetName();
    }

    public void setName(String name) {
        String old = this.ssEntity.getStudySetName();
        this.ssEntity.setStudySetName(name);
        if (isSaved() && !old.equals(name)) {
            save();
        }
        this.pcs.firePropertyChange(new PropertyChangeEvent(this, "name", old, name));
    }

    public String getUniqueName() {
        return ssEntity.getId() == null ? this.uniqueName.toString() : ssEntity.getId().toString();
    }
    
    /**
     * Gets a snapshot of the ids of the entities in this set.
     *
     * @return snapshot of entity ids
     */
    public synchronized Set<Integer> getIdSet() {
        if (isSaved()) {
            return StudySetManager.StudySetManagerFactory.getDefault().getIdSet(this);
        } else {
            return this.tempIdSet;
        }
    }

    /**
     * Sets the ids for the entities that are in this set
     *
     * @param idSet of ids
     */
    public synchronized void setIdSet(Set<Integer> idSet) {
        if (!isSaved()) {
            StudySetManagerFactory.getDefault().setIdSet(this, idSet);
            this.pcs.firePropertyChange(new PropertyChangeEvent(this, "count", 0, idSet.size()));
            this.pcs.firePropertyChange(new PropertyChangeEvent(this, PROP_ADDED_FEATURES, null, getIdSet()));
        } else {
            Set<Integer> old = StudySetManagerFactory.getDefault().getIdSet(this);
            if (!(old.containsAll(idSet) && idSet.containsAll(old))) {
                StudySetManagerFactory.getDefault().setIdSet(this, idSet);
                this.pcs.firePropertyChange(new PropertyChangeEvent(this, "count", old.size(), idSet.size()));
                this.pcs.firePropertyChange(new PropertyChangeEvent(this, PROP_ADDED_FEATURES, null, getIdSet()));
            }
        }
    }

    public synchronized String getDescription() {
        return ssEntity.getStudySetDescription();
    }

    public synchronized void setDescription(String description) {
        String old = this.ssEntity.getStudySetDescription();
        this.ssEntity.setStudySetDescription(description);
        if (isSaved()) {
            if (!old.equals(description)) {
                save();
            }
        }
    }

    protected StudySetEntity getStudySetEntity() {
        return this.ssEntity;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    protected void save() {
        StudySetManagerFactory.getDefault().saveStudySet(this);
    }

    protected void delete() {
        Collection<? extends StudySetServiceProvider> providers = Lookup.getDefault().lookupAll(StudySetServiceProvider.class);
        for (StudySetServiceProvider service : providers) {
            service.studySetDeleted(DataManager.getDefault().getConnectionId(), this);
        }
        StudySetManagerFactory.getDefault().deleteStudySet(this);
        this.pcs.firePropertyChange(PROP_DELETED, null, this);
    }

    public void setFocusEntity(FocusEntity focusEntity) {
        StudySetManagerFactory.getDefault().setFocusEntity(this, focusEntity);
    }
    
    public FocusEntity getFocusEntity() {
        return StudySetManagerFactory.getDefault().getFocusEntity(this);
    }

    protected synchronized void setIsBlocked(boolean isBlocked) {
        this.block = isBlocked;
    }

    public synchronized boolean isBlocked() {
        return this.block;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StudySet other = (StudySet) obj;
        return this.getUniqueName().equals(other.getUniqueName());
    }

    @Override
    public int hashCode() {
        return this.getUniqueName().hashCode();
    }
}
