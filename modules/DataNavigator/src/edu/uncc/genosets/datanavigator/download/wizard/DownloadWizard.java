/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.download.wizard;

import edu.uncc.genosets.datamanager.api.DownloadFormat;
import edu.uncc.genosets.datamanager.api.DownloadSet;
import org.openide.loaders.TemplateWizard;
import org.openide.util.NbBundle;

/**
 *
 * @author aacain
 */
@NbBundle.Messages("CTL_DownloadWizardTitle=Download File Type")
public class DownloadWizard extends TemplateWizard {

    private DownloadSet ds;
    private DownloadFormat format;

    public DownloadWizard(DownloadSet ds, DownloadFormat format) {
        super();
        this.ds = ds;
        this.format = format;
    }

    @Override
    public String getTitle() {
        return NbBundle.getMessage(DownloadWizard.class, "CTL_DownloadWizardTitle");
    }
}
