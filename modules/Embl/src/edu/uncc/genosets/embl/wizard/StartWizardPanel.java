/*
 * 
 * 
 */
package edu.uncc.genosets.embl.wizard;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

public class StartWizardPanel implements WizardDescriptor.Panel, PropertyChangeListener {

    private ChangeSupport cs = new ChangeSupport(this);
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private StartVisualPanel component;
    private WizardDescriptor wd;
    private String sourceType;
    private boolean isValid = true;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new StartVisualPanel();
            if (component.getProjectsRadioButton().isSelected()) {
                sourceType = WizardConstants.SELECTED_PROJECT;
            } else if (component.getAccessionRadioButton().isSelected()) {
                sourceType = WizardConstants.BY_ACCESSION;
            } else if (component.getFileRadioButton().isSelected()) {
                sourceType = WizardConstants.FROM_FILE;
            }
            // Sets step number of a component
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, 1);
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
        return isValid;
    }

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    @Override
    public void readSettings(Object settings) {
        this.wd = (WizardDescriptor) settings;
        this.component.addPropertyChangeListener(this);
    }

    @Override
    public void storeSettings(Object settings) {
        wd.putProperty("resultList", "holder");
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        this.cs.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        this.cs.removeChangeListener(l);
    }

    @Override
    //Listens for changes to visual panel
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(WizardConstants.PROP_SOURCE_TYPE)) {
            if (!evt.getOldValue().equals(evt.getNewValue())) {
                wd.putProperty(WizardConstants.PROP_SOURCE_TYPE, evt.getNewValue());
                this.cs.fireChange();
            }
        }
    }
}
