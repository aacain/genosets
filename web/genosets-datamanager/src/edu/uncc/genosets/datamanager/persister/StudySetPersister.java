package edu.uncc.genosets.datamanager.persister;

import edu.uncc.genosets.datamanager.entity.FactStudySet;
import edu.uncc.genosets.datamanager.entity.StudySetEntity;
import org.hibernate.StatelessSession;

/**
 *
 * @author aacain
 */
public class StudySetPersister implements Persister{
    StudySetEntity studySet;
    FactStudySet fact;

    public StudySetPersister(StudySetEntity studySet, FactStudySet fact) {
        this.studySet = studySet;
        this.fact = fact;
    }
    

    @Override
    public void persist(StatelessSession session) {
        if(studySet.getStudySetId() == null){
            session.insert(studySet);
        }
        fact.setStudySetId(studySet.getStudySetId());
        session.insert(fact);
    }

}
