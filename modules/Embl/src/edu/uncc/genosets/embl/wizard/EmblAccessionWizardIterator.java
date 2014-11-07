/*
 * 
 * 
 */
package edu.uncc.genosets.embl.wizard;

import edu.uncc.genosets.datamanager.embl.LoadEmbl;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.wizards.MethodWizardPanel;
import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import java.awt.Component;
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
 * Load EMBL wizard by Accession numbers A new annotation method is created for
 * each file. The accession and the version are stored in the annotation method.
 *
 * @author aacain
 */
public class EmblAccessionWizardIterator implements InstantiatingIterator {

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
            methodConstants.setMethodCategory(org.openide.util.NbBundle.getMessage(EmblAccessionWizardIterator.class, "CTL_MethodCategory"));
            methodConstants.setMethodType(org.openide.util.NbBundle.getMessage(EmblAccessionWizardIterator.class, "CTL_MethodType"));
            methodConstants.setMethodSourceType("(autoset to data class from file)");
            methodConstants.setMethodName("(autoset to accession)");
            panels = new WizardDescriptor.Panel[]{
                new AccessionSelectWizardPanel(),
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
        String accession = (String) wizard.getProperty(WizardConstants.PROP_ACCESSION);
        AnnotationMethod method = (AnnotationMethod) wizard.getProperty(MethodWizardPanel.PROP_AnnotationMethod);
        //seperate
        String ss[] = accession.split("[\n\t\r;,]");
        for (String entry : ss) {
            entry = entry.trim();
            if (entry.length() > 0) {
                String ss2[] = entry.split("-");
                int rangeIndex = -1;
                if (ss2.length == 2) {
                    //Clone the method
                    AnnotationMethod myMethod = method;
                    try {
                        myMethod = method.clone();
                    } catch (CloneNotSupportedException ex) {
                    }
                    myMethod.setMethodName(entry);
                    ss2[0] = ss2[0].trim();
                    ss2[1] = ss2[1].trim();
                    for (int i = 0; rangeIndex < 0 && i < ss2[0].length(); i++) {
                        if (ss2[0].charAt(i) != ss2[1].charAt(i)) {
                            rangeIndex = i;
                        }
                    }
                    try {
                        String prefix = ss2[0].substring(0, rangeIndex);
                        int start = Integer.parseInt(ss2[0].substring(rangeIndex, ss2[0].length()));
                        int end = Integer.parseInt(ss2[1].substring(rangeIndex, ss2[1].length()));
                        StringBuilder bldr = null;
                        try {
                            for (int i = start; i <= end; i++) {
                                bldr = new StringBuilder(Integer.toString(i));
                                while ((prefix.length() + bldr.length()) < ss2[0].length()) {
                                    bldr.insert(0, '0');
                                }
                                bldr.insert(0, prefix);
                                LoadEmbl loadEmbl = LoadEmbl.instantiate();
                                loadEmbl.start(bldr.toString(), myMethod);
                            }
                        } catch (Exception ex) {
                            StringBuilder message = new StringBuilder("There was a problem loading accession: ");
                            message.append(bldr).append(" from range ").append(entry);
                            TaskLogFactory.getDefault().log("EMBL load error", "EmblAccessionWizardIterator", message.toString(), TaskLog.ERROR, new Date());
                        }
                    } catch (Exception ex) {
                        StringBuilder message = new StringBuilder();
                        message.append("There was a problem with the range ").append(entry);
                        TaskLogFactory.getDefault().log("EMBL load error", "EmblAccessionWizardIterator", message.toString(), TaskLog.ERROR, new Date());
                    }
                } else {
                    try {
                        //Clone the method
                        AnnotationMethod myMethod = method;
                        try {
                            myMethod = method.clone();
                        } catch (CloneNotSupportedException ex) {
                        }
                        myMethod.setMethodName(entry);
                        LoadEmbl loadEmbl = LoadEmbl.instantiate();
                        loadEmbl.start(entry, myMethod);
                    } catch (Exception ex) {
                        StringBuilder message = new StringBuilder();
                        message.append("Couldn't load accession: ").append(entry);
                        TaskLogFactory.getDefault().log("EMBL load error", "EmblAccessionWizardIterator", message.toString(), TaskLog.ERROR, new Date());
                    }
                }
            }
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
