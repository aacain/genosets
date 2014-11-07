/*
 * 
 * 
 */
package edu.uncc.genosets.embl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import org.openide.util.Exceptions;

public final class AccessionLookupAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        try{
            Map<String, String> lookup = AccessionLookup.lookup("YP_001501127");
            if(lookup != null){
                System.out.println(lookup.get("Accession"));
                System.out.println(lookup.get("Protein names"));
            }
        }catch (Exception ex){
            Exceptions.printStackTrace(ex);
        }
    }
}
