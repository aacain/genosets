/*
 * Copyright (C) 2013 Aurora Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.uncc.genosets.taskmanager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;

/**
 *
 * @author aacain
 */
public class RunningTaskListener implements PropertyChangeListener {

    HashMap<Task, ProgressHandle> handleMap = new HashMap<Task, ProgressHandle>();

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Task task = (Task) evt.getNewValue();
        if (evt.getPropertyName().equals(TaskManager.PROP_RUNNING_LIST_ADD)) {
            ProgressHandle handle = ProgressHandleFactory.createHandle(task.getName() == null ? "Running" : task.getName());
            task.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    updateDisplayName((Task) evt.getSource());
                }
            });
            handle.start();
            handleMap.put(task, handle);
        } else if (evt.getPropertyName().equals(TaskManager.PROP_RUNNING_LIST_REMOVE)) {
            ProgressHandle handle = handleMap.remove(task);
            if (handle != null) {
                handle.finish();
            }
        }
    }

    private void updateDisplayName(Task task) {
        ProgressHandle handle = handleMap.get(task);
        if (handle != null) {
            handle.setDisplayName(task.getName());
        }
    }
}
