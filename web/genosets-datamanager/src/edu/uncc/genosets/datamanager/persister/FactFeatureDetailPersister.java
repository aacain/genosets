/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.persister;

import edu.uncc.genosets.datamanager.entity.FeatureClusterClassification;
import edu.uncc.genosets.datamanager.entity.FeatureClusterDetail;
import org.hibernate.StatelessSession;
/**
 * To use this persister, you should set all the values that
 * you know using the setter methods.
 * The fact can be just an empty fact and the id's will be set later.
 *
 * @author aacain
 */
public class FactFeatureDetailPersister extends FactDetailPersister<FeatureClusterClassification, FeatureClusterDetail>{
    
    @Override
    public void persist(StatelessSession session) {
        detailFact.setAnnotationMethodId(parentFact.getAnnotationMethodId());
        detailFact.setFeatureClusterId(parentFact.getFeatureClusterId());
        detailFact.setOrganismId(parentFact.getOrganismId());
        detailFact.setAssembledUnitId(parentFact.getAssembledUnitId());
        detailFact.setFeatureId(parentFact.getFeatureId());
        detailFact.setClusterFactId(parentFact.getClusterClassificationId());

        session.insert(this.detailEntityName, detailFact);
    }
}
