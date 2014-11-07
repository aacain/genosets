/*
 * 
 * 
 */
package edu.uncc.genosets.taskmanager.api;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author aacain
 */
public class AbstractProgramParameter implements ProgramParameter {

    private ProgramStep step;
    private String name;
    private String parameterValue;
    private String defaultParameterValue;
    private String parameterDescription;
    private boolean optional;
    private boolean userRequired = false;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public AbstractProgramParameter(ProgramStep parentStep){
        this.step = parentStep;
    }

    public void setParameterDefaultValue(String defaultValue) {
        this.defaultParameterValue = defaultValue;
        this.parameterValue = defaultValue;
    }

    public void setParameterDescription(String description) {
        this.parameterDescription = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public void setIsUserRequired(boolean required) {
        this.userRequired = required;
    }

    @Override
    public boolean isUserRequired() {
        return userRequired;
    }

    @Override
    public void setParameterValue(String value) {
        this.parameterValue = value;
    }

    @Override
    public String getDefaultParameterValue() {
        return defaultParameterValue;
    }

    @Override
    public String getParameterDescription() {
        return this.parameterDescription;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getParameterValue() {
        return this.parameterValue;
    }

    @Override
    public boolean isOptional() {
        return this.optional;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);

    }

    @Override
    public void setParentStep(ProgramStep step) {
        this.step = step;
    }

    @Override
    public ProgramStep getParentStep() {
        return this.step;
    }
}
