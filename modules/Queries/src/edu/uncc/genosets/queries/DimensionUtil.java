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
package edu.uncc.genosets.queries;

import edu.uncc.genosets.queries.core.EntityQuery;
import edu.uncc.genosets.datamanager.api.DataManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.List;
import org.openide.util.WeakListeners;

/**
 *
 * @author aacain
 */
public class DimensionUtil implements PropertyChangeListener {

    /**
     * empty array
     */
    private static final AbstractDimensionObject[] EMPTY_ARRAY = new AbstractDimensionObject[0];
    private WeakReference<DimensionObject> ref;
    private static WeakReference<DimensionUtil> instance;

    public DimensionUtil() {
        DataManager.getDefault().addPropertyChangeListener(WeakListeners.propertyChange(this, DataManager.getDefault()));
    }

    public static DimensionObject getRootDimension() {
        DimensionObject root = null;
        DimensionUtil util = null;
        synchronized (EMPTY_ARRAY) {
            if (instance != null) {
                util = instance.get();
                if (util == null) {
                    util = new DimensionUtil();
                    instance = new WeakReference<DimensionUtil>(util);
                }
            } else {
                util = new DimensionUtil();
                instance = new WeakReference<DimensionUtil>(util);
            }
            if (util.ref != null) {
                root = util.ref.get();
            }
            if (root == null) {
                root = init();
                util.ref = new WeakReference<DimensionObject>(root);
            }
        }
        return root;
    }

    private static DimensionObject init() {
        DimensionObject root = new DimensionFolder(null, "", true);
        for (EntityQuery eq : EntityQuery.getAllEntityQueries()) {
            eq.addPropertyChangeListener(WeakListeners.propertyChange(instance.get(), eq));
            for (Group group : eq.getGroups()) {
                root.createDimensionItem(group);
            }
        }
        return root;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void propertyChange(PropertyChangeEvent evt) {
        if (DataManager.PROP_DB_CHANGED.equals(evt.getPropertyName())) {
            synchronized (EMPTY_ARRAY) {
                instance = null;
            }
        } else if (EntityQuery.PROP_GROUPS_ADDED.equals(evt.getPropertyName())) {
            List<Group> ssGroups = (List<Group>) evt.getNewValue();
            for (Group group : ssGroups) {
                getRootDimension().createDimensionItem(group);
            }
        } else if (EntityQuery.PROP_GROUPS_REMOVED.equals(evt.getPropertyName())) {
            List<Group> ssGroups = (List<Group>) evt.getNewValue();
            for (Group group : ssGroups) {
                DimensionObject parent = getRootDimension().getChild(group.getPath());
                DimensionObject child = parent.getChild(group.getGroupDescription());
                child.delete();
            }
        }
    }
}
