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
package edu.uncc.genosets.studyset.navigator;

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class FocusEntityNodeFactory extends Children.Keys<FocusEntity> {

    HashMap<FocusEntity, List<StudySet>> map = new HashMap<FocusEntity, List<StudySet>>();

    @Override
    protected void addNotify() {
        super.addNotify();
        for (FocusEntity focusEntity : FocusEntity.getEntities()) {
            map.put(focusEntity, new ArrayList<StudySet>());
        }
        List<StudySet> studySets = StudySetManager.StudySetManagerFactory.getDefault().getStudySets();
        for (StudySet studySet : studySets) {
            List<StudySet> get = map.get(studySet.getFocusEntity());
            get.add(studySet);
        }
        this.setKeys(FocusEntity.getEntities());
    }

    @Override
    protected Node[] createNodes(FocusEntity key) {
        ArrayList<Node> nodeList = new ArrayList<Node>();
        List<StudySet> ss = map.get(key);
        if (ss != null) {
            nodeList.add(new FocusEntityNode(key, ss));
        }
        return nodeList.toArray(new Node[nodeList.size()]);
    }

    public static class FocusEntityNode extends AbstractNode {

        public FocusEntityNode(FocusEntity entity, List<StudySet> studySets) {
            super(new StudySetNodeFactory(entity, studySets));
            this.setName(entity.getDisplayName());
            this.setDisplayName(entity.getDisplayName());
        }
    }
}
