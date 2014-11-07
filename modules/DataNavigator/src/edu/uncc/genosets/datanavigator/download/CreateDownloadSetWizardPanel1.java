/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.download;

import java.io.File;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.WeakListeners;

public class CreateDownloadSetWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, ChangeListener {

    public static final String PROP_SETNAME = "PROP_SETNAME";
    public static final String PROP_FACTTYPE = "PROP_FACTTYPE";
    public static final String PROP_FOLDER = "PROP_FOLDER";
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private CreateDownloadSetVisualPanel1 component;
    private WizardDescriptor wd;
    private ChangeSupport cs = new ChangeSupport(this);

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public CreateDownloadSetVisualPanel1 getComponent() {
        if (component == null) {
            component = new CreateDownloadSetVisualPanel1();
            component.addChangeListener(WeakListeners.change(this, component));
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        if (component.getSetName() == null || component.getSetName().length() < 1) {
            wd.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "The download set must have a name.");
            return false;
        }
        File f = new File(component.getLocationText());
        if (!(f.exists() && f.isDirectory())) {
            wd.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "Location must be an existing folder");
            return false;
        }
        if (component.getFactType() == null) {
            wd.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "The download set must have a fact type");
            return false;
        }

        wd.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        cs.removeChangeListener(l);
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        this.wd = wiz;
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        this.wd.putProperty(PROP_SETNAME, component.getSetName());
        this.wd.putProperty(PROP_FACTTYPE, component.getFactType());
        this.wd.putProperty(PROP_FOLDER, component.getLocationText());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        cs.fireChange();
    }
}
