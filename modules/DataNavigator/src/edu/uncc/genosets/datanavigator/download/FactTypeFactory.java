/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.datanavigator.download;

import edu.uncc.genosets.datamanager.api.FactType;
import edu.uncc.genosets.datanavigator.FactFlavor;
import java.util.Collection;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class FactTypeFactory extends ChildFactory.Detachable<FactType>{
    private final Collection<? extends FactType> factTypes;

    public FactTypeFactory(Collection<? extends FactType> factTypes){
        this.factTypes = factTypes;
    }
    @Override
    protected boolean createKeys(List<FactType> toPopulate) {
        for (FactType factType : factTypes) {
            toPopulate.add(factType);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(FactType key) {
        AbstractNode n = new AbstractNode(Children.LEAF, Lookups.singleton(key));
        n.setDisplayName(key.getName());
        return n;
    }
    
}
