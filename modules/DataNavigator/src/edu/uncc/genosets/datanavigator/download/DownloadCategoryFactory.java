/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.download;

import edu.uncc.genosets.datamanager.api.DownloadSet;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.AbstractAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class DownloadCategoryFactory extends ChildFactory.Detachable<DownloadSetCategory> implements PropertyChangeListener {

    private final static String METHODS = "Included Methods";
    private final static String FORMATS = "File Formats";
    private final static String SETTINGS = "Settings";
    private final DownloadSet ds;

    public DownloadCategoryFactory(DownloadSet ds) {
        this.ds = ds;
    }

    @Override
    protected boolean createKeys(List<DownloadSetCategory> toPopulate) {
        toPopulate.add(new DownloadSetCategory(METHODS, "edu/uncc/genosets/datanavigator/resources/source.gif"));
        toPopulate.add(new DownloadSetCategory(FORMATS, "edu/uncc/genosets/datanavigator/resources/folder_open.png" ));
//        toPopulate.add(new DownloadSetCategory(SETTINGS, "edu/uncc/genosets/datanavigator/resources/importantfiles.png"));
        return true;
    }

    @Override
    protected Node createNodeForKey(DownloadSetCategory key) {
        AbstractNode n = null;
        if (METHODS.equals(key.getName())) {
            n = new AbstractNode(Children.create(new MethodChildFactory(ds), true));
            n.setName("A");
        } else if (FORMATS.equals(key.getName())) {
            n = new FormatParentNode(ds);
            n.setName("B");
       } else if (SETTINGS.equals(key.getName())) {
            n = new AbstractNode(Children.LEAF);
            n.setName("C");
        }else {
            n = new AbstractNode(Children.LEAF);
        }
        if (key.getIconBase() != null) {
            n.setIconBaseWithExtension(key.getIconBase());
        }
        n.setDisplayName(key.getName());
        return n;
    }

    @Override
    protected void addNotify() {
        ds.addPropertyChangeListner(this);
    }

    @Override
    protected void removeNotify() {
        ds.removePropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
        //refresh(false);
    }
    
    public class EditSettingsAction extends AbstractAction{

        public EditSettingsAction() {
            super("Edit");
        }  
        
        @Override
        public void actionPerformed(ActionEvent e) {
            
        }
    }
}
