/*
 * 
 * 
 */
package edu.uncc.genosets.core.api;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.openide.util.Exceptions;

public final class LocationAmbiguityAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        Annotation a = new Annotation();
        try{
            a.run(new File("/Users/aacain/Documents/My Dropbox/vibrio/Annotation/Criteria.txt"), new File("/Users/aacain/Documents/My Dropbox/vibrio/Annotation"));
        }catch(Exception ex){
            Exceptions.printStackTrace(ex);
        }
    }
}
