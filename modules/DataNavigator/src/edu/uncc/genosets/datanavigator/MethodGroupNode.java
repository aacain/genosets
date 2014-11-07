/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author lucy
 */
public class MethodGroupNode extends AbstractNode{
    public MethodGroupNode(List<AnnotationMethod> methods, String name, Children children) {
        super(children);
        this.setName(name == null ? "null" : name);
        this.setDisplayName(name == null ? "null" : name);
        this.setIconBaseWithExtension("edu/uncc/genosets/datanavigator/resources/source.gif");
    }
}
