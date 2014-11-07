/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.connections;

import edu.uncc.genosets.connections.Connection;
import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.connections.NoSuchDatabaseException;
import edu.uncc.genosets.connections.SchemaInvalidException;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.connections.ConnectionNodeFactory.ConnectionNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.*;

public class ConnectionWizardPanel1 implements WizardDescriptor.AsynchronousValidatingPanel {

    protected static int MODE_NORMAL = 0;
    protected static int MODE_EDIT = 2;
    protected static int MODE_NEW = 3;
    protected static int MODE_REMOVE = 5;
    private ConnectionVisualPanel1 component;
    private Lookup.Result<Connection> result;
    private ConnectionManager connectMgr;
    private int currentMode = 0;
    private WizardDescriptor wd;
    private boolean connectionValid = true;

    public ConnectionVisualPanel1 getComponent() {
        if (component == null) {
            connectMgr = ConnectionManagerFactory.getConnectionManager();
            component = new ConnectionVisualPanel1(connectMgr);
            //set listeners
            result = component.getLookup().lookupResult(Connection.class);
            result.addLookupListener(new LookupListener() {
                @Override
                public void resultChanged(LookupEvent ev) {
                    setFieldsByConnection();
                }
            });
            DocumentListener docListener = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    inputChanged();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    inputChanged();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    //plain text components don't use this.
                }
            };
            this.component.getURLField().getDocument().addDocumentListener(docListener);
            this.component.getUsernameField().getDocument().addDocumentListener(docListener);
            this.component.getPasswordField().getDocument().addDocumentListener(docListener);
            this.component.getConnectionNameField().getDocument().addDocumentListener(docListener);

            //edit button
            this.component.getEditButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setMode(MODE_EDIT);
                }
            });
            //new button
            this.component.getNewButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setMode(MODE_NEW);
                }
            });
            //remove button
            this.component.getRemoveButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setMode(MODE_REMOVE);
                }
            });

            this.component.getTypeComboBox().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JComboBox box = (JComboBox) e.getSource();
                    String selectedType = (String) box.getSelectedItem();
                    setSelectedType(selectedType);
                }
            });

            this.component.getDefaultConnectionCheckBox().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireChangeEvent();
                }
            });

            this.component.getSavePasswordCheckBox().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireChangeEvent();
                }
            });

            setSelectedType((String) this.component.getTypeComboBox().getSelectedItem());
            setMode(MODE_NORMAL);
        }

        return component;
    }

    private void setSelectedType(String selectedType) {
        Connection selectedConnection = getComponent().getLookup().lookup(Connection.class);
        if (selectedConnection == null || currentMode == MODE_NEW) {
            if (selectedType.equals(Connection.TYPE_DIRECT_DB)) {
                this.component.getURLField().setText("localhost:3306/dbName");
            } else if (selectedType.equals(Connection.TYPE_WEB_SERVICE)) {
                this.component.getURLField().setText("http://localhost:8084/GenoSetsServer/");
            }
        }
    }

    private void inputChanged() {
        if (!connectionValid) {
            connectionValid = true;
            fireChangeEvent();
        }
    }

    private void setFieldsByConnection() {
        Connection selectedConnection = getComponent().getLookup().lookup(Connection.class);
        if (currentMode == MODE_NEW) {
            selectedConnection = null;
        }
        if (selectedConnection == null) {
            //set fields null
            this.component.getURLField().setText("");
            this.component.getUsernameField().setText("");
            this.component.getPasswordField().setText("");
            this.component.getConnectionNameField().setText("");
            this.component.getDefaultConnectionCheckBox().setSelected(Boolean.FALSE);
            this.component.getSavePasswordCheckBox().setSelected(Boolean.FALSE);
            this.component.getTypeComboBox().setSelectedItem(Connection.TYPE_DIRECT_DB);
        } else {
            this.component.getURLField().setText(selectedConnection.getUrl());
            this.component.getUsernameField().setText(selectedConnection.getUserName());
            this.component.getPasswordField().setText(selectedConnection.getPassword());
            this.component.getConnectionNameField().setText(selectedConnection.getConnectionName());
            this.component.getTypeComboBox().setSelectedItem(selectedConnection.getConnectionType());
            this.component.getDefaultConnectionCheckBox().setSelected(selectedConnection.isIsDefault());
            this.component.getSavePasswordCheckBox().setSelected(selectedConnection.getPassword() == null ? Boolean.FALSE : Boolean.TRUE);
            setMode(MODE_NORMAL);
        }
    }

    private void setMode(int mode) {
        currentMode = mode;
        if (MODE_EDIT == mode) {
            this.component.getEditButton().setEnabled(false);
            this.component.getNewButton().setEnabled(false);
            this.component.getRemoveButton().setEnabled(false);

            //set field enabled
            this.component.getURLField().setEnabled(false);
            this.component.getUsernameField().setEnabled(true);
            this.component.getPasswordField().setEnabled(true);
            this.component.getConnectionNameField().setEnabled(true);
            this.component.getTypeComboBox().setEnabled(false);
            this.component.getSavePasswordCheckBox().setEnabled(true);
            this.component.getDefaultConnectionCheckBox().setEnabled(true);
            //disable connection pane
            this.component.getListView().setEnabled(false);
        } else if (MODE_NEW == mode) {
            this.component.getEditButton().setEnabled(false);
            this.component.getNewButton().setEnabled(false);
            this.component.getRemoveButton().setEnabled(false);
            //set field enabled
            this.component.getURLField().setEnabled(true);
            this.component.getUsernameField().setEnabled(true);
            this.component.getPasswordField().setEnabled(true);
            this.component.getConnectionNameField().setEnabled(true);
            this.component.getTypeComboBox().setEnabled(true);
            this.component.getListView().setEnabled(false);
            this.component.getSavePasswordCheckBox().setEnabled(true);
            this.component.getDefaultConnectionCheckBox().setEnabled(true);
            this.setFieldsByConnection();
        } else if (MODE_NORMAL == mode) {
            Connection selectedConnection = getComponent().getLookup().lookup(Connection.class);
            if (selectedConnection == null) {
                this.component.getEditButton().setEnabled(false);
                this.component.getNewButton().setEnabled(true);
                this.component.getRemoveButton().setEnabled(false);
            } else {
                this.component.getEditButton().setEnabled(true);
                this.component.getNewButton().setEnabled(true);
                this.component.getRemoveButton().setEnabled(true);
            }
            //set field disabled
            this.component.getURLField().setEnabled(false);
            this.component.getUsernameField().setEnabled(false);
            this.component.getPasswordField().setEnabled(false);
            this.component.getConnectionNameField().setEnabled(false);
            this.component.getTypeComboBox().setEnabled(false);
            this.component.getSavePasswordCheckBox().setEnabled(false);
            this.component.getDefaultConnectionCheckBox().setEnabled(false);
            //set connection list enabled
            this.component.getListPane().setEnabled(true);
            this.component.getListView().setEnabled(true);
        } else if (MODE_REMOVE == mode) {
            removeNode();
        }
        fireChangeEvent();
    }

    private void removeNode() {
        Connection selectedConnection = getComponent().getLookup().lookup(Connection.class);
        ConnectionNode node = getComponent().getLookup().lookup(ConnectionNode.class);
        try {
            node.destroy();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        if (selectedConnection != null) {
            connectMgr.removeConnection(selectedConnection);
        }
        setMode(MODE_NORMAL);
    }

    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    public boolean isValid() {
        if (this.component.getDefaultConnectionCheckBox().isSelected() && !this.component.getSavePasswordCheckBox().isSelected()) {
            this.wd.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "You must save the password to set this connection as the default connection.");
            return false;
        }
        if (!connectionValid) {
            return false;
        }
        if (MODE_NORMAL == currentMode) {
            Connection selectedConnection = getComponent().getLookup().lookup(Connection.class);
            if (selectedConnection == null) {
                this.wd.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "Select a connection.");
                return false;
            }
        }
        if (MODE_NEW == currentMode) {
            //TODO: check if all fields are entered
        }
        this.wd.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
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
    public synchronized void readSettings(Object settings) {
        this.wd = (WizardDescriptor) settings;
        this.wd.putProperty(WizardConstants.PROP_NEEDS_UPDATE, Boolean.FALSE);
        this.wd.putProperty(WizardConstants.PROP_NO_DB_EXISTS, Boolean.FALSE);
    }

    public synchronized void storeSettings(Object settings) {
        this.wd.putProperty(WizardConstants.PROP_MODE, currentMode);
        if (MODE_NORMAL == currentMode || MODE_EDIT == currentMode) {
            Connection selectedConnection = getComponent().getLookup().lookup(Connection.class);
            this.wd.putProperty(WizardConstants.PROP_CONNECTION, selectedConnection);
        }
        if (MODE_EDIT == currentMode || MODE_NEW == currentMode || MODE_NORMAL == currentMode) {
            this.wd.putProperty(WizardConstants.PROP_URL, this.component.getURLField().getText());
            this.wd.putProperty(WizardConstants.PROP_USER, this.component.getUsernameField().getText());
            this.wd.putProperty(WizardConstants.PROP_PASSWORD, this.component.getPasswordField().getText());
            this.wd.putProperty(WizardConstants.PROP_CONNECTION_NAME, this.component.getConnectionNameField().getText());
            this.wd.putProperty(WizardConstants.PROP_TYPE, (String) (this.component.getTypeComboBox().getSelectedItem()));
            this.wd.putProperty(WizardConstants.PROP_IS_DEFAULT, this.component.getDefaultConnectionCheckBox().isSelected());
            this.wd.putProperty(WizardConstants.PROP_SAVE_PASSWORD, this.component.getSavePasswordCheckBox().isSelected());
        }
    }

    @Override
    public void validate() throws WizardValidationException {
        final int mode = currentMode;
        final String url = (String) wd.getProperty(WizardConstants.PROP_URL);
        final String user = (String) wd.getProperty(WizardConstants.PROP_USER);
        final String password = (String) wd.getProperty(WizardConstants.PROP_PASSWORD);
        final String type = (String) wd.getProperty(WizardConstants.PROP_TYPE);
        final Connection selectedConnection = (Connection) wd.getProperty(WizardConstants.PROP_CONNECTION);
        try {
            if (ConnectionWizardPanel1.MODE_NORMAL == mode) {
                DataManager.testConnection(selectedConnection);
            } else if (ConnectionWizardPanel1.MODE_NEW == mode || ConnectionWizardPanel1.MODE_EDIT == mode) {
                ConnectionManagerFactory.getConnectionManager().testConnection(url, user, password, type);
            }
            this.connectionValid = Boolean.TRUE;
            this.wd.putProperty(WizardConstants.PROP_NEEDS_UPDATE, Boolean.FALSE);
            this.wd.putProperty(WizardConstants.PROP_NO_DB_EXISTS, Boolean.FALSE);
            this.wd.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        } catch (SchemaInvalidException ex) {
            this.wd.putProperty(WizardConstants.PROP_NEEDS_UPDATE, Boolean.TRUE);
            if (!type.equals(Connection.TYPE_DIRECT_DB)) {
                throw new WizardValidationException(component, ex.getMessage(), ex.getLocalizedMessage());
            }
        } catch (NoSuchDatabaseException ex) {
            this.wd.putProperty(WizardConstants.PROP_NO_DB_EXISTS, Boolean.TRUE);
            if (!type.equals(Connection.TYPE_DIRECT_DB)) {
                throw new WizardValidationException(component, ex.getMessage(), ex.getLocalizedMessage());
            }
        } catch (InvalidConnectionException ex) {
            throw new WizardValidationException(component, ex.getMessage(), ex.getLocalizedMessage());
        }finally{
            this.wd.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, null);
        }
    }

    @Override
    public void prepareValidation() {
        storeSettings(this);
        this.connectionValid = Boolean.FALSE;
        this.wd.putProperty(WizardDescriptor.PROP_INFO_MESSAGE, "Testing connection.");
        this.fireChangeEvent();

    }
}
