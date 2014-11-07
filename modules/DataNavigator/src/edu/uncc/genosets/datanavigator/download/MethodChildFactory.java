/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.download;

import edu.uncc.genosets.datamanager.api.DownloadSet;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class MethodChildFactory extends ChildFactory.Detachable<AnnotationMethod> implements PropertyChangeListener {

    private final DownloadSet set;

    public MethodChildFactory(DownloadSet set) {
        this.set = set;
    }

    @Override
    protected boolean createKeys(List<AnnotationMethod> toPopulate) {
        for (AnnotationMethod method : set.getMethods()) {
            toPopulate.add(method);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(AnnotationMethod key) {
        AbstractNode n = new AbstractNode(Children.LEAF, Lookups.singleton(key));
        n.setName(key.getAnnotationMethodId().toString());
        n.setDisplayName(key.getMethodName());
        n.setIconBaseWithExtension("edu/uncc/genosets/datanavigator/resources/method.png");
        return n;
    }

    @Override
    protected void addNotify() {
        this.set.addPropertyChangeListner(this);
    }

    @Override
    protected void removeNotify() {
        this.set.removePropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (DownloadSet.PROP_METHODS_LIST.equals(evt.getPropertyName())) {
            refresh(false);
        }
    }
}
