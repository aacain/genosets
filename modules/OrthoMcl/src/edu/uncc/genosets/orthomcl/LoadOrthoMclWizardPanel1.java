/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.orthomcl;

import java.io.File;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;

public class LoadOrthoMclWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, DocumentListener {

    static final String PROP_FASTA_FOLDER_PATH = "PROP_FASTA_FOLDER_PATH";
    static final String PROP_GROUPS_FILE_PATH = "PROP_GROUPS_FILE_PATH";
    private ChangeSupport cs = new ChangeSupport(this);
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private LoadOrthoMclVisualPanel1 component;
    private WizardDescriptor wiz;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public LoadOrthoMclVisualPanel1 getComponent() {
        if (component == null) {
            component = new LoadOrthoMclVisualPanel1();
            component.getFastaFolderText().getDocument().addDocumentListener(this);
            component.getGroupsFileText().getDocument().addDocumentListener(this);;
        }
        return component;
    }


    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("edu.uncc.genosets.orthomcl.load-general");
    }

    @Override
    public boolean isValid() {
        File fasta = new File(component.getFastaFolderText().getText());
        if (!fasta.isDirectory()) {
            wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "FASTA folder is invalid.");
            return false;
        }

        File groups = new File(component.getGroupsFileText().getText());
        if (!groups.isFile()) {
            wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "Cluster file is invalid.");
            return false;
        }

        wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
        return true;
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
        this.wiz = wiz;
        String fastaFolder = (String) this.wiz.getProperty(PROP_FASTA_FOLDER_PATH);
        String groupsFile = (String) this.wiz.getProperty(PROP_GROUPS_FILE_PATH);
        if (fastaFolder == null && groupsFile == null) {
            String lastDownload = NbPreferences.forModule(OrthoMclFormat.class).get("orthomcl-run", null);
            if(lastDownload != null){
                File parentFolder = new File(lastDownload);
                if(parentFolder.exists()){
                    File g = new File(parentFolder, "groups.txt");
                    File f = new File(parentFolder, "fasta");
                    if(g.exists() && !g.isDirectory() && f.exists() && f.isDirectory()){//both files exist
                        getComponent().getFastaFolderText().setText(f.getAbsolutePath());
                        getComponent().getGroupsFileText().setText(g.getAbsolutePath());
                    }
                }
            }
        } else {
            if (fastaFolder != null) {
                getComponent().getFastaFolderText().setText(fastaFolder);
            }
            if (groupsFile != null) {
                getComponent().getGroupsFileText().setText(groupsFile);
            }
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        wiz.putProperty(PROP_FASTA_FOLDER_PATH, component.getFastaFolderText().getText());
        wiz.putProperty(PROP_GROUPS_FILE_PATH, component.getGroupsFileText().getText());
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        cs.fireChange();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        cs.fireChange();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        cs.fireChange();
    }
}
