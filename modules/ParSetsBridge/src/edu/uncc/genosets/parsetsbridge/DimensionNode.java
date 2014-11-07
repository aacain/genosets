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
import edu.uncc.genosets.queries.DimensionObject;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.openide.explorer.view.CheckableNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

/**
 *
 * @author aacain
 */
public class DimensionNode extends AbstractNode implements CheckableNode, PropertyChangeListener {

    private final GenoSetsDimensionHandle dim;
    private boolean selected = false;
    private final GenoSetsController controller;

    public DimensionNode(DimensionObject key, GenoSetsDimensionHandle dim, GenoSetsController controller) {
        super(new CategoryChildren(dim, controller));
        this.dim = dim;
        this.controller = controller;
        this.setName(dim.getHandle());
        this.setDisplayName(dim.getName());
        this.setIconBaseWithExtension("edu/uncc/genosets/parsetsbridge/resources/dimension.png");
    }

    @Override
    public boolean isCheckable() {
        return Boolean.TRUE;
    }

    @Override
    public boolean isCheckEnabled() {
        return Boolean.TRUE;
    }

    @Override
    public Boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(Boolean selected) {
        boolean old = this.selected;
        this.selected = selected;
        if (old != this.selected) {
            if (this.selected) {
                this.controller.addAxis(dim);
            } else {
                this.controller.removeAxis(dim);
            }
        }
        //update children
        Children children = this.getChildren();
        for (Node node : children.getNodes()) {
            if (node instanceof CategoryChildren.CategoryNode) {
                CategoryChildren.CategoryNode ck = (CategoryChildren.CategoryNode) node;
                if (this.selected) {
                    ck.setDimensionSelected(Boolean.TRUE);
                } else {
                    ck.setDimensionSelected(Boolean.FALSE);
                }
            }
        }
    }

    public void updateParent() {
        //update children
        Children children = this.getChildren();
        for (Node node : children.getNodes()) {
            if (node instanceof CategoryChildren.CategoryNode) {
                CategoryChildren.CategoryNode ck = (CategoryChildren.CategoryNode) node;
                if (ck.isSelected()) {
                    this.selected = true;
                    fireIconChange();
                    return;
                }
            }
        }
        this.selected = false;
        fireIconChange();

    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set setProps = Sheet.createPropertiesSet();
        setProps.setDisplayName("Properties");
        sheet.put(setProps);
        Object descriptionObject = dim.getGroup().getDescriptionObject();
        if (descriptionObject != null) {
            try {
                for (PropertyDescriptor pd : Introspector.getBeanInfo(descriptionObject.getClass()).getPropertyDescriptors()) {
                    Node.Property other = new PropertySupport.Reflection(descriptionObject, pd.getPropertyType(), pd.getReadMethod(), pd.getWriteMethod()) {
                        @Override
                        public boolean canWrite() {
                            return Boolean.FALSE;
                        }
                    };
                    if (other.getPropertyEditor() != null && !other.getName().equals("class")) {
                        other.setDisplayName(updateDisplayName(pd.getDisplayName()).toString());
                        setProps.put(other);
                    }
                }
            } catch (IntrospectionException ex) {
                
            }
        }
        return sheet;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    protected static StringBuilder updateDisplayName(String name) {
        StringBuilder bldr = new StringBuilder(name);
        for (int bldrIndex = 0; bldrIndex < bldr.length(); bldrIndex++) {
            char charAt = bldr.charAt(bldrIndex);
            if (Character.isUpperCase(charAt)) {
                bldr.replace(bldrIndex, bldrIndex + 1, Character.toString(Character.toLowerCase(charAt)));
                bldr.insert(bldrIndex, ' ');
                bldrIndex++;
            }
        }
        return bldr;
    }
}