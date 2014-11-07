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
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.FactDetailLocation;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.propertieseditor.PropertiesEditable;
import edu.uncc.genosets.propertieseditor.ProxyProperty;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.actions.FocusChangedAction;
import static edu.uncc.genosets.studyset.listview.LocationNode.getRegisterActions;
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
public class DetailNode extends AbstractNode implements PropertiesEditable, ListViewProperties, GroupHierarchy.HierarchyGroupChangeListener, Savable {

    private Sheet.Set editableProperties;
    private static List<? extends Action> registeredActions;
    private final GroupHierarchy groupHierarchy;
    private final int level;
    private final static String[] props = new String[]{
        "id", "ID",
        "detailType", "Detail Type",
        "detailValue", "Detail Value"};

    public DetailNode(StudySet studySet, AnnotationMethod annoMethod, Feature feature, Location location, FactDetailLocation detail, GroupHierarchy hierarchy, int level) {
        this(studySet, location, detail, hierarchy, level, createLookup(studySet, feature, location, detail));
    }

    private DetailNode(StudySet studySet, Location location, FactDetailLocation detail, GroupHierarchy hierarchy, int level, Lookup lookup) {
        super(hierarchy.getChildren(level, studySet, null, null, null, null, location), lookup);
        this.setName(detail.getFactId().toString());
        this.setDisplayName(detail.getDetailType() + ":" + detail.getDetailValue());
        this.groupHierarchy = hierarchy;
        this.level = level;
        this.groupHierarchy.addHierarchyGroupChangeListener(WeakListeners.create(GroupHierarchy.HierarchyGroupChangeListener.class, this, this.groupHierarchy));
    }
    
    protected static List<? extends Action> getRegisterActions() {
        if (registeredActions == null) {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/Nodes/FactDetailLocation"));
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

    private static Lookup createLookup(StudySet studySet, Feature feature, Location location, FactDetailLocation detail) {
        if(feature == null){
            return Lookups.fixed(studySet, location, detail);
        }
        if(location == null){
            return Lookups.fixed(studySet, feature, detail);
        }
        return Lookups.fixed(studySet, feature, location, detail);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set setProps = Sheet.createPropertiesSet();
        setProps.setDisplayName("Properties");
        sheet.put(setProps);
        final FactDetailLocation detail = getLookup().lookup(FactDetailLocation.class);
        if (editableProperties == null) {
            editableProperties = Sheet.createPropertiesSet();
        }

        Property<String> locId = new PropertySupport.ReadOnly<String>("id", String.class, "ID", "db id") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return detail.getFactId().toString();
            }
        };
        setProps.put(locId);
        editableProperties.put(locId);

        Property detailType = new PropertySupport.ReadWrite<String>("detailType", String.class, "Detail Type", "The detail type.") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }

            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                if (detail.getDetailType() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + detail.getDetailType() + "</font>");
                }
                return detail.getDetailType();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (!val.equals(detail.getDetailType())) {
                    detail.setDetailType(val);
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + detail.getDetailType() + "</font>");
                    firePropertyChange("detailType", null, val);
                }
            }
        };
        setProps.put(detailType);
        editableProperties.put(new ProxyProperty<String>(this, detailType, true));

        Property detailValue = new PropertySupport.ReadWrite<String>("detailType", String.class, "Detail Type", "The detail type.") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }

            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                if (detail.getDetailValue() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + detail.getDetailValue() + "</font>");
                }
                return detail.getDetailValue();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (!val.equals(detail.getDetailValue())) {
                    detail.setDetailValue(val);
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + detail.getDetailValue() + "</font>");
                    firePropertyChange("detailValue", null, val);
                }
            }
        };
        setProps.put(detailValue);
        editableProperties.put(new ProxyProperty<String>(this, detailValue, true));


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
        FactDetailLocation detail = getLookup().lookup(FactDetailLocation.class);
        DataManager.getDefault().save(detail);
    }
}
