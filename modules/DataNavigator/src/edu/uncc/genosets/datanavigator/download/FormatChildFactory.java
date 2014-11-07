/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.datanavigator.download;

import edu.uncc.genosets.datamanager.api.DownloadFormat;
import edu.uncc.genosets.datamanager.api.DownloadSet;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class FormatChildFactory extends ChildFactory.Detachable<DownloadFormat> implements PropertyChangeListener{
    private final DownloadSet ds;

    public FormatChildFactory(DownloadSet ds){
        this.ds = ds;
    }
    
    @Override
    protected boolean createKeys(List<DownloadFormat> toPopulate) {
        for (DownloadFormat downloadFormat : ds.getFormats()) {
            toPopulate.add(downloadFormat);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(DownloadFormat key) {
        FormatNode n = new FormatNode(ds, key);
        return n;
    }
    
    

    @Override
    protected void addNotify() {
        this.ds.addPropertyChangeListner(this);
    }

    @Override
    protected void removeNotify() {
        this.ds.removePropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(DownloadSet.PROP_FORMATS_LIST.equals(evt.getPropertyName())){
            refresh(false);
        }
    }
}
