package edu.uncc.genosets.datamanager.taxonomy;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author aacain
 */
public class TaxonomyLookupFactory {
    private static Collection<TaxonomyLookup> instances;
    public static Collection<? extends TaxonomyLookup> getTaxonomyLookups(){
        if(instances == null){
            instances = new ArrayList<TaxonomyLookup>(2);
            instances.add(new UniprotTaxonomyLookup());
            instances.add(new EnaTaxonomyLookup());
        }
        return instances;
    }
}
