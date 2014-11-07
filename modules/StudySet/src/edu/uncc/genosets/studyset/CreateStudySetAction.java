
package edu.uncc.genosets.studyset;

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.studyset.actions.CreateStudySetWizardIterator;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;

/**
 *
 * @author aacain
 */
@ActionID(id = "edu.uncc.genosets.studyset.CreateStudySetAction", category = "StudySet")
@ActionRegistration(displayName = "#CTL_CreateStudySetAction")
@NbBundle.Messages({
    "CTL_CreateStudySetAction=Create StudySet"
})
public class CreateStudySetAction extends AbstractAction implements Presenter.Popup {

    private StudySet studySet;
    private final FocusEntity focusEntity;
    private final Set<Integer> idSet;

    public CreateStudySetAction() {
        this(null, null);
    }

    public CreateStudySetAction(FocusEntity focusEntity, Set<Integer> idSet) {
        super(Bundle.CTL_CreateStudySetAction());
        this.focusEntity = focusEntity;
        this.idSet = idSet;
    }

    /**
     *
     * @return the study set or null if the wizard was cancelled
     */
    public StudySet getStudySet() {
        return this.studySet;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        WizardDescriptor wiz = new WizardDescriptor(new CreateStudySetWizardIterator());
        // {0} will be replaced by WizardDescriptor.Panel.getComponent().getName()
        // {1} will be replaced by WizardDescriptor.Iterator.name()
        wiz.setTitleFormat(new MessageFormat("{0} ({1})"));
        wiz.setTitle("Create Study Set");
        wiz.putProperty(CreateStudySetWizardIterator.PROP_FOCUS_ENTITY, this.focusEntity);
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            this.studySet = (StudySet) wiz.getProperty(CreateStudySetWizardIterator.PROP_STUDY_SET);
            this.studySet.setFocusEntity(focusEntity);
            Set<Integer> oldIds = this.studySet.getIdSet();
            if(oldIds != null){
                this.idSet.addAll(oldIds);
            }
            studySet.setIdSet(this.idSet);
        }
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return new JMenuItem("Add StudySet");
    }
}
