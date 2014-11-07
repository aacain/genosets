/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.studyset.actions;

import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetManager.StudySetManagerFactory;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Actions",
id = "edu.uncc.genosets.studyset.actions.CreateStudySetAction")
@ActionRegistration(displayName = "#CTL_CreateStudySet")
@ActionReferences(value = {
    @ActionReference(path = "Actions/Nodes/Feature", position = 500)
})
@NbBundle.Messages("CTL_CreateStudySet=Add studyset")
public final class CreateStudySetAction implements ActionListener {

    private final List<Feature> context;

    public CreateStudySetAction(List<Feature> context) {
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
            if(featureIdSet == null){
                featureIdSet = new HashSet<Integer>();
            }
            for (Feature feature : context) {
                featureIdSet.add(feature.getFeatureId());
            }
            studySet.setIdSet(featureIdSet);
        }
    }
}
