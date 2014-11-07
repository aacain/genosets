/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.studyset.actions;

import edu.uncc.genosets.studyset.StudySet;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class StudySetPropertiesWizardPanel implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private StudySetPropertiesVisualPanel component;
    private StudySet set;
    private WizardDescriptor wiz;
    TreeSet<String> studySetNames;
    boolean valid = true;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public StudySetPropertiesVisualPanel getComponent() {
        if (component == null) {
            component = new StudySetPropertiesVisualPanel();
            component.getNameText().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    checkName();
                }
            });
        }
        return component;
    }

    private void checkName() {
        String text = this.component.getNameText().getText();
        if (this.set.getName().equals(text)) {
            valid = true;
        } else {
            valid = !studySetNames.contains(text);
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
        return valid;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        set = (StudySet) wiz.getProperty(CreateStudySetWizardIterator.PROP_STUDY_SET);
        if (studySetNames == null) {
            studySetNames = new TreeSet();
            SortedSet<StudySet> sortedStudySets = (SortedSet<StudySet>) wiz.getProperty(CreateStudySetWizardIterator.PROP_ALL_STUDY_SETS);
            for (StudySet studySet : sortedStudySets) {
                studySetNames.add(studySet.getName());
            }
        }
        getComponent().getNameText().setText(set.getName() == null ? "" : set.getName());
        getComponent().getDescriptionTextArea().setText(set.getDescription() == null ? "" : set.getDescription());
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        set.setName(component.getNameText().getText());
        set.setDescription(component.getDescriptionTextArea().getText());
    }
}
