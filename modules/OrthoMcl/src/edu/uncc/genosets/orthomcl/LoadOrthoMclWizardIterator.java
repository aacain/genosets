/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.orthomcl;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.wizards.MethodWizardPanel;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle.Messages;

@Messages({
    "LoadOrthoMclWizardIterator_displayName=Load OrthoMcl v2",
    "CTL_MethodCategory=Ortholog",
    "CTL_MethodType=OrthoMcl",
    "CTL_MethodSource=OrthoMcl v2"})
public final class LoadOrthoMclWizardIterator implements WizardDescriptor.InstantiatingIterator<WizardDescriptor> {

    private int index;
    private WizardDescriptor wizard;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;

    @SuppressWarnings("unchecked")
    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
        if (panels == null) {
            //create method constants
            AnnotationMethod methodConstants = new AnnotationMethod();
            methodConstants.setMethodCategory(org.openide.util.NbBundle.getMessage(LoadOrthoMclWizardIterator.class, "CTL_MethodCategory"));
            methodConstants.setMethodType(org.openide.util.NbBundle.getMessage(LoadOrthoMclWizardIterator.class, "CTL_MethodType"));
            methodConstants.setMethodSourceType(org.openide.util.NbBundle.getMessage(LoadOrthoMclWizardIterator.class, "CTL_MethodSource"));
            Date date = new Date();
            methodConstants.setLoadDate(date);
            methodConstants.setRunDate(date);
            //initalize panels
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new LoadOrthoMclWizardPanel1());
            panels.add(new MethodWizardPanel(methodConstants));
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
        // TODO return set of FileObject (or DataObject) you have created
        String fastaPath = (String)wizard.getProperty(LoadOrthoMclWizardPanel1.PROP_FASTA_FOLDER_PATH);
        String groupsPath = (String)wizard.getProperty(LoadOrthoMclWizardPanel1.PROP_GROUPS_FILE_PATH);
        AnnotationMethod method = (AnnotationMethod)wizard.getProperty(MethodWizardPanel.PROP_AnnotationMethod);
        File fasta = new File(fastaPath);
        File groups = new File(groupsPath);
        LoadOrthoMcl.loadOrthoMcl(method, fasta, groups);
        return Collections.emptySet();
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
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
