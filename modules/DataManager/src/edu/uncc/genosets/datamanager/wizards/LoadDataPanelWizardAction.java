/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datamanager.wizards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Set;
import org.openide.ErrorManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

@ActionID(id = "com.aacain.template.wizard.LoadDataPanelWizardAction", category = "Load")
@ActionRegistration(iconInMenu = true, displayName = "#CTL_LoadDataAction", iconBase = "edu/uncc/genosets/datamanager/resources/add.png")
@ActionReferences(value = {
    @ActionReference(path = "Menu/Add Data", position = 1100),
    @ActionReference(path = "Toolbars/Data", position = 100)})
public final class LoadDataPanelWizardAction implements ActionListener {

    private FileObject templatesFolder;
    private RequestProcessor.Task bodyTask;

    public LoadDataPanelWizardAction() {
        bodyTask = new RequestProcessor("LoadDataBody").create(new Runnable() { // NOI18N
            @Override
            public void run() {
                doPerform();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        bodyTask.schedule(0);

        if ("waitFinished".equals(e.getActionCommand())) {
            bodyTask.waitFinished();
        }
    }

    private void doPerform() {
        if (templatesFolder == null) {
            templatesFolder = FileUtil.getConfigRoot().getFileObject("Templates/Load");
        }
        final TemplateWizard wizard = new GenoSetsTemplateWizard(templatesFolder);

        final Set newObjects;
        try {
            newObjects = wizard.instantiate();
        } catch (IOException e) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, e);
            return;
        }
    }
}
