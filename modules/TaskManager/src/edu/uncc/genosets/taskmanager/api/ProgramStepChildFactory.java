/*
 * 
 * 
 */

package edu.uncc.genosets.taskmanager.api;

import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class ProgramStepChildFactory extends ChildFactory<ProgramStep>{

    private List<ProgramStep> steps;

    public ProgramStepChildFactory(List<ProgramStep> steps) {
        this.steps = steps;
    }


    @Override
    protected boolean createKeys(List<ProgramStep> toPopulate) {
          toPopulate.addAll(steps);
          return true;
    }

    @Override
    protected Node createNodeForKey(ProgramStep key) {
        return new ProgramStepNode(key);
    }

}
