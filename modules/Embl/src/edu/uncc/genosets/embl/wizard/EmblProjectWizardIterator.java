/*
 * 
 * 
 */
package edu.uncc.genosets.embl.wizard;

import edu.uncc.genosets.datamanager.embl.LoadEmbl;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.wizards.MethodWizardPanel;
import edu.uncc.genosets.taskmanager.TaskManager;
import edu.uncc.genosets.taskmanager.TaskManagerFactory;
import java.awt.Component;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.InstantiatingIterator;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 * Loads EMBL from project list.
 * Creates a new 
 * @author aacain
 */
public class EmblProjectWizardIterator implements InstantiatingIterator {

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
            methodConstants.setMethodCategory(org.openide.util.NbBundle.getMessage(EmblAccessionWizardIterator.class, "CTL_MethodCategory"));
            methodConstants.setMethodType(org.openide.util.NbBundle.getMessage(EmblAccessionWizardIterator.class, "CTL_MethodType"));
            methodConstants.setMethodSourceType("(autoset to data class from file)");
            methodConstants.setMethodName("accession");
            panels = new WizardDescriptor.Panel[]{
                new ProjectSelectWizardPanel(),
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
        Node root = (Node) wizard.getProperty("resultList");
        AnnotationMethod method = (AnnotationMethod) wizard.getProperty(MethodWizardPanel.PROP_AnnotationMethod);
        Children children = root.getChildren();
        List<Node> snapshot = children.snapshot();
        TaskManager mgr = TaskManagerFactory.getDefault();
        for (Node node : snapshot) {
            //Clone the method
            AnnotationMethod myMethod = method;
            try {
                myMethod = method.clone();
            } catch (CloneNotSupportedException ex) {
                Exceptions.printStackTrace(ex);
            }
            if (node instanceof EmblProjectNode) {
                EmblProjectNode eNode = (EmblProjectNode) node;
                LoadEmbl loadEmbl = LoadEmbl.instantiate();
                myMethod.setMethodName(eNode.getName());
                loadEmbl.start(eNode.getName(), myMethod);
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
