/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.datanavigator.download;

import edu.uncc.genosets.datamanager.api.DownloadSet;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *  
 * @author aacain
 */
public class DownloadSetFactory extends ChildFactory.Detachable<DownloadSet> implements ChangeListener{
    private final DownloadSetList model;

    public DownloadSetFactory(DownloadSetList model){
        this.model = model;
    }

    @Override
    protected boolean createKeys(List<DownloadSet> toPopulate) {
        for (DownloadSet downloadSet : model.list()) {
            toPopulate.add(downloadSet);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(DownloadSet key) {
        Node n = new DownloadSetNode(model, key);
        return n;
    }

    @Override
    protected void addNotify() {
        model.addChangeListener(this);
    }

    @Override
    protected void removeNotify() {
        System.out.println("Removed listener");
        model.removeChangeListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        refresh(false);
    }

}
