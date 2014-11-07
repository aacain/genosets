/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.pathway;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.WeakListeners;

public class LoadPathwayWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private LoadPathwayVisualPanel1 component;
    private ChangeSupport cs = new ChangeSupport(this);
    public static final String PROP_LOCATION_ID = "PROP_LOCATION_ID";
    public static final String PROP_PATH_NAME_COLUMN = "PROP_PATH_NAME_COLUMN";
    public static final String PROP_PATH_ID_COLUMN = "PROP_PATH_ID_COLUMN";
    private Pattern pattern = Pattern.compile("[0-9]+");

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public LoadPathwayVisualPanel1 getComponent() {
        if (component == null) {
            component = new LoadPathwayVisualPanel1();
            component.addPropertyChangeListener(WeakListeners.create(PropertyChangeListener.class, this, component));
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
        if(!pattern.matcher(component.getLocationIdColumn().getText()).matches() || 
                !pattern.matcher(component.getPathIdColumn().getText()).matches() || 
                !pattern.matcher(component.getPathNameColumn().getText()).matches()){
            return false;
        }
        return true;

    }

    @Override
    public void addChangeListener(ChangeListener l) {
        this.cs.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        this.cs.addChangeListener(l);
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        wiz.putProperty(PROP_LOCATION_ID, Integer.parseInt(component.getLocationIdColumn().getText()));
        wiz.putProperty(PROP_PATH_ID_COLUMN, Integer.parseInt(component.getPathIdColumn().getText()));
        wiz.putProperty(PROP_PATH_NAME_COLUMN, Integer.parseInt(component.getPathNameColumn().getText()));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.cs.fireChange();
    }
}
