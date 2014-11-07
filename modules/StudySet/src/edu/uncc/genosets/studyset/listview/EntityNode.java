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

import edu.uncc.genosets.studyset.StudySet;
import org.openide.nodes.AbstractNode;
import org.openide.util.WeakListeners;

/**
 *
 * @author aacain
 */
public class EntityNode extends AbstractNode implements GroupHierarchy.HierarchyGroupChangeListener {

    private final GroupHierarchy groupHierarchy;
    private final StudySet studySet;

    public EntityNode(StudySet studySet, GroupHierarchy groupHierarchy) {
        super(groupHierarchy.getChildren(0, studySet, null, null, null, null, null));
        this.studySet = studySet;
        this.groupHierarchy = groupHierarchy;
        this.groupHierarchy.addHierarchyGroupChangeListener(WeakListeners.create(GroupHierarchy.HierarchyGroupChangeListener.class, this, this.groupHierarchy));
    }

    @Override
    public void groupAdded(GroupHierarchy.HierarchyGroupChangeEvent evt) {
        if (evt.getLevel() == 0) {
            this.setChildren(groupHierarchy.getChildren(0, studySet, null, null, null, null, null));
        }
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public GroupHierarchy getHierarchyGroup() {
        return this.groupHierarchy;
    }
}
