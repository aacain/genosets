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

package edu.uncc.genosets.queries.core;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.queries.Category;
import edu.uncc.genosets.queries.Group;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetEvent;
import edu.uncc.genosets.studyset.StudySetManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class OrganismEntityQuery extends EntityQuery {
    private Map<StudySet, List<Group>> groupsByStudySet = new HashMap<StudySet, List<Group>>();
    private static final String path = "Organism";

    @Override
    public List<Group> getGroups() {
        List<Group> groups = new ArrayList<Group>();
        Group group = new Group(null, "All Organisms", Group.TYPE_NATIVE, this, path, new Group.GenericDescription("All Organisms", "All organisms")); 
        groups.add(group);
        for (StudySet ss : StudySetManager.StudySetManagerFactory.getDefault().getStudySets()) {
            if (ss.getFocusEntity() == FocusEntity.getEntity("Organism")) {
                group = new Group(Collections.singleton(Integer.parseInt(ss.getUniqueName())), ss.getName(), Group.TYPE_STUDYSET, this, path+"/StudySets", ss);
                groups.add(group);
                synchronized (this) {
                    groupsByStudySet.put(ss, Collections.singletonList(group));
                }
            }
        }
        return groups;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Category.CategoryIds> getIdsByGroup(Group group, Set<Integer> allIds, FocusEntity focusEntity) {
        List<Category.CategoryIds> list = new ArrayList<Category.CategoryIds>();
        if (Group.TYPE_STUDYSET.equals(group.getType())) {
            StringBuilder bldr = new StringBuilder("SELECT f.");
            bldr.append(focusEntity.getFactTableId());
            bldr.append(" FROM FactStudySet as ssFact, ").append(focusEntity.getFactTable()).append(" as f ");
            bldr.append(" WHERE ssFact.studySetId = ").append(group.getQueryIds().toArray()[0]);
            bldr.append(" AND f.organismId = ssFact.entityId ");
            bldr.append(" GROUP BY f.").append(focusEntity.getFactTableId());
            List<? extends Integer> createQuery = DataManager.getDefault().createQuery(bldr.toString(), Integer.class);
            Set<Integer> yesSet = new HashSet<Integer>(createQuery);
            Set<Integer> all = new HashSet<Integer>(allIds);
            List<Category> get = groupCategoriesMap.get(group);
            for (Category category : get) {
                if (category.getCategoryName().equals("Yes")) {
                    list.add(new Category.CategoryIds(category, yesSet));
                } else {
                    all.removeAll(yesSet);
                    list.add(new Category.CategoryIds(category, all));
                }
            }
        } else {
            //we have the organism
            List<Category> get = groupCategoriesMap.get(group);
            HashMap<Integer, Category.CategoryIds> catLookup = new HashMap<Integer, Category.CategoryIds>();
            for (Category category : get) {
                catLookup.put(category.getCategoryId(), new Category.CategoryIds(category, new HashSet<Integer>()));
            }
            StringBuilder bldr = new StringBuilder("SELECT f.");
            bldr.append(focusEntity.getFactTableId());
            bldr.append(", f.organismId");
            bldr.append(" FROM ").append(focusEntity.getFactTable()).append(" as f ");
            bldr.append(" GROUP BY f.").append(focusEntity.getFactTableId()).append(", f.organismId");
            List<Object[]> createQuery = (List<Object[]>) DataManager.getDefault().createQuery(bldr.toString());
            for (Object[] object : createQuery) {
                Category.CategoryIds catIds = catLookup.get((Integer) object[1]);
                catIds.getIds().add((Integer) object[0]);
            }
            list.addAll(catLookup.values());
        }
        return list;
    }

    @Override
    public List<Category> getCategories(Group group) {
        List<Category> get = groupCategoriesMap.get(group);
        if (get == null) {
            get = new ArrayList<Category>();
            if (group.getType().equals(Group.TYPE_STUDYSET)) {
                Category category = new Category();
                category.setCategoryName("Yes");
                category.setGroup(group);
                category.setCategoryId(1);
                get.add(category);
                category = new Category();
                category.setCategoryName("No");
                category.setGroup(group);
                category.setCategoryId(0);
                get.add(category);
            } else {
                List<? extends Organism> orgs = DataManager.getDefault().createQuery("SELECT o FROM Organism as o", Organism.class);
                for (Organism org : orgs) {
                    Category category = new Category();
                    category.setCategoryName(org.getStrain());
                    category.setGroup(group);
                    category.setCategoryId(org.getId());
                    get.add(category);
                }
            }
            groupCategoriesMap.put(group, get);
        }
        return get;
    }

    private void addOrganismGroup(StudySet ss) {
        if (ss.getFocusEntity() == FocusEntity.getEntity("Organism")) {
            Group group = new Group(Collections.singleton(Integer.parseInt(ss.getUniqueName())), ss.getName(), Group.TYPE_STUDYSET, this, path + "/StudySets", ss);
            synchronized (this) {
                groupsByStudySet.put(ss, Collections.singletonList(group));
            }
            this.pcs.firePropertyChange(PROP_GROUPS_ADDED, null, Collections.singletonList(group));
        }
    }

    @SuppressWarnings("unchecked")
    private void removeOrganismGroup(StudySet ss) {
        synchronized (this) {
            List<Group> ssGroups = groupsByStudySet.remove(ss);
            if (ssGroups != null) {
                this.pcs.firePropertyChange(PROP_GROUPS_REMOVED, null, new ArrayList(ssGroups));
            }
        }
    }

    @Override
    public void studySetAdded(StudySetEvent evt) {
        StudySet ss = evt.getStudySet();
        if (ss.getFocusEntity() == FocusEntity.getEntity("Organism")) {
            addOrganismGroup(ss);
        }
    }

    @Override
    public void studySetRemoved(StudySetEvent evt) {
        StudySet ss = evt.getStudySet();
        if (ss.getFocusEntity() == FocusEntity.getEntity("Organism")) {
            removeOrganismGroup(ss);
        }
    }

    @Override
    public void selectedStudySetsChanged(StudySetEvent evt) {
    }

}
