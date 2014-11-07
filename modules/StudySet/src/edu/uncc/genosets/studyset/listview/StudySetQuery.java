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

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.FactDetailLocation;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.studyset.StudySet;
import java.util.List;

/**
 *
 * @author aacain
 */
public class StudySetQuery {

    public static List<? extends Organism> byOrganism(StudySet studySet, int firstResult, int maxResult) {
        StringBuilder bldr = new StringBuilder("SELECT org FROM Organism as org, FactStudySet as f, Location as l WHERE f.entityId = l.");
        bldr.append(studySet.getFocusEntity().getFactTableId()).append(" AND f.studySetId = ").append(studySet.getUniqueName()).append(" AND l.organismId = org.organismId");
        bldr.append(" GROUP BY org.organismId ORDER BY org.organismId");
        return DataManager.getDefault().createQuery(bldr.toString(), Organism.class, firstResult, maxResult);
    }

    public static List<? extends Feature> byFeature(StudySet studySet, Organism org, int firstResult, int maxResult) {
        StringBuilder bldr = new StringBuilder("SELECT f FROM AnnoFact fact, FactStudySet ss, Feature as f inner join fetch f.assembledUnit");
        bldr.append(" WHERE fact.featureId = f.featureId ");
        if (org != null) {
            bldr.append(" AND fact.organismId = ").append(org.getOrganismId());
        }
        bldr.append(" AND ss.studySetId = ").append(studySet.getUniqueName()).append(" AND ss.entityId = fact.").append(studySet.getFocusEntity().getFactTableId());
        bldr.append(" GROUP BY f.featureId ORDER BY f.featureId");
        return DataManager.getDefault().createQuery(bldr.toString(), Feature.class, firstResult, maxResult);
    }

    static List<? extends Location> byLocation(StudySet studySet, Feature feature, int firstResult, int maxResult) {
        StringBuilder bldr = new StringBuilder("SELECT l FROM Location as l, FactStudySet as s ");
        bldr.append("WHERE s.entityId = l.").append(studySet.getFocusEntity().getFactTableId()).append(" AND s.studySetId =  ").append(studySet.getUniqueName());
        if (feature != null) {
            bldr.append(" AND l.featureId = ").append(feature.getFeatureId());
        }
        bldr.append(" GROUP BY l.locationId ORDER BY l.locationId");
        return DataManager.getDefault().createQuery(bldr.toString(), Location.class, firstResult, maxResult);
    }

    static List<? extends AssembledUnit> byAssUnit(StudySet studySet, Organism organism, int firstResult, int maxResult) {
        StringBuilder bldr = new StringBuilder("Select a FROM AssembledUnit as a, FactStudySet as s ");
        bldr.append("WHERE s.entityId = a.").append(studySet.getFocusEntity().getFactTableId()).append(" AND s.studySetId = ").append(studySet.getUniqueName());
        if(organism != null){
            bldr.append(" AND a.organismId = ").append(organism.getOrganismId());
        }
        bldr.append(" GROUP BY a.assembledUnitId ORDER BY a.assembledUnitId" );
        return DataManager.getDefault().createQuery(bldr.toString(), AssembledUnit.class, firstResult, maxResult);
    }

    static List<? extends FactDetailLocation> byDetail(StudySet studySet, Location location, AnnotationMethod annoMethod, int firstResult, int maxResult) {
        StringBuilder bldr = new StringBuilder("SELECT f FROM AnnoFactDetail as f ");
        bldr.append(" WHERE f.locationId = ").append(location.getLocationId());
        if(annoMethod != null){
            bldr.append(" AND f.annotationMethodId = ").append(annoMethod.getAnnotationMethodId());
        }
        bldr.append(" ORDER BY f.annotationMethodId, f.factId");
        return DataManager.getDefault().createQuery(bldr.toString(), FactDetailLocation.class, firstResult, maxResult);
    }
    
    static List<? extends AnnotationMethod> byAnnoMethod(Feature feature, Location location, int firstResult, int maxResult){
        StringBuilder bldr = new StringBuilder("SELECT a FROM AnnotationMethod as a, AnnoFact as f ");
        bldr.append("WHERE a.annotationMethodId = f.annotationMethodId ");
        if(location != null){
            bldr.append(" AND f.locationId = ").append(location.getLocationId());
        }else if(feature != null){
            bldr.append(" AND f.featureId = ").append(feature.getFeatureId());
        }
        bldr.append(" GROUP BY a.annotationMethodId ORDER BY a.annotationMethodId");
        return DataManager.getDefault().createQuery(bldr.toString(), AnnotationMethod.class, firstResult, maxResult);
    }
}
