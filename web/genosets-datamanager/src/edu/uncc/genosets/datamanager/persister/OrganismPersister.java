/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.persister;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.List;
import org.hibernate.StatelessSession;

/**
 * To use this persister, you should set all the values that you know using the
 * setter methods. The fact can be just an empty fact and the id's will be set
 * later.
 *
 * @author aacain
 */
public abstract class OrganismPersister implements Persister {

    protected static PropertyChangeSupport pcs = new PropertyChangeSupport(instantiate());

    public abstract void setup(Organism organism);

    public static OrganismPersister instantiate() {
        return new OrganismPersisterImpl();
    }

    public static void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public static void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    protected static void firePropertyChange(String property, Object oldValue, Object newValue) {
        pcs.firePropertyChange(property, oldValue, newValue);
    }

    private static class OrganismPersisterImpl extends OrganismPersister {

        private Organism organism;

        @Override
        public void persist(StatelessSession session) {
            if (organism.getOrganismId() == null) {
                //lookupOrganism();
                if (organism.getOrganismId() == null) {
                    session.insert(organism.getEntityName(), organism);
                    //fire property change
                    firePropertyChange(Organism.DEFAULT_NAME, null, null);
                }
            }
        }

        private void lookupOrganism() {

            Collection<Organism> orgs = OrganismLookup.lookup(organism);
            if (orgs != null && !orgs.isEmpty()) {
                for (Organism existingOrg : orgs) {
                    organism.setOrganismId(existingOrg.getOrganismId());
                    organism.setStrain(existingOrg.getStrain());
                }
            }
        }

        @Override
        public void setup(Organism organism) {
            this.organism = organism;
        }
    }

    private static class OrganismLookup implements QueryCreator {

        public static List<Organism> lookup(Organism organism) {
            return DataManager.getDefault().createQuery("from Organism as o where o.projectId = '" + organism.getProjectId() + "'");
        }
    }
}
