/*
 * 
 * 
 */
package edu.uncc.genosets.embl.wizard;

import java.awt.Component;
import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;

public final class EmblWizardIterator implements WizardDescriptor.InstantiatingIterator, ChangeListener {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wizard;
    private ChangeSupport cs = new ChangeSupport(this);
    //constants
    public static final int INDEX_START = 0;
    public static final int INDEX_PROJECTSELECT = 1;
    public static final int INDEX_CONFIRM = 2;

    /**
     * Initialize panels representing individual wizard's steps and sets
     * various properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {

        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                        new ProjectSelectWizardPanel()
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

    public Set instantiate() throws IOException {
        try{Thread.sleep(30000);}catch(Exception e){}
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

    // If nothing unusual changes in the middle of the wizard, simply:
    public void addChangeListener(ChangeListener l) {
        this.cs.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        this.cs.removeChangeListener(l);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    }

    protected final void fireChangeEvent() {
        cs.fireChange();
    }

    private String[] createSteps() {

//        String[] res = new String[(beforeSteps.length - 2) + panels.length];
//        for (int i = 0; i < res.length; i++) {
//            if (i < (beforeSteps.length - 1)) {
//                res[i] = beforeSteps[i];
//            } else {
//                res[i] = panels[i - beforeSteps.length + 1].getComponent().getName();
//            }
//            res[i] = res[i] + "hello";
//        }
        String[] res = new String[panels.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = panels[i].getComponent().getName();
        }
        return res;
    }
}
