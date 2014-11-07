/*
 * 
 * 
 */

package edu.uncc.genosets.treemap;

import java.util.ArrayList;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.ProxyLookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aacain
 */
public interface GenoSetsTreeMapSource extends Lookup.Provider{

    public void addLookup(Lookup lookup);
    public void removeLookup(Lookup lookup);

    @ServiceProvider(service=GenoSetsTreeMapSource.class)
    public class GenoSetsTreeMapSourceImpl implements GenoSetsTreeMapSource{
        private ArrayList<Lookup> lookupList = new ArrayList<Lookup>();
        private ProxyLookup proxy;
        private static Object defaultLookupLock = new Object();
        @Override
        public Lookup getLookup() {
            if(lookupList.isEmpty()){
                lookupList.add(Utilities.actionsGlobalContext());
            }
            proxy = new ProxyLookup(lookupList.toArray(new Lookup[lookupList.size()]));
            return proxy;
        }

        @Override
        public void addLookup(Lookup lookup) {
            lookupList.add(lookup);
        }

        @Override
        public void removeLookup(Lookup lookup) {
            lookupList.remove(lookup);
        }
    }
}
