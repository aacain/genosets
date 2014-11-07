/*
 * Copyright (C) 2013 Aurora Cain
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
package edu.uncc.genosets.studyset.download;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.NbPreferences;

public class FolderSelectionWizardPanel implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private FolderSelectionVisualPanel component;
    private DownloadStudyWizard wiz;
    private String dir;
    private File downloadLocation;
    private String fileName = "";
    private boolean valid = false;
    private ChangeSupport cs = new ChangeSupport(this);
    Pattern pattern = Pattern.compile("[\\w\\s]+", Pattern.MULTILINE);

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public FolderSelectionVisualPanel getComponent() {
        if (component == null) {
            component = new FolderSelectionVisualPanel();
            init();
        }
        return component;
    }

    private void init() {
        dir = NbPreferences.forModule(FileChooserBuilder.class).get("download-dir", System.getProperty("user.home"));
        this.component.getLocationField().setText(dir != null ? dir : "");

        this.component.getLocationBrowseButton().addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationBrowseButtonActionPerformed(evt);
            }
        });
        this.component.getNameField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                nameFieldChanged(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                nameFieldChanged(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        this.component.getLocationField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                locationFieldChanged(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                locationFieldChanged(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                locationFieldChanged(e);
            }
        });
    }

    private void locationFieldChanged(DocumentEvent e) {
        Document doc = e.getDocument();
        try {
            this.dir = doc.getText(0, doc.getLength());
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            this.dir = null;
        }
        validate();
    }

    private void nameFieldChanged(DocumentEvent e) {
        Document doc = e.getDocument();
        try {
            this.fileName = doc.getText(0, doc.getLength());
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            this.fileName = null;
        }
        validate();
    }

    private void validate() {
        boolean isOk = true;
        this.downloadLocation = null;
        File file = null;
        if (this.fileName.isEmpty()) {
            file = new File(this.dir);
            this.wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "Download folder is not a valid path");
            isOk = false;
        } else {
            file = new File(this.dir, this.fileName);
        }
        file = file.getAbsoluteFile();
        if (!validFolderName(file)) {
            this.wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "Download folder is not a valid path");
            isOk = false;
        }
        
        if (file.exists() && file.isDirectory() && file.listFiles().length > 0) {
            this.wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "Download folder already exists and is not empty.");
            isOk = false;
        }
        getComponent().getFileField().setText(file.getAbsolutePath());
        if (isOk) {
            this.wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
        }
        this.downloadLocation = file;
        setValid(isOk);
    }

    private boolean validFolderName(String fileName) {
        Matcher matcher = pattern.matcher(fileName);
        return matcher.matches();
    }

    private boolean validFolderName(File file) {
        if (file == null) {
            return true;
        }
        if (!validFolderName(file.getName())) {
            return false;
        }
        if (file.getParentFile().exists()) {
            return true;
        }
        return validFolderName(file.getParentFile());
    }

    private void setValid(boolean valid) {
        this.valid = valid;
        fireChange();
    }

    private void locationBrowseButtonActionPerformed(ActionEvent evt) {
        //The default dir to use if no value is stored
        File home = new File(System.getProperty("user.home"));
        //Now build a file chooser and invoke the dialog in one line of code
        //"libraries-dir" is our unique key
        File toAdd = new FileChooserBuilder("download-dir").setTitle("Select Download Location").
                setDefaultWorkingDirectory(home).setDirectoriesOnly(true).setApproveText("Add").showOpenDialog();
        //Result will be null if the user clicked cancel or closed the dialog w/o OK
        if (toAdd != null) {
            //do something
            this.component.getLocationField().setText(toAdd.getAbsolutePath());
            this.dir = toAdd.getAbsolutePath();
            validate();
        }
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
        return this.valid;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        this.cs.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        this.cs.removeChangeListener(l);
    }

    private void fireChange() {
        this.cs.fireChange();
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        this.wiz = (DownloadStudyWizard) wiz;
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        if (this.downloadLocation != null) {
            this.wiz.setDirectory(this.downloadLocation.getAbsolutePath());
        }
    }
}
