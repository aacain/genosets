/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.datamanager.entity.Organism;
import java.util.Collection;

/**
 *
 * @author aacain
 */
public interface OrganismLookup {
    /**
     *
     *
     * @param org
     * @return list of organisms or an empty list (never returns null).
     */
    public Collection<Organism> lookup(Organism org);
}
