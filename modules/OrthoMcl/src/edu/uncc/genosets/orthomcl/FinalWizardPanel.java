/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.orthomcl;

import static edu.uncc.genosets.orthomcl.OrthoMclScriptWizardIterator.PROP_ORTHOMCL_FORMAT;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;

public class FinalWizardPanel implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private FinalVisualPanel component;
    private WizardDescriptor wiz;
    private ChangeSupport cs = new ChangeSupport(this);
    private boolean isWindows = false;
    private OrthoMclFormat format;

    @Override
    public FinalVisualPanel getComponent() {
        if (component == null) {
            component = new FinalVisualPanel();
            component.getExecuteCheckBox().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cs.fireChange();
                }
            });
            //get os
            String os = System.getProperty("os.name").toLowerCase();
            if (os.indexOf("win") >= 0) {
                this.isWindows = true;
            }

            component.getValidateButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    validate();
                }
            });
        }
        return component;
    }

    private OrthoMclFormat validate(final OrthoMclFormat myFormat, boolean isCorrection) {
        final PathValidation validation = new PathValidation();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    validation.validate(myFormat);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };

        ProgressUtils.showProgressDialogAndRun(r, "Validating...");
        Object[] options;
        if (validation.isPathValid() && !isCorrection) {
            options = new Object[]{"Close"};
        } else if (isCorrection) {
            options = new Object[]{"Revert", "Keep"};
        } else {
            options = new Object[]{"Correct", "Ignore"};
        }
        Object result = DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                new ValidatingPanel(validation),
                "Validating Paths",
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.INFORMATION_MESSAGE,
                options,
                NotifyDescriptor.CANCEL_OPTION));
        if (result.equals("Correct")) {
            OrthoMclFormat corrected = validation.attemptCorrection(myFormat);
            OrthoMclFormat mine = validate(corrected, true);
            if(mine == null){
                return myFormat;
            }
            return mine;
        }else if(result.equals("Keep")){
            return myFormat;
        }else if(result.equals("Revert")){
            return null;
        }
        return myFormat;
    }

    private void validate() {
        OrthoMclFormat myformat = (OrthoMclFormat) this.wiz.getProperty(PROP_ORTHOMCL_FORMAT);
        OrthoMclFormat validated = validate(myformat, false);
        this.wiz.putProperty(PROP_ORTHOMCL_FORMAT, validated);
        if(validated != myformat){
            //update preference settings
            OrthoMclFormat.updatePreferences(validated);
        }
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("edu.uncc.genosets.orthomcl.run-general");
    }

    @Override
    public boolean isValid() {
        if (getComponent().getExecuteCheckBox().isSelected()) {
            Boolean isWindowsScript = format.isIsWindows();
            if (isWindowsScript != null && isWindowsScript != isWindows) {
                this.wiz.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "Script is not for this operating system and may not execute properly.");
                return true;
            }
        }
        this.wiz.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        return true;
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        this.wiz = wiz;
        this.format = (OrthoMclFormat) wiz.getProperty(OrthoMclScriptWizardIterator.PROP_ORTHOMCL_FORMAT);
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        this.format.setExecute(getComponent().getExecuteCheckBox().isSelected());
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        this.cs.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        this.cs.removeChangeListener(l);
    }
}
