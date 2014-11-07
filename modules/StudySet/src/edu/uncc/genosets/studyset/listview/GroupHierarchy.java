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

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.studyset.StudySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;

/**
 *
 * @author aacain
 */
public class GroupHierarchy {

    private final List<String> groupings;
    private final ArrayList<HierarchyGroupChangeListener> listeners;

    public GroupHierarchy(List<String> groupings) {
        this.groupings = groupings;
        this.listeners = new ArrayList<HierarchyGroupChangeListener>();
    }

    Children getChildren(int level, StudySet studySet, AnnotationMethod method, Organism organism, AssembledUnit assUnit, Feature feature, Location location) {
        ChildFactory factory = null;
        String grouping = null;
        try{
            grouping = groupings.get(level);
        }catch(IndexOutOfBoundsException ex){
            
        }
        
        if (grouping == null) {
            return Children.LEAF;
        }
        
        if (("Organism").equals(grouping)) {
            factory = new OrganismChildFactory(studySet, this, level);
        } else if (("AssembledUnit").equals(grouping)) {
            factory = new AssembledUnitChildFactory(studySet, organism, this, level);
        } else if (("Feature").equals(grouping)) {
            factory = new FeatureChildFactory(studySet, organism, this, level);
        } else if (("Location").equals(grouping)) {
            factory = new LocationChildFactory(studySet, feature, this, level);
        }else if(("AnnotationMethod").equals(grouping)){
            factory = new AnnotationMethodChildFactory(studySet, feature, location, this, level);
        }else if("FactDetailLocation".equals(grouping)){
            factory = new DetailChildFactory(studySet, feature, location, method, this, level);
        }
        if (factory != null) {
            return Children.create(factory, true);
        }
        return Children.LEAF;
        
    }

    /**
     * Returns a copy of the hierarchy list.
     *
     * @return copy of the hierarchy list.
     */
    public List<String> getHierarchyList() {
        return new ArrayList(this.groupings);
    }

    public static GroupHierarchy getDefaultHierarchy(String grouping) {
        if (("Organism").equals(grouping)) {
            return new GroupHierarchy(new ArrayList(Arrays.asList("Organism", "Feature", "Location", "AnnotationMethod", "FactDetailLocation")));
        } else if (("AssembledUnit").equals(grouping)) {
            return new GroupHierarchy(new ArrayList(Arrays.asList("Organism", "AssembledUnit", "Feature", "Location", "AnnotationMethod", "FactDetailLocation")));
        }else if (("Feature").equals(grouping)) {
            return new GroupHierarchy(new ArrayList(Arrays.asList("Organism", "Feature", "Location", "AnnotationMethod", "FactDetailLocation")));
        } else if (("Location").equals(grouping)) {
            return new GroupHierarchy(new ArrayList(Arrays.asList("Organism", "Feature", "Location", "AnnotationMethod", "FactDetailLocation")));
        }
        return null;
    }

    public static List<String> getFocusEntities() {
        return new ArrayList(Arrays.asList("Organism", "AssembledUnit", "Feature", "Location", "AnnotationMethod" , "FactDetailLocation"));
    }

    public static List<String> getEntityHierarchy() {
        return new ArrayList(Arrays.asList("Organism", "AssembledUnit", "Feature", "Location", "AnnotationMethod", "FactDetailLocation"));
    }

    public synchronized void addHierarchyGroupChangeListener(HierarchyGroupChangeListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeHierarchyGroupChangeListener(HierarchyGroupChangeListener listener) {
        listeners.remove(listener);
    }

    public void fireGroupAddedEvent(int level, String group) {
        ArrayList<HierarchyGroupChangeListener> fire = new ArrayList<HierarchyGroupChangeListener>();
        synchronized (this) {
            ArrayList<HierarchyGroupChangeListener> remove = new ArrayList<HierarchyGroupChangeListener>();
            for (HierarchyGroupChangeListener listener : listeners) {
                if (listener.getLevel() > level) {
                    remove.add(listener);
                } else if (listener.getLevel() == level) {
                    fire.add(listener);
                }
            }
            listeners.removeAll(remove);
            listeners.removeAll(fire);
        }
        for (HierarchyGroupChangeListener listener : fire) {
            listener.groupAdded(new HierarchyGroupChangeEvent(this, level, group));
        }
    }

    void addGrouping(String grouping, int level) {
        this.groupings.add(level, grouping);
        fireGroupAddedEvent(level, grouping);
    }

    public interface HierarchyGroupChangeListener extends EventListener {

        public void groupAdded(HierarchyGroupChangeEvent evt);

        public int getLevel();

        public GroupHierarchy getHierarchyGroup();
    }

    public class HierarchyGroupChangeEvent extends EventObject {

        private final int level;
        private final String grouping;

        public HierarchyGroupChangeEvent(Object source, int level, String grouping) {
            super(source);
            this.level = level;
            this.grouping = grouping;
        }

        public int getLevel() {
            return level;
        }

        public String getGrouping() {
            return grouping;
        }
    }
}
