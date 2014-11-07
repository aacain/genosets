/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager;

import edu.uncc.genosets.datamanager.api.AssembledUnitFinder;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import java.util.List;
import org.openide.util.lookup.ServiceProvider;

/**
 * 
 *
 * @author aacain
 */
@ServiceProvider(service = AssembledUnitFinder.class)
public class AssembledUnitFinderImpl implements AssembledUnitFinder {

    @Override
    public List<? extends AssembledUnit> assembledUnitLookup(AssembledUnit assUnit) {
        String query = "select a from AssembledUnit a "
                + "where a.assembledUnitName = '" + assUnit.getAssembledUnitName() + "'";
        DataManager mgr = DataManager.getDefault();
        List<? extends AssembledUnit> createQuery = null;
        if(mgr != null)
            createQuery = mgr.createQuery(query, AssembledUnit.class);
        return createQuery;
    }

    @Override
    public String getLookupDetails() {
        return "Looks up assembled unit by assembled unit name";
    }
}
