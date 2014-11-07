/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.download;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class MethodListNode extends AbstractNode {

    public MethodListNode() {
        super(Children.LEAF);
        setName("B");
        setIconBaseWithExtension("edu/uncc/genosets/datanavigator/resources/source.gif");
    }
    
    
}
