/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.entity.FeatureClusterClassification;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import org.hibernate.StatelessSession;
import org.openide.util.Lookup;

/**
 * 
 * @author aacain
 */
@Deprecated
public abstract class FeatureClusterClassificationPersister implements OldPersister {

    final public static String METHOD_CATEGORY = "Feature Classification";
    protected static PropertyChangeSupport pcs = new PropertyChangeSupport(new Singleton());

    protected abstract FeatureClusterClassificationPersister create();

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    protected static void firePropertyChange(String property, Object oldValue, Object newValue) {
        pcs.firePropertyChange(property, oldValue, newValue);
    }

    public static FeatureClusterClassificationPersister instantiate() {
        FeatureClusterClassificationPersister lookup = Lookup.getDefault().lookup(FeatureClusterClassificationPersister.class);
        return lookup.create();
    }

    public abstract void setValues(AnnotationMethod method, FeatureCluster cluster, Collection<ClusterFact> clusterFacts);

    public static class ClusterFact extends Fact<Feature, FeatureClusterClassification> {

        public ClusterFact(Feature entity, FeatureClusterClassification factEntity) {
            super(entity, factEntity);
        }
    }

    private static class Singleton extends FeatureClusterClassificationPersister{

        @Override
        protected FeatureClusterClassificationPersister create() {
            return new Singleton();
        }

        @Override
        public void setValues(AnnotationMethod method, FeatureCluster cluster, Collection<ClusterFact> clusterFacts) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean persist(StatelessSession session, boolean needsLookup) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
