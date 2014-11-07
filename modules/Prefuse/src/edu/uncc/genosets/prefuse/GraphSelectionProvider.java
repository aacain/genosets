/*
 * 
 * 
 */

package edu.uncc.genosets.prefuse;

import prefuse.data.event.TupleSetListener;

/**
 *
 * @author aacain
 */
public interface GraphSelectionProvider {
    public static final String SELECTED_ITEMS = "SELECTED_ITEMS";

    public void addSelectionListener(TupleSetListener listener);
    public void removeSelectionListener(TupleSetListener listener);
}
