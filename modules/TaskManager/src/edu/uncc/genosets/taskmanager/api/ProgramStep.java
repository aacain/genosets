/*
 * 
 * 
 */

package edu.uncc.genosets.taskmanager.api;

import java.beans.PropertyChangeListener;
import java.util.List;

/**
 *
 * @author aacain
 */
public interface ProgramStep {
    public String getName();
    public String getDescription();
    public String getCommand();
    public List<? extends ProgramParameter> getProgramParameters();
    public void addPropertyChangeListener(PropertyChangeListener listener);
    public void removePropertyChangeListener(PropertyChangeListener listener);

    public static final String PROP_NAME = "PROP_NAME";
    public static final String PROP_DESCRIPTION = "PROP_DESCRIPTION";
    public static final String PROP_COMMAND = "PROP_COMMAND";
}
