/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.actions;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetManager.StudySetManagerFactory;
import edu.uncc.genosets.studyset.actions.CreateStudySetWizardIterator;
import edu.uncc.genosets.studyset.actions.SelectStudySetWizardPanel;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractAction;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;

import org.openide.awt.ActionRegistration;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "Actions",
id = "edu.uncc.genosets.datanavigator.actions.AddStudySetAction")
@ActionRegistration(displayName = "#CTL_AddStudySetAction")
@ActionReferences({@ActionReference(path = "AnnotationMethod/Nodes/Annotations/Actions", position = 200)})
@Messages("CTL_AddStudySetAction=Add StudySet")
public final class AddStudySetAction extends AbstractAction {

    private final List<AnnotationMethod> context;

    public AddStudySetAction(List<AnnotationMethod> context) {
        this.putValue(NAME, Bundle.CTL_AddStudySetAction());
        this.context = context;
    }

    public void actionPerformed(ActionEvent ev) {
        WizardDescriptor wiz = new WizardDescriptor(new CreateStudySetWizardIterator());
        // {0} will be replaced by WizardDescriptor.Panel.getComponent().getName()
        // {1} will be replaced by WizardDescriptor.Iterator.name()
        wiz.setTitleFormat(new MessageFormat("{0} ({1})"));
        wiz.setTitle("Create Study Set");
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            StudySet studySet = (StudySet) wiz.getProperty(CreateStudySetWizardIterator.PROP_STUDY_SET);
            StudySetManagerFactory.getDefault().saveStudySet(studySet);
            Set<Integer> featureIdSet = studySet.getIdSet();
            if (featureIdSet == null) {
                featureIdSet = new HashSet<Integer>();
            }
            for (AnnotationMethod annotationMethod : context) {
                featureIdSet.addAll(FeatureQueryCreator.getIds(annotationMethod));
            }
            studySet.setIdSet(featureIdSet);
        }
    }
    
    private static class FeatureQueryCreator implements QueryCreator{
        static List<Integer> getIds(AnnotationMethod method){
            return DataManager.getDefault().createQuery("SELECT f.featureId FROM AnnoFact AS f WHERE f.annotationMethodId = " + method.getAnnotationMethodId());
        }
    }
}
