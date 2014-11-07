/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.actions;

import edu.uncc.genosets.datamanager.api.DownloadFormat;
import edu.uncc.genosets.datamanager.api.DownloadSet;
import edu.uncc.genosets.datamanager.api.Editable;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;
import org.openide.util.Exceptions;

/**
 *
 * @author aacain
 */
public class EditFormatAction extends AbstractAction {

    private final DownloadSet ds;
    private final DownloadFormat format;

    public EditFormatAction(DownloadFormat format, DownloadSet ds) {
        putValue(NAME, "Edit");
        this.ds = ds;
        this.format = format;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (format instanceof Editable) {
            Editable editable = (Editable)format;
            try {
                FileObject itObj = FileUtil.getConfigFile(editable.getIteratorPath());
                FileObject folder = itObj.getParent();
                DataObject obj = DataObject.find(itObj);

                TemplateWizard wiz = new TemplateWizard();
                wiz.putProperty(DownloadFormat.WIZARD_DOWNLOAD_FORMAT_OBJECT, format);
                wiz.putProperty(DownloadFormat.WIZARD_DOWNLOAD_SET_OBJECT, ds);
                DataFolder templatesFolder = null;
                if (folder != null && folder.isFolder()) {
                    templatesFolder = DataFolder.findFolder(folder);
                }
                wiz.setTemplatesFolder(templatesFolder);
                wiz.setTemplate(obj);
                wiz.putProperty(WizardDescriptor.PROP_WARNING_MESSAGE, "Do not change the download format type");
                wiz.instantiate();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
