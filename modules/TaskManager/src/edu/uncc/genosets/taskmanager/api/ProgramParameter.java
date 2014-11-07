/*
 * 
 * 
 */

package edu.uncc.genosets.taskmanager.api;

import java.beans.PropertyChangeListener;

/**
 *
 * @author aacain
 */
public interface ProgramParameter {

    public void setParentStep(ProgramStep step);
    public ProgramStep getParentStep();
    public void setParameterValue(String value);
    public String getName();
    public String getParameterValue();
    public String getDefaultParameterValue();
    public String getParameterDescription();
    public boolean isOptional();
    public boolean isUserRequired();
    public void addPropertyChangeListener(PropertyChangeListener listener);
    public void removePropertyChangeListener(PropertyChangeListener listener);
    public static final String PROP_NAME = "PROP_NAME";
    public static final String PROP_VALUE = "PROP_VALUE";
    public static final String PROP_DEFAULTVALUE = "PROP_DEFAULTVALUE";
    public static final String PROP_DESCRIPTION = "PROP_DESCRIPTION";
    public static final String PROP_IS_OPTIONAL = "PROP_IS_OPTIONAL";
    public static final String PROP_IS_USERREQUIRED = "PROP_IS_USERREQUIRED";
}
