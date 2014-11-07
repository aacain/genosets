/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.connections;

import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.datamanager.api.DataManagerJDBC;
import edu.uncc.genosets.datamanager.api.DatabaseMigrationException;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.WizardDescriptor;
import org.openide.util.*;

public class ConnectionUpdatePanel implements WizardDescriptor.Panel {

    private ConnectionUpdateVisualPanel component;
    private WizardDescriptor wd;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public ConnectionUpdateVisualPanel getComponent() {
        if (component == null) {
            component = new ConnectionUpdateVisualPanel();
            component.getUpdateButton().addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    updateButtonActionPerformed(evt);
                }
            });
            fireChangeEvent();
        }

        return component;
    }

    private void updateButtonActionPerformed(ActionEvent evt) {
        if ((Boolean) this.wd.getProperty(WizardConstants.PROP_NEEDS_UPDATE)) {
            final String url = (String) wd.getProperty(WizardConstants.PROP_URL);
            final String user = (String) wd.getProperty(WizardConstants.PROP_USER);
            final String password = (String) wd.getProperty(WizardConstants.PROP_PASSWORD);
            //create a new runnable thread that will execute this long running task
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        DataManagerJDBC.updateSchema(url, user, password);
                        wd.putProperty(WizardConstants.PROP_NEEDS_UPDATE, Boolean.FALSE);
                        wd.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
                        getComponent().getUpdateButton().setEnabled(false);
                    } catch (DatabaseMigrationException ex) {
                        wd.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "Could not update schema.");
                    } catch (InvalidConnectionException ex) {
                        wd.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "Connection is not valid");
                    } finally {
                        fireChangeEvent();
                    }
                }
            };//end runnable
            ProgressHandle handle = ProgressHandleFactory.createHandle("Updating...");
            this.wd.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Updating");
            ProgressUtils.showProgressDialogAndRun(runnable, handle, true);
            this.wd.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
            this.wd.doNextClick();
        }
    }

    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    public boolean isValid() {
        return !(Boolean) this.wd.getProperty(WizardConstants.PROP_NEEDS_UPDATE);
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

            ChangeEvent ev = new ChangeEvent(this);
            while (it.hasNext()) {
                it.next().stateChanged(ev);
            }
        }
    }

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {
        this.wd = (WizardDescriptor) settings;
        String user = (String) this.wd.getProperty(WizardConstants.PROP_USER);
        String password = (String) this.wd.getProperty(WizardConstants.PROP_PASSWORD);
        Boolean needsUpdate = (Boolean) this.wd.getProperty(WizardConstants.PROP_NEEDS_UPDATE);
        getComponent().getUserField().setText(user == null ? "" : user);
        getComponent().getPasswordField().setText(password == null ? "" : password);
        getComponent().getUpdateButton().setEnabled(needsUpdate);
    }

    public void storeSettings(Object settings) {
    }
}
