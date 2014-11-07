/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager;

import edu.uncc.genosets.datamanager.api.FeatureClusterClassificationPersister;
import edu.uncc.genosets.datamanager.api.FeatureClusterClassificationPersister.ClusterFact;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.entity.FeatureClusterClassification;
import java.util.Collection;
import org.hibernate.StatelessSession;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 * @author aacain
 */
@ServiceProvider(service = FeatureClusterClassificationPersister.class)
public class FeatureClusterClassificationPersisterImpl extends FeatureClusterClassificationPersister {
    private AnnotationMethod method;
    private FeatureCluster featureCluster;
    private Collection<ClusterFact> clusterFacts;

    @Override
    public boolean persist(StatelessSession session, boolean needsLookup) {
        //persist method
        if (method.getAnnotationMethodId() == null) {
            session.insert(method.getEntityName(), method);
        }

        //persist featureCluster
        if (featureCluster.getFeatureClusterId() == null) {
            session.insert(featureCluster.getEntityName(), featureCluster);
        }

        //add all facts
        for (ClusterFact clusterFact : clusterFacts) {
            insertFact(session, clusterFact);
        }
        
        return true;
    }

    private void insertFact(StatelessSession session, ClusterFact clusterFact) {
        Feature feature = clusterFact.getEntity();
        clusterFact.setEntity(refreshEntity(session, feature));

        FeatureClusterClassification factEntity = clusterFact.getFactEntity();
        factEntity.setAnnotationMethodId(method.getAnnotationMethodId());
        factEntity.setAssembledUnitId(feature.getAssembledUnit().getAssembledUnitId());
        factEntity.setFeatureId(feature.getFeatureId());
        factEntity.setFeatureClusterId(featureCluster.getFeatureClusterId());
        factEntity.setOrganismId(feature.getOrganism().getOrganismId());

        //old
        //TODO:fix entity association
        factEntity.setAnnotationMethod(method);
        factEntity.setAssembledUnit(feature.getAssembledUnit());
        factEntity.setFeature(feature);
        factEntity.setFeatureCluster(featureCluster);
        factEntity.setOrganism(feature.getOrganism());

        session.insert(factEntity.getEntityName(), factEntity);
    }

    private Feature refreshEntity(StatelessSession session, Feature feature){
        if(feature.getOrganism() == null || feature.getAssembledUnit() == null){
            Feature f = (Feature)session.get(Feature.DEFAULT_NAME, feature.getFeatureId());
            feature.setOrganism(f.getOrganism());
            feature.setAssembledUnit(f.getAssembledUnit());
            feature = (Feature)session.get(Feature.DEFAULT_NAME, feature.getFeatureId());
        }
        return feature;
    }

    @Override
    public void setValues(AnnotationMethod method, FeatureCluster cluster, Collection<ClusterFact> clusterFacts) {
        this.method = method;
        this.featureCluster = cluster;
        this.clusterFacts = clusterFacts;
    }

    @Override
    protected FeatureClusterClassificationPersister create() {
        return new FeatureClusterClassificationPersisterImpl();
    }
}
