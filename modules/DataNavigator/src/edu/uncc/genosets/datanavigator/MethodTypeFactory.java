/*
 * Copyright (C) 2013 Aurora Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
 * @author aacain
 */
public class MethodTypeFactory extends ChildFactory.Detachable<Map.Entry<String, List<AnnotationMethod>>> {

    private final Fact fact;
    
    public MethodTypeFactory(Fact fact) {
        this.fact = fact;
    }
    
    @Override
    protected boolean createKeys(List<Map.Entry<String, List<AnnotationMethod>>> toPopulate) {
        HashMap<String, List<AnnotationMethod>> map = new HashMap<String, List<AnnotationMethod>>();
        if (fact.getMethods() != null) {
            for (AnnotationMethod m : fact.getMethods()) {
                List<AnnotationMethod> get = map.get(m.getMethodType());
                if (get == null) {
                    get = new LinkedList<AnnotationMethod>();
                    map.put(m.getMethodType(), get);
                }
                get.add(m);
            }
            toPopulate.addAll(map.entrySet());
        }
        return true;
    }
    
    @Override
    protected Node createNodeForKey(Map.Entry<String, List<AnnotationMethod>> key) {
        Node n = new MethodGroupNode(key.getValue(), key.getKey(), Children.create(new MethodSourceFactory(fact, key.getValue()), true));
        return n;
    }
}
