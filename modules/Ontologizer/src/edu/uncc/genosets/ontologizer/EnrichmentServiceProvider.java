/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetServiceProvider;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 * Class responsible for registering GO enrichment results with the study sets
 *
 * @author aacain
 */
@ServiceProvider(service = StudySetServiceProvider.class)
public class EnrichmentServiceProvider implements StudySetServiceProvider {

    private HashMap<StudySet, List<GoEnrichment>> enrichments = new HashMap<StudySet, List<GoEnrichment>>();
    private PropertyChangeListener dbChangeListener;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    public static final String PROP_ENRICHMENTS_CHANGED = "PROP_ENRICHMENT_ADDED";
    public static final String PROP_STUDYSET_DELETED = "PROP_STUDYSET_DELETED";
    private String currentDatabase = null;

    /**
     * Initializes the go enrichment for this study set.
     *
     * @param database
     * @param studySet
     */
    @Override
    public void initialize(String database, StudySet studySet) {
        if (currentDatabase == null || !database.equals(currentDatabase)) {
            enrichments = new HashMap<StudySet, List<GoEnrichment>>();
        }
        this.currentDatabase = database;
        registerListeners();
        //get the enrichment folder
        FileObject ssRoot = FileUtil.getConfigFile(database + "/Ontologizer/" + studySet.getUniqueName());
        if (ssRoot != null) {

            List<GoEnrichment> results = Ontologizer.getResults(studySet, ssRoot);
            for (GoEnrichment goEnrichment : results) {
                addEnrichment(studySet, goEnrichment);
            }

            //lock the root folder so that we can get the children
            FileLock lock = null;
            List<FileObject> children = null;
            try {
                while(ssRoot.isLocked()){
                    
                }
                lock = ssRoot.lock();
                children = Arrays.asList(ssRoot.getChildren());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                if (lock != null) {
                    lock.releaseLock();
                }
            }
            if (children != null) {
                boolean hasDefault = false;
                for (FileObject fo : children) {
                    if (fo.getExt().equals("default")) {
                        GoEnrichment find = find(studySet, fo.getName());
                        if (find != null) {
                            find.setIsDefault(Boolean.TRUE);
                            hasDefault = true;
                        }
                    }
                }
                if (!hasDefault && results != null && !results.isEmpty()) {
                    //set the firt to default
                    GoEnrichment get = results.get(0);
                    get.setIsDefault(Boolean.TRUE);
                }
            }
        }
    }

    /**
     * Removes all the GO enrichment files associated with the study set
     *
     * @param database
     * @param studySet to remove
     */
    @Override
    public void studySetDeleted(String database, StudySet studySet) {
        //get the enrichment folder
        FileObject ssRoot = FileUtil.getConfigFile(database + "/Ontologizer/" + studySet.getUniqueName());
        if (ssRoot != null) {
            try {
                ssRoot.delete();
            } catch (IOException ex) {
                Logger.getLogger("edu.uncc.genosets.ontologizer.EnrichmentServiceProvider").log(Level.WARNING, "Could not remove ontologizer enrichment files for deleted study set");
            }
        }
        if (enrichments != null) {
            enrichments.remove(studySet);
        }
        this.pcs.firePropertyChange(PROP_STUDYSET_DELETED, null, studySet);
    }

    public synchronized List<GoEnrichment> getEnrichments(StudySet studySet) {
        if (enrichments != null) {
            return enrichments.get(studySet);
        }
        return null;
    }

    /**
     *     /**
     * Deletes this GoEnrichment and deletes all associated files. This method
     * calls GoEnrichment.delete
     *
     * @param GoEnrichment to delete
     */
    public synchronized void deleteEnrichment(GoEnrichment go) {
        if (enrichments == null) {
            return;
        }
        List<GoEnrichment> list = enrichments.get(go.getStudySet());
        if (list == null) {
            return;
        }
        list.remove(go);
        go.delete();
        this.pcs.firePropertyChange(PROP_ENRICHMENTS_CHANGED, null, go);
    }

    public synchronized void addEnrichment(StudySet set, GoEnrichment enrichment) {
        List<GoEnrichment> e = getEnrichments(set);
        if (e == null) {
            e = new LinkedList<GoEnrichment>();
            enrichments.put(set, e);
        }
        if (!e.contains(enrichment)) {
            e.add(enrichment);
            this.pcs.firePropertyChange(new PropertyChangeEvent(this, PROP_ENRICHMENTS_CHANGED, null, enrichment));
        }
    }

    public synchronized GoEnrichment find(StudySet set, String uniqueName) {
        List<GoEnrichment> e = getEnrichments(set);
        if (e != null) {
            for (GoEnrichment go : e) {
                if (go.getUniqueName().equals(uniqueName)) {
                    return go;
                }
            }
        }
        return null;
    }

    public synchronized void setAsDefault(GoEnrichment enrichment) {
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
        this.pcs.addPropertyChangeListener(l);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
        this.pcs.removePropertyChangeListener(l);
    }

    private void registerListeners() {
        if (dbChangeListener == null) {
            dbChangeListener = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    dataManagerChanged(evt);
                }
            };
            DataManager.getDefault().addPropertyChangeListener(dbChangeListener);
        }
    }

    private synchronized void dataManagerChanged(PropertyChangeEvent evt) {
    }
}
