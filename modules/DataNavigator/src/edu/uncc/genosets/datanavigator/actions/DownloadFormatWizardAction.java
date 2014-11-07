/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.actions;

import edu.uncc.genosets.datamanager.api.DownloadFormat;
import edu.uncc.genosets.datamanager.api.DownloadSet;
import edu.uncc.genosets.datanavigator.download.wizard.DownloadWizard;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Set;
import javax.swing.AbstractAction;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author aacain
 */
@NbBundle.Messages("CTL_DownloadFormatWizardAction=New File Type")
public class DownloadFormatWizardAction extends AbstractAction {

    private static DataObject selectedTemplate;
    private static DataFolder templatesFolder;
    private DownloadSet ds;

    public DownloadFormatWizardAction(DownloadSet ds) {
        this.ds = ds;
        this.putValue(NAME, NbBundle.getMessage(DownloadFormatWizardAction.class, "CTL_DownloadFormatWizardAction"));
    }

    public void actionPerformed(ActionEvent e) {
        String factType = ds.getFactType().getClass().getName().replaceAll("\\.", "-");
        FileObject folder = FileUtil.getConfigFile("Templates/Downloads/" + factType);
        if (folder != null && folder.isFolder()) {
            templatesFolder = DataFolder.findFolder(folder);
        }
        DownloadWizard wizard = new DownloadWizard(this.ds, null);

        if (wizard instanceof TemplateWizard) {
            if (selectedTemplate != null && selectedTemplate.isValid()) {
                wizard.setTemplate(selectedTemplate);
            }
            if (templatesFolder != null && templatesFolder.isValid()) {
                wizard.setTemplatesFolder(templatesFolder);
            }
        }
        boolean instantiated = false;
        try {
            wizard.putProperty(DownloadFormat.WIZARD_DOWNLOAD_SET_OBJECT, ds);
            // clears the name to default
            wizard.setTargetName(null);
            // instantiates
            Set<DataObject> instantiate = wizard.instantiate();
            //instantiated = wizard.instantiate() != null;

        } catch (IOException exception) {
            Exceptions.attachLocalizedMessage(exception,
                    org.openide.util.NbBundle.getMessage(org.openide.loaders.DataObject.class,
                    "EXC_TemplateFailed"));
            Exceptions.printStackTrace(exception);
        }
    }
}
