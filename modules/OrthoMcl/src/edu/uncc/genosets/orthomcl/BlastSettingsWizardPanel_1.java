/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.orthomcl;

import static edu.uncc.genosets.orthomcl.OrthoMclScriptWizardIterator.PROP_ORTHOMCL_FORMAT;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;

public class BlastSettingsWizardPanel_1 implements WizardDescriptor.Panel<WizardDescriptor> {

    private transient final ChangeSupport cs = new ChangeSupport(this);
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private BlastSettingsVisualPanel_1 component;
    private WizardDescriptor wiz;
    private boolean binExists = true;
    private OrthoMclFormat format;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public BlastSettingsVisualPanel_1 getComponent() {
        if (component == null) {
            component = new BlastSettingsVisualPanel_1();
            component.getBlastpCheck().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateCheckBox();
                }
            });
            component.getMakeBlastDbCheck().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateCheckBox();
                }
            });
            component.getBlastBinText().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    checkBin(component.getBlastBinText().getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    checkBin(component.getBlastBinText().getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    checkBin(component.getBlastBinText().getText());
                }
            });

            component.getBrowseButton().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    browseButtonSelected();
                }
            });
        }
        return component;
    }
    
    private void browseButtonSelected(){
                //The default dir to use if no value is stored
        File home = new File(System.getProperty("user.home"));
        //Now build a file chooser and invoke the dialog in one line of code
        //"libraries-dir" is our unique key
        File file = new FileChooserBuilder("OrthoMclScriptWizard").setTitle("Select BLAST bin Folder").
                setDefaultWorkingDirectory(home).setApproveText("Add").showOpenDialog();
        //Result will be null if the user clicked cancel or closed the dialog w/o OK
        if (file != null) {
            //set the directory as the parent
            NbPreferences.forModule(FileChooserBuilder.class).put("OrthoMclScriptWizard", file.getParent());
            //do something
            component.getBlastBinText().setText(file.getAbsolutePath());
            checkBin(file);
        }
    }

    private void updateCheckBox() {
        if (component.getMakeBlastDbCheck().isSelected()) {
            component.getMakeblastdbText().setEnabled(true);

        } else {
            component.getMakeblastdbText().setEnabled(false);

        }
        if (component.getBlastpCheck().isSelected()) {
            component.getBlastpText().setEnabled(true);
        } else {
            component.getBlastpText().setEnabled(false);
        }
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("edu.uncc.genosets.orthomcl.run-general");
    }

    @Override
    public boolean isValid() {
        if (this.binExists) {
            this.wiz.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        } else {
            this.wiz.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "BLAST (blastp) program not found in folder");
        }
        return true;
    }

    private void checkBin(File file) {
        boolean exists = true;
        File child = new File(file, "blastp");
        if (!child.exists()) {
            child = new File(file, "blastp.exe");
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
        String blastpParams = (String) this.wiz.getProperty(OrthoMclFormat.PROP_BLASTP_PARAMETERS);
        if (blastpParams != null) {
            component.getBlastpText().setText(blastpParams);
        }
        String makeblastdbParams = (String) this.wiz.getProperty(OrthoMclFormat.PROP_MAKEBLASTDB_PARAMETERS);
        if (makeblastdbParams != null) {
            component.getMakeblastdbText().setText(makeblastdbParams);
        }
        
        String blastBin = format.getBlastBin();
        if (blastBin == null) {
            blastBin = NbPreferences.forModule(OrthoMclFormat.class).get("blast-dir", "");
        }
        component.getBlastBinText().setText(blastBin);
        
        Boolean doBlastp = (Boolean) this.wiz.getProperty(OrthoMclFormat.PROP_DO_BLASTP);
        if (doBlastp != null) {
            component.getBlastpCheck().setSelected(doBlastp);
        }
        Boolean doMakeBlastDb = (Boolean) this.wiz.getProperty(OrthoMclFormat.PROP_DO_MAKEBLAST);
        if (doMakeBlastDb != null) {
            component.getMakeBlastDbCheck().setSelected(doMakeBlastDb);
        }
        updateCheckBox();
        checkBin(component.getBlastBinText().getText());
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        this.wiz.putProperty(OrthoMclFormat.PROP_DO_BLASTP, component.getBlastpCheck().isSelected());
        this.wiz.putProperty(OrthoMclFormat.PROP_DO_MAKEBLAST, component.getMakeBlastDbCheck().isSelected());
        this.wiz.putProperty(OrthoMclFormat.PROP_BLASTP_PARAMETERS, component.getBlastpText().getText());
        this.wiz.putProperty(OrthoMclFormat.PROP_MAKEBLASTDB_PARAMETERS, component.getMakeblastdbText().getText());
        format.setBlastBin(component.getBlastBinText().getText());
        NbPreferences.forModule(OrthoMclFormat.class).put("blast-dir", component.getBlastBinText().getText());
    }
}
