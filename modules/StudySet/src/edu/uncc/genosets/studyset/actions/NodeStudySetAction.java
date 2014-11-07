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
package edu.uncc.genosets.studyset.actions;

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 *
 * @author aacain
 */
@ActionID(category = "Actions",
        id = "edu.uncc.genosets.studyset.actions.NodeStudySetAction")
@ActionRegistration(displayName = "#CTL_NodeStudySetAction")
@ActionReferences(value = {
    @ActionReference(path = "Nodes/Organism/Actions", position = 500)
})
@NbBundle.Messages("CTL_NodeStudySetAction=Create Study Set")
public class NodeStudySetAction extends AbstractAction implements LookupListener, ContextAwareAction, Presenter.Popup {

    private final Lookup context;
    private Lookup.Result<CustomizableEntity> lkpInfo;
    private RequestProcessor.Task bodyTask;

    public NodeStudySetAction() {
        this(Utilities.actionsGlobalContext());
    }

    public NodeStudySetAction(Lookup context) {
        super(Bundle.CTL_NodeStudySetAction());
        this.context = context;
        bodyTask = new RequestProcessor("DownloadBody").create(new Runnable() { // NOI18N
            @Override
            public void run() {
                doPerform();
            }
        });
    }

    private void init() {
        if (lkpInfo != null) {
            return;
        }
        lkpInfo = context.lookupResult(CustomizableEntity.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
    }

    private void doPerform() {
        Collection<? extends CustomizableEntity> entities = lkpInfo.allInstances();
        FocusEntity focus = null;
        Set<Integer> ids = new HashSet<Integer>();
        for (CustomizableEntity entity : entities) {
            if (focus == null) {
                focus = FocusEntity.getEntity(entity.getEntityName());
            }
            ids.add(entity.getId());
        }
        edu.uncc.genosets.studyset.CreateStudySetAction createAction = new edu.uncc.genosets.studyset.CreateStudySetAction(focus, ids);
        createAction.actionPerformed(null);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        bodyTask.schedule(0);
        if ("waitFinished".equals(e.getActionCommand())) {
            bodyTask.waitFinished();
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Collection<? extends CustomizableEntity> allInstances = lkpInfo.allInstances();
        if (allInstances.isEmpty()) {
            setEnabled(false);
            return;
        }
        String focus = null;
        for (CustomizableEntity entity : allInstances) {
            if (focus == null) {
                focus = entity.getEntityName();
            }
            if (!entity.getEntityName().equals(focus)) {
                setEnabled(false);
                return;
            }
        }
    }

    @Override
    public boolean isEnabled() {
        init();
        return super.isEnabled();
    }

    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new NodeStudySetAction(context);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return new JMenuItem(this);
    }
}
