/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer.view;

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.geneontology.obo.OboWizardPanel;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.datamanager.wizards.MultiFileFolderSelectionWizardPanel;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;

public final class RunOntologizerWizardWizardIterator implements WizardDescriptor.Iterator<WizardDescriptor> {

    private int index;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;
    public static final String PROP_ONTOLOGIZER_PARAMS = "PROP_ONTOLOGIZER_PARAMS";
    private final List<StudySet> studysets;
    private final FocusEntity focusEntity;

    public RunOntologizerWizardWizardIterator(List<StudySet> studysets, FocusEntity focusEntity) {
        this.studysets = studysets;
        this.focusEntity = focusEntity;
    }
    
    

    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
        if (panels == null) {
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new RunPopulationWizardPanel(focusEntity));
            //panels.add(new OboSelectWizardPanel(Collections.singleton("FULL"), Boolean.TRUE));
            panels.add(new OboWizardPanel());
            panels.add(new RunOntologizerWizardWizardPanel2());
            String[] steps = new String[panels.size()];
            for (int i = 0; i < panels.size(); i++) {
                Component c = panels.get(i).getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
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
}
