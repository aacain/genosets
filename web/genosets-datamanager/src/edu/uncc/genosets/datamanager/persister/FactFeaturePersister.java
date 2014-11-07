/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.persister;

import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.FactFeature;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.Organism;
import org.hibernate.StatelessSession;

/**
 * To use this persister, you should set all the values that
 * you know using the setter methods.
 * The fact can be just an empty fact and the id's will be set later.
 *
 * @author aacain
 */
public class FactFeaturePersister extends FactPersister<FactFeature> {

    private Organism organism;
    private AssembledUnit assUnit;
    private Feature feature;

    public static FactFeaturePersister instantiate() {
        return new FactFeaturePersister();
    }

    @Override
    public void persist(StatelessSession session) {
        if (method.getAnnotationMethodId() == null) {
            session.insert(this.methodEntityName, this.method);
        }
        if (cluster.getFeatureClusterId() == null) {
            session.insert(this.clusterEntityName, this.cluster);
        }     
        //set fact values
        fact.setAnnotationMethodId(method.getAnnotationMethodId());
        fact.setFeatureClusterId(cluster.getFeatureClusterId());

        //set associations
        if(fact.getOrganismId() == null){
            if(organism.getOrganismId() == null){
                session.insert(organism.getEntityName(), organism);
            }
            fact.setOrganismId(organism.getOrganismId());
        }
        if(fact.getAssembledUnitId() == null){
            if(assUnit.getAssembledUnitId() == null){
                session.insert(assUnit.getEntityName(), assUnit);
            }
            fact.setAssembledUnitId(assUnit.getAssembledUnitId());
        }
        if(fact.getFeatureId() == null){
            if(feature.getFeatureId() == null){
                session.insert(feature.getEntityName(), feature);
            }
            fact.setFeatureId(feature.getFeatureId());
        }

        session.insert(factEntityName, fact);
    }
}
