/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.embl;

import edu.uncc.genosets.datamanager.entity.Organism;

/**
 *
 * @author aacain
 */
public interface TaxonomyLookup {

    public static final String RESOURCE = "rdf:resource";
    public static final String ABOUT = "rdf:about";
    public static final String SCIENTIFIC_NAME = "scientificName";
    public static final String SUPERKINGDOM = "Superkingdom";
    public static final String PHYLUM = "Phylum";
    public static final String TAXCLASS = "Class";
    public static final String TAXORDER = "Order";
    public static final String FAMILY = "Family";
    public static final String GENUS = "Genus";
    public static final String SPECIES = "Species";
    public static final String LOWLEVEL = "LOWLEVEL";

    public boolean lookupByTaxId(Organism org); 
}
