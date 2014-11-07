/*
 * 
 * 
 */

package edu.uncc.genosets.treemap;

import edu.uncc.genosets.datamanager.api.DataManager;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import org.openide.util.Lookup;

/**
 *
 * @author aacain
 */
public class ListViewLoader {
    HashMap<Integer, Object[]> featureLookup = new HashMap<Integer, Object[]>();
    String[] header = new String[]{
        "Feature Id", "Locus Tag", "Organism", "Product", "End Position"
    };

    @SuppressWarnings("unchecked")
    public ListViewLoader(){
        DataManager mgr = DataManager.getDefault();
        List<Object[]> list = mgr.createQuery("Select fact.featureId, f.primaryName,  o.strain, f.product, l.endPosition "
                + " from AnnoFact as fact"
                + ", Feature as f, Organism as o, Location as l "
                + " where fact.featureId = f.featureId and o.organismId = fact.organismId and l.locationId = fact.locationId");
        for (Object[] line : list) {
            featureLookup.put((Integer)line[0], line);
        }
    }

    public void load(Collection<Integer> featureIds){
        Object[][] modelList = new Object[featureIds.size()][];
        int i = 0;
        for (Integer id : featureIds) {
            modelList[i] = featureLookup.get(id);

            i++;
        }

        ListViewTopComponent window = new ListViewTopComponent(header, modelList);
        window.open();
        window.requestActive();
    }
}
