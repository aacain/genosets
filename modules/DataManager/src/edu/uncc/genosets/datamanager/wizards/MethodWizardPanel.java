/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.wizards;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.awt.Component;
import java.util.Date;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

public class MethodWizardPanel implements WizardDescriptor.Panel<WizardDescriptor>, ChangeListener {

    private final AnnotationMethod methodConstants;
    private final ChangeSupport cs = new ChangeSupport(this);
    public static final String PROP_AnnotationMethod = "PROP_AnnotationMethod";

    /**
     * The method constants to be used in the panel. All values in the
     * Annotation Method will appear to the user but will not be editable. The
     * store settings method will create a copy of the methodConstants and store
     * it in the wizard descriptor as PROP_AnnotationMethod.
     *
     * @param methodConstants
     */
    public MethodWizardPanel(AnnotationMethod methodConstants) {
        this.methodConstants = methodConstants;
    }
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private MethodVisualPanel component;
    private WizardDescriptor wd;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new MethodVisualPanel(methodConstants);
            // Sets step number of a component
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, 3);
            // Sets steps names for a panel
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, component.getName());
            // Turn on subtitle creation on each step
            component.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
            // Show steps on the left side with the image on the background
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
            // Turn on numbering of all steps
            component.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
            component.addChangeListener(this);
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
        if(component.getNameField() == null || component.getNameField().getText().trim().length() == 0){
            this.wd.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "Enter a valid name.");
            return false;
        }
        this.wd.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        return true;
    }

    public final void addChangeListener(ChangeListener l) {
        synchronized (cs) {
            this.cs.addChangeListener(l);
        }
    }

    public final void removeChangeListener(ChangeListener l) {
        synchronized (cs) {
            this.cs.removeChangeListener(l);
        }
    }

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(WizardDescriptor settings) {
        this.wd = settings;
        MethodVisualPanel c = (MethodVisualPanel) getComponent();
        c.initMethod(methodConstants);
    }

    /**
     * Returns a copy of the method created
     *
     * @param settings
     */
    public void storeSettings(WizardDescriptor settings) {
        AnnotationMethod method = new AnnotationMethod();
        method.setMethodCategory(component.getCategoryField().getText());
        method.setMethodType(component.getTypeField().getText());
        method.setMethodSourceType(component.getSourceField().getText());
        method.setMethodName(component.getNameField().getText());
        method.setMethodDescription(component.getDescriptionArea().getText());
        method.setRunDate(new Date());
        wd.putProperty(MethodWizardPanel.PROP_AnnotationMethod, method);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.cs.fireChange();
    }
}
