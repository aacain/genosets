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
package edu.uncc.genosets.studyset.listview;

import edu.uncc.genosets.studyset.actions.FocusChangedAction;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.propertieseditor.PropertiesEditable;
import edu.uncc.genosets.propertieseditor.ProxyProperty;
import edu.uncc.genosets.studyset.StudySet;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.actions.Savable;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.NodeListener;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;
import org.springframework.util.ConcurrentReferenceHashMap;

/**
 *
 * @author aacain
 */
public class FeatureNode extends AbstractNode implements PropertiesEditable, ListViewProperties, GroupHierarchy.HierarchyGroupChangeListener, Savable {

    private Sheet.Set editableProperties;
    private static final ConcurrentReferenceHashMap<Integer, Organism> orgs = new ConcurrentReferenceHashMap<Integer, Organism>();
    private final String[] props = new String[]{
        "id", "ID",
        "product", "Product",
        "organism", "Organism",
        "assembledUnit", "Assembled Unit",
        "repUnitName", "Rep Unit Name"};
    private static List<? extends Action> registeredActions;
    NodeListener lis;
    private GroupHierarchy groupHierarchy;
    private final int level;

    /**
     * Create a FeatureNode.
     *
     * @param studySet
     * @param organism - can be null
     * @param feature
     * @param hierarchy
     * @param level
     */
    public FeatureNode(StudySet studySet, Organism organism, Feature feature, GroupHierarchy hierarchy, int level) {
        this(studySet, organism, feature, hierarchy, level, createLookup(studySet, organism, feature));
    }

    /**
     * FeatureNode with the given lookup. The lookup should contain the feature
     * and the studySet.
     *
     * @param studySet
     * @param organism - can be null
     * @param feature
     * @param hierarchy
     * @param level
     * @param lookup
     */
    private FeatureNode(StudySet studySet, Organism organism, Feature feature, GroupHierarchy hierarchy, int level, Lookup lookup) {
        super(hierarchy.getChildren(level, studySet, null, organism, null, feature, null), lookup);
        this.setName(feature.getPrimaryName());
        this.setIconBaseWithExtension("edu/uncc/genosets/studyset/resources/dna-icon.png");
        this.groupHierarchy = hierarchy;
        this.level = level;
        this.groupHierarchy.addHierarchyGroupChangeListener(WeakListeners.create(GroupHierarchy.HierarchyGroupChangeListener.class, this, this.groupHierarchy));
    }

    private static Lookup createLookup(StudySet studySet, Organism organism, Feature feature) {
        if (organism == null) {
            return Lookups.fixed(studySet, feature);
        } else {
            return Lookups.fixed(studySet, feature, organism);
        }
    }

    protected static List<? extends Action> getRegisterActions() {
        if (registeredActions == null) {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/Nodes/Feature"));
            actions.addAll(Utilities.actionsForPath("Actions/Nodes/All"));
            registeredActions = actions;
        }
        return registeredActions;
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<Action>();
        actions.addAll(getRegisterActions());
        actions.addAll(Arrays.asList(super.getActions(context)));
        FocusChangeListener tc = Utilities.actionsGlobalContext().lookup(FocusChangeListener.class);
        if (tc != null) {
            actions.add(new FocusChangedAction(tc, this));
        }
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    protected Sheet createSheet() {

//        Node.Property<Integer> idProp = new PropertySupport.ReadOnly<Integer>("id", Integer.class, "ID", "identification") {
//            @Override
//            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
//                return (null != f) ? f.getFeatureId() : null;
//            }
//        };
//        setProps.put(idProp);
//        editableProperties.put(idProp);
//        
//        Node.Property<String> orgProp = new PropertySupport.ReadOnly<String>("organism", String.class, "Organism", "organism") {
//            @Override
//            public String getValue() throws IllegalAccessException, InvocationTargetException {
//                return (null != org) ? org.getStrain() : "";
//            }
//        };
//        setProps.put(orgProp);
//        editableProperties.put(orgProp);
//        
//        Node.Property<String> locusProp = new PropertySupport.ReadOnly<String>("locus", String.class, "Locus", "Locus") {
//            @Override
//            public String getValue() throws IllegalAccessException, InvocationTargetException {
//                return (null != f.getPrimaryName()) ? f.getPrimaryName() : "";
//            }
//        };
//        setProps.put(locusProp);
//        editableProperties.put(locusProp);
//        
//        Node.Property<String> unProdProp = new PropertySupport.ReadWrite<String>("product", String.class, "Product", "Product") {
//
//            @Override
//            public boolean canWrite() {
//                return false;
//            }
//            
//            @Override
//            public String getValue() throws IllegalAccessException, InvocationTargetException {
//                return (null != f.getProduct()) ? f.getProduct() : "";
//            }
//
//            @Override
//            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//                if (!val.equals(f.getProduct())) {
//                    f.setProduct(val);
//                    DataManager.getDefault().save(f);
//                }
//            }
//        };
//        setProps.put(unProdProp);
//        
//
//        Node.Property<String> assProp = new PropertySupport.ReadOnly<String>("assembledUnit", String.class, "Assembled Unit", "assembled unit") {
//            @Override
//            public String getValue() throws IllegalAccessException, InvocationTargetException {
//                return (null != f) ? f.getAssembledUnit().getAssembledUnitName() : "";
//            }
//        };
//        setProps.put(assProp);
//        editableProperties.put(assProp);
//        
//        Node.Property<String> repNameProp = new PropertySupport.ReadOnly<String>("repUnitName", String.class, "Rep Unit Name", "The name of the replicating name") {
//            @Override
//            public String getValue() throws IllegalAccessException, InvocationTargetException {
//                return (null != f) ? f.getAssembledUnit().getRepUnitName() : "";
//            }
//        };
//        setProps.put(repNameProp);
//        editableProperties.put(repNameProp);
//
//        idProp.setValue("suppressCustomEditor", Boolean.TRUE);
//        locusProp.setValue("suppressCustomEditor", Boolean.TRUE);
//        unProdProp.setValue("suppressCustomEditor", Boolean.TRUE);
//        orgProp.setValue("suppressCustomEditor", Boolean.TRUE);
//        assProp.setValue("suppressCustomEditor", Boolean.TRUE);
//        repNameProp.setValue("suppressCustomEditor", Boolean.TRUE);
//        idProp.setValue("htmlDisplayValue", "<font color='000000'>" + (f.getFeatureId().toString()) + "</font>");
//        locusProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f.getPrimaryName()) ? f.getPrimaryName() : "") + "</font>");
//        productProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f.getProduct()) ? f.getProduct() : "") + "</font>");
//        orgProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f) ? org.getStrain() : "") + "</font>");
//        assProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f) ? f.getAssembledUnit().getAssembledUnitName() : "") + "</font>");
//        repNameProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f) ? f.getAssembledUnit().getRepUnitName() : "") + "</font>");

        final Feature f = getLookup().lookup(Feature.class);
        Organism tempOrg = getLookup().lookup(Organism.class);
        if (tempOrg == null) {
            tempOrg = orgs.get(f.getOrganismId());
            if (tempOrg == null) {
                tempOrg = (Organism) DataManager.getDefault().get("Organism", f.getOrganismId());
            }
        }
        orgs.putIfAbsent(tempOrg.getOrganismId(), tempOrg);
        final Organism org = tempOrg;


        Sheet sheet = Sheet.createDefault();
        Sheet.Set setProps = Sheet.createPropertiesSet();
        setProps.setDisplayName("general");
        sheet.put(setProps);
        
        if(editableProperties == null){
            editableProperties = Sheet.createPropertiesSet();
        }

        setProps.setDisplayName("general");
        Node.Property<Integer> idProp = new PropertySupport.ReadOnly<Integer>("id", Integer.class, "ID", "identification") {
            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                setValue("htmlDisplayValue", "<font color='000000'>" + (f.getFeatureId().toString()) + "</font>");
                return (null != f) ? f.getFeatureId() : null;
            }
        };
        setProps.put(idProp);
        editableProperties.put(idProp);
        
        Node.Property<String> orgProp = new PropertySupport.ReadOnly<String>("organism", String.class, "Organism", "organism") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f) ? org.getStrain() : "") + "</font>");
                return (null != org) ? org.getStrain() : "";
            }
        };
        setProps.put(orgProp);
        editableProperties.put(orgProp);
        
        Node.Property<String> locusProp = new PropertySupport.ReadWrite<String>("locus", String.class, "Locus", "Locus") {
            @Override
            public boolean canWrite() {
                return false;
            }
            
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f.getPrimaryName()) ? f.getPrimaryName() : "") + "</font>");
                return (null != f.getPrimaryName()) ? f.getPrimaryName() : "";
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (!val.equals(f.getPrimaryName())) {
                    f.setPrimaryName(val);
                    setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f.getPrimaryName()) ? f.getPrimaryName() : "") + "</font>");
                    firePropertyChange("locus", null, val);
                }
            }
        };
        setProps.put(locusProp);
        editableProperties.put(new ProxyProperty<String>(this, locusProp, true));
        
        Node.Property<String> productProp = new PropertySupport.ReadWrite<String>("product", String.class, "Product", "Product") {
            @Override
            public boolean canWrite() {
                return false;
            }
            
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f.getProduct()) ? f.getProduct() : "") + "</font>");
                return (null != f.getProduct()) ? f.getProduct() : "";
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (!val.equals(f.getProduct())) {
                    f.setProduct(val);
                    setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f.getProduct()) ? f.getProduct() : "") + "</font>");
                    firePropertyChange("product", null, val);
                }
            }
        };
        setProps.put(productProp);
        editableProperties.put(new ProxyProperty<String>(this, productProp, true));
        
        Node.Property<String> assProp = new PropertySupport.ReadOnly<String>("assembledUnit", String.class, "Assembled Unit", "assembled unit") {
            @Override
            public boolean canWrite() {
                return false;
            }
            
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f) ? f.getAssembledUnit().getAssembledUnitName() : "") + "</font>");
                return (null != f) ? f.getAssembledUnit().getAssembledUnitName() : "";
            }
        };
        setProps.put(assProp);
        editableProperties.put(assProp);
        
        
        Node.Property<String> repNameProp = new PropertySupport.ReadOnly<String>("repUnitName", String.class, "Rep Unit Name", "The name of the replicating name") {

            @Override
            public boolean canWrite() {
                return false;
            }           
            
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f) ? f.getAssembledUnit().getRepUnitName() : "") + "</font>");
                return (null != f) ? f.getAssembledUnit().getRepUnitName() : "";
            }
        };
        setProps.put(repNameProp);
        editableProperties.put(repNameProp);
        
        idProp.setValue("suppressCustomEditor", Boolean.TRUE);
        locusProp.setValue("suppressCustomEditor", Boolean.TRUE);
        productProp.setValue("suppressCustomEditor", Boolean.TRUE);
        orgProp.setValue("suppressCustomEditor", Boolean.TRUE);
        assProp.setValue("suppressCustomEditor", Boolean.TRUE);
        repNameProp.setValue("suppressCustomEditor", Boolean.TRUE);
           
        return sheet;
    }
    

    @Override
    public PropertySet[] getEditablePropertySets() {
        if (editableProperties == null) {
            createSheet();
        }
        return new PropertySet[]{editableProperties};
    }

    @Override
    public String[] getProperties() {
        return props;
    }

    @Override
    public void groupAdded(GroupHierarchy.HierarchyGroupChangeEvent evt) {
        if (evt.getLevel() == this.level) {
            this.setChildren(Children.LEAF);
            this.setChildren(groupHierarchy.getChildren(this.level, getLookup().lookup(StudySet.class), null, null, null, getLookup().lookup(Feature.class), null));
        }
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public GroupHierarchy getHierarchyGroup() {
        return this.groupHierarchy;
    }

    @Override
    public void save() throws IOException {
        Feature f = getLookup().lookup(Feature.class);
        DataManager.getDefault().save(f);
    }
}
