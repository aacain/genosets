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
package edu.uncc.genosets.studyset;

import edu.uncc.genosets.datamanager.entity.FactLocation;
import edu.uncc.genosets.datamanager.entity.FactStudySet;
import edu.uncc.genosets.datamanager.entity.StudySetEntity;
import edu.uncc.genosets.datamanager.persister.FactPersister;
import java.util.Set;
import org.hibernate.StatelessSession;

/**
 *
 * @author aacain
 */
public class StudySetPersister extends FactPersister<FactLocation> {

    protected StudySetEntity studySet;
    protected Set<FactStudySet> toAdd;
    protected Set<FactStudySet> toDelete;

    public StudySetPersister(StudySetEntity studySet, Set<FactStudySet> toAdd, Set<FactStudySet> toDelete) {
        this.studySet = studySet;
        this.toAdd = toAdd;
        this.toDelete = toDelete;
    }

    @Override
    public void persist(StatelessSession ss) {
        if (studySet.getId() == null) {
            ss.insert(studySet.getEntityName(), studySet);
        } else {
            ss.update(studySet.getEntityName(), studySet);
        }
        if (toAdd != null) {
            for (FactStudySet factStudySet : toAdd) {
                if (factStudySet.getFactId() == null) {
                    factStudySet.setStudySetId(studySet.getId());
                    ss.insert(factStudySet.getEntityName(), factStudySet);
                }
            }
        }
        if (toDelete != null) {
            for (FactStudySet factStudySet : toDelete) {
                if (factStudySet.getFactId() != null) {
                    ss.delete(factStudySet.getEntityName(), factStudySet);
                }
            }
        }
    }
}
