/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer.view;

import edu.uncc.genosets.ontologizer.OntologizerParameters;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class RunOntologizerWizardWizardPanel2 implements WizardDescriptor.Panel<WizardDescriptor> {

    public static String MTC_Bonferroni = "Bonferroni";
    public static String MTC_None = "None";
    public static String MTC_Westfall = "Westfall-Young-Single-Step";
    public static String CALC_PCunion = "Parent-Child-Union";
    public static String CALC_PCintersection = "Parent-Child-Intersection";
    public static String CALC_Term4Term = "Term-For-Term";
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private RunOntologizerWizardVisualPanel2 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public RunOntologizerWizardVisualPanel2 getComponent() {
        if (component == null) {
            component = new RunOntologizerWizardVisualPanel2();
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        OntologizerParameters params = (OntologizerParameters)wiz.getProperty(RunOntologizerWizardWizardIterator.PROP_ONTOLOGIZER_PARAMS);
        params.setMtc(component.getMTC());
        params.setCalculation(component.getCalculationType());
    }
}
