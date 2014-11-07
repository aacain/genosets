/*
 * 
 * 
 */
package edu.uncc.genosets.studyset.navigator;

import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetManager;
import edu.uncc.genosets.studyset.StudySetManager.StudySetManagerFactory;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 *
 * @author aacain
 */
@ActionID(id = "edu.uncc.genosets.studyset.navigator.DeleteAction", category = "StudySet")
@ActionRegistration(displayName = "Delete")
@ActionReference(path = "StudySet/Nodes/Actions", position = 2300)
public class DeleteAction extends AbstractAction implements LookupListener, ContextAwareAction, Presenter.Popup {

    private Lookup.Result<StudySet> result;
    private StudySetManager mgr;

    public DeleteAction() {
        this(Utilities.actionsGlobalContext());
    }

    public DeleteAction(Lookup lookup) {
        super("Delete");
        this.mgr = StudySetManagerFactory.getDefault();
        this.result = lookup.lookupResult(StudySet.class);
        this.result.addLookupListener(this);
        this.resultChanged(new LookupEvent(result));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (StudySet studySet : this.result.allInstances()) {
            StudySetManagerFactory.getDefault().deleteStudySet(studySet);
        }
    }

    @Override
    public Action createContextAwareInstance(Lookup lookup) {
        return new DeleteAction(lookup);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return new JMenuItem(this);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (this.result.allInstances().size() > 0) {
            this.setEnabled(true);
        } else {
            this.setEnabled(false);
        }
    }
}
