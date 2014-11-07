/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.fasta;

import edu.uncc.genosets.datamanager.api.DownloadFormat;
import edu.uncc.genosets.datamanager.api.DownloadSet;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.templates.TemplateRegistration;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle.Messages;

// TODO define position attribute
@TemplateRegistration(folder = "Downloads/edu-uncc-genosets-datamanager-api-AnnotationFactType", id = "FASTA Protein (faa)", displayName = "#AnnoFastaProteinWizardIterator_displayName", iconBase = "edu/uncc/genosets/fasta/resources/download.png", description = "annoFastaProtein.html")
@Messages("AnnoFastaProteinWizardIterator_displayName=FASTA Protein (faa)")
public final class AnnoFastaProteinWizardIterator implements WizardDescriptor.InstantiatingIterator<WizardDescriptor> {

    private int index;
    private WizardDescriptor wizard;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;

    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
        if (panels == null) {
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new AnnoFastaProteinWizardPanel1());
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

    @Override
    public Set<?> instantiate() throws IOException {
        AnnoFastaFormat format = (AnnoFastaFormat) this.wizard.getProperty(DownloadFormat.WIZARD_DOWNLOAD_FORMAT_OBJECT);
        DownloadSet set = (DownloadSet) this.wizard.getProperty(DownloadFormat.WIZARD_DOWNLOAD_SET_OBJECT);
        //adds the format 
        if (format == null) {
            format = new AnnoFastaFormat(set);
            this.wizard.putProperty(DownloadFormat.WIZARD_DOWNLOAD_FORMAT_OBJECT, format);
            set.addFormat(format);
        }
        //update the format settings
        format.setFilePerOrganism((Boolean) this.wizard.getProperty(AnnoFastaProteinWizardPanel1.PROP_FILE_PER_ORGANISM));
        format.setPrefixOrganism((Boolean) this.wizard.getProperty(AnnoFastaProteinWizardPanel1.PROP_PREFIX_FOUR_LETTER_ORGANISM));
        format.setFilePerMethod((Boolean) this.wizard.getProperty(AnnoFastaProteinWizardPanel1.PROP_FILE_PER_METHOD));

        return Collections.emptySet();
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
        //get the download set from the wizard
        DownloadSet set = (DownloadSet) this.wizard.getProperty(DownloadFormat.WIZARD_DOWNLOAD_SET_OBJECT);
        //if the format already exists, then we should add the settings
        AnnoFastaFormat format = (AnnoFastaFormat) this.wizard.getProperty(DownloadFormat.WIZARD_DOWNLOAD_FORMAT_OBJECT);
        if (format != null) {
            this.wizard.putProperty(AnnoFastaProteinWizardPanel1.PROP_FILE_PER_ORGANISM, format.isFilePerOrganism());
            this.wizard.putProperty(AnnoFastaProteinWizardPanel1.PROP_PREFIX_FOUR_LETTER_ORGANISM, format.isPrefixOrganism());
            this.wizard.putProperty(AnnoFastaProteinWizardPanel1.PROP_FILE_PER_METHOD, format.isFilePerMethod());
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
