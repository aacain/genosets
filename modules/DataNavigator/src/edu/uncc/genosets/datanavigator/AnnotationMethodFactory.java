/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author lucy
 */
public class AnnotationMethodFactory extends ChildFactory.Detachable<AnnotationMethod> {

    private final List<AnnotationMethod> methods;
    private final Fact fact;

    public AnnotationMethodFactory(Fact fact, List<AnnotationMethod> methodsBySource) {
        this.fact = fact;
        this.methods = methodsBySource;
    }

    @Override
    protected boolean createKeys(List<AnnotationMethod> toPopulate) {
        if(methods != null){
            toPopulate.addAll(methods);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(AnnotationMethod key) {
        return new AnnotationMethodNode(fact, key);
    }

}
