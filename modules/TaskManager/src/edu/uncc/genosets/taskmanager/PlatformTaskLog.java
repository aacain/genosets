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

import edu.uncc.genosets.datamanager.api.DataManager;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.logging.LogFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author aacain
 */
public class PlatformTaskLog extends TaskLog{
    /**
     * Create all necessary files.
     */
    @Override
    protected void createFiles() {
        //this.logFile = null;
        logFile = null;
        FileObject logFo = null;
        String db = DataManager.getDefault().getConnectionId();
        if (db != null) {
            try {
                FileObject dbRoot = FileUtil.getConfigFile(db);
                FileObject logRoot = dbRoot.getFileObject("log");
                if (logRoot == null) { //create the log root
                    logRoot = dbRoot.createFolder("log");
                }
                logFo = logRoot.getFileObject("message", "log");
                if (logFo == null) {
                    logFo = logRoot.createData("message", "log");
                }
                logFile = FileUtil.toFile(logFo);
            } catch (IOException ex) {
                LogFactory.getLog(PlatformTaskLog.class).warn("Could not create task log file.");
            }
        }
    }

    @Override
    public synchronized void log(String desc, String source, String details, String level, Date date) {
        super.log(desc, source, details, level, date);
    }
}
