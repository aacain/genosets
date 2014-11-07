/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.featuregodetail.view;

import edu.uncc.genosets.datamanager.entity.Feature;
import java.util.Collection;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class AllFeatureNodeFactory extends ChildFactory<Feature>{
    private final Collection<? extends Feature> features;

    public AllFeatureNodeFactory(Collection<? extends Feature> features){
        this.features = features;
    }
    
    @Override
    protected boolean createKeys(List<Feature> toPopulate) {
        toPopulate.addAll(features);
        return true;
    }

    @Override
    protected Node[] createNodesForKey(Feature key) {
        Children children = Children.create(new FeatureNodeFactory(key), true);
        AbstractNode node = new AbstractNode(children);
        node.setDisplayName(key.getPrimaryName() + " " + key.getProduct());
        return new Node[]{node};
    }
 
}
