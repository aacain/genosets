/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.shutdown;

import edu.uncc.genosets.taskmanager.TaskManager;
import edu.uncc.genosets.taskmanager.TaskManagerFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.LifecycleManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aacain
 */
@ServiceProvider(service = LifecycleManager.class, position = 1)
public class GenoSetsLifecycleManager extends LifecycleManager {

    private static boolean canClose = false;

    @Override
    public void saveAll() {
//        for (LifecycleManager mgr : Lookup.getDefault().lookupAll(LifecycleManager.class)) {
//            if(mgr != this){
//                this.saveAll();
//            }
//        }
    }

    @Override
    public void exit() {
        if (canClose) {
            for (LifecycleManager mgr : Lookup.getDefault().lookupAll(LifecycleManager.class)) {
                if (mgr != this) {
                    mgr.exit();
                }
            }
        } else {
            TaskManager tskMgr = TaskManagerFactory.getDefault();
            if (tskMgr.getRunningTasks().isEmpty()) {
                canClose = true;
                for (LifecycleManager mgr : Lookup.getDefault().lookupAll(LifecycleManager.class)) {
                    mgr.exit();
                }
            } else {
                DialogPanel panel = new DialogPanel();
                DialogDescriptor dd = new DialogDescriptor(panel, "Shutdown", true, DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.CANCEL_OPTION, null);
                Object result = DialogDisplayer.getDefault().notify(dd);
                if(null != result && DialogDescriptor.OK_OPTION == result){
                    canClose = true;
                    for (LifecycleManager mgr : Lookup.getDefault().lookupAll(LifecycleManager.class)) {
                        mgr.exit();
                    }
                }
            }
        }
    }
}
