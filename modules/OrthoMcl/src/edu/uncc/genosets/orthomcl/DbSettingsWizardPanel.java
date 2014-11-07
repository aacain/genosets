/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.orthomcl;

import edu.uncc.genosets.datamanager.api.DatabaseValidator;
import static edu.uncc.genosets.orthomcl.OrthoMclScriptWizardIterator.PROP_ORTHOMCL_FORMAT;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Pattern;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.NbPreferences;

public class DbSettingsWizardPanel implements WizardDescriptor.Panel<WizardDescriptor>, ChangeListener, DocumentListener {

    private transient final ChangeSupport cs = new ChangeSupport(this);
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private DbSettingsVisualPanel component;
    private WizardDescriptor wiz;
    private boolean binExists;
    private boolean needsValidation = true;
    private boolean connectionValid = true;
    private ImageIcon validIcon;
    private ImageIcon errorIcon;
    Pattern dbNamePattern = Pattern.compile("^[a-z]+[\\w]+");
    private OrthoMclFormat format;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public DbSettingsVisualPanel getComponent() {
        if (component == null) {
            component = new DbSettingsVisualPanel();
            validIcon = ImageUtilities.loadImageIcon("edu/uncc/genosets/icons/check.png", false);
            errorIcon = ImageUtilities.loadImageIcon("edu/uncc/genosets/icons/error.png", false);
            component.getUserNameText().getDocument().addDocumentListener(this);
            component.getPasswordText().getDocument().addDocumentListener(this);
            component.getDatabaseNameText().getDocument().addDocumentListener(this);
            component.getBrowseButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    browseButtonSelected();
                }
            });

            component.getDropCreateDatabase().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    component.getMysqlBin().setEnabled(component.getDropCreateDatabase().isSelected());
                    component.getBrowseButton().setEnabled(component.getDropCreateDatabase().isSelected());
                }
            });

            component.getMysqlBin().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkBin(component.getMysqlBin().getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    checkBin(component.getMysqlBin().getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    checkBin(component.getMysqlBin().getText());
                }
            });
            
            component.getValidateButton().addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                     validateConnection();
                }
               
            });
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("edu.uncc.genosets.orthomcl.run-general");
    }

    @Override
    public boolean isValid() {
        if (needsValidation) {
            getComponent().getValidateLabel().setIcon(null);
            if (component.getDatabaseNameText().getText().length() == 0) {
                this.wiz.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "You must set the database name.");
                return false;
            }else{
                if(!dbNameValid()){
                    this.wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "Database name is not valid.");
                    return false;
                }
            }
            if (component.getUserNameText().getText().length() == 0) {
                this.wiz.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "You must set the database user.");
                return false;
            }
            if (component.getPasswordText().getText().length() == 0) {
                this.wiz.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "You must set the user password.");
                return false;
            }          

            if (component.getDropCreateDatabase().isSelected()) {
                if (!binExists) {
                    this.wiz.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "MySQL cannot be found in selected folder");
                }
            }

            this.wiz.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
            this.wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
        }
        return true;
    }
    
    private boolean dbNameValid(){
        return dbNamePattern.matcher(getComponent().getDatabaseNameText().getText()).matches();
    }
    
    

    private void browseButtonSelected() {
        //The default dir to use if no value is stored
        File home = new File(System.getProperty("user.home"));
        //Now build a file chooser and invoke the dialog in one line of code
        //"libraries-dir" is our unique key
        File file = new FileChooserBuilder("DbSettingsWizardPanel").setTitle("Select MySQL bin Folder").
                setDefaultWorkingDirectory(home).setApproveText("Okay").showOpenDialog();
        //Result will be null if the user clicked cancel or closed the dialog w/o OK
        if (file != null) {
            //set the directory as the parent
            NbPreferences.forModule(FileChooserBuilder.class).put("DbSettingsWizardPanel", file.getParent());
            //do something
            component.getMysqlBin().setText(file.getAbsolutePath());
            checkBin(file);
        }
    }

    private void checkBin(File file) {
        boolean exists = true;
        File child = new File(file, "mysql");
        if (!child.exists()) {
            child = new File(file, "mysql.exe");
            if (!child.exists()) {
                exists = false;
            }
        }
        this.binExists = exists;
        this.cs.fireChange();
    }

    private void checkBin(String fileName) {
        if (fileName.isEmpty()) {
            this.binExists = true;
            this.cs.fireChange();
            return;
        }
        checkBin(new File(fileName));
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
    public void readSettings(WizardDescriptor wiz) {
        this.wiz = wiz;
        format = (OrthoMclFormat) this.wiz.getProperty(PROP_ORTHOMCL_FORMAT);
        
        String userName = format.getDb_userName();
        String password = format.getDb_password();
        String dbName = format.getDatabaseName();
        Boolean dropCreate = format.getDropCreateDatabase();
        String mysqlBin = format.getMysqlBin();
        if (userName != null) {
            component.getUserNameText().setText(userName);
        }else{
            component.getUserNameText().setText(NbPreferences.forModule(OrthoMclFormat.class).get("mysql-user", ""));
        }
        if (password != null) {
            component.getPasswordText().setText(password);
        }
        if (dbName != null) {
            component.getDatabaseNameText().setText(dbName);
        }
        if (dropCreate != null) {
            component.getDropCreateDatabase().setSelected(dropCreate);
        }
        if (mysqlBin == null) {
            mysqlBin = NbPreferences.forModule(OrthoMclFormat.class).get("mysql-dir", "");
        }
        component.getMysqlBin().setText(mysqlBin);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        format.setDb_userName(component.getUserNameText().getText());
        format.setDb_password(component.getPasswordText().getText());
        format.setDatabaseName(component.getDatabaseNameText().getText());
        format.setDropCreateDatabase(component.getDropCreateDatabase().isSelected());
        format.setMysqlBin(component.getMysqlBin().getText());
        NbPreferences.forModule(OrthoMclFormat.class).put("mysql-dir", component.getMysqlBin().getText());
        NbPreferences.forModule(OrthoMclFormat.class).put("mysql-user", component.getUserNameText().getText());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.cs.fireChange();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        needsValidation = true;
        stateChanged(null);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        needsValidation = true;
        stateChanged(null);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        needsValidation = true;
        stateChanged(null);
    }
    
    public boolean validateConnection() {
        needsValidation = false;
        getComponent().getValidateLabel().setIcon(validIcon);
        this.wiz.getOptions();
        boolean validUser = DatabaseValidator.validateUser("localhost", "3306", getComponent().getUserNameText().getText(), getComponent().getPasswordText().getText());
        if (!validUser) {
            connectionValid = false;
            this.wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "Database username or password is invalid.");
            getComponent().getValidateLabel().setIcon(errorIcon);
            return connectionValid;
        }
        boolean dbExists = DatabaseValidator.databaseExists("localhost", "3306", getComponent().getUserNameText().getText(), getComponent().getPasswordText().getText(), getComponent().getDatabaseNameText().getText());
        if (dbExists) {
            connectionValid = false;
            this.wiz.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "Database already exists.");
            getComponent().getValidateLabel().setIcon(errorIcon);
            return connectionValid;
        }
        getComponent().getValidateLabel().setIcon(validIcon);
        return connectionValid;
    }
}
