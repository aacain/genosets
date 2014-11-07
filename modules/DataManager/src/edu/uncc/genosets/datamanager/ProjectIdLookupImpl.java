/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.OrganismLookup;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aacain
 */
@ServiceProvider(service = OrganismLookup.class)
public class ProjectIdLookupImpl implements OrganismLookup {

    @Override
    public Collection<Organism> lookup(Organism org) {
        if (org.getProjectId() != null) {
            List<Organism> orgList = DataManager.getDefault().createQuery("from Organism as o where o.projectId = '" + org.getProjectId() + "'");
            return orgList;
        }
        return Collections.EMPTY_LIST;
    }
}
