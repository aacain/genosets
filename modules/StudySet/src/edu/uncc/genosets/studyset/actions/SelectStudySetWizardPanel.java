/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.studyset.actions;

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetManager;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class SelectStudySetWizardPanel implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private SelectStudySetVisualPanel component;
    private WizardDescriptor wiz;
    private SortedSet<StudySet> sortedStudySets;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public SelectStudySetVisualPanel getComponent() {
        if (component == null) {  
            component = new SelectStudySetVisualPanel();
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
        if(this.wiz == null){
            this.wiz = wiz;
            StudySetManager mgr = StudySetManager.StudySetManagerFactory.getDefault();
            Comparator<StudySet> comparator = new Comparator<StudySet>(){
                @Override
                public int compare(StudySet o1, StudySet o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            };
            sortedStudySets = new TreeSet<StudySet>(comparator);
            sortedStudySets.addAll(mgr.getStudySets());
            FocusEntity focusEntity = (FocusEntity) wiz.getProperty(CreateStudySetWizardIterator.PROP_FOCUS_ENTITY);
            Set<StudySet> filteredSet;
            if (focusEntity == null) {
                filteredSet = sortedStudySets;
            } else {
                filteredSet = new TreeSet(comparator);
                for (StudySet studySet : sortedStudySets) {
                    if(studySet.getFocusEntity() != null && studySet.getFocusEntity().equals(focusEntity)){
                        filteredSet.add(studySet);
                    }
                }
            }
            getComponent().setStudySets(filteredSet);
            this.wiz.putProperty(CreateStudySetWizardIterator.PROP_ALL_STUDY_SETS, sortedStudySets);
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        this.wiz.putProperty(CreateStudySetWizardIterator.PROP_STUDY_SET, component.getSelectedStudySet());
    }
}
