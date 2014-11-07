/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.datanavigator.download;

import edu.uncc.genosets.datamanager.api.AnnotationFactType;
import edu.uncc.genosets.datamanager.api.AssUnitFactType;
import edu.uncc.genosets.datamanager.api.FactType;
import edu.uncc.genosets.datanavigator.AnnoFactFlavor;
import edu.uncc.genosets.datanavigator.AssAcqFlavor;
import edu.uncc.genosets.datanavigator.FactFlavor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author aacain
 */
public class FactFlavorLookup {
    private static HashMap<Class<? extends FactType>, List<FactFlavor>> map = new HashMap<Class<? extends FactType>, List<FactFlavor>>();
    static {
        addSupportedFlavor(AnnotationFactType.ANNO_FACT_TYPE, AnnoFactFlavor.ANNO_FACT_FLAVOR);
        addSupportedFlavor(AssUnitFactType.ASS_UNIT_FACT_TYPE, AssAcqFlavor.ASS_ACQ_FLAVOR);
    }

    public static synchronized List<? extends FactFlavor> getFlavors(FactType factType) {
        List<FactFlavor> get = map.get(factType.getClass());
        if(get == null){
            return null;
        }
        return new ArrayList(map.get(factType.getClass()));
    }
    
    public static synchronized void addSupportedFlavor(FactType factType, FactFlavor flavor){
        List<FactFlavor> list = map.get(factType.getClass());
        if(list == null){
            list = new LinkedList<FactFlavor>();
            map.put(factType.getClass(), list);
        }
        list.add(flavor);
    }
}
