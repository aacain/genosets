/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.actions;

import edu.uncc.genosets.datamanager.api.DownloadFormat;
import edu.uncc.genosets.datamanager.api.DownloadSet;
import edu.uncc.genosets.datamanager.api.DownloadException;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author aacain
 */
@NbBundle.Messages("CTL_DownloadAction=Download")
public class DownloadAction extends AbstractAction {

    private final DownloadSet ds;

    public DownloadAction(DownloadSet ds) {
        this.ds = ds;
        this.putValue(NAME, NbBundle.getMessage(DownloadAction.class, "CTL_DownloadAction"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (ds.getMethods().size() > 0) {
            List<? extends DownloadFormat> formats = ds.getFormats();
            for (DownloadFormat f : formats) {
                try {
                    f.download();
                } catch (DownloadException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
