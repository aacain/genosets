/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer;

import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.TermCalculation;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;

/**
 *
 * @author aacain
 */
public class GoEnrichment {

    private StudySet studySet;
    private final String uniqueName;
    private Boolean needsUpdate;
    private boolean isDefault = false;
    private final FileObject annoFileObject;
    private SoftReference<HashMap<String, TermCalculation>> calcMap;
    private OntologizerParameters params;
    /**
     * Change listeners and change support
     */
    private transient PropertyChangeSupport pcs;
    private transient final PropertyChangeListener ssListener;
    /**
     * Static property fields
     */
    public static final String PROP_NEEDS_UPDATE = "PROP_NEEDS_UPDATE";
    public final static String PROP_IS_DEFAULT = "PROP_IS_DEFAULT";
    private final static String formatString = "#.##";

    /**
     * Constructs a new GOenrichment
     *
     * @param uniqueName - unique name
     * @param studySet - the study set that this enrichment belongs to
     * @param annoFileObject - the primary annotation file
     */
    public GoEnrichment(String uniqueName, StudySet studySet, FileObject annoFileObject) {
        this.uniqueName = uniqueName;
        this.annoFileObject = annoFileObject;
        this.studySet = studySet;
        this.ssListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                studySetChanged(evt);
            }
        };
        studySet.addPropertyChangeListener(WeakListeners.create(PropertyChangeListener.class, ssListener, studySet));
    }

    public StudySet getStudySet() {
        return studySet;
    }

    public void setStudySet(StudySet studySet) {
        this.studySet = studySet;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public synchronized boolean getIsUpdateNeeded() {
        if (this.needsUpdate == null) {
            setNeedsUpdate(checkNeedsUpdate());
        }
        return this.needsUpdate;
    }

    private synchronized void setNeedsUpdate(boolean needsUpdate) {
        Boolean old = this.needsUpdate;
        this.needsUpdate = needsUpdate;
        if (old == null || !old.equals(this.needsUpdate)) {
            firePropertyChange(new PropertyChangeEvent(this, PROP_NEEDS_UPDATE, old, this.needsUpdate));
        }
    }

    private boolean checkNeedsUpdate() {
        OntologizerParameters p = getOntologizerParameters();
        if (p != null) {
            if (p.isMissingPopulationSet()) {
                return true;
            }
            if (p.getDate().before(this.studySet.getLastUpdated())) {
                return true;
            }
            if (p.getPopulationSets() != null) {
                for (StudySet ss : p.getPopulationSets()) {
                    if (p.getDate().before(ss.getLastUpdated())) {
                        return true;
                    }
                }
            } else { //no population sets
                return true;
            }
        }
        return false;
    }

    public synchronized boolean getIsDefault() {
        return isDefault;
    }

    public synchronized void setIsDefault(boolean isDefault) {
        boolean old = this.isDefault;
        this.isDefault = isDefault;
        if (old != this.isDefault) {
            if (this.isDefault) {
                //delete the other default file in this directory
                for (FileObject fo : annoFileObject.getParent().getChildren()) {
                    if (fo.getExt().equals("default")) {
                        GoEnrichment oldDefault = Lookup.getDefault().lookup(EnrichmentServiceProvider.class).find(this.studySet, fo.getName());
                        if (oldDefault != this) {
                            oldDefault.setIsDefault(Boolean.FALSE);
                            //then remove
                            FileLock lock = null;
                            try {
                                lock = fo.lock();
                                fo.delete(lock);
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            } finally {
                                if (lock != null) {
                                    lock.releaseLock();
                                }
                            }
                        }
                    }
                }
                //now create the default file for this object
                try {
                    //create the default file
                    if (FileUtil.findBrother(annoFileObject, "default") == null) {
                        annoFileObject.getParent().createData(annoFileObject.getName(), "default");
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                //now add it to the study set instance content
                Collection<? extends GoEnrichment> all = this.studySet.getLookup().lookupAll(GoEnrichment.class);
                if (all != null) {
                    for (GoEnrichment go : all) {
                        this.studySet.removeInstanceContent(go);
                    }
                }
                //add me
                this.studySet.addInstanceContent(this);
            }//end set as default
            firePropertyChange(new PropertyChangeEvent(this, PROP_IS_DEFAULT, old, this.isDefault));
        }
    }

    public final OntologizerParameters getOntologizerParameters() {
        if (params == null) {
            FileObject details = FileUtil.findBrother(annoFileObject, "details");
            if (details != null) {
                try {
                    this.params = OntologizerParameters.parseParameters(details);
                    if (this.params.getPopulationSets() != null) {
                        for (StudySet ss : this.params.getPopulationSets()) {
                            ss.addPropertyChangeListener(WeakListeners.create(PropertyChangeListener.class, ssListener, ss));
                        }
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return params;
    }

    public void setOntologizerParameters(OntologizerParameters params) {
        this.params = params;
        List<StudySet> popList = this.params.getPopulationSets();
        if (popList != null) {
            for (StudySet ss : popList) {
                ss.addPropertyChangeListener(WeakListeners.create(PropertyChangeListener.class, ssListener, ss));
            }
        }
    }

    public synchronized HashMap<String, TermCalculation> getTermCalculationMap() {
        HashMap<String, TermCalculation> termMap = null;
        if (calcMap == null || calcMap.get() == null) {
            termMap = new HashMap<String, TermCalculation>();
            calcMap = new SoftReference<HashMap<String, TermCalculation>>(termMap);
            //then read file
            readFile(termMap);
        } else {
            termMap = calcMap.get();
        }
        return termMap;
    }

    /**
     * Deletes this GoEnrichment and deletes all associated
     * files.  
     */
    protected final void delete() {
        EnrichmentServiceProvider provider = Lookup.getDefault().lookup(EnrichmentServiceProvider.class);
        List<FileObject> children = Arrays.asList(this.annoFileObject.getParent().getChildren());
        for (FileObject fo : children) {
            if (fo.getName().equals(this.annoFileObject.getName())) {
                FileLock lock = null;
                try {
                    lock = fo.lock();
                    fo.delete(lock);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    if (lock != null) {
                        lock.releaseLock();
                    }
                }
            }
        }
    }

    private void readFile(HashMap<String, TermCalculation> termMap) {
        try {
            parseTableFile(termMap);
            parseAnnoFile(termMap);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void parseTableFile(HashMap<String, TermCalculation> termMap) throws IOException {
        FileObject fo = FileUtil.findBrother(annoFileObject, "table");
        if (fo != null) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(fo.getInputStream()));
                String line = null;
                for (int j = 0; (line = br.readLine()) != null; j++) {
                    String[] ss = line.split("\t");
                    if (j != 0) { // skip header line
                        //parse long value
                        TermCalculation calc = new TermCalculation(ss[0], Integer.parseInt(ss[1]), Integer.parseInt(ss[2]), Integer.parseInt(ss[3]), Integer.parseInt(ss[4]), Integer.parseInt(ss[5]), Integer.parseInt(ss[6]), Integer.parseInt(ss[7]), Boolean.parseBoolean(ss[8]), parseLong(ss[9]), parseLong(ss[10]), parseLong(ss[11]));
                        termMap.put(calc.getTermId(), calc);
                    }
                }
            } catch (IOException ex) {
                throw ex;
            } finally {
                if (br != null) {
                    br.close();
                }
            }
        }
    }

    private void parseAnnoFile(HashMap<String, TermCalculation> termMap) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(annoFileObject.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] ss = line.split("\t");
                if (ss.length == 3) {
                    parseAnnoString(ss[0], new StringBuilder(ss[2]), termMap);
                }
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    private Double parseLong(String s) {
        DecimalFormat format = new DecimalFormat(formatString);
        ParsePosition parsePosition = new ParsePosition(0);
        Number number = format.parse(s, parsePosition);
        if (number == null) {
            return null;
        } else {
            return new Double(number.doubleValue());
        }
    }

    private void parseAnnoString(String featureId, StringBuilder annoString, HashMap<String, TermCalculation> termMap) {
        //get annotations
        int annoStart = annoString.indexOf("annotations={");
        if (annoStart > -1) {
            annoStart = annoStart + 13;
            int annoEnd = annoString.indexOf("}");
            String annos = annoString.substring(annoStart, annoEnd);
            String[] ss = annos.split(",");
            for (String anno : ss) {
                String x = anno;
                TermCalculation term = termMap.get(x);
                term.addFeature(Integer.parseInt(featureId));
            }

            //get parental annotations
            int pStart = annoString.indexOf("parental_annotations={");
            if (pStart > -1) {
                pStart = pStart + 22;
                int pEnd = annoString.lastIndexOf("}");
                String pAnnos = annoString.substring(pStart, pEnd);
                String[] pp = pAnnos.split(",");
                for (String pAnno : pp) {
                    String y = pAnno;
                    TermCalculation term = termMap.get(y);
                    term.addFeature(Integer.parseInt(featureId));
                }
            }
        }
    }
    
    public Set<FileObject> download(FileObject directory, String prefix) throws IOException {
        Set<FileObject> files = new HashSet<FileObject>(3);
        files.add(this.annoFileObject.copy(directory, prefix, "anno"));
        FileObject origTable = FileUtil.findBrother(this.annoFileObject, "table");
        if(origTable == null){
            throw new IOException("GO enrichment table could not be found.");
        }
        files.add(origTable.copy(directory, prefix, "table"));
        return files;
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
        if (this.pcs == null) {
            this.pcs = new PropertyChangeSupport(this);
        }
        this.pcs.addPropertyChangeListener(l);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener l) {
        if (this.pcs == null) {
            return;
        }
        this.pcs.removePropertyChangeListener(l);
    }

    private void firePropertyChange(PropertyChangeEvent evt) {
        if (this.pcs == null) {
            return;
        }
        this.pcs.firePropertyChange(evt);
    }

    @SuppressWarnings("unchecked")
    private void studySetChanged(PropertyChangeEvent evt) {
        if (StudySet.PROP_ADDED_FEATURES.equals(evt.getPropertyName())) {
            setNeedsUpdate(checkNeedsUpdate());
        } else if (StudySet.PROP_DELETED.equals(evt.getPropertyName())) {
            StudySet deletedSS = (StudySet) evt.getNewValue();
            deletedSS.removePropertyChangeListener(ssListener);
            getOntologizerParameters().setMissingPopulationSet(Boolean.TRUE);
            setNeedsUpdate(Boolean.TRUE);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GoEnrichment other = (GoEnrichment) obj;
        if (this.studySet != other.studySet && (this.studySet == null || !this.studySet.equals(other.studySet))) {
            return false;
        }
        if ((this.uniqueName == null) ? (other.uniqueName != null) : !this.uniqueName.equals(other.uniqueName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.studySet != null ? this.studySet.hashCode() : 0);
        hash = 53 * hash + (this.uniqueName != null ? this.uniqueName.hashCode() : 0);
        return hash;
    }
}
