/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.connections;

import edu.uncc.genosets.connections.Connection;
import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.datamanager.api.DataManager;
import java.awt.Component;
import java.awt.Dialog;
import java.text.MessageFormat;
import javax.swing.JComponent;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;

@ActionID(id = "edu.uncc.genosets.datamanager.connections.ConnectionWizardAction", category = "Database")
@ActionRegistration(iconInMenu = true, displayName = "#CTL_ConnectionWizardAction")
@ActionReference(path = "Menu/Database", position = 300)
public final class ConnectionWizardAction extends CallableSystemAction {

    private WizardDescriptor.Panel[] panels;

    public void performAction() {
        ConnectionWizardIterator it = new ConnectionWizardIterator();
        WizardDescriptor wizardDescriptor = new WizardDescriptor(it);
        it.initialize(wizardDescriptor);
        //WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels());
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
        wizardDescriptor.setTitle("Connection Wizard");
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            DataManager mgr = DataManager.getDefault();
            ConnectionManager connMgr = ConnectionManagerFactory.getConnectionManager();
            Connection connection = (Connection) wizardDescriptor.getProperty(WizardConstants.PROP_CONNECTION);
            int mode = (Integer) wizardDescriptor.getProperty(WizardConstants.PROP_MODE);
            if (mode == ConnectionWizardPanel1.MODE_NEW) {
                String url = (String) wizardDescriptor.getProperty(WizardConstants.PROP_URL);
                String user = (String) wizardDescriptor.getProperty(WizardConstants.PROP_USER);
                String password = (String) wizardDescriptor.getProperty(WizardConstants.PROP_PASSWORD);
                String connName = (String) wizardDescriptor.getProperty(WizardConstants.PROP_CONNECTION_NAME);
                String connType = (String) wizardDescriptor.getProperty(WizardConstants.PROP_TYPE);
                boolean isDefault = (Boolean) wizardDescriptor.getProperty(WizardConstants.PROP_IS_DEFAULT);
                boolean isSavePass = (Boolean) wizardDescriptor.getProperty(WizardConstants.PROP_SAVE_PASSWORD);
                connection = connMgr.createConnection(connName, url, user, password, connType, isSavePass, isDefault);
            } else if (mode == ConnectionWizardPanel1.MODE_EDIT) {
                String user = (String) wizardDescriptor.getProperty(WizardConstants.PROP_USER);
                String password = (String) wizardDescriptor.getProperty(WizardConstants.PROP_PASSWORD);
                String connName = (String) wizardDescriptor.getProperty(WizardConstants.PROP_CONNECTION_NAME);
                boolean isDefault = (Boolean) wizardDescriptor.getProperty(WizardConstants.PROP_IS_DEFAULT);
                boolean isSavePass = (Boolean) wizardDescriptor.getProperty(WizardConstants.PROP_SAVE_PASSWORD);
                connMgr.updateConnectionName(connection, connName);
                connMgr.updateUserName(connection, user);
                connMgr.updatePassword(connection, password, isSavePass);
                if (isDefault) {
                    connMgr.setDefaultConnection(connection);
                }
            }
            final Connection myConnection = connection;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        DataManager.openConnection(myConnection);
                    } catch (InvalidConnectionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            };

            ProgressHandle handle = ProgressHandleFactory.createHandle("Connecting...");
            ProgressUtils.showProgressDialogAndRun(runnable, handle, true);

        }
        panels = null;
    }

    /**
     * Initialize panels representing individual wizard's steps and sets various
     * properties for them influencing wizard appearance.
     *
     * Now use ConnectionManagerIterator for dynamic content.
     */
    @Deprecated
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                new ConnectionWizardPanel1(),
                new ConnectionWizardPanel2()
            };
            String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    public String getName() {
        return org.openide.util.NbBundle.getMessage(ConnectionVisualPanel1.class, "CTL_ConnectionWizardAction");
    }

    @Override
    public String iconResource() {
        return null;
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }
}
