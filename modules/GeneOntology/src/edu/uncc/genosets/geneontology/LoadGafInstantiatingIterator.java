/*
 * 
 * 
 */
package edu.uncc.genosets.geneontology;

import edu.uncc.genosets.geneontology.obo.OboWizardPanel;
import bioio.GoAnnotationFileFormat;
import edu.uncc.genosets.bioio.FileSelectorWizardPanel1;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.wizards.MethodWizardPanel;
import edu.uncc.genosets.geneontology.api.GeneOntology;
import edu.uncc.genosets.geneontology.obo.OboDataObject;
import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import edu.uncc.genosets.taskmanager.TaskManagerFactory;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressRunnable;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.InstantiatingIterator;
import org.openide.util.Exceptions;

/**
 *
 * @author aacain
 */
public class LoadGafInstantiatingIterator implements InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wizard;

    /**
     * Initialize panels representing individual wizard's steps and sets various
     * properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            String[] exts = {"gaf", "GAF"};
            //set AnnotationMethod constants
            AnnotationMethod method = new AnnotationMethod();
            method.setMethodCategory("GO Annotation");
            method.setMethodType("Gene Ontology");
            Date date = new Date();
            method.setLoadDate(date);
            method.setRunDate(date);
            panels = new WizardDescriptor.Panel[]{
                new FileSelectorWizardPanel1(exts),
                new OboWizardPanel(),
                //new OboSelectWizardPanel(Collections.singleton("FULL"), Boolean.TRUE),
                new SelectGafVersionWizardPanel1(),
                new MethodWizardPanel(method)
            };
            String[] steps = createSteps();
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                // Default step name to component name of panel.
                if (steps[i] == null) {
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    private String[] createSteps() {
        String[] res = new String[panels.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = panels[i].getComponent().getName();
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    public Set instantiate()  {
        AnnotationMethod method = (AnnotationMethod) this.wizard.getProperty(MethodWizardPanel.PROP_AnnotationMethod);
        //final OboDataObject obodao = (OboDataObject) this.wizard.getProperty(OboSelectWizardPanel.PROP_OBO_DATAOBJECT);
        final OboDataObject oboFile = (OboDataObject)this.wizard.getProperty(OboWizardPanel.PROP_OBO);
        Set<File> files = (Set<File>) this.wizard.getProperty(FileSelectorWizardPanel1.PROP_FILES);
        Integer format = (Integer) this.wizard.getProperty(SelectGafVersionWizardPanel1.PROP_GAF_VERSION);

        //load the obo
        ProgressRunnable<edu.uncc.genosets.geneontology.api.GoTermPersister> runnable = new ProgressRunnable<edu.uncc.genosets.geneontology.api.GoTermPersister>() {
            @Override
            public edu.uncc.genosets.geneontology.api.GoTermPersister run(ProgressHandle handle) {
                handle.switchToDeterminate(100);
                edu.uncc.genosets.geneontology.api.GoTermPersister termPersister = null;
                try {
                    // Connect
                    InputStream is = oboFile.getFileObject(handle).getInputStream();
                    termPersister = edu.uncc.genosets.geneontology.api.GoTermPersister.instantiate(is, handle);
                    //termPersister = edu.uncc.genosets.geneontology.api.GoTermPersister.instantiate(obodao.getPrimaryInputStream(), handle);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                return termPersister;
            }
        };
        edu.uncc.genosets.geneontology.api.GoTermPersister termPersister = ProgressUtils.showProgressDialogAndRun(runnable, "Loading obo file", true);

        //parse the gaf file
        for (File file : files) {
            try {
                FileInputStream is = new FileInputStream(file);
                GoAnnotationFileFormat gaf = GoAnnotationFileFormat.readAnnotations(is, format);
                if (termPersister != null) {
                    try {
                        AnnotationMethod myMethod = method.clone();
                        GeneOntology go = edu.uncc.genosets.geneontology.api.GeneOntology.instantiate(termPersister, myMethod, gaf, file.getName());
                        TaskManagerFactory.getDefault().addPendingTask(go);
                    } catch (CloneNotSupportedException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            } catch (IOException ex) {
                TaskLogFactory.getDefault().log("Could not load Go Ontology annotations for " + file.getName(), LoadGafInstantiatingIterator.class.getName(), ex.getMessage(), TaskLog.ERROR, new Date());
                Exceptions.printStackTrace(ex);
            }
        }
        return Collections.EMPTY_SET;
    }

    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    public void uninitialize(WizardDescriptor wizard) {
        index = 0;
        this.wizard = null;
        panels = null;
    }

    public WizardDescriptor.Panel current() {
        return getPanels()[index];
    }

    public String name() {
        return index + 1 + ". from " + getPanels().length;
    }

    public boolean hasNext() {
        return index < getPanels().length - 1;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
}
