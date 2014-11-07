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
package edu.uncc.genosets.parsetsbridge;

import edu.uncc.genosets.parsetsbridge.property.GenoSetsController;
import edu.uncc.genosets.parsetsbridge.property.GenoSetsDimensionHandle;
import edu.uncc.genosets.queries.DimensionChangeListener;
import edu.uncc.genosets.queries.DimensionEvent;
import edu.uncc.genosets.queries.DimensionObject;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.SwingUtilities;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class DimensionObjectChildren extends Children.Keys<DimensionObject> implements DimensionChangeListener {

    private final DimensionObject dimObject;
    private final GenoSetsController controller;

    DimensionObjectChildren(DimensionObject dimObj, GenoSetsController controller) {
        this.dimObject = dimObj;
        this.controller = controller;
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        this.dimObject.addDimensionChangeListener(WeakListeners.create(DimensionChangeListener.class, this, this.dimObject));
        this.setKeys(this.dimObject.getChildren(false));
    }

    @Override
    protected Node[] createNodes(DimensionObject key) {
        ArrayList<Node> nodeList = new ArrayList<Node>();
        if (key.isFolder()) {
            Node node = new DimensionPathNode(key, controller);
            nodeList.add(node);
        } else {
            GenoSetsDimensionHandle dimHandle = this.dimObject.getLookup().lookup(GenoSetsDimensionHandle.class);
            this.dimObject.getLookup().lookupResult(GenoSetsDimensionHandle.class).allInstances();
            Collection<? extends GenoSetsDimensionHandle> allInstances = key.getLookup().lookupResult(GenoSetsDimensionHandle.class).allInstances();
            for (GenoSetsDimensionHandle genoSetsDimensionHandle : allInstances) {
                dimHandle = genoSetsDimensionHandle;
            }
            //nodeList.add(new DimensionNode(key, view, dimHandle));
            nodeList.add(new DimensionNode(key, dimHandle, controller));
        }

        return nodeList.toArray(new Node[nodeList.size()]);
    }

    @Override
    public void dimensionChanged(DimensionEvent de) {
    }

    @Override
    public void dimensionAdded(final DimensionEvent de) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setKeys(de.getFiredFrom().getChildren(false));
            }
        });
    }

    @Override
    public void dimensionRemoved(final DimensionEvent de) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setKeys(de.getFiredFrom().getChildren(false));
            }
        });

    }

    @Override
    public void dimensionInitalized(DimensionEvent de) {
        return;
    }

    public static class DimensionPathNode extends AbstractNode {

        public DimensionPathNode(DimensionObject key, GenoSetsController controller) {
            super(new DimensionObjectChildren(key, controller), Lookups.singleton(key));
            this.setName(key.getName());
            this.setDisplayName(key.getName());
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set setProps = Sheet.createPropertiesSet();
            setProps.setDisplayName("Properties");
            sheet.put(setProps);
            final DimensionObject dimObj = getLookup().lookup(DimensionObject.class);
            if (dimObj != null) {
                Node.Property<String> pathProp = new PropertySupport.ReadOnly<String>("path", String.class, "Path", "Path of this dimension") {
                    @Override
                    public String getValue() throws IllegalAccessException, InvocationTargetException {
                        return dimObj.getPath();
                    }
                };
                setProps.put(pathProp);
            }
            
            return sheet;
        }
    }
}
