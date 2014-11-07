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

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.queries.Category;
import edu.uncc.genosets.queries.Group;
import edu.uncc.genosets.studyset.StudySetChangeListener;
import edu.uncc.genosets.studyset.StudySetManager.StudySetManagerFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openide.util.WeakListeners;

/**
 *
 * @author aacain
 */
public abstract class EntityQuery implements StudySetChangeListener {

    HashMap<Group, List<Category>> groupCategoriesMap = new HashMap<Group, List<Category>>();
    PropertyChangeSupport pcs;
    static List<EntityQuery> allEntityQueries;
    public static final String PROP_GROUPS_ADDED = "PROP_GROUPS_ADDED";
    public static final String PROP_GROUPS_REMOVED = "PROP_GROUPS_REMOVED";

    public EntityQuery() {
        this.pcs = new PropertyChangeSupport(this);
        StudySetManagerFactory.getDefault().addStudySetChangeListener(WeakListeners.create(StudySetChangeListener.class, this, StudySetManagerFactory.getDefault()));
    }

    public abstract List<Group> getGroups();

    public abstract List<Category.CategoryIds> getIdsByGroup(Group group, Set<Integer> allIds, FocusEntity focusEntity);

    public abstract List<Category> getCategories(Group group);

    @SuppressWarnings("unchecked")
    public static Set<Integer> getAllIds(FocusEntity focusEntity) {
        StringBuilder bldr = new StringBuilder();
        bldr.append("SELECT f.").append(focusEntity.getIdProperty()).
                append(" FROM ").append(focusEntity.getFactTable()).append(" as f ");
        return new HashSet<Integer>(DataManager.getDefault().createQuery(bldr.toString()));
    }

    @SuppressWarnings("unchecked")
    public static List<? extends EntityQuery> getAllEntityQueries() {
        if (allEntityQueries == null) {
            allEntityQueries = new ArrayList(10);
            EntityQuery entityQuery = new OrganismEntityQuery();
            allEntityQueries.add(entityQuery);
            entityQuery = new OrthologEntityQuery();
            allEntityQueries.add(entityQuery);
            entityQuery = new AnnotationEntityQuery();
            allEntityQueries.add(entityQuery);
            entityQuery = new StudySetQuery();
            allEntityQueries.add(entityQuery);
        }
        return allEntityQueries;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void firepropertyChange(PropertyChangeEvent evt) {
        this.pcs.firePropertyChange(evt);
    }
}
