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

import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.studyset.StudySet;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;

/**
 *
 * @author aacain
 */
public class OrganismChildFactory extends ChildFactory<Organism> {

    private final StudySet studySet;
    private int currentResult = 0;
    private final GroupHierarchy hierarchy;
    private final int level;

    public OrganismChildFactory(StudySet studySet, GroupHierarchy hierarchy, int level) {
        this.studySet = studySet;
        this.hierarchy = hierarchy;
        this.level = level;
    }

    @Override
    protected boolean createKeys(List<Organism> toPopulate) {
        if (Thread.interrupted()) {
            return true;
        }
        int increment = 1000;
        List<? extends Organism> byOrganism = StudySetQuery.byOrganism(studySet, currentResult, increment);
        if(byOrganism.isEmpty()){
            return true;
        }
        currentResult = currentResult + increment;
        toPopulate.addAll(byOrganism);
        return true;
    }

    @Override
    protected Node createNodeForKey(Organism key) {
        return new OrganismNode(key, studySet, hierarchy, level + 1);
    }
}
