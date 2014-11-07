/*
 * 
 * 
 */
package edu.uncc.genosets.geneontology;

import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.persister.Persister;
import org.hibernate.StatelessSession;

/**
 *
 * @author aacain
 */
public class GoTermPersister implements Persister {

    private FeatureCluster cluster;

    public void setup(FeatureCluster cluster){
        this.cluster = cluster;
    }

    @Override
    public void persist(StatelessSession session) {
       if(cluster.getFeatureClusterId() == null){
           session.insert(cluster.getEntityName(), cluster);
       }
    }
}
