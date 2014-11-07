/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.featuregodetail.view;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.studyset.GoTerm;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class FeatureNodeFactory extends ChildFactory<GoTerm>{
    Feature feature;
    public FeatureNodeFactory(Feature f){
        this.feature = f;
    }

    @Override
    protected boolean createKeys(List<GoTerm> list) {
        //get the cluster details
        StringBuilder bldr = new StringBuilder();
        bldr.append("SELECT t.clusterName from Fact_Feature_GoAnno as f, Cluster_GoTerm as t where t.featureClusterId = f.featureClusterId and f.featureId = ").append(feature.getFeatureId());
        List<String> result = DataManager.getDefault().createQuery(bldr.toString());
        for (String id : result) {
            list.add(new GoTerm(id));
        }
        return true;
    }

    @Override
    protected Node[] createNodesForKey(GoTerm key) {
        AbstractNode node = new AbstractNode(Children.LEAF, Lookups.singleton(key));
        node.setDisplayName(key.getGoId() + " " +key.getGoName());
        node.setDisplayName(key.getGoName());
        return new Node[]{node};
    }
    
    
}
