/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.persister;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.FactLocation;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.datamanager.entity.MolecularSequence;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import java.util.List;
import org.hibernate.StatelessSession;

/**
 * To use this persister, you should set all the values that you know using the
 * setter methods. The fact can be just an empty fact and the id's will be set
 * later.
 *
 * @author aacain
 */
public class FactLocationPersister extends FactPersister<FactLocation> {

    private Organism organism;
    private AssembledUnit assUnit;
    private Feature feature;
    private Location location;
    private String sequence;
    static int count = 0;

    public static FactLocationPersister instantiate() {
        return new FactLocationPersister();
    }

    public AssembledUnit getAssUnit() {
        return assUnit;
    }

    public void setAssUnit(AssembledUnit assUnit) {
        this.assUnit = assUnit;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Organism getOrganism() {
        return organism;
    }

    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
    
    public void setEntities(Organism organism, AssembledUnit assUnit, Feature feature, Location location){
        setOrganism(organism);
        setAssUnit(assUnit);
        setFeature(feature);
        setLocation(location);
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

        //set known associations
        if (fact.getOrganismId() == null) {
            if (organism.getOrganismId() == null) {
                session.insert(organism.getEntityName(), organism);
            }
            fact.setOrganismId(organism.getOrganismId());
        }
        if (fact.getAssembledUnitId() == null) {
            if (assUnit.getAssembledUnitId() == null) {
                session.insert(assUnit.getEntityName(), assUnit);
            }
            fact.setAssembledUnitId(assUnit.getAssembledUnitId());
        }

        //persist feature, location, and sequence if they dont' exist
        boolean added = false;
        if (fact.getFeatureId() == null) {
            if (feature.getFeatureId() == null) {
                feature.setOrganismId(fact.getOrganismId());
                feature.setAssembledUnitId(fact.getAssembledUnitId());
                if (feature.getProduct() != null && feature.getProduct().length() > 255) {
                    feature.setProduct(feature.getProduct().substring(0, 255));
                }
                added = true;
                session.insert(Feature.DEFAULT_NAME, feature);
            }
            fact.setFeatureId(feature.getFeatureId());
        }
        if (fact.getLocationId() == null) {
            if (location.getLocationId() == null) {
                location.setFeatureId(fact.getFeatureId());
                location.setOrganismId(fact.getOrganismId());
                location.setAssembledUnitId(fact.getAssembledUnitId());
                if (location.getProduct() != null && location.getProduct().length() > 255) {
                    location.setProduct(location.getProduct().substring(0, 255));
                }
                added = true;
                session.insert(Location.DEFAULT_NAME, location);
                fact.setLocationId(location.getLocationId());
            } else {
                fact.setLocationId(location.getLocationId());
            }
        }
        if (sequence != null) {
            //test to see if it exists
            boolean exists = FeatureSequenceQuery.exists(location.getLocationId());
            if (!exists) {
                MolecularSequence seq = new MolecularSequence();
                seq.setMolecularSequenceId(location.getLocationId());
                seq.setForwardSequence(sequence);
                session.insert("ProteinSequence", seq);
            }
        }
        session.insert(factEntityName, fact);

    }//end class

    private static class FeatureSequenceQuery implements QueryCreator {

        public static boolean exists(int sequenceId) {
            StringBuilder builder = new StringBuilder("SELECT molecularSequenceId FROM ProteinSequence WHERE molecularSequenceId = ");
            builder.append(sequenceId);
            List createQuery = DataManager.getDefault().createQuery(builder.toString());
            if (createQuery == null || createQuery.isEmpty()) {
                return false;
            }
            return true;
        }
    }
}//end class
