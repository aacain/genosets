/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.parsetsbridge;

import edu.uncc.genosets.parsetsbridge.property.GenoSetsCategoryHandle;
import edu.uncc.genosets.parsetsbridge.property.GenoSetsController;
import edu.uncc.genosets.parsetsbridge.property.GenoSetsDimensionHandle;
import edu.uncc.parsets.data.CategoryHandle;
import java.util.ArrayList;
import java.util.Collection;
import org.openide.explorer.view.CheckableNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

/**
 *
 * @author aacain
 */
public class CategoryChildren extends Children.Keys<GenoSetsCategoryHandle> {

    private final GenoSetsDimensionHandle dim;
    private final GenoSetsController controller;

    CategoryChildren(GenoSetsDimensionHandle dim, GenoSetsController controller) {
        this.dim = dim;
        this.controller = controller;
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        Collection<GenoSetsCategoryHandle> cats = new ArrayList(this.dim.getCategories().size());
        for (CategoryHandle categoryHandle : this.dim.getCategories()) {
            cats.add((GenoSetsCategoryHandle) categoryHandle);
        }
        this.setKeys(cats);
    }

    @Override
    protected Node[] createNodes(GenoSetsCategoryHandle key) {
        return new Node[]{new CategoryNode(key, controller)};
    }

    public static class CategoryNode extends AbstractNode implements CheckableNode {

        private final GenoSetsCategoryHandle cat;
        private Boolean selected = null;
        private final GenoSetsController controller;

        public CategoryNode(GenoSetsCategoryHandle cat, GenoSetsController controller) {
            super(Children.LEAF);
            this.cat = cat;
            this.setName(this.cat.getHandle());
            this.setDisplayName(this.cat.getName());
            this.setIconBaseWithExtension("edu/uncc/genosets/parsetsbridge/resources/category.png");
            this.controller = controller;
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
            if (selected != null) {
                return selected;
            }
            if (this.getParentNode() instanceof DimensionNode) {
                selected = ((DimensionNode) this.getParentNode()).isSelected();
            }
            return selected;
        }

        @Override
        public void setSelected(Boolean selected) {
            boolean old = this.selected;
            this.selected = selected;
            if (old != this.selected) {
                if (this.selected) {
                    this.controller.addCategory(cat.getDimension(), cat);
                } else {
                    this.controller.removeCategory(cat.getDimension(), cat);
                }
                if (this.getParentNode() instanceof DimensionNode) {
                    ((DimensionNode) this.getParentNode()).updateParent();
                }
                this.fireIconChange();
            }
        }

        void setDimensionSelected(boolean selected) {
            this.selected = selected;
            this.fireIconChange();
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set setProps = Sheet.createPropertiesSet();
            setProps.setDisplayName("Properties");
            sheet.put(setProps);
            
            return sheet;
        }
    }
}
