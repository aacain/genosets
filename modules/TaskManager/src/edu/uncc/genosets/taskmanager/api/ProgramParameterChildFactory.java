/*
 * 
 * 
 */

package edu.uncc.genosets.taskmanager.api;

import java.util.Arrays;
import java.util.List;
import org.openide.nodes.ChildFactory;

/**
 *
 * @author aacain
 */
public class ProgramParameterChildFactory extends ChildFactory<ProgramParameter>{

    private List<? extends ProgramParameter> paramList;

    public ProgramParameterChildFactory(List<? extends ProgramParameter> paramList) {
        this.paramList = paramList;
    }

    public ProgramParameterChildFactory(ProgramParameter[] params) {
        this(Arrays.asList(params));
    }

    @Override
    protected boolean createKeys(List<ProgramParameter> toPopulate) {
        toPopulate.addAll(paramList);
        return true;
    }
}
