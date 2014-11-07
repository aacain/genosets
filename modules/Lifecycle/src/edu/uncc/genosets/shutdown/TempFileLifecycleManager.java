/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.shutdown;

import edu.uncc.genosets.taskmanager.TaskManager;
import edu.uncc.genosets.taskmanager.TaskManagerFactory;
import java.io.IOException;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aacain
 */
@ServiceProvider(service = LifecycleManager.class, position = 2)
public class TempFileLifecycleManager extends LifecycleManager {

    @Override
    public void saveAll() {

    }

    @Override
    public void exit() {
        FileObject tempFolder = FileUtil.getConfigFile("Temp");
        attemptDelete(tempFolder, 0);
        
    }
    
    private void attemptDelete(FileObject fo, int index){
        index++;
        if(fo == null || index > 1000){
            return;
        }
        try {
            fo.delete();
        } catch (IOException ex) {
            attemptDelete(fo, index);
        }
    }
}
