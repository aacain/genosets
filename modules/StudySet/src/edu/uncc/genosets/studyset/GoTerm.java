/*
 * 
 * 
 */

package edu.uncc.genosets.studyset;

import edu.uncc.genosets.geneontology.obo.Obo;
import edu.uncc.genosets.geneontology.obo.OboManager;
import edu.uncc.genosets.geneontology.obo.Term;

/**
 *
 * @author aacain
 */
public class GoTerm {
    private String goId;
    //private String goName;
    //private static HashMap<String, String> goIdToNameLookup;

    public GoTerm(){
        
    }

    public GoTerm(String goId){
        this.goId = goId;
    }

    public String getGoId() {
        return goId;
    }

    public void setGoId(String goId) {
        this.goId = goId;
    }

    public String getGoName() {
        Term term = OboManager.getTerm(goId);
        if(term != null){
            return term.getName();
        }
        return null;
    }

//    private synchronized static void setGONames(){
//        goIdToNameLookup = new HashMap<String, String>();
//        DataManager mgr = DataManager.getDefault();
//        List<FeatureCluster> clusters = mgr.createQuery("SELECT t from Cluster_GoTerm as t" );
//        for (FeatureCluster t : clusters) {
//            goIdToNameLookup.put(t.getClusterName(), (String)t.getValueOfCustomField("goName"));
//        }
//    }

    public static String getGOName(String goId){
        if(goId.equals("GO:0000000")){
            return "All";
        }
        Term term = OboManager.getTerm(goId);
        if(term != null){
            return term.getName();
        }
        return null;
    }
}
