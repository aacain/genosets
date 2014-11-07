/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.fasta.load;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.wizards.MethodWizardPanel;
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

/**
 *
 * @author aacain
 */
public class FastaNucleotideWizardIterator implements InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wizard;

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            AnnotationMethod methodConstants = new AnnotationMethod();
            methodConstants.setMethodCategory("Acquisition");
            methodConstants.setMethodType("Assembled Unit");
            methodConstants.setMethodSourceType("Autoset as filename");
            wizard.putProperty(MethodWizardPanel.PROP_AnnotationMethod, methodConstants);
            panels = new WizardDescriptor.Panel[]{
                new FastaNucWizardPanel1(),
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
        Organism org = (Organism) wizard.getProperty(WizardConstants.PROP_ORGANISM);
        File file = (File) wizard.getProperty(WizardConstants.PROP_FILE);
        AnnotationMethod method = (AnnotationMethod) wizard.getProperty(MethodWizardPanel.PROP_AnnotationMethod);
        method.setRunDate(new Date(file.lastModified()));
        method.setLoadDate(new Date());
        FastaLoadTask task = new FastaLoadTask(org, method, file);
        TaskManager mgr = TaskManagerFactory.getDefault();
        mgr.addPendingTask(task);
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
