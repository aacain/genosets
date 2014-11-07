/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.download;

import edu.uncc.genosets.datamanager.api.DownloadSet;
import edu.uncc.genosets.datanavigator.actions.DownloadFormatWizardAction;
import java.util.ArrayList;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author aacain
 */
public class FormatParentNode extends AbstractNode {

    private final DownloadSet ds;

    public FormatParentNode(DownloadSet ds) {
        super(Children.create(new FormatChildFactory(ds), true));
        this.ds = ds;
        setDisplayName("File Formats");
        setIconBaseWithExtension("edu/uncc/genosets/datanavigator/resources/source.gif");
    }

    @Override
    public Action[] getActions(boolean context) {
        ArrayList<Action> actions = new ArrayList<Action>();
        actions.add(new DownloadFormatWizardAction(ds));
        return actions.toArray(new Action[actions.size()]);
    }
}
