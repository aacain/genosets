/*
 * 
 * 
 */
package edu.uncc.genosets.dimensionhandler;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public final class TempTableCreateAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        FeatureDimensionHandler h = FeatureDimensionHandler.instantiate();
        h.createTempTable();
        //h.createNewProperties();
        h.writeProperties();
    }
}
