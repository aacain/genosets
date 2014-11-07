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
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.queries.Category;
import edu.uncc.genosets.queries.Group;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetEvent;
import edu.uncc.genosets.studyset.StudySetManager;
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
public class OrthologEntityQuery extends EntityQuery {

    public static final String TYPE_HIER = "TYPE_HIER";
    private final String rootPath = "Ortholog";
    HashMap<Group, Integer> methodMap = new HashMap<Group, Integer>();
    Set<Group> groups;
    Map<StudySet, List<Group>> groupsByStudySet = new HashMap<StudySet, List<Group>>();

    @Override
    @SuppressWarnings("unchecked")
    public List<Group> getGroups() {
        if (groups == null) {
            groups = new HashSet<Group>();
            //get methods
            List<? extends AnnotationMethod> methods = DataManager.getDefault().createQuery("SELECT m FROM AnnotationMethod m, OrthoFact as f WHERE m.annotationMethodId = f.annotationMethod GROUP by f.annotationMethod", AnnotationMethod.class);
            //get all the studysets
            //                StringBuilder bldr = new StringBuilder();
            //                bldr.append("SELECT s from StudySetEntity s WHERE s.entityTable = ");
            //                bldr.append("'").append(DataManager.getDefault().getDatabaseTableName(Organism.DEFAULT_NAME)).append("'");
            //                bldr.append(" GROUP BY s.studySetId");
            //                List<? extends StudySetEntity> studysets = DataManager.getDefault().createQuery(bldr.toString(), StudySetEntity.class);
            for (AnnotationMethod method : methods) {
                String path = rootPath + "/" + method.getMethodName();
                Group group;
                //get all the organisms
                StringBuilder bldr = new StringBuilder("SELECT o FROM Organism o, OrthoFact as f WHERE f.organismId = o.organismId AND f.annotationMethodId = ");
                bldr.append(method.getAnnotationMethodId()).append(" GROUP BY o.organismId");
                List<? extends Organism> orgs = DataManager.getDefault().createQuery(bldr.toString(), Organism.class);
                List<HashMap<String, Set<Integer>>> hierarchy = getHierarchy((List<Organism>) orgs);
                for (Organism organism : orgs) {
                    group = new Group(Collections.singleton(organism.getOrganismId()), organism.getStrain(), Group.TYPE_NATIVE, this, path, organism);
                    addGroup(group, method, null);
                }
                for (HashMap<String, Set<Integer>> hashMap : hierarchy) {
                    for (Map.Entry<String, Set<Integer>> entry : hashMap.entrySet()) {
                        String myPath = path;
                        if (entry.getKey().startsWith("Kingdom:")) {
                            myPath = myPath + "/Kingdom";
                        } else if (entry.getKey().startsWith("Phylum")) {
                            myPath = myPath + "/Phylum";
                        } else if (entry.getKey().startsWith("Class")) {
                            myPath = myPath + "/Class";
                        } else if (entry.getKey().startsWith("Order")) {
                            myPath = myPath + "/Order";
                        } else if (entry.getKey().startsWith("Family")) {
                            myPath = myPath + "/Family";
                        } else if (entry.getKey().startsWith("Genus")) {
                            myPath = myPath + "/Genus";
                        } else if (entry.getKey().startsWith("Species")) {
                            myPath = myPath + "/Species";
                        }
                        group = new Group(entry.getValue(), entry.getKey(), TYPE_HIER, this, myPath, new Group.GenericDescription(myPath, "Organism group " + entry.getValue()));
                        addGroup(group, method, null);
                    }
                }
                for (StudySet e : StudySetManager.StudySetManagerFactory.getDefault().getStudySets()) {
                    if (e.getFocusEntity() == FocusEntity.getEntity("Organism")) {
                        group = new Group(Collections.singleton(Integer.parseInt(e.getUniqueName())), e.getName(), Group.TYPE_STUDYSET, this, path + "/StudySets", e);
                        addGroup(group, method, e);
                    }
                }
            }
        }
        return new ArrayList(groups);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Category.CategoryIds> getIdsByGroup(Group group, Set<Integer> allIds, FocusEntity focusEntity) {
        Integer methodId = methodMap.get(group);
        List<Category.CategoryIds> list = new ArrayList<Category.CategoryIds>();
        //get the organisms to include
        List<Integer> orgList = new ArrayList<Integer>();
        if (Group.TYPE_STUDYSET.equals(group.getType())) {
            StringBuilder bldr = new StringBuilder();
            bldr.append("SELECT ssFact.entityId FROM FactStudySet as ssFact WHERE ssFact.studySetId = ").append(group.getQueryIds().toArray()[0]);
            orgList = (List<Integer>) DataManager.getDefault().createQuery(bldr.toString(), Integer.class);
        } else if (group.getType().equals(Group.TYPE_NATIVE) || group.getType().equals(TYPE_HIER)) {
            orgList.addAll(group.getQueryIds());
        }
        assert !orgList.isEmpty();
        //now create the query
        StringBuilder bldr = new StringBuilder();
        String databaseColumnName = DataManager.getDefault().getDatabaseColumnName(focusEntity.getFactTable(), focusEntity.getFactTableId());
        bldr.append("SELECT SubQuery.TotalOrgs, SubQuery.FeatureCluster as o1, fact_location_ortho_fact.").append(databaseColumnName).append(" as o2 FROM (SELECT COUNT(SubQuery.Organism) AS TotalOrgs, SubQuery.FeatureCluster FROM (SELECT fact_location_ortho_fact.Organism, fact_location_ortho_fact.FeatureCluster FROM fact_location_ortho_fact WHERE fact_location_ortho_fact.AnnotationMethod = ").append(methodId).append(" AND fact_location_ortho_fact.Organism IN (");
        int i = 0;
        for (Integer orgId : orgList) {
            if (i > 0) {
                bldr.append(", ");
            }
            bldr.append(orgId.toString());
            i++;
        }
        bldr.append(")").append(" GROUP BY fact_location_ortho_fact.Organism, fact_location_ortho_fact.FeatureCluster) SubQuery GROUP BY SubQuery.FeatureCluster) SubQuery INNER JOIN  fact_location_ortho_fact ON SubQuery.FeatureCluster = fact_location_ortho_fact.FeatureCluster GROUP BY fact_location_ortho_fact.").append(databaseColumnName);
        List<Object[]> ids = DataManager.getDefault().createNativeSQLQuery(bldr.toString());
        List<Integer> core = new ArrayList();
        List<Integer> dispensible = new ArrayList();
        Set<Integer> none = new HashSet<Integer>(allIds);
        for (Object[] objects : ids) {
            Integer total = ((BigInteger) objects[0]).intValue();
            if (total.intValue() != orgList.size()) {
                dispensible.add((Integer) objects[2]);
            } else {
                core.add((Integer) objects[2]);
            }
            none.remove((Integer) objects[2]);
        }
        //get undefined
        StringBuilder byMethodBldr = new StringBuilder();
        byMethodBldr.append("SELECT f.").append(focusEntity.getFactTableId()).append(" FROM OrthoFact as f WHERE f.annotationMethodId = ").append(methodId).append(" GROUP BY f.").append(focusEntity.getFactTableId());
        Set<Integer> byMethod = new HashSet(DataManager.getDefault().createQuery(byMethodBldr.toString()));
        Set<Integer> undefined = new HashSet(allIds);
        undefined.removeAll(byMethod);
        none.removeAll(undefined);
        byMethod = null;
        //            none.removeAll(core);
        //            none.removeAll(dispensible);
        List<Category> get = groupCategoriesMap.get(group);
        for (Category category : get) {
            if (category.getCategoryName().equals("Yes") || category.getCategoryName().equals("Core")) {
                list.add(new Category.CategoryIds(category, new HashSet(core)));
            } else if (category.getCategoryName().equals("No") || category.getCategoryName().equals("None")) {
                list.add(new Category.CategoryIds(category, none));
            } else if (category.getCategoryName().equals("Dispensable")) {
                list.add(new Category.CategoryIds(category, new HashSet(dispensible)));
            } else if (category.getCategoryName().equals("Undefined")) {
                list.add(new Category.CategoryIds(category, undefined));
            }
        }
        return list;
    }

    @Override
    public List<Category> getCategories(Group group) {
        List<Category> get = groupCategoriesMap.get(group);
        if (get == null) {
            get = new ArrayList<Category>();
            if (group.getType().equals(Group.TYPE_STUDYSET) || group.getType().equals(TYPE_HIER)) {
                Category category = new Category();
                category.setCategoryName("Core");
                category.setGroup(group);
                category.setCategoryId(1);
                get.add(category);
                category = new Category();
                category.setCategoryName("Dispensable");
                category.setGroup(group);
                category.setCategoryId(2);
                get.add(category);
                category = new Category();
                category.setCategoryName("None");
                category.setGroup(group);
                category.setCategoryId(0);
                get.add(category);
                category = new Category();
                category.setCategoryName("Undefined");
                category.setGroup(group);
                category.setCategoryId(3);
                get.add(category);
            } else if (group.getType().equals(Group.TYPE_NATIVE)) {
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
                category = new Category();
                category.setCategoryName("Undefined");
                category.setGroup(group);
                category.setCategoryId(2);
                get.add(category);
            }
            groupCategoriesMap.put(group, get);
        }
        return get;
    }

    @SuppressWarnings("unchecked")
    public List<Group> addOrganismGroup(StudySet ss) {
        List<Group> ssGroups = groupsByStudySet.get(ss);
        if (ssGroups != null) {
            for (Group group : ssGroups) {
                removeGroup(group);
            }
            this.pcs.firePropertyChange(PROP_GROUPS_REMOVED, null, ssGroups);
        }
        List<? extends AnnotationMethod> methods = DataManager.getDefault().createQuery("SELECT m FROM AnnotationMethod m, OrthoFact as f WHERE m.annotationMethodId = f.annotationMethod GROUP by f.annotationMethod", AnnotationMethod.class);
        List<Group> newGroups = new ArrayList(methods.size());
        for (AnnotationMethod method : methods) {
            String path = rootPath + "/" + method.getMethodName() + "/StudySets";
            Group group = new Group(Collections.singleton(Integer.parseInt(ss.getUniqueName())), ss.getName(), Group.TYPE_STUDYSET, this, path, method);
            addGroup(group, method, ss);
            newGroups.add(group);
        }
        this.pcs.firePropertyChange(PROP_GROUPS_ADDED, null, newGroups);
        return newGroups;
    }

    @SuppressWarnings("unchecked")
    public void removeOrganismGroup(StudySet ss) {
        List<Group> ssGroups;
        synchronized (this) {
            ssGroups = groupsByStudySet.remove(ss);
        }
        if (ssGroups != null) {
            List<Group> removed = new ArrayList(ssGroups);
            for (Group group : ssGroups) {
                removeGroup(group);
            }
            this.pcs.firePropertyChange(PROP_GROUPS_REMOVED, null, removed);
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized void addGroup(Group group, AnnotationMethod method, StudySet studySet) {
        this.groups.add(group);
        if (method != null) {
            this.methodMap.put(group, method.getAnnotationMethodId());
        }
        if (studySet != null) {
            List<Group> ssGroups = this.groupsByStudySet.get(studySet);
            if (ssGroups == null) {
                ssGroups = new ArrayList();
                this.groupsByStudySet.put(studySet, ssGroups);
            }
            ssGroups.add(group);
        }
    }

    private synchronized void removeGroup(Group group) {
        this.groupCategoriesMap.remove(group);
        this.methodMap.remove(group);
        this.groups.remove(group);
    }

    //        private void removeGroup(Group group) {
    //            if (group != null) {
    //                methodMap.remove(group);
    //                groups.remove(group);
    //                groupsByStudySet.get(pcs)
    //
    //            }
    //        }
    @SuppressWarnings("unchecked")
    private List<HashMap<String, Set<Integer>>> getHierarchy(List<Organism> orgs) {
        List<HashMap<String, Set<Integer>>> orgMaps = new ArrayList();
        for (int i = 0; i < 7; i++) {
            orgMaps.add(new HashMap());
        }
        for (Organism org : orgs) {
            addToHier("Kingdom: ", org.getKingdom(), org, orgMaps.get(0));
            addToHier("Phylum: ", org.getPhylum(), org, orgMaps.get(1));
            addToHier("Class: ", org.getTaxClass(), org, orgMaps.get(2));
            addToHier("Order: ", org.getTaxOrder(), org, orgMaps.get(3));
            addToHier("Family: ", org.getFamily(), org, orgMaps.get(4));
            addToHier("Genus: ", org.getGenus(), org, orgMaps.get(5));
            addToHier("Species: ", org.getSpecies(), org, orgMaps.get(6));
        }
        return orgMaps;
    }

    @SuppressWarnings("unchecked")
    private void addToHier(String level, String value, Organism org, HashMap<String, Set<Integer>> orgMap) {
        if (value != null && !value.equals("")) {
            Set<Integer> orgs = orgMap.get(level + value);
            if (orgs == null) {
                orgs = new HashSet();
                orgMap.put(level + value, orgs);
            }
            orgs.add(org.getId());
        }
    }
    //
    //        public void propertyChange(PropertyChangeEvent evt) {
    //            if (StudySetManager.PROP_STUDYSET_ADDED.equals(evt.getPropertyName())) {
    //                StudySet ss = (StudySet) evt.getNewValue();
    //                if (ss.getFocusEntity() == FocusEntity.getEntityClass("Organism")) {
    //                    addOrganismGroup(ss);
    //                }
    //            } else if (StudySetManager.PROP_STUDYSET_REMOVED.equals(evt.getPropertyName())) {
    //                StudySet ss = (StudySet) evt.getNewValue();
    //                if (ss.getFocusEntity() == FocusEntity.getEntityClass("Organism")) {
    //                    removeOrganismGroup(ss);
    //                }
    //            }
    //            firepropertyChange(evt);
    //        }

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
