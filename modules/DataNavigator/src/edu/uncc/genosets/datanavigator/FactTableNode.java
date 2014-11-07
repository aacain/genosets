/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author lucy
 */
public class FactTableNode extends AbstractNode{
    //private final List<AnnotationMethod> methods;

    public FactTableNode(Fact fact){
        //super(Children.create(new AnnotationMethodFactory(factType, methods), true));
        super((fact.getMethods() != null && !fact.getMethods().isEmpty()) ? Children.create(new MethodTypeFactory(fact), true) : Children.LEAF, Lookups.singleton(fact));
        this.setName(fact.getDisplayName());
        this.setDisplayName(fact.getDisplayName());
        this.setIconBaseWithExtension((fact.getMethods() != null && !fact.getMethods().isEmpty()) 
                ? "edu/uncc/genosets/datanavigator/resources/facts.gif" : "edu/uncc/genosets/datanavigator/resources/facts_empty.gif");
    }
}
