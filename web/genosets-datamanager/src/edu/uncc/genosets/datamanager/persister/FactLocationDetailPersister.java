/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.persister;

import edu.uncc.genosets.datamanager.entity.FactDetailLocation;
import edu.uncc.genosets.datamanager.entity.FactLocation;
import org.hibernate.StatelessSession;

/**
 * To use this persister, you should set all the values that
 * you know using the setter methods.
 * The fact can be just an empty fact and the id's will be set later.
 *
 * @author aacain
 */
public class FactLocationDetailPersister extends FactDetailPersister<FactLocation, FactDetailLocation>{


    public static FactLocationDetailPersister instantiate(FactLocation parentFact, FactDetailLocation detailFact, String detailEntityName){
        FactLocationDetailPersister p = new FactLocationDetailPersister();
        p.setParentFact(parentFact);
        p.setDetailFact(detailFact);
        p.setDetailEntityName(detailEntityName);
        return p;
    }
    @Override
    public void persist(StatelessSession session) {
        detailFact.setAnnotationMethodId(parentFact.getAnnotationMethodId());
        detailFact.setFeatureClusterId(parentFact.getFeatureClusterId());
        detailFact.setOrganismId(parentFact.getOrganismId());
        detailFact.setAssembledUnitId(parentFact.getAssembledUnitId());
        detailFact.setFeatureId(parentFact.getFeatureId());
        detailFact.setLocationId(parentFact.getLocationId());
        detailFact.setFeatureSequenceId(parentFact.getFeatureSequenceId());
        detailFact.setParentFactId(parentFact.getFactId());
        session.insert(this.detailEntityName, detailFact);
    }
}
