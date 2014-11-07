/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.gffloader.download;

import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class AnnoGffWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private AnnoGffVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public AnnoGffVisualPanel1 getComponent() {
        if (component == null) {
            component = new AnnoGffVisualPanel1();
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
        Boolean isMugsy = (Boolean)wiz.getProperty(AnnoGffFormat.PROP_MUGSY_FORMAT);
        Boolean isIncludeFasta = (Boolean)wiz.getProperty(AnnoGffFormat.PROP_INCLUDE_FASTA);
        Boolean isMinimalDetails = (Boolean)wiz.getProperty(AnnoGffFormat.PROP_MINIMAL_DETAILS);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        wiz.putProperty(AnnoGffFormat.PROP_ORIGINAL_ASSUNIT_IDS, component.getOrigAssUnitIdCheck().isSelected());
        wiz.putProperty(AnnoGffFormat.PROP_INCLUDE_FASTA, component.getIncludeFastaCheck().isSelected());
        wiz.putProperty(AnnoGffFormat.PROP_MUGSY_FORMAT, Boolean.FALSE);
        wiz.putProperty(AnnoGffFormat.PROP_CREATE_ASS_MAPPING, component.getAssUnitMappingCheckBox().isSelected());
    }
}
