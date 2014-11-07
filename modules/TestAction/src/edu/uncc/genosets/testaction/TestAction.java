/*
 * 
 * 
 */
package edu.uncc.genosets.testaction;

import edu.uncc.genosets.core.api.LegacyLoader;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.dimension2.DimensionImpl4;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

public final class TestAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {
        try{
            //testDimension();
            //loadLegacy();
            testHibUtil();
        }catch(Exception ex){
            Exceptions.printStackTrace(ex);
        }
    }

    private void testHibUtil(){
        DataManager mgr = Lookup.getDefault().lookup(DataManager.class);
        List<Object[]> createNativeQuery = mgr.createNativeQuery("select * from feature");
        System.out.println(createNativeQuery);
    }

    private void loadLegacy(){
        LegacyLoader l = new LegacyLoader(new File("/Users/aacain/BioTools/data/legacyContig.txt"),
                new File("/Users/aacain/BioTools/data/legacyGenes.txt"));
        l.load();
        
    }

    private void testDimension() {
            DimensionImpl4 dim = new DimensionImpl4();
            dim.createQuery("MyFeature");
    }
}
