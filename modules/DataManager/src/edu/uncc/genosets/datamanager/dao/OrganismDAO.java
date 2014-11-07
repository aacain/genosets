/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.dao;

import edu.uncc.genosets.datamanager.entity.Organism;
import java.util.List;

/**
 *
 * @author aacain
 */
public abstract class OrganismDAO implements GenericDAO<OrganismDAO> {

    public abstract Organism lookupById(Long id);

    public abstract Organism lookupByProjectId(String projectId);

    public abstract List<Organism> lookupByTaxonId(Integer taxonId);

    public abstract Organism createOrganism(Integer taxonomyIdentitifer, String projectId);

    @Override
    public OrganismDAO instantiate() {
        return new OrganismDAOImpl();
    }

    private static class OrganismDAOImpl extends OrganismDAO {

        @Override
        public Organism lookupById(Long id) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Organism lookupByProjectId(String projectId) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<Organism> lookupByTaxonId(Integer taxonId) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Organism createOrganism(Integer taxonomyIdentitifer, String projectId) {
            Organism org = new Organism();
            org.setTaxonomyIdentifier(taxonomyIdentitifer);
            org.setProjectId(projectId);
            return org;
        }
    }
}
