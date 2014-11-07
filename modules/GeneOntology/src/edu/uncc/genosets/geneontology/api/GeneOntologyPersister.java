/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.geneontology.api;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class GeneOntologyPersister {
    private static Set<GeneOntologyPersister> instances;
    
    public static synchronized GeneOntologyPersister instantiate(){
        GeneOntologyPersister impl = new GeneOntologyPersister();
        if(instances == null){
            instances = new HashSet<GeneOntologyPersister>();
        }
        instances.add(impl);
        return impl;
    }
    
    public static synchronized void removeInstance(GeneOntologyPersister inst){
        instances.remove(inst);
        if(instances == null){
            
        }
    }
    
    public boolean persist(){
        
        return true;
    }
}
