/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer.view;

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.ontologizer.OntologizerParameters;
import edu.uncc.genosets.studyset.StudySet;
import java.util.LinkedList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardValidationException;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

public class RunPopulationWizardPanel implements WizardDescriptor.AsynchronousValidatingPanel {
   
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private RunPopulationVisualPanel component;
    private final ChangeSupport cs = new ChangeSupport(this);
    private WizardDescriptor wiz;
    private OntologizerParameters params;
    //private final Set<Integer> studySetsIds;
    private boolean valid = true;
    private final FocusEntity focusEntity;
    
    
    public RunPopulationWizardPanel(FocusEntity focusEntity) {
        this.focusEntity = focusEntity;
    }

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public RunPopulationVisualPanel getComponent() {
        if (component == null) {
            component = new RunPopulationVisualPanel(focusEntity);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        //return HelpCtx.DEFAULT_HELP;
        // If you have context help:
         return new HelpCtx("edu.uncc.genosets.ontologizer.run");
    }

    @Override
    public boolean isValid() {
        return true;
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
    public void readSettings(Object settings) {
        this.wiz = (WizardDescriptor) settings;
        this.params = (OntologizerParameters) this.wiz.getProperty(RunOntologizerWizardWizardIterator.PROP_ONTOLOGIZER_PARAMS);
    }

    @Override
    public void storeSettings(Object wiz) {
        List<StudySet> popList = new LinkedList<StudySet>();
        for (StudySet ss : component.getSelectedStudySets()) {
            popList.add(ss);
        }
        this.params.setPopulationSets(popList);
    }

    @Override
    public void prepareValidation() {
        
    }

    @Override
    public void validate() throws WizardValidationException {
        if(getComponent().getSelectedStudySets().isEmpty()){
            throw new WizardValidationException(component, "No population set is selected.", "No population set is selected.");
        }
    }
}
