/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.shutdown;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.modules.ModuleInstall;

/**
 * Manages a module's lifecycle. Remember that an installer is optional and
 * often not needed at all.
 */
public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        // By default, do nothing.
        //redirectSystemStreams();
        //delete the temp directory
        FileObject temp = FileUtil.getConfigFile("Temp");
        if (temp != null) {
            FileLock lock = null;
            try {
                lock = temp.lock();
            } catch (IOException ex) {
            }
            try {
                temp.delete(lock);
            } catch (IOException ex) {
            } finally {
                if (lock != null) {
                    lock.releaseLock();
                }
            }
        }
    }
}
