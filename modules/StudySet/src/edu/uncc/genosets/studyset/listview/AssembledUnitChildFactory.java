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

import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.studyset.StudySet;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class AssembledUnitChildFactory extends ChildFactory<AssembledUnit> {

    private final StudySet studySet;
    private final Organism organism;
    private int currentResult = 0;
    private final GroupHierarchy hierarchy;
    private final int level;

    public AssembledUnitChildFactory(StudySet studySet, Organism organism, GroupHierarchy hierarchy, int level) {
        this.studySet = studySet;
        this.organism = organism;
        this.hierarchy = hierarchy;
        this.level = level;
    }

    @Override
    protected boolean createKeys(List<AssembledUnit> toPopulate) {
        if (Thread.interrupted()) {
            return true;
        }
        int increment = 1000;
        List<? extends AssembledUnit> byAssUnit = StudySetQuery.byAssUnit(studySet, organism, currentResult, increment);
        if(byAssUnit.isEmpty()){
            return true;
        }
        toPopulate.addAll(byAssUnit);
        currentResult = currentResult + increment;
        return false;
    }
    
    @Override
    protected Node createNodeForKey(AssembledUnit key) {
        return new AssembledUnitNode(studySet, organism, key, hierarchy, level + 1);
    }
}
