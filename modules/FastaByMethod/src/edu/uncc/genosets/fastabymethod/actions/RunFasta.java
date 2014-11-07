/*
 * 
 * 
 */
package edu.uncc.genosets.fastabymethod.actions;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.fastabymethod.api.FastaCreator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

public final class RunFasta implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        try{
            FileObject root = FileUtil.getConfigRoot();
            FileObject fo = root.getFileObject("Fasta");
            if(fo != null){
                fo.delete();
            }
            fo = root.createFolder("Fasta");
            FastaCreator creator = new FastaCreator(fo);
            DataManager mgr = Lookup.getDefault().lookup(DataManager.class);
            StringBuilder bldr = new StringBuilder("SELECT a.locationId, a.organismId FROM AnnoFact as a WHERE a.annotationMethodId = 3309573 OR a.annotationMethodId = 3309575 OR a.annotationMethodId = 3309569 OR a.annotationMethodId = 3309571 ");

            List<Object[]> result = mgr.createQuery(bldr.toString());
            HashMap<String, Set<Integer>> map = new HashMap<String, Set<Integer>>();
            for (Object[] line : result) {
                Set<Integer> locSet = map.get(line[1].toString());
                if(locSet == null){
                    locSet = new HashSet<Integer>();
                    map.put(line[1].toString(), locSet);
                }
                locSet.add((Integer)line[0]);
            }

            for (Entry<String, Set<Integer>> entry : map.entrySet()) {
                String fileId = "xx" + entry.getKey();

                creator.createFiles(fileId.substring(0, fileId.length() < 4 ? fileId.length() : 4), entry.getValue());
            }
        }catch (Exception ex){
            Exceptions.printStackTrace(ex);
        }
    }
}
