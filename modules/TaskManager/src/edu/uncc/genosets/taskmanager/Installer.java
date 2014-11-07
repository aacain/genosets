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

import edu.uncc.genosets.taskmanager.view.TaskLogTopComponent;
import org.openide.modules.ModuleInstall;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        //Listen for running tasks
        TaskManagerFactory.getDefault().addPropertyChangeListener(new RunningTaskListener());
        //Have TaskLogTopComponent listen for changes to the task log.
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                TaskLogTopComponent tc = (TaskLogTopComponent) WindowManager.getDefault().findTopComponent("TaskLogTopComponent");
                if (tc != null) {
                    TaskLogFactory.getDefault().addPropertyChangeListener(tc);
                }
            }
        });
    }
}
