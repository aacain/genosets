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
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.FactDetailLocation;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.propertieseditor.PropertiesEditable;
import edu.uncc.genosets.propertieseditor.ProxyProperty;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.actions.FocusChangedAction;
import static edu.uncc.genosets.studyset.listview.DetailNode.getRegisterActions;
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
public class AnnotationMethodNode extends AbstractNode implements PropertiesEditable, ListViewProperties, GroupHierarchy.HierarchyGroupChangeListener, Savable {

    private Sheet.Set editableProperties;
    private static List<? extends Action> registeredActions;
    private final GroupHierarchy groupHierarchy;
    private final int level;
    private final static String[] props = new String[]{
        "id", "ID",};

    public AnnotationMethodNode(StudySet studySet, AnnotationMethod annoMethod, Feature feature, Location location, GroupHierarchy hierarchy, int level) {
        this(studySet, annoMethod, feature, location, hierarchy, level, createLookup(studySet, annoMethod, feature, location));
    }

    public AnnotationMethodNode(StudySet studySet, AnnotationMethod annoMethod, Feature feature, Location location, GroupHierarchy hierarchy, int level, Lookup lookup) {
        super(hierarchy.getChildren(level, studySet, annoMethod, null, null, feature, location));
        this.groupHierarchy = hierarchy;
        this.level = level;
        this.setName(annoMethod.getAnnotationMethodId().toString());
        this.setDisplayName(annoMethod.getMethodName());
        this.groupHierarchy.addHierarchyGroupChangeListener(WeakListeners.create(GroupHierarchy.HierarchyGroupChangeListener.class, this, this.groupHierarchy));
    }

    private static Lookup createLookup(StudySet studySet, AnnotationMethod annoMethod, Feature feature, Location location) {
        if (feature == null) {
            return Lookups.fixed(studySet, annoMethod, location);
        }
        if (location == null) {
            return Lookups.fixed(studySet, annoMethod, feature);
        }
        return Lookups.fixed(studySet, annoMethod, feature, location);
    }

    protected static List<? extends Action> getRegisterActions() {
        if (registeredActions == null) {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/Nodes/AnnotationMethod"));
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
        Sheet sheet = Sheet.createDefault();
        Sheet.Set setProps = Sheet.createPropertiesSet();
        setProps.setDisplayName("Properties");
        sheet.put(setProps);
        final AnnotationMethod annoMethod = getLookup().lookup(AnnotationMethod.class);
        if (editableProperties == null) {
            editableProperties = Sheet.createPropertiesSet();
        }

        Property<String> id = new PropertySupport.ReadOnly<String>("id", String.class, "ID", "db id") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return annoMethod.getAnnotationMethodId().toString();
            }
        };
        setProps.put(id);
        editableProperties.put(id);

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
        AnnotationMethod annoMethod = getLookup().lookup(AnnotationMethod.class);
        DataManager.getDefault().save(annoMethod);
    }
}
