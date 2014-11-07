/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.fasta.load;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.wizards.MethodWizardPanel;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.WeakListeners;

public class FastaNucWizardPanel1 implements WizardDescriptor.Panel, PropertyChangeListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private FastaNucVisualPanel1 component;
    private WizardDescriptor wd;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new FastaNucVisualPanel1();
            component.addPropertyChangeListener(WeakListeners.propertyChange(this, component));
        }
        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return new HelpCtx("edu.uncc.genosets.gffloader.general");
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }

    public boolean isValid() {
        File file = component.getFile();
        if(file == null || !file.exists()){
            this.wd.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "File not found");
            return false;
        }
        if(component.getOrganism() == null){
            this.wd.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "Select organism");
            return false;
        }
        this.wd.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        
        return true;
    }
    
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

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {
        this.wd = (WizardDescriptor) settings;
    }

    public void storeSettings(Object settings) {
        this.wd.putProperty(WizardConstants.PROP_ORGANISM, component.getOrganism());
        this.wd.putProperty(WizardConstants.PROP_FILE, component.getFile());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        fireChangeEvent();
    }
}
