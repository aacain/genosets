/*
 * 
 * 
 */
package edu.uncc.genosets.embl.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class SelectedEmblProjectChildren extends Children.Keys<Node> implements PropertyChangeListener {

    private ExplorerManager availableManager;
    private ExplorerManager selectedManager;
    private Set selectedNodes = new TreeSet();

    public SelectedEmblProjectChildren(ExplorerManager availableManager, ExplorerManager selectedManager) {
        this.availableManager = availableManager;
        this.selectedManager = selectedManager;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (ProjectSelectVisualPanel.PROP_ADD.equals(evt.getPropertyName())) {
            Collection toAdd = Arrays.asList(availableManager.getSelectedNodes());
            selectedNodes.addAll(toAdd);
            this.setKeys(selectedNodes);
        }else if(ProjectSelectVisualPanel.PROP_REMOVE.equals(evt.getPropertyName())) {
            Collection toRemove = Arrays.asList(selectedManager.getSelectedNodes());
            selectedNodes.removeAll(toRemove);
            this.setKeys(selectedNodes);
        }else if(ProjectSelectVisualPanel.PROP_ADDALL.equals(evt.getPropertyName())) {

        }else if(ProjectSelectVisualPanel.PROP_REMOVEALL.equals(evt.getPropertyName())) {
            selectedNodes = new TreeSet();
            this.setKeys(selectedNodes);
        }
    }

    @Override
    protected Node[] createNodes(Node key) {
        if(key instanceof EmblProjectNode){
            return new Node[]{this.copyNode((EmblProjectNode) key)};
        }

        return new Node[]{};
    }

    private Node copyNode(EmblProjectNode key) {
        return new EmblProjectNode(key);
    }

}
