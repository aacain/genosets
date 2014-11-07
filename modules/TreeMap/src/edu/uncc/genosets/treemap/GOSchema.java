/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.treemap;

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

    
    public static final String IS_SELECTED = "GoSelected";
    public static final String TOTAL_COUNT = "TotalCount";
    public static final String STUDY_COUNT = "SelectedCount";
    public static final String P_VALUE = "PercentChange";
    public static final String RATIO = "Ratio";

    public final static String BACKING_INDEX = "BackingIndex";

    public GOSchema(){
        this.addColumn(ID, int.class);
        this.addColumn(GO_TERM_ID, String.class);
        this.addColumn(NAME, String.class);
        
        this.addColumn(IS_SELECTED, boolean.class, Boolean.FALSE);
        this.addColumn(TOTAL_COUNT, int.class, 0);
        this.addColumn(STUDY_COUNT, int.class, 0);
        this.addColumn(P_VALUE, double.class, 1.0);
        this.addColumn(BACKING_INDEX, int.class);
        this.addColumn(RATIO, double.class, 0.0);
    }
}
