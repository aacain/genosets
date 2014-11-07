/*
 * 
 * 
 */

package edu.uncc.genosets.taskmanager.api;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

/**
 *
 * @author aacain
 */
public class AbstractProgramStep implements ProgramStep{
    private String name;
    private String description;
    private String command;
    private List<? extends ProgramParameter> programParameters;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<? extends ProgramParameter> getProgramParameters() {
        return programParameters;
    }

    public void setProgramParameters(List<? extends ProgramParameter> programParameters) {
        this.programParameters = programParameters;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);

    }


}
