/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.orthomcl;

import static edu.uncc.genosets.orthomcl.OrthoMclScriptWizardIterator.PROP_ORTHOMCL_FORMAT;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;

public class PathSettingsWizardPanel implements WizardDescriptor.Panel<WizardDescriptor> {

    private transient final ChangeSupport cs = new ChangeSupport(this);
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private PathSettingsVisualPanel component;
    private WizardDescriptor wiz;
    private boolean isWindows = false;
    private boolean perlValid = true;
    private boolean orthoMclValid = true;
    private boolean mclValid = true;
    private OrthoMclFormat format;

    @Override
    public PathSettingsVisualPanel getComponent() {
        if (component == null) {
            component = new PathSettingsVisualPanel();
            component.getWindowsRadioButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cs.fireChange();
                }
            });
            component.getMacRadioButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cs.fireChange();
                }
            });
            component.getPerlBinText().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    validatePerl(component.getPerlBinText().getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    validatePerl(component.getPerlBinText().getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    validatePerl(component.getPerlBinText().getText());
                }
            });
            component.getOrthomclBinText().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    validateOrthoMcl(component.getOrthomclBinText().getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    validateOrthoMcl(component.getOrthomclBinText().getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    validateOrthoMcl(component.getOrthomclBinText().getText());
                }
            });
            component.getMclBinText().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    validateMcl(component.getMclBinText().getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    validateMcl(component.getMclBinText().getText());
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    validateMcl(component.getMclBinText().getText());
                }
            });
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("edu.uncc.genosets.orthomcl.run-general");
    }

    private void validatePerl(String fileName) {
        boolean valid = true;
        if (!fileName.isEmpty()) {
            File file = new File(fileName, "perl");
            if (!file.exists()) {
                file = new File(fileName, "perl.exe");
                valid = file.exists();
            }
        }
        component.perlIcon(!valid);
        this.perlValid = valid;
        this.cs.fireChange();
    }

    private void validateOrthoMcl(String fileName) {
        boolean valid = true;
        if (!fileName.isEmpty()) {
            File file = new File(fileName, "orthomclInstallSchema");
            valid = file.exists();
        }
        component.orthoMclIcon(!valid);
        this.orthoMclValid = valid;
        this.cs.fireChange();
    }

    private void validateMcl(String fileName) {
        boolean valid = true;
        if (!fileName.isEmpty()) {
            File file = new File(fileName, "mcl");
            if (!file.exists()) {
                file = new File(fileName, "mcl.exe");
                valid = file.exists();
            }
        }
        component.mclIcon(!valid);
        this.mclValid = valid;
        this.cs.fireChange();
    }

    @Override
    public boolean isValid() {
        boolean valid = true;
        boolean warn = false;
        if (component.getWindowsRadioButton().isSelected() != this.isWindows) {
            this.wiz.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "The script created is not for this computers operating system.\nhello");
        }
        if (!component.getPerlBinText().getText().isEmpty()) {
            if (component.getOrthomclBinText().getText().isEmpty()) {
                this.wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "If the perl bin is set, the orthomcl bin must also be set.");
                valid = false;
            }
        }
        if (component.getWindowsRadioButton().isSelected() && component.getOrthomclBinText().getText().isEmpty()) {
            this.wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "You must set the orthomcl bin for a windows script.");
            valid = false;
        }
        if (!valid) {
            return false;
        }
        if (warn) {
            return true;
        }
        this.wiz.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, null);
        this.wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
        return true;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        synchronized (cs) {
            this.cs.addChangeListener(l);
        }
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        synchronized (cs) {
            this.cs.removeChangeListener(l);
        }
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        this.wiz = wiz;
        format = (OrthoMclFormat) this.wiz.getProperty(PROP_ORTHOMCL_FORMAT);
        String perlBin = format.getPerlDir();
        if (perlBin == null) {
            perlBin = NbPreferences.forModule(OrthoMclFormat.class).get("perl-dir", "");
        }
        component.setPerlBinText(perlBin);
        validatePerl(perlBin);
        String orthoBin = format.getOrthoDir();
        if (orthoBin == null) {
            orthoBin = NbPreferences.forModule(OrthoMclFormat.class).get("ortho-dir", "");
        }
        component.setOrthomclBinText(orthoBin);
        validateOrthoMcl(orthoBin);
        String mclBin = format.getMclDir();
        if (mclBin == null) {
            mclBin = NbPreferences.forModule(OrthoMclFormat.class).get("mcl-dir", "");
        }
        component.setMclBinText(mclBin);
        validateMcl(mclBin);
        if (format.isIsWindows() == null) {
            //get os
            String os = System.getProperty("os.name").toLowerCase();
            if (os.indexOf("win") >= 0) {
                this.isWindows = true;
                component.getWindowsRadioButton().setSelected(true);
                component.getMacRadioButton().setSelected(false);
            } else {
                component.getWindowsRadioButton().setSelected(false);
                component.getMacRadioButton().setSelected(true);
            }
        } else {
            component.getWindowsRadioButton().setSelected(format.isIsWindows());
            component.getMacRadioButton().setSelected(!format.isIsWindows());
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        if (component.getPerlBinText().getText() != null) {
            NbPreferences.forModule(OrthoMclFormat.class).put("perl-dir", component.getPerlBinText().getText());
        }
        if (component.getOrthomclBinText().getText() != null) {
            NbPreferences.forModule(OrthoMclFormat.class).put("ortho-dir", component.getOrthomclBinText().getText());
        }
        if (component.getMclBinText().getText() != null) {
            NbPreferences.forModule(OrthoMclFormat.class).put("mcl-dir", component.getMclBinText().getText());
        }

        format.setPerlDir(component.getPerlBinText().getText());
        format.setOrthoDir(component.getOrthomclBinText().getText());
        format.setMclDir(component.getMclBinText().getText());
        format.setIsWindows(component.getWindowsRadioButton().isSelected());
    }
}
