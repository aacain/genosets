/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.orthomcl;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

public class GeneralOrthoMclSettingsWizardPanel implements WizardDescriptor.Panel<WizardDescriptor>, ChangeListener {

    private transient final ChangeSupport cs = new ChangeSupport(this);
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private GeneralOrthoMclSettingsVisualPanel component;
    private WizardDescriptor wiz;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public GeneralOrthoMclSettingsVisualPanel getComponent() {
        if (component == null) {
            component = new GeneralOrthoMclSettingsVisualPanel();

        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("edu.uncc.genosets.orthomcl.run-general");
    }

    @Override
    public boolean isValid() {
        if (component.geteValueText().getText().length() == 0) {
            return false;
        }
        if (component.getFilterLengthText().getText().length() == 0) {
            return false;
        }
        if (component.getFilterStopCodons().getText().length() == 0) {
            return false;
        }
        if (component.getPercentMatchText().getText().length() == 0) {
            return false;
        }

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
        String protLength = (String) wiz.getProperty(OrthoMclFormat.PROP_MIN_PROT_LENGTH);
        String percentStops = (String) wiz.getProperty(OrthoMclFormat.PROP_MAX_PERCENT_STOPS);
        String percentMatch = (String) wiz.getProperty(OrthoMclFormat.PROP_PERCENT_MATCH);
        String evalue = (String) wiz.getProperty(OrthoMclFormat.PROP_EVALUE_CUTOFF);
        if (protLength != null) {
            component.getFilterLengthText().setText(protLength);
        }
        if (percentStops != null) {
            component.getFilterStopCodons().setText(percentStops);
        }
        if (percentMatch != null) {
            component.getPercentMatchText().setText(percentMatch);
        }
        if (evalue != null) {
            component.geteValueText().setText(evalue);
        }

    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        this.wiz.putProperty(OrthoMclFormat.PROP_MIN_PROT_LENGTH, component.getFilterLengthText().getText());
        this.wiz.putProperty(OrthoMclFormat.PROP_MAX_PERCENT_STOPS, component.getFilterStopCodons().getText());
        this.wiz.putProperty(OrthoMclFormat.PROP_PERCENT_MATCH, component.getPercentMatchText().getText());
        this.wiz.putProperty(OrthoMclFormat.PROP_EVALUE_CUTOFF, component.geteValueText().getText());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.cs.fireChange();
    }
}
