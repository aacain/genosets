/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.download;

import edu.uncc.genosets.datamanager.api.DownloadFormat;
import edu.uncc.genosets.datamanager.api.DownloadSet;
import edu.uncc.genosets.datanavigator.actions.EditFormatAction;
import java.util.ArrayList;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class FormatNode extends AbstractNode {

    private final DownloadFormat format;
    private final DownloadSet ds;

    public FormatNode(DownloadSet set, DownloadFormat format) {
        super(Children.LEAF, Lookups.singleton(format));
        setDisplayName(format.getFormatFolderName());
        this.format = format;
        this.ds = set;
    }

    @Override
    public Action[] getActions(boolean context) {
        ArrayList<Action> actions = new ArrayList<Action>();
        actions.add(new EditFormatAction(format, ds));
        return actions.toArray(new Action[actions.size()]);
    }
}
