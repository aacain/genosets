/*
 * 
 * 
 */
package edu.uncc.genosets.embl.wizard;

import edu.uncc.genosets.bioio.FileSelectorWizardPanel1;
import edu.uncc.genosets.datamanager.embl.EmblTransformer;
import edu.uncc.genosets.datamanager.embl.LoadEmbl;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.wizards.MethodWizardPanel;
import edu.uncc.genosets.taskmanager.AbstractTask;
import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import edu.uncc.genosets.taskmanager.TaskManager;
import edu.uncc.genosets.taskmanager.TaskManagerFactory;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.InstantiatingIterator;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * Load EMBL wizard by Accession numbers A new annotation method is created for
 * each file. The accession and the version are stored in the annotation method.
 *
 * @author aacain
 */
public class EmblLocalWizardIterator implements InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wizard;

    /**
     * Initialize panels representing individual wizard's steps and sets various
     * properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            AnnotationMethod methodConstants = new AnnotationMethod();
            methodConstants.setMethodCategory(org.openide.util.NbBundle.getMessage(EmblLocalWizardIterator.class, "CTL_MethodCategory"));
            methodConstants.setMethodType(org.openide.util.NbBundle.getMessage(EmblLocalWizardIterator.class, "CTL_MethodType"));
            methodConstants.setMethodSourceType("(autoset to data class from file)");
            panels = new WizardDescriptor.Panel[]{
                new FileSelectorWizardPanel1(new String[]{"embl", "txt"}),
                new MethodWizardPanel(methodConstants)
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
                    // Sets step number of a component
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
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

    public Set instantiate() throws IOException {
        AnnotationMethod method = (AnnotationMethod) wizard.getProperty(MethodWizardPanel.PROP_AnnotationMethod);
        Set<File> files = (Set<File>) wizard.getProperty(FileSelectorWizardPanel1.PROP_FILES);
        //Clone the method
        try {
            if (files != null) {
                for (final File file : files) {
                    final AnnotationMethod myMethod = method.clone();
                    AbstractTask task = new AbstractTask("Parsing " + file.getName()) {

                        @Override
                        public void performTask() {
                            try {
                                FileObject fo = FileUtil.toFileObject(file);
                                String emblString = fo.asText();
                                EmblTransformer transformer = EmblTransformer.instantiate();
                                transformer.transform(emblString, myMethod);
                                //start persist task with org dependancy
                                LoadEmbl.LoadEmblImpl.PersistTask persistTask = new LoadEmbl.LoadEmblImpl.PersistTask(transformer);
                                TaskManager mgr = TaskManagerFactory.getDefault();
                                mgr.addPendingTask(persistTask);
                            } catch (IOException ex) {
                                TaskLogFactory.getDefault().log("Could not load EMBL file: " + file.getName(), "EmblLocalWizardIterator", "Could not load EMBL file: " + file.getName(), TaskLog.ERROR, new Date());
                            }
                        }

                        @Override
                        public void uninitialize() {
                        }

                        @Override
                        public void logErrors() {
                        }

                        @Override
                        public Organism getOrganismDependency() {
                            return null;
                        }

                        @Override
                        public void setOrganismDependency(Organism org) {
                        }
                    };
                    TaskManager mgr = TaskManagerFactory.getDefault();
                    mgr.addPendingTask(task);
                }
            }
        } catch (CloneNotSupportedException ex) {
        }
        return Collections.EMPTY_SET;
    }

    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    public void uninitialize(WizardDescriptor wizard) {
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
