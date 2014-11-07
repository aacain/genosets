/*
 * Copyright (C) 2014 Aurora Cain
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
import edu.uncc.genosets.queries.Category;
import edu.uncc.genosets.queries.Group;
import static edu.uncc.genosets.queries.core.EntityQuery.PROP_GROUPS_ADDED;
import static edu.uncc.genosets.queries.core.EntityQuery.PROP_GROUPS_REMOVED;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetEvent;
import edu.uncc.genosets.studyset.StudySetManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class StudySetQuery extends EntityQuery{

    @Override
    public List<Group> getGroups() {
        HashMap<String, List<StudySet>> hierMap = new HashMap<String, List<StudySet>>();
        List<Group> groups = new ArrayList<Group>();
        String path = "Study Sets";
        for (StudySet ss : StudySetManager.StudySetManagerFactory.getDefault().getStudySets()) {
            Group group = new Group(Collections.singleton(Integer.parseInt(ss.getUniqueName())), ss.getName(), Group.TYPE_STUDYSET, this, path, ss);
            groups.add(group);
            List<StudySet> list = hierMap.get(ss.getFocusEntity().getEntityName());
            if(list == null){
                list = new ArrayList<StudySet>();
                hierMap.put(ss.getFocusEntity().getEntityName(), list);
            }
            list.add(ss);
        }
        
        for (FocusEntity focusEntity : FocusEntity.getEntities()) {
            List<StudySet> ssList = hierMap.get(focusEntity.getEntityName());
            if(ssList != null){
                for (StudySet ss : ssList) {
                    Group group = new Group(Collections.singleton(Integer.parseInt(ss.getUniqueName())), ss.getName(), Group.TYPE_STUDYSET, this, path + "/" + focusEntity.getDisplayName(), ss);
                    groups.add(group);
                }
            }
        }
        return groups;
    }

    @Override
    public List<Category.CategoryIds> getIdsByGroup(Group group, Set<Integer> allIds, FocusEntity focusEntity) {
        StudySet ss = (StudySet)group.getDescriptionObject();
        boolean sameFactTable = false;
        if(ss.getFocusEntity().getFactTable().equals(focusEntity.getFactTable())){
            sameFactTable = true;
        }
        StringBuilder bldr = new StringBuilder();
        if(sameFactTable){
            bldr.append("SELECT ssFactTable.");
        }else{
            bldr.append("SELECT focusFactTable.");
        }
        bldr.append(focusEntity.getFactTableId());
        bldr.append(" FROM FactStudySet as ssFact, ").append(ss.getFocusEntity().getFactTable()).append(" as ssFactTable");
        if(!sameFactTable){
            bldr.append(", ").append(focusEntity.getFactTable()).append(" as focusFactTable");
        }
        bldr.append(" WHERE ssFact.studySetId = ").append(Integer.parseInt(ss.getUniqueName())).append(" AND ssFact.entityId = ssFactTable.").append(ss.getFocusEntity().getFactTableId());
        if(!sameFactTable){
            String lowestGranularity = FocusEntity.getLowestGranularity(ss.getFocusEntity(), focusEntity).getLowestGranularity();
            bldr.append(" AND ssFactTable.").append(lowestGranularity).append(" = ").append("focusFactTable.").append(lowestGranularity);
        }
        List<? extends Integer> query = DataManager.getDefault().createQuery(bldr.toString(), Integer.class);
        Set<Integer> yesIds = new HashSet<Integer>(query);
        Set<Integer> noIds = new HashSet<Integer>(allIds);
        noIds.removeAll(yesIds);
        List<Category> cats = getCategories(group);
        List<Category.CategoryIds> result = new ArrayList<Category.CategoryIds>(2);
        result.add(new Category.CategoryIds(cats.get(0), yesIds));
        result.add(new Category.CategoryIds(cats.get(1), noIds));
        
        return result;
        
    }

    @Override
    public List<Category> getCategories(Group group) {
        List<Category> cats = groupCategoriesMap.get(group);
        if (cats == null) {
            cats = new ArrayList<Category>();
            if (Group.TYPE_STUDYSET.equals(group.getType())) {
                Category category = new Category();
                category.setCategoryName("Yes");
                category.setGroup(group);
                category.setCategoryId(1);
                cats.add(category);
                category = new Category();
                category.setCategoryName("No");
                category.setGroup(group);
                category.setCategoryId(0);
                cats.add(category);
            }
        }
        groupCategoriesMap.put(group, cats);
        return cats;
    }

    @Override
    public void studySetAdded(StudySetEvent evt) {
//        String path = "Study Sets";
//        Group group = new Group(Collections.singleton(Integer.parseInt(evt.getStudySet().getUniqueName())), evt.getStudySet().getName(), Group.TYPE_STUDYSET, this, path, evt.getStudySet());
//        this.pcs.firePropertyChange(PROP_GROUPS_ADDED, null, Collections.singletonList(group));
    }

    @Override
    public void studySetRemoved(StudySetEvent evt) {
//        String path = "Study Sets";
//        Group group = new Group(Collections.singleton(Integer.parseInt(evt.getStudySet().getUniqueName())), evt.getStudySet().getName(), Group.TYPE_STUDYSET, this, path, evt.getStudySet());
//        this.pcs.firePropertyChange(PROP_GROUPS_REMOVED, null, new ArrayList(group));
    }

    @Override
    public void selectedStudySetsChanged(StudySetEvent evt) {
        
    }

}
