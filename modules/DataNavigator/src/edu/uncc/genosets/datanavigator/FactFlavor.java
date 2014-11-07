/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.datanavigator;

import edu.uncc.genosets.datamanager.api.FactType;
import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author aacain
 */
public abstract class FactFlavor extends DataFlavor{
    public FactFlavor(Class<?> representationClass, String humanPresentableName){
        super(representationClass, humanPresentableName);
    }   
    
    public abstract FactType getFactType();
    
}
