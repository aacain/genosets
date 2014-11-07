/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import java.util.List;

/**
 *
 * @author aacain
 */
public interface AssembledUnitFinder {

    public List<? extends AssembledUnit> assembledUnitLookup(AssembledUnit assUnit);

    public String getLookupDetails();
}
