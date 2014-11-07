/*
 * 
 * 
 */

package edu.uncc.genosets.taskmanager.api;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class ProgramStepNode extends AbstractNode implements PropertyChangeListener{

    public ProgramStepNode(ProgramStep step){      
        this(step, Lookups.singleton(step));
    }

    public ProgramStepNode(ProgramStep step, Lookup lookup){
        super(Children.create(new ProgramParameterChildFactory(step.getProgramParameters()), true), Lookups.singleton(step));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
    }

}
