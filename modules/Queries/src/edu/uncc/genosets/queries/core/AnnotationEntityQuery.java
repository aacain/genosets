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
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.queries.Category;
import edu.uncc.genosets.queries.Group;
import edu.uncc.genosets.queries.Group.GenericDescription;
import edu.uncc.genosets.studyset.StudySetEvent;
import java.math.BigInteger;
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
public class AnnotationEntityQuery extends EntityQuery {

    private static final String TYPE_NATIVESET = "TYPE_NATIVESET";
    private static final String BASE_FACT_TABLE = "AnnoFact";

    @Override
    @SuppressWarnings("unchecked")
    public List<Group> getGroups() {
        String path = "Annotation";
        List<Group> groups = new ArrayList<Group>();
        Group group;
        //get all the annotations
        List<? extends AnnotationMethod> methods = DataManager.getDefault().createQuery("SELECT a FROM AnnoFact as f, AnnotationMethod as a WHERE f.annotationMethodId = a.annotationMethodId GROUP BY a.annotationMethodId", AnnotationMethod.class);
        HashMap<String, Set<Integer>> sourceTypeMap = new HashMap();
        HashMap<String, Set<Integer>> typeMap = new HashMap();
        for (AnnotationMethod method : methods) {
            group = new Group(Collections.singleton(method.getAnnotationMethodId()), method.getMethodName(), Group.TYPE_NATIVE, this, path, method);
            groups.add(group);
            Set<Integer> set = sourceTypeMap.get(method.getMethodSourceType());
            if (set == null) {
                set = new HashSet();
                sourceTypeMap.put(method.getMethodSourceType(), set);
            }
            set.add(method.getAnnotationMethodId());
            set = typeMap.get(method.getMethodType());
            if (set == null) {
                set = new HashSet();
                typeMap.put(method.getMethodType(), set);
            }
            set.add(method.getAnnotationMethodId());
        }
        //add hierarchy
        for (Map.Entry<String, Set<Integer>> entry : sourceTypeMap.entrySet()) {
            group = new Group(entry.getValue(), "Source: " + entry.getKey(), TYPE_NATIVESET, this, path + "/Source", new GenericDescription("Source: " + entry.getKey(), "Annotation method grouped by source."));
            groups.add(group);
        }
        for (Map.Entry<String, Set<Integer>> entry : typeMap.entrySet()) {
            group = new Group(entry.getValue(), "Type: " + entry.getKey(), TYPE_NATIVESET, this, path + "/SourceType", new GenericDescription("SourceType: " + entry.getKey(), "Annotation method grouped by source type."));
            groups.add(group);
        }
        return groups;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Category.CategoryIds> getIdsByGroup(Group group, Set<Integer> allIds, FocusEntity focusEntity) {
        List<Category.CategoryIds> list = new ArrayList<Category.CategoryIds>();
        Set<Integer> all = new HashSet();
        Set<Integer> some = new HashSet();
        Set<Integer> none = new HashSet(allIds);
        if (Group.TYPE_NATIVE.equals(group.getType())) {
            StringBuilder bldr = new StringBuilder();
            if (BASE_FACT_TABLE.equals(focusEntity.getFactTable())) {
                bldr.append("SELECT f.");
                bldr.append(focusEntity.getFactTableId());
                bldr.append(" FROM AnnoFact as f WHERE f.annotationMethodId = ").append(group.getQueryIds().toArray()[0]);
                bldr.append(" GROUP BY f.").append(focusEntity.getFactTableId());
            } else {
                bldr.append("SELECT f.").append(focusEntity.getFactTableId());
                bldr.append(" FROM ").append(focusEntity.getFactTable()).append(" as f, AnnoFact as a ");
                bldr.append(" WHERE f.").append(focusEntity.getLowestGranularity()).append(" = a.").append(focusEntity.getLowestGranularity());
                bldr.append(" AND a.annotationMethodId = ").append(group.getQueryIds().toArray()[0]);
                bldr.append(" GROUP BY f.").append(focusEntity.getFactTableId());
            }
            List<? extends Integer> createQuery = DataManager.getDefault().createQuery(bldr.toString(), Integer.class);
            all = new HashSet(createQuery);
            none.removeAll(all);
        } else if (Group.TYPE_STUDYSET.equals(group.getType())) { //This has not been tested.
            StringBuilder bldr = new StringBuilder();
            if (BASE_FACT_TABLE.equals(focusEntity.getFactTable())) {
                bldr.append("SELECT ssFact.entityId FROM FactStudySet as ssFact WHERE ssFact.studySetId = ").append(group.getQueryIds().toArray()[0]);
                List<Integer> methodList = (List<Integer>) DataManager.getDefault().createQuery(bldr.toString(), Integer.class);
                String entityNative = DataManager.getDefault().getDatabaseColumnName(focusEntity.getFactTable(), focusEntity.getFactTableId());
                String entityFactNative = DataManager.getDefault().getDatabaseTableName(focusEntity.getFactTable());
                String entityLowestGranNative = DataManager.getDefault().getDatabaseColumnName(focusEntity.getLowestGranularity(), focusEntity.getFactTable());
                String annoLowestGranNative = DataManager.getDefault().getDatabaseColumnName(focusEntity.getLowestGranularity(), "AnnoFact");

                bldr = new StringBuilder();
                bldr.append("SELECT SubQuery.").append(entityNative);
                bldr.append(", COUNT(SubQuery.AnnotationMethod) AS TotalCount FROM (SELECT fact_location_anno_fact.AnnotationMethod, ");
                bldr.append(entityFactNative).append(".").append(entityNative);
                bldr.append(" FROM fact_location_anno_fact, ").append(entityFactNative);
                bldr.append(" WHERE ").append("fact_location_anno_fact.").append(annoLowestGranNative).append(" = ").append(entityFactNative).append(".").append(entityLowestGranNative);
                bldr.append(" AND fact_location_anno_fact.AnnotationMethod IN (");
                int i = 0;
                for (Integer integer : methodList) {
                    if (i > 0) {
                        bldr.append(", ");
                    }
                    bldr.append(integer.toString());
                    i++;
                }
                bldr.append(") GROUP BY fact_location_anno_fact.AnnotationMethod, fact_location_anno_fact.");
                bldr.append(entityNative);
                bldr.append(") SubQuery GROUP BY SubQuery.");
                bldr.append(entityNative);
                List<Object[]> createNativeSQLQuery = DataManager.getDefault().createNativeSQLQuery(bldr.toString());
                for (Object[] objects : createNativeSQLQuery) {
                    int total = ((BigInteger) objects[1]).intValue();
                    if (total < methodList.size()) {
                        some.add((Integer) objects[0]);
                    } else {
                        all.add((Integer) objects[0]);
                    }
                }
            } else {
                bldr.append("SELECT ssFact.entityId FROM FactStudySet as ssFact WHERE ssFact.studySetId = ").append(group.getQueryIds().toArray()[0]);
                List<Integer> methodList = (List<Integer>) DataManager.getDefault().createQuery(bldr.toString(), Integer.class);
                String entityNative = DataManager.getDefault().getDatabaseColumnName(focusEntity.getFactTable(), focusEntity.getFactTableId());
                bldr = new StringBuilder();
                bldr.append("SELECT SubQuery.").append(entityNative);
                bldr.append(", COUNT(SubQuery.AnnotationMethod) AS TotalCount FROM (SELECT fact_location_anno_fact.AnnotationMethod, fact_location_anno_fact.");
                bldr.append(entityNative);
                bldr.append(" FROM fact_location_anno_fact WHERE fact_location_anno_fact.AnnotationMethod IN (");
                int i = 0;
                for (Integer integer : methodList) {
                    if (i > 0) {
                        bldr.append(", ");
                    }
                    bldr.append(integer.toString());
                    i++;
                }
                bldr.append(") GROUP BY fact_location_anno_fact.AnnotationMethod, fact_location_anno_fact.");
                bldr.append(entityNative);
                bldr.append(") SubQuery GROUP BY SubQuery.");
                bldr.append(entityNative);
                List<Object[]> createNativeSQLQuery = DataManager.getDefault().createNativeSQLQuery(bldr.toString());
                for (Object[] objects : createNativeSQLQuery) {
                    int total = ((BigInteger) objects[1]).intValue();
                    if (total < methodList.size()) {
                        some.add((Integer) objects[0]);
                    } else {
                        all.add((Integer) objects[0]);
                    }
                }
            }
        } else if (TYPE_NATIVESET.equals(group.getType())) {
            StringBuilder bldr = new StringBuilder();
            if (BASE_FACT_TABLE.equals(focusEntity.getFactTable())) {
                Set<Integer> methodList = group.getQueryIds();
                bldr.append("SELECT f.").append(focusEntity.getFactTableId()).append(" FROM AnnoFact as f WHERE f.annotationMethodId in (");
                int i = 0;
                for (Integer integer : methodList) {
                    if (i > 0) {
                        bldr.append(", ");
                    }
                    bldr.append(integer.toString());
                    i++;
                }
                bldr.append(") GROUP BY f.").append(focusEntity.getFactTableId());
                List<Integer> query = DataManager.getDefault().createQuery(bldr.toString());
                for (Integer id : query) {
                    all.add(id);
                }
            } else { //focus entity fact table is different
                Set<Integer> methodList = group.getQueryIds();
                bldr.append("SELECT f.").append(focusEntity.getFactTableId());
                bldr.append(" FROM ").append(focusEntity.getFactTable()).append(" as f, AnnoFact as a");
                bldr.append(" WHERE f.").append(focusEntity.getLowestGranularity()).append(" = a.").append(focusEntity.getLowestGranularity()).append(" AND a.annotationMethodId in (");
                int i = 0;
                for (Integer integer : methodList) {
                    if (i > 0) {
                        bldr.append(", ");
                    }
                    bldr.append(integer.toString());
                    i++;
                }
                bldr.append(") GROUP BY f.").append(focusEntity.getFactTableId());
                List<Integer> query = DataManager.getDefault().createQuery(bldr.toString());
                for (Integer id : query) {
                    all.add(id);
                }
            }
        }
        none.removeAll(all);
        none.removeAll(some);
        List<Category> cats = groupCategoriesMap.get(group);
        for (Category category : cats) {
            Category.CategoryIds ids;
            if (category.getCategoryId().equals(0)) {
                ids = new Category.CategoryIds(category, none);
            } else if (category.getCategoryId().equals(1)) {
                ids = new Category.CategoryIds(category, all);
            } else {
                ids = new Category.CategoryIds(category, some);
            }
            list.add(ids);
        }
        return list;
    }

    @Override
    public List<Category> getCategories(Group group) {
        List<Category> get = groupCategoriesMap.get(group);
        if (get == null) {
            get = new ArrayList<Category>();
            if (Group.TYPE_NATIVE.equals(group.getType())) {
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
            } else if (Group.TYPE_STUDYSET.equals(group.getType())) {
                Category category = new Category();
                category.setCategoryName("All");
                category.setGroup(group);
                category.setCategoryId(1);
                get.add(category);
                category = new Category();
                category.setCategoryName("Some");
                category.setGroup(group);
                category.setCategoryId(2);
                get.add(category);
                category = new Category();
                category.setCategoryName("None");
                category.setGroup(group);
                category.setCategoryId(0);
                get.add(category);
            } else if (TYPE_NATIVESET.equals(group.getType())) {
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
            }
            groupCategoriesMap.put(group, get);
        }
        return get;
    }

    @Override
    public void studySetAdded(StudySetEvent evt) {
    }

    @Override
    public void studySetRemoved(StudySetEvent evt) {
    }

    @Override
    public void selectedStudySetsChanged(StudySetEvent evt) {
    }
}
