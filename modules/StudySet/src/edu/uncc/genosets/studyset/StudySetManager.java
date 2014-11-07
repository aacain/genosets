/*
 * 
 * 
 */
package edu.uncc.genosets.studyset;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.datamanager.entity.FactStudySet;
import edu.uncc.genosets.datamanager.entity.StudySetEntity;
import edu.uncc.genosets.taskmanager.SimpleTask;
import edu.uncc.genosets.taskmanager.TaskException;
import edu.uncc.genosets.taskmanager.TaskManagerFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressRunnable;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.util.Exceptions;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.ServiceProvider;
import org.springframework.util.ConcurrentReferenceHashMap;

/**
 *
 * @author aacain
 */
public interface StudySetManager {

    public static final String PROP_STUDYSET_CHANGE = "PROP_STUDYSET_CHANGE";
    public static final String PROP_DB_CHANGE = "PROP_DB_CHANGED";
    public static final String PROP_STUDYSET_ADDED = "PROP_STUDYSET_ADDED";
    public static final String PROP_STUDYSET_REMOVED = "PROP_STUDYSET_REMOVED";

    /**
     * Gets a snapshot of the studySets.
     *
     * @return
     */
    public List<StudySet> getStudySets();

    /**
     * Saves the study set.
     *
     * @param set
     */
    public void saveStudySet(StudySet set);

    /**
     * Deletes the study set.
     *
     * @param set
     */
    public void deleteStudySet(StudySet set);

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    public void addStudySetChangeListener(StudySetChangeListener listener);

    public void removeStudySetChangeListener(StudySetChangeListener listener);

    public void fireStudySetAdded(StudySetEvent evt);

    public void fireStudySetRemoved(StudySetEvent evt);

    public void fireSelectedStudySetChanged(StudySetEvent evt);

    public Integer getNextUniqueId();

    public StudySet getStudySet(String uniqueName);

    public StudySet getSelectedStudySet();

    public StudySet setSelectedStudySet(StudySet selectedStudySet);

    public Set<Integer> getIdSet(StudySet studySet);

    public void setIdSet(StudySet studySet, Set<Integer> idSet);

    public void setFocusEntity(StudySet studySet, FocusEntity focusEntity);

    public FocusEntity getFocusEntity(StudySet studySet);

    public void firePropertyChange(PropertyChangeEvent evt);

    public class StudySetManagerFactory {

        static final StudySetManager instance = new StudySetManagerImpl();

        public static StudySetManager getDefault() {
            return instance;
        }
    }

    @ServiceProvider(service = StudySetManager.class)
    public static class StudySetManagerImpl implements StudySetManager, LookupListener, PropertyChangeListener {

        private static final StudySet[] LISTENER_LOCK = new StudySet[0];
        private HashMap<String, StudySet> studySets;
        private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
        private ArrayList<StudySetChangeListener> listeners;
        private static int nextId = Math.abs((int) System.currentTimeMillis());
        private StudySet selectedSet = null;
        private final Result<StudySet> studySetResult;
        private ConcurrentReferenceHashMap<StudySet, Set<FactStudySet>> factMap;

        public StudySetManagerImpl() {
            DataManager mgr = DataManager.getDefault();
            mgr.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    datamanagerChanged(evt);
                }
            });
            this.factMap = new ConcurrentReferenceHashMap(16, 0.75f, 16, ConcurrentReferenceHashMap.ReferenceType.SOFT);
            this.studySetResult = Utilities.actionsGlobalContext().lookupResult(StudySet.class);
            this.studySetResult.addLookupListener(WeakListeners.create(LookupListener.class, this, this.studySetResult));
        }

        private HashMap<String, StudySet> getMyStudySet() {
            if (studySets == null) {
                studySets = new HashMap<String, StudySet>();
                loadStudySets();
            }
            return studySets;
        }

        private void datamanagerChanged(PropertyChangeEvent evt) {
            if (DataManager.PROP_DB_CHANGED.equals(evt.getPropertyName())) {
                changeDatabase();
            }
        }

        private void changeDatabase() {
            //uninitialize
            studySets = null;
            selectedSet = null;
            setNextUniqueId(Math.abs((int) System.currentTimeMillis()));
            setSelectedStudySet(null);
            this.factMap = new ConcurrentReferenceHashMap(16, 0.75f, 16, ConcurrentReferenceHashMap.ReferenceType.SOFT);
            firePropertyChange(new PropertyChangeEvent(this, PROP_DB_CHANGE, null, getStudySets()));
        }

        @Override
        public synchronized Integer getNextUniqueId() {
            setNextUniqueId(nextId + 1);
            return nextId;
        }

        private synchronized void setNextUniqueId(int n) {
            nextId = n;
        }

        private void saveStudySets() {
        }

        private void loadStudySets() {
            setNextUniqueId(Math.abs((int) System.currentTimeMillis()));
            List<StudySetEntity> ss = StudySetQuery.getStudySets();
            for (StudySetEntity ssExt : ss) {
                StudySet s = new StudySet(ssExt);
                studySets.put(s.getUniqueName(), s);
            }
        }

        @Override
        public synchronized List<StudySet> getStudySets() {
            return new ArrayList<StudySet>(getMyStudySet().values());
        }

        @Override
        public synchronized void saveStudySet(final StudySet set) {
            String oldName = set.getUniqueName();
            if (set.getName() == null) {
                set.setName(set.getUniqueName());
            }
            ProgressRunnable r;
            r = new ProgressRunnable() {
                @Override
                public Object run(ProgressHandle handle) {
                    try {
                        //save study set
                        DataManager.getDefault().save(set.getStudySetEntity());
                    } catch (Exception ex) {
                        Logger.getLogger("edu.uncc.genosets.studyset.StudySetManager").log(Level.SEVERE, "Error saving studyset.", ex);
                    }
                    return null;
                }
            };
            ProgressUtils.showProgressDialogAndRun(r, "Querying", true);
            this.getMyStudySet().remove(oldName);
            this.getMyStudySet().put(set.getUniqueName(), set);
            //this.pcs.firePropertyChange(PROP_STUDYSET_CHANGE, null, getStudySet());
        }

        @Override
        public synchronized void deleteStudySet(StudySet set) {
            TaskManagerFactory.getDefault().addPendingTask(new DeleteStudySetTask(this, set, getFactSet(set), "Deleting study set " + set.getName()));

        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            this.pcs.addPropertyChangeListener(listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            this.pcs.removePropertyChangeListener(listener);
        }

        @Override
        public synchronized StudySet getStudySet(String uniqueName) {
            return getMyStudySet().get(uniqueName);
        }

        @Override
        public synchronized StudySet getSelectedStudySet() {
            return this.selectedSet;
        }

        @Override
        public StudySet setSelectedStudySet(StudySet selectedStudySet) {
            synchronized (this) {
                if (selectedStudySet != this.selectedSet) {
                    StudySet old = this.selectedSet;
                    this.selectedSet = selectedStudySet;
                    this.fireSelectedStudySetChanged(new StudySetEvent(this, selectedStudySet));
                    firePropertyChange(new PropertyChangeEvent(this, PROP_STUDYSET_CHANGE, old, this.selectedSet));
                }
            }
            return selectedStudySet;
        }

        @Override
        public void resultChanged(LookupEvent ev) {
            Collection<? extends StudySet> allInstances = this.studySetResult.allInstances();
            for (StudySet studySet : allInstances) {
                this.setSelectedStudySet(studySet);
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
        }

        @Override
        public Set<Integer> getIdSet(StudySet studySet) {
            Set<FactStudySet> facts = getFactSet(studySet);
            Set<Integer> idSet = new HashSet<Integer>();
            for (FactStudySet factStudySet : facts) {
                idSet.add(factStudySet.getEntityId());
            }
            return idSet;
        }

        private Set<FactStudySet> getFactSet(StudySet studySet) {
            Set<FactStudySet> facts = this.factMap.get(studySet);
            if (facts == null) {
                facts = StudySetQuery.getFacts(studySet);
                this.factMap.put(studySet, facts);
            }
            return facts;
        }

        @Override
        public void setIdSet(final StudySet studySet, Set<Integer> idSet) {
            String oldName = studySet.getUniqueName();
            Set<Integer> old = getIdSet(studySet);
            Set<Integer> added = new HashSet(idSet);
            added.removeAll(old);
            Set<Integer> deleted = new HashSet(old);
            deleted.removeAll(idSet);
            StudySetEntity ssEntity = studySet.getStudySetEntity();
            final Set<FactStudySet> factSet = new HashSet(getFactSet(studySet));
            final Set<FactStudySet> toDelete = new HashSet();
            for (FactStudySet fss : factSet) {
                if (deleted.contains(fss.getEntityId())) {
                    toDelete.add(fss);
                }
            }
            for (Integer integer : added) {
                factSet.add(new FactStudySet(integer, studySet.getStudySetEntity()));
            }
            this.factMap.put(studySet, factSet);
            ssEntity.setModifiedDate(new Date());

            SaveIdsTask task = new SaveIdsTask(this, studySet, factSet, toDelete, "Saving studyset " + studySet.getName());
            TaskManagerFactory.getDefault().addPendingTask(task);
            //saveStudySet(studySet);
        }

        @Override
        public FocusEntity getFocusEntity(StudySet studySet) {
            return FocusEntity.getEntity(studySet.getStudySetEntity().getEntityTable());
        }

        @Override
        public void setFocusEntity(StudySet studySet, FocusEntity focusEntity) {
            studySet.getStudySetEntity().setEntityTable(focusEntity.getEntityName());
        }

        //********Listeners
        @Override
        public void firePropertyChange(PropertyChangeEvent evt) {
            this.pcs.firePropertyChange(evt);
        }

        @Override
        public void addStudySetChangeListener(StudySetChangeListener listener) {
            synchronized (LISTENER_LOCK) {
                if (listeners == null) {
                    listeners = new ArrayList();
                }
                listeners.add(listener);
            }
        }

        @Override
        public void removeStudySetChangeListener(StudySetChangeListener listener) {
            synchronized (LISTENER_LOCK) {
                if (listeners != null) {
                    listeners.remove(listener);
                }
            }
        }

        @Override
        public void fireStudySetAdded(StudySetEvent evt) {
            synchronized (LISTENER_LOCK) {
                if (listeners != null) {
                    for (StudySetChangeListener l : listeners) {
                        l.studySetAdded(evt);
                    }
                }
            }
        }

        @Override
        public void fireStudySetRemoved(StudySetEvent evt) {
            synchronized (LISTENER_LOCK) {
                if (listeners != null) {
                    for (StudySetChangeListener l : listeners) {
                        l.studySetRemoved(evt);
                    }
                }
            }
        }

        @Override
        public void fireSelectedStudySetChanged(StudySetEvent evt) {
            synchronized (LISTENER_LOCK) {
                if (listeners != null) {
                    for (StudySetChangeListener l : listeners) {
                        l.selectedStudySetsChanged(evt);
                    }
                }
            }
        }
    }

    class StudySetQuery implements QueryCreator {

        static List<StudySetEntity> getStudySets() {
            return (List<StudySetEntity>) DataManager.getDefault().createQuery("SELECT s FROM StudySetEntity as s", StudySetEntity.class);
        }

        static Set<FactStudySet> getFacts(StudySet studySet) {
            if (studySet.getStudySetEntity().getId() == null) {
                return new HashSet();
            }
            return new HashSet(DataManager.getDefault().createQuery("SELECT f FROM FactStudySet as f WHERE f.studySetId = " + studySet.getStudySetEntity().getStudySetId(), FactStudySet.class));

        }
    }

    public static class DeleteStudySetTask extends SimpleTask {

        private StudySetManagerImpl mgr;
        private StudySet studySet;
        private Set<FactStudySet> toDelete;

        public DeleteStudySetTask(StudySetManagerImpl mgr, StudySet studySet, Set<FactStudySet> toDelete, String name) {
            super(name);
            this.mgr = mgr;
            this.studySet = studySet;
            this.toDelete = toDelete;
        }

        @Override
        public void performTask() throws TaskException {
            String oldName = studySet.getUniqueName();
            try {
                //save study set
                studySet.setIsBlocked(Boolean.TRUE);
                DataManager.getDefault().persist(Collections.singletonList(new StudySetDeleter(studySet.getStudySetEntity(), toDelete)));

            } catch (Exception ex) {
                Logger.getLogger("edu.uncc.genosets.studyset.StudySetManager").log(Level.SEVERE, "Error saving studyset.", ex);
            } finally {
                studySet.setIsBlocked(Boolean.FALSE);
                mgr.getMyStudySet().remove(oldName);
                mgr.getMyStudySet().put(studySet.getUniqueName(), studySet);
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            mgr.fireStudySetRemoved(new StudySetEvent(this, studySet));
                            mgr.firePropertyChange(new PropertyChangeEvent(this, PROP_STUDYSET_CHANGE, null, mgr.getStudySets()));
                            mgr.firePropertyChange(new PropertyChangeEvent(this, PROP_STUDYSET_REMOVED, null, studySet));
                        }
                    });
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (InvocationTargetException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    public static class SaveIdsTask extends SimpleTask {

        private StudySetManagerImpl mgr;
        private StudySet studySet;
        private Set<FactStudySet> toAdd;
        private Set<FactStudySet> toDelete;

        public SaveIdsTask(StudySetManagerImpl mgr, StudySet studySet, Set<FactStudySet> toAdd, Set<FactStudySet> toDelete, String name) {
            super(name);
            this.mgr = mgr;
            this.studySet = studySet;
            this.toAdd = toAdd;
            this.toDelete = toDelete;
        }

        @Override
        public void performTask() throws TaskException {
            String oldName = studySet.getUniqueName();
            boolean isSaved = false;
            try {
                studySet.setIsBlocked(Boolean.TRUE);
                //save study set
                if (studySet.getStudySetEntity().getStudySetId() != null) {
                    isSaved = true;
                }
                DataManager.getDefault().persist(Collections.singletonList(new StudySetPersister(studySet.getStudySetEntity(), toAdd, toDelete)));
            } catch (Exception ex) {
                Logger.getLogger("edu.uncc.genosets.studyset.StudySetManager").log(Level.SEVERE, "Error saving studyset.", ex);
            } finally {
                studySet.setIsBlocked(Boolean.FALSE);
                if (!oldName.equals(studySet.getUniqueName())) {
                    mgr.getMyStudySet().remove(oldName);
                    mgr.firePropertyChange(new PropertyChangeEvent(this, PROP_STUDYSET_REMOVED, null, studySet));
                }
                mgr.getMyStudySet().put(studySet.getUniqueName(), studySet);
                try {
                    final boolean fIsSaved = isSaved;
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            if (!fIsSaved) {
                                mgr.fireStudySetAdded(new StudySetEvent(this, studySet));
                            }
                            mgr.firePropertyChange(new PropertyChangeEvent(this, PROP_STUDYSET_CHANGE, null, mgr.getStudySets()));
                            mgr.firePropertyChange(new PropertyChangeEvent(this, PROP_STUDYSET_ADDED, null, studySet));
                        }
                    });
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (InvocationTargetException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
