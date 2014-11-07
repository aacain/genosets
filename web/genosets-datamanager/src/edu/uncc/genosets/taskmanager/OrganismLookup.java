/*
 * 
 * 
 */
package edu.uncc.genosets.taskmanager;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author aacain
 */
public class OrganismLookup {

    /**
     *
     *
     * @param org
     * @return list of organisms or an empty list (never returns null).
     */
    public static Collection<Organism> lookup(Organism org) {
        if (org.getProjectId() != null) {
            List<Organism> orgList = DataManager.getDefault().createQuery("from Organism as o where o.projectId = '" + org.getProjectId() + "'");
            return orgList;
        }
        return Collections.EMPTY_LIST;
    }
}
