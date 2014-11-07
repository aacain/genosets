/*
 * 
 * 
 */

package edu.uncc.genosets.taskmanager;

import edu.uncc.genosets.datamanager.entity.Organism;

/**
 *
 * @author aacain
 */
public abstract class SimpleTask extends AbstractTask{

    public SimpleTask(String name){
        super(name);
    }
    @Override
    public void uninitialize() {

    }

    @Override
    public void logErrors() {

    }

    @Override
    public Organism getOrganismDependency() {
        return null;
    }

    @Override
    public void setOrganismDependency(Organism org) {
        
    }

}
