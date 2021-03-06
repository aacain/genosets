/*
 * 
 * 
 */
package edu.uncc.genosets.embl.wizard;

import edu.uncc.genosets.datamanager.embl.EmblProject;
import edu.uncc.genosets.datamanager.embl.EmblProjectClient;
import java.awt.Component;
import java.util.Collection;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class ProjectSelectWizardPanel implements WizardDescriptor.Panel {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private ProjectSelectVisualPanel component;
    private WizardDescriptor wd;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            EmblProjectClient client = new EmblProjectClient();
            Collection<EmblProject> emblProjects = client.getEmblProjects();
            component = new ProjectSelectVisualPanel(emblProjects);
            // Sets step number of a component
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, 2);
            // Sets steps names for a panel
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, component.getName());
            // Turn on subtitle creation on each step
            component.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
            // Show steps on the left side with the image on the background
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
            // Turn on numbering of all steps
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
        }
        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }

    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        this.wd.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "All files for an organism may not be included in this list.");
        return true;
        // If it depends on some condition (form filled out...), then:
        // return someCondition();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent();
        // and uncomment the complicated stuff below.
    }

    public final void addChangeListener(ChangeListener l) {
    }

    public final void removeChangeListener(ChangeListener l) {
    }
    /*
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
    public final void addChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.add(l);
    }
    }
    public final void removeChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.remove(l);
    }
    }
    protected final void fireChangeEvent() {
    Iterator<ChangeListener> it;
    synchronized (listeners) {
    it = new HashSet<ChangeListener>(listeners).iterator();
    }
    ChangeEvent ev = new ChangeEvent(this);
    while (it.hasNext()) {
    it.next().stateChanged(ev);
    }
    }
     */

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {
        this.wd = (WizardDescriptor) settings;
    }

    public void storeSettings(Object settings) {
        wd.putProperty("resultList", component.getSelectedRoot());
    }
}
