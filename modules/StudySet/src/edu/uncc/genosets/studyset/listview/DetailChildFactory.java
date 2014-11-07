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

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.FactDetailLocation;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.studyset.StudySet;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class DetailChildFactory extends ChildFactory<FactDetailLocation>{
    private final StudySet studySet;
    private final Feature feature;
    private final Location location;
    private int currentResult = 0;
    private final GroupHierarchy hierarchy;
    private final int level;
    private final AnnotationMethod annotationMethod;
    
    public DetailChildFactory(StudySet studySet, Feature feature, Location location, AnnotationMethod annotationMethod, GroupHierarchy hierarchy, int level){
        this.location = location;
        this.feature = feature;
        this.annotationMethod = annotationMethod;
        this.studySet = studySet;
        this.hierarchy = hierarchy;
        this.level = level;
    }
    
    @Override
    protected Node createNodeForKey(FactDetailLocation key) {
        return new DetailNode(studySet, annotationMethod, feature, location, key, hierarchy, level + 1);
    }

    @Override
    protected boolean createKeys(List<FactDetailLocation> toPopulate) {
        if(Thread.interrupted()){
            return true;
        }
        int increment = 1000;
        List<? extends FactDetailLocation> byLocation = StudySetQuery.byDetail(studySet, location, annotationMethod, currentResult, increment);
        if(byLocation.isEmpty()){
            return true;
        }
        toPopulate.addAll(byLocation);
        currentResult = currentResult + increment;
        return false;
    }
}
