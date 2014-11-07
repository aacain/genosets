/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.FeatureAnnotation;
import edu.uncc.genosets.datamanager.entity.FeatureAnnotationDetail;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Set;
import org.hibernate.StatelessSession;
import org.openide.util.Lookup;

/**
 * this class should be thread safe
 *
 * @author aacain
 */
@Deprecated
public abstract class FeatureAnnotationPersister implements OldPersister {

    final public static String METHOD_CATEGORY = "Feature Annotation";
    protected static PropertyChangeSupport pcs = new PropertyChangeSupport(new Singleton());

    protected abstract FeatureAnnotationPersister create();

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    protected static void firePropertyChange(String property, Object oldValue, Object newValue) {
        pcs.firePropertyChange(property, oldValue, newValue);
    }

    public static FeatureAnnotationPersister instantiate() {
        FeatureAnnotationPersister lookup = Lookup.getDefault().lookup(FeatureAnnotationPersister.class);
        return lookup.create();
    }


    public abstract void setValues(AnnotationMethod method, Organism org, AssembledUnit assUnit, Collection<FeatureAnnotationFact> featureFacts);

    public static class FeatureAnnotationFact extends Fact<Feature, FeatureAnnotation> {
        private Set<FeatureAnnotationDetail> details;
        public FeatureAnnotationFact(Feature entity, FeatureAnnotation factEntity, Set<FeatureAnnotationDetail> details) {
            super(entity, factEntity);
            this.details = details;
        }

        public Set<FeatureAnnotationDetail> getDetails() {
            return details;
        }

        public void setDetails(Set<FeatureAnnotationDetail> details) {
            this.details = details;
        }
    }

    private static class Singleton extends FeatureAnnotationPersister {

        @Override
        protected FeatureAnnotationPersister create() {
            return new Singleton();
        }

        @Override
        public void setValues(AnnotationMethod method, Organism org, AssembledUnit assUnit, Collection<FeatureAnnotationFact> featureFacts) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean persist(StatelessSession session, boolean needsLookup) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
