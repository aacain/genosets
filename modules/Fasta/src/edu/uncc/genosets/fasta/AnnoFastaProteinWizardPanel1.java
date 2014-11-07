/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.fasta;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

public class AnnoFastaProteinWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, ChangeListener {
    //static property names

    public static final String PROP_FILE_PER_ORGANISM = "PROP_FILE_PER_ORGANISM";
    public static final String PROP_FILE_PER_METHOD = "PROP_FILE_PER_METHOD";
    public static final String PROP_PREFIX_FOUR_LETTER_ORGANISM = "PROP_PREFIX_FOUR_LETTER_ORGANISM";
    private final ChangeSupport cs = new ChangeSupport(this);
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private AssUnitNucFastaVisualPanel1 component;
    private WizardDescriptor wiz;

// Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public AssUnitNucFastaVisualPanel1 getComponent() {
        if (component == null) {
            component = new AssUnitNucFastaVisualPanel1();
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
    public void readSettings(WizardDescriptor wiz) {
        this.wiz = wiz;
        Boolean perOrgSelected = (Boolean) wiz.getProperty(AnnoFastaProteinWizardPanel1.PROP_FILE_PER_ORGANISM);
        Boolean prefixSelected = (Boolean) wiz.getProperty(AnnoFastaProteinWizardPanel1.PROP_PREFIX_FOUR_LETTER_ORGANISM);
        Boolean perMethodSelected = (Boolean) wiz.getProperty(AnnoFastaProteinWizardPanel1.PROP_FILE_PER_METHOD);
        component.getCheckFilePerOrganism().setSelected(perOrgSelected == null ? false : perOrgSelected);
        component.getCheckPrefix4Letters().setSelected(prefixSelected == null ? false : prefixSelected);
        component.getCheckFilePerMethod().setSelected(perMethodSelected == null ? false : perMethodSelected);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        this.wiz.putProperty(AnnoFastaProteinWizardPanel1.PROP_FILE_PER_ORGANISM, component.getCheckFilePerOrganism().isSelected());
        this.wiz.putProperty(AnnoFastaProteinWizardPanel1.PROP_PREFIX_FOUR_LETTER_ORGANISM, component.getCheckPrefix4Letters().isSelected());
        this.wiz.putProperty(AnnoFastaProteinWizardPanel1.PROP_FILE_PER_METHOD, component.getCheckFilePerMethod().isSelected());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.cs.fireChange();
    }
}
