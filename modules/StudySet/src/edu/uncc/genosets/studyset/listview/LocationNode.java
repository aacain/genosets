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

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.studyset.actions.FocusChangedAction;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.propertieseditor.PropertiesEditable;
import edu.uncc.genosets.propertieseditor.ProxyProperty;
import edu.uncc.genosets.studyset.StudySet;
import static edu.uncc.genosets.studyset.listview.OrganismNode.getRegisterActions;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.actions.Savable;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class LocationNode extends AbstractNode implements PropertiesEditable, ListViewProperties, GroupHierarchy.HierarchyGroupChangeListener, Savable {

    private final static String[] props = new String[]{
        "id", "ID",
        "min position", "Min Position",
        "max position", "Max Position",
        "start position", "Start Position",
        "end position", "End Position",
        "length", "Length",
        "positive strand", "Positive Strand",
        "primary name", "Primary Name",
        "product", "Product",
        "type", "Type"};
    private Sheet.Set editableProperties;
    private static List<? extends Action> registeredActions;
    private final GroupHierarchy groupHierarchy;
    private final int level;

    public LocationNode(StudySet studySet, Location location, GroupHierarchy hierarchy, int level) {
        this(studySet, location, hierarchy.getChildren(level, studySet, null, null, null, null, location), hierarchy, level);
    }

    public LocationNode(StudySet studySet, Location location, Children children, GroupHierarchy hierarchy, int level) {
        this(location, children, Lookups.fixed(location, studySet, hierarchy), hierarchy, level);
    }

    private LocationNode(Location location, Children children, Lookup lookup, GroupHierarchy hierarchy, int level) {
        super(children, lookup);
        this.setName(location.getStartPosition() + " - " + location.getEndPosition());
        this.groupHierarchy = hierarchy;
        this.level = level;
        this.groupHierarchy.addHierarchyGroupChangeListener(WeakListeners.create(GroupHierarchy.HierarchyGroupChangeListener.class, this, this.groupHierarchy));
    }

    protected static List<? extends Action> getRegisterActions() {
        if (registeredActions == null) {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/Nodes/Location"));
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
    public String[] getProperties() {
        return getNodeProperties();
    }

    public static String[] getNodeProperties() {
        return props;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set setProps = Sheet.createPropertiesSet();
        setProps.setDisplayName("Properties");
        sheet.put(setProps);
        final Location location = getLookup().lookup(Location.class);
        if(editableProperties == null){
            editableProperties = Sheet.createPropertiesSet();
        }
        
        Property<String> locId = new PropertySupport.ReadOnly<String>("id", String.class, "ID", "db id") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return location.getLocationId().toString();
            }
        };
        setProps.put(locId);
        editableProperties.put(locId);

        Property minPosition = new PropertySupport.ReadOnly<Integer>("min position", Integer.class, "Min Position", "Minimum position on the assembled unit.") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }

            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                if (location.getMinPosition() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + location.getMinPosition() + "</font>");
                }
                return location.getMinPosition();
            }

        };
        setProps.put(minPosition);
        editableProperties.put(minPosition);

        Property maxPosition = new PropertySupport.ReadOnly<Integer>("max position", Integer.class, "Max Position", "Maximum position on the assembled unit.") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }

            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                if (location.getMaxPosition() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + location.getMaxPosition() + "</font>");
                }
                return location.getMaxPosition();
            }
        };
        setProps.put(maxPosition);
        editableProperties.put(maxPosition);

        Property startPosition = new PropertySupport.ReadOnly<Integer>("start position", Integer.class, "Start Position", "Start position on the assembled unit.") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }

            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                if (location.getStartPosition() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + location.getStartPosition() + "</font>");
                }
                return location.getStartPosition();
            }
        };
        setProps.put(startPosition);
        editableProperties.put(startPosition);

        Property endPosition = new PropertySupport.ReadOnly<Integer>("end position", Integer.class, "End Position", "End position on the assembled unit.") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }

            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                if (location.getMinPosition() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + location.getEndPosition() + "</font>");
                }
                return location.getEndPosition();
            }
        };
        setProps.put(endPosition);
        editableProperties.put(endPosition);
        
        Property lengthProp = new PropertySupport.ReadOnly<Integer>("length", Integer.class, "Length", "The length of this nucleotide.") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }

            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                if (location.getMinPosition() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + (location.getMaxPosition() - location.getMinPosition() + 1) + "</font>");
                }
                return (location.getMaxPosition() - location.getMinPosition() + 1);
            }
        };
        setProps.put(lengthProp);
        editableProperties.put(new ProxyProperty<String>(this, lengthProp, true));

        Property strand = new PropertySupport.ReadOnly<Boolean>("positive strand", Boolean.class, "Positive Strand", "True if this location is on the positive strand, false if not.") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }


            @Override
            public Boolean getValue() throws IllegalAccessException, InvocationTargetException {
                if (location.getIsForward() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + location.getIsForward() + "</font>");
                }
                return location.getIsForward();
            }
        };
        setProps.put(strand);
        editableProperties.put(strand);

        Property primaryName = new PropertySupport.ReadWrite<String>("primary name", String.class, "Primary Name", "The primary name of this location.") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }

            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                if (location.getPrimaryName() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + location.getPrimaryName() + "</font>");
                }
                return location.getPrimaryName();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (!val.equals(location.getPrimaryName())) {
                    location.setPrimaryName(val);
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + location.getPrimaryName() + "</font>");
                    firePropertyChange("primary name", null, val);
                }
            }
        };
        setProps.put(primaryName);
        editableProperties.put(new ProxyProperty<String>(this, primaryName, true));


        Property product = new PropertySupport.ReadWrite<String>("product", String.class, "Product", "The product of this location.") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }

            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                if (location.getProduct() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + location.getProduct() + "</font>");
                }
                return location.getProduct();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (!val.equals(location.getProduct())) {
                    location.setProduct(val);
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + location.getProduct() + "</font>");
                    firePropertyChange("product", null, val);
                }
            }
        };
        setProps.put(product);
        editableProperties.put(new ProxyProperty<String>(this, product, true));

        Property lType = new PropertySupport.ReadWrite<String>("type", String.class, "Type", "The feature type of this location.") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }

            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                if (location.getFeatureType() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + location.getFeatureType() + "</font>");
                }
                return location.getFeatureType();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (!val.equals(location.getFeatureType())) {
                    location.setFeatureType(val);
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + location.getFeatureType() + "</font>");
                    firePropertyChange("type", null, val);
                }
            }
        };
        setProps.put(lType);
        editableProperties.put(new ProxyProperty<String>(this, lType, true));

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
    public void groupAdded(GroupHierarchy.HierarchyGroupChangeEvent evt) {
        if (evt.getLevel() == this.level) {
            this.setChildren(Children.LEAF);
            this.setChildren(groupHierarchy.getChildren(this.level, getLookup().lookup(StudySet.class), null, null, null, null, getLookup().lookup(Location.class)));
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
        Location location = getLookup().lookup(Location.class);
        DataManager.getDefault().save(location);
    }
}
