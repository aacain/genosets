/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.geneontology.obo;

import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbPreferences;

/**
 * Responsible for storing a cache of parsed OboDataObjects
 *
 * @author aacain
 */
public class OboManager {

    private static OboDataObject obodao;
    private static final Lock lock = new ReentrantLock();
    
    public static synchronized void setLastUsed(OboDataObject oboDAO) {
        lock.lock();
        try{
        obodao = oboDAO;
        NbPreferences.forModule(OboManager.class).put("obo-last-url", oboDAO.getUrl());
        NbPreferences.forModule(OboManager.class).put("obo-last-path", oboDAO.getLocalUrl());
        }finally{
            lock.unlock();
        }
    }

    public static OboDataObject getLastUsed() {
        lock.lock();
        try {
            if (obodao == null) {
                String path = NbPreferences.forModule(OboManager.class).get("obo-last-path", null);
                String url = NbPreferences.forModule(OboManager.class).get("obo-last-url", null);
                if(path == null){
                    return null;
                }
                FileObject fo = FileUtil.toFileObject(new File(path));
                obodao = new OboDataObject(url, fo);
            }
            return obodao;
        } finally {
            lock.unlock();
        }
    }

    public static Term getTerm(String termId) {
        Term term = getLastUsed().getObo().getTerm(termId);
        return term;
    }
}
