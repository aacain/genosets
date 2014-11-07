/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.prefuse;

import prefuse.data.Schema;

/**
 *
 * @author aacain
 */
public class FeatureSchema extends Schema {
    public final static String FEATURE_ID = "FeatureId";
    public final static String START_POSITION = "StartPosition";
    public final static String LENGTH = "Length";
    public final static String END_POSITION = "EndPosition";
    public final static String CHROMOSOME = "Chromosome";
    public final static String REPUNIT_TYPE = "RepUnitType";
    public final static String COMMON_NAME = "CommonName";
    public final static String FEATURE_TYPE = "FeatureType";
    public final static String ORGANISM = "Organism";
    public final static String BIOVAR = "Biovar";
    public final static String SELECTED = "Selected";
    public final static String PRODUCT = "Product";
    public final static String LOCUS = "Locus";
    public final static String NOTE = "Note";
    public final static String STRAND = "Strand";

    public FeatureSchema(){
        addColumn(FeatureSchema.FEATURE_ID, int.class, -1);
        addColumn(FeatureSchema.START_POSITION, int.class, 0);
        addColumn(FeatureSchema.LENGTH, int.class, 0);
        addColumn(FeatureSchema.END_POSITION, int.class, 0);
        addColumn(FeatureSchema.STRAND, Character.class);
        addColumn(FeatureSchema.CHROMOSOME, String.class);
        addColumn(FeatureSchema.REPUNIT_TYPE, String.class);
        addColumn(FeatureSchema.COMMON_NAME, String.class);
        addColumn(FeatureSchema.FEATURE_TYPE, String.class, "");
        addColumn(FeatureSchema.BIOVAR, String.class);

        addColumn(FeatureSchema.SELECTED, boolean.class, true);
        addColumn(FeatureSchema.PRODUCT, String.class);
        addColumn(FeatureSchema.NOTE, String.class);
        addColumn(FeatureSchema.LOCUS, String.class);
    }


}
