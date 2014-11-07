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
public class GOSchema extends Schema{
    public static final String ID = "ID";
    public static final String GO_TERM_ID = "GoTermId";
    public static final String ACCESSION = "Accession";
    public static final String NAME = "Name";
    public static final String TERM_TYPE = "TermType";
    public static final String IS_OBSOLETE = "IsObsolete";
    public static final String IS_ROOT = "IsRoot";
    
    public static final String SELECTED = "GoSelected";
    public static final String TOTAL_COUNT = "TotalCount";
    public static final String SPAWNING_COUNT = "SpawningCount";
    public static final String SELECTED_COUNT = "SelectedCount";
    public static final String PERCENT_CHANGE = "PercentChange";

    public final static String BACKING_INDEX = "BackingIndex";

    public GOSchema(){
        this.addColumn(ID, int.class);
        this.addColumn(GO_TERM_ID, String.class);
        this.addColumn(ACCESSION, String.class);
        this.addColumn(NAME, String.class);
        this.addColumn(TERM_TYPE, String.class);
        this.addColumn(IS_OBSOLETE, int.class);
        this.addColumn(IS_ROOT, int.class);
        
        this.addColumn(SELECTED, boolean.class);
        this.addColumn(TOTAL_COUNT, int.class, 0);
        this.addColumn(SELECTED_COUNT, int.class, 0);
        this.addColumn(PERCENT_CHANGE, double.class, 0.0);
        this.addColumn(SPAWNING_COUNT, int.class, 0);

        this.addColumn(BACKING_INDEX, int.class);
    }
}
