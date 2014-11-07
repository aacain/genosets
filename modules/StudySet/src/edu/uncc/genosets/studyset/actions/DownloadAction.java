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
package edu.uncc.genosets.studyset.actions;

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.download.DownloadStudyWizard;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
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
        id = "edu.uncc.genosets.studyset.actions.CreateStudySetAction")
@ActionRegistration(displayName = "#CTL_Download", iconBase = "edu/uncc/genosets/icons/download.png")
@ActionReferences(value = {
    @ActionReference(path = "StudySet/Nodes/Actions", position = 500),
    @ActionReference(path = "Toolbars/Data", position = 200)
})
@NbBundle.Messages("CTL_Download=Download")
public class DownloadAction extends AbstractAction implements LookupListener, ContextAwareAction, Presenter.Popup {

    private final Lookup context;
    private Lookup.Result<StudySet> lkpInfo;
    private FileObject templateFolder;
    private RequestProcessor.Task bodyTask;

    public DownloadAction() {
        this(Utilities.actionsGlobalContext());
    }

    public DownloadAction(Lookup context) {
        super(Bundle.CTL_Download(), ImageUtilities.loadImageIcon("edu/uncc/genosets/icons/download.png", true));
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
        lkpInfo = context.lookupResult(StudySet.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public boolean isEnabled() {
        init();
        return super.isEnabled();
    }

    private void doPerform() {
        Collection<? extends StudySet> studySets = lkpInfo.allInstances();
        DownloadStudyWizard wizard = new DownloadStudyWizard(templateFolder, studySets);
        boolean instantiated = false;
        wizard.putProperty("studySet", studySets);
        // clears the name to default
        wizard.setTargetName(null);
        try {
            // instantiates
            Set<DataObject> newObjects = wizard.instantiate();
            instantiated = newObjects != null;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Collection<? extends StudySet> allInstances = lkpInfo.allInstances();
                if (allInstances.isEmpty()) {
                    setEnabled(false);
                    return;
                }
                FocusEntity focus = null;
                for (StudySet studySet : lkpInfo.allInstances()) {
                    if (focus == null) {
                        focus = studySet.getFocusEntity();
                    }
                    if (studySet.getFocusEntity() != focus) {
                        setEnabled(false);
                        templateFolder = null;
                        return;
                    }
                }
                //        Enumeration<? extends FileObject> children = FileUtil.getConfigRoot().getChildren(Boolean.TRUE);
                //        while(children.hasMoreElements()){
                //            System.out.println(children.nextElement().getPath());
                //        }
                FileObject configFile = FileUtil.getConfigFile("C/anotherShadow.shadow");
                templateFolder = FileUtil.getConfigFile("Templates/Downloads/" + focus.getEntityName());
                if (templateFolder == null) {
                    setEnabled(false);
                    return;
                }
                setEnabled(true);
            }
        });
    }

    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new DownloadAction(context);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return new JMenuItem(this);
    }
}
