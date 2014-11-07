/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author lucy
 */
public class MethodSourceFactory extends ChildFactory.Detachable<Map.Entry<String, List<AnnotationMethod>>> {
    private final List<AnnotationMethod> methods;
    private final Fact fact;


    public MethodSourceFactory(Fact fact, List<AnnotationMethod> methods) {
        this.fact = fact;
        this.methods = methods;
    }

    @Override
    protected boolean createKeys(List<Map.Entry<String, List<AnnotationMethod>>> toPopulate) {
        HashMap<String, List<AnnotationMethod>> map = new HashMap<String, List<AnnotationMethod>>();
        if (methods != null) {
            for (AnnotationMethod m : methods) {
                List<AnnotationMethod> get = map.get(m.getMethodSourceType());
                if (get == null) {
                    get = new LinkedList<AnnotationMethod>();
                    map.put(m.getMethodSourceType(), get);
                }
                get.add(m);
            }
            for (Map.Entry<String, List<AnnotationMethod>> entry : map.entrySet()) {
                toPopulate.add(entry);
            }
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Map.Entry<String, List<AnnotationMethod>> key) {
        Node n = new MethodGroupNode(key.getValue(), key.getKey(), Children.create(new AnnotationMethodFactory(fact, key.getValue()), true));
        return n;
    }
}
