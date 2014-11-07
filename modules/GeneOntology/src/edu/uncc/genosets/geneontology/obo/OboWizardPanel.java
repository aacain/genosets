/*
 * Copyright (C) 2014 Aurora Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.uncc.genosets.geneontology.obo;

import edu.uncc.genosets.geneontology.obo.OboDataObject;
import edu.uncc.genosets.geneontology.obo.OboManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileUtil;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;

/**
 *
 * @author aacain
 */
public class OboWizardPanel implements WizardDescriptor.Panel<WizardDescriptor> {

    ChangeSupport cs = new ChangeSupport(this);
    private OboVisualPanel component;
    private WizardDescriptor wiz;
    private boolean oboValid = true;
    public final static String PROP_OBO = "PROP_OBO";
    private DateFormat dateFormat = DateFormat.getDateTimeInstance();
    private OboDataObject lastUsed;

    @Override
    public OboVisualPanel getComponent() {
        if (component == null) {
            component = new OboVisualPanel();
            this.lastUsed = OboManager.getLastUsed();
            component.getOntologyTextField().setText("http://www.geneontology.org/ontology/gene_ontology_edit.obo");
            component.getBrowseButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fileBrowse();
                }
            });
            component.getOntologyTextField().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    validate();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    validate();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });

            component.getLastUsedRadio().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    radioSelected();
                }
            });
            component.getFileRadio().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    radioSelected();
                }
            });
            if (lastUsed != null) {
                String prefLastUsed = NbPreferences.forModule(OboWizardPanel.class).get("OboWizardPanel.LastUsed", null);
                if (prefLastUsed != null) {
                    component.getLastUsedRadio().setSelected(true);
                }
            }

            if (component.getLastUsedRadio().isSelected()) {
                component.getOntologyTextField().setEnabled(false);
                component.getBrowseButton().setEnabled(false);
            } else {
                component.getOntologyTextField().setEnabled(true);
                component.getBrowseButton().setEnabled(true);
            }
        }
        return component;
    }

    private void radioSelected() {
        if (getComponent().getLastUsedRadio().isSelected()) {
            getComponent().getOntologyTextField().setEnabled(false);
            getComponent().getBrowseButton().setEnabled(false);
        } else {
            getComponent().getOntologyTextField().setEnabled(true);
            getComponent().getBrowseButton().setEnabled(true);
        }
    }

    private void fileBrowse() {
        //The default dir to use if no value is stored
        File home = new File(System.getProperty("user.home"));
        //Now build a file chooser and invoke the dialog in one line of code
        //"libraries-dir" is our unique key
        FileNameExtensionFilter filter = new FileNameExtensionFilter("OBO Files", "obo", "OBO", "*");
        File toAdd = new FileChooserBuilder("obo-dir").setTitle("Select Ontology File").addFileFilter(filter).
                setDefaultWorkingDirectory(home).setDirectoriesOnly(false).setApproveText("Okay").showOpenDialog();
        //Result will be null if the user clicked cancel or closed the dialog w/o OK
        if (toAdd != null) {
            //do something
            getComponent().getOntologyTextField().setText(toAdd.getAbsolutePath());

        }
        validate();
    }

    private void validate() {
        Document doc = getComponent().getOntologyTextField().getDocument();
        try {
            String text = doc.getText(0, doc.getLength());
            if (text.startsWith("http://")) {
                oboValid = true;
            } else {
                File file = new File(text);
                oboValid = file.exists();
            }
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            cs.fireChange();
        }
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(WizardDescriptor settings) {
        this.wiz = settings;
        if (lastUsed == null) {
            component.getLastUsedNameField().setVisible(false);
            component.getLastUsedDateField().setVisible(false);
            component.getLastUsedRadio().setVisible(false);
        } else {
            try {
                File myFile = FileUtil.toFile(lastUsed.getFileObject());
                component.getLastUsedNameField().setText(lastUsed.getUrl());
                component.getLastUsedDateField().setText(dateFormat.format(new Date(myFile.lastModified())));
            } catch (IOException ex) {
                component.getLastUsedNameField().setVisible(false);
                component.getLastUsedDateField().setVisible(false);
                component.getLastUsedRadio().setVisible(false);
            }
        }
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        if (getComponent().getFileRadio().isSelected()) {
            this.wiz.putProperty(PROP_OBO, new OboDataObject(getComponent().getOntologyTextField().getText()));
            NbPreferences.forModule(OboWizardPanel.class).remove("OboWizardPanel.LastUsed");
        } else {
            NbPreferences.forModule(OboWizardPanel.class).put("OboWizardPanel.LastUsed", "true");
            this.wiz.putProperty(PROP_OBO, lastUsed);
        }

    }

    @Override
    public boolean isValid() {
        if (oboValid) {
        }
        return oboValid;
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
