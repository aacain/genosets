/*
 * 
 * 
 */
package edu.uncc.genosets.core.api;

import edu.uncc.genosets.core.CmrClassifications;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.util.Exceptions;

public final class LoadCMR implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        try{
        CmrClassifications cmr = new CmrClassifications();
        cmr.run("/Users/aacain/Desktop/CMR");
        }catch(Exception ex){
            Exceptions.printStackTrace(ex);
        }
    }
}
