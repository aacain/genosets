/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.geneontology;

import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import java.util.HashMap;

/**
 *
 * @author aacain
 */
public class TermLookup {
    private HashMap<String, FeatureCluster> clusterMap = new HashMap<String, FeatureCluster>();

    public synchronized FeatureCluster lookup(String termId){
        FeatureCluster fc = clusterMap.get(termId);
        if(fc == null){
            fc = new FeatureCluster();
            clusterMap.put(termId, fc);
        }
        return fc;
    }
}
