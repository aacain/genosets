/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.orthomcl;

import edu.uncc.genosets.datamanager.api.DownloadException;
import edu.uncc.genosets.datamanager.api.DownloadFormat;
import edu.uncc.genosets.studyset.download.DownloadStudyWizard;
import edu.uncc.genosets.studyset.download.FolderSelectionWizardPanel;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;

@Messages("OrthoMclScriptWizardIterator_displayName=OrthoMcl")
public final class OrthoMclScriptWizardIterator implements WizardDescriptor.InstantiatingIterator<WizardDescriptor> {

    private int index;
    private DownloadStudyWizard wizard;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;
    public static final String PROP_ORTHOMCL_FORMAT = "PROP_ORTHOMCL_FORMAT";

    protected List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
        if (panels == null) {
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>(5);
            panels.add(new GroupingWizardPanel());
            panels.add(new PathSettingsWizardPanel());
            panels.add(new BlastSettingsWizardPanel_1());
            panels.add(new DbSettingsWizardPanel());
            panels.add(new GeneralOrthoMclSettingsWizardPanel());
            panels.add(new FolderSelectionWizardPanel());
            panels.add(new FinalWizardPanel());
            String[] steps = createSteps();
            for (int i = 0; i < panels.size(); i++) {
                Component c = panels.get(i).getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                }
            }
        }
        return panels;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<?> instantiate() throws IOException {
        //get the format from the wizard
        OrthoMclFormat format = (OrthoMclFormat) wizard.getProperty(PROP_ORTHOMCL_FORMAT);
        format.setDirectory(wizard.getDirectory());
        //update the format settings
//        format.setFilePerOrganism((Boolean) this.wizard.getProperty(GroupingWizardPanel.PROP_FILE_PER_ORGANISM));
//        format.setPrefixOrganism((Boolean) this.wizard.getProperty(GroupingWizardPanel.PROP_PREFIX_FOUR_LETTER_ORGANISM));
//        format.setFilePerMethod((Boolean) this.wizard.getProperty(GroupingWizardPanel.PROP_FILE_PER_METHOD));
        
//        format.setIsWindows((Boolean) this.wizard.getProperty(OrthoMclFormat.PROP_IS_WINDOWS));
//        format.setPerlDir((String) this.wizard.getProperty(OrthoMclFormat.PROP_PERL_DIR));
//        format.setOrthoDir((String) this.wizard.getProperty(OrthoMclFormat.PROP_ORTHO_DIR));
//        format.setMclDir((String) this.wizard.getProperty(OrthoMclFormat.PROP_MCL_DIR));

//        format.setDb_userName((String) this.wizard.getProperty(OrthoMclFormat.PROP_USER_NAME));
//        format.setDb_password((String) this.wizard.getProperty(OrthoMclFormat.PROP_PASSWORD));
//        format.setDatabaseName((String) this.wizard.getProperty(OrthoMclFormat.PROP_DB_NAME));
//        format.setDropCreateDatabase((Boolean) this.wizard.getProperty(OrthoMclFormat.PROP_DROP_CREATE_DB));
        //format.setMysqlBin((String) this.wizard.getProperty(OrthoMclFormat.PROP_MYSQL_BIN));

        format.setRunBlastp((Boolean) this.wizard.getProperty(OrthoMclFormat.PROP_DO_BLASTP));
        format.setRunMakeBlastDb((Boolean) this.wizard.getProperty(OrthoMclFormat.PROP_DO_MAKEBLAST));
        //format.setBlastBin((String) wizard.getProperty(OrthoMclFormat.PROP_BLAST_BIN));
        format.setMakeblastdbParameters((String) wizard.getProperty(OrthoMclFormat.PROP_MAKEBLASTDB_PARAMETERS));
        format.setBlastpParameters((String) wizard.getProperty(OrthoMclFormat.PROP_BLASTP_PARAMETERS));

        format.setMinProteinLength((String) this.wizard.getProperty(OrthoMclFormat.PROP_MIN_PROT_LENGTH));
        format.setMaxPercentStops((String) this.wizard.getProperty(OrthoMclFormat.PROP_MAX_PERCENT_STOPS));
        format.setPercentMatchCutoff((String) this.wizard.getProperty(OrthoMclFormat.PROP_PERCENT_MATCH));
        format.seteValueCutoff((String) this.wizard.getProperty(OrthoMclFormat.PROP_EVALUE_CUTOFF));
//        format.setExecute((Boolean)this.wizard.getProperty(OrthoMclFormat.PROP_EXECUTE));
        try {
            final OrthoMclFormat myFormat = format;
            myFormat.download();
        } catch (DownloadException ex) {
            Exceptions.printStackTrace(ex);
        }
        //set the last directory
        if(format.getDirectory() != null){
             NbPreferences.forModule(OrthoMclFormat.class).put("orthomcl-run", format.getDirectory());
        }

        return Collections.emptySet();
    }

    @Override
    public void initialize(WizardDescriptor wiz) {
        this.wizard = (DownloadStudyWizard) wiz;
        //if the format already exists, then we should add the settings
        OrthoMclFormat format = (OrthoMclFormat) this.wizard.getProperty(PROP_ORTHOMCL_FORMAT);
        if (format == null) {
            format = new OrthoMclFormat(this.wizard.getStudySets());
            this.wizard.putProperty(PROP_ORTHOMCL_FORMAT, format);
        }
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
    }

    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return getPanels().get(index);
    }

    @Override
    public String name() {
        return index + 1 + ". from " + getPanels().size();
    }

    @Override
    public boolean hasNext() {
        return index < getPanels().size() - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then use
    // ChangeSupport to implement add/removeChangeListener and call fireChange
    // when needed

    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
    private String[] createSteps() {
        String[] res = new String[panels.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = panels.get(i).getComponent().getName();
        }
        return res;
    }
}
