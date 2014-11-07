/*
 * 
 * 
 */
package edu.uncc.genosets.core;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.FactDetailLocation;
import edu.uncc.genosets.datamanager.entity.FactFeature;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.persister.FactFeaturePersister;
import edu.uncc.genosets.datamanager.persister.Persister;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author aacain
 */
public class CmrClassifications {

    public final static String CUSTOM_MAIN_CATEGORY = "mainCategory";
    public final static String CATEGORY = "CMR Classification";
    public final static String CATEGORY_TYPE = "CMR Classification";
    public final static String CLUSTER_ENTITY_NAME = "Cluster_Cmr";
    public final static String FACT_ENTITY_NAME = "Fact_Feature_CmrAnno";
    public final static String METHOD_ENTITY_NAME = AnnotationMethod.DEFAULT_NAME;
    private HashMap<String, FactDetailLocation> goaLookup = new HashMap<String, FactDetailLocation>();
    private HashMap<String, FeatureCluster> catLookup = new HashMap<String, FeatureCluster>();
    private AnnotationMethod method;
    private DataManager mgr;

    private void loadGOA() {
        String queryString = "SELECT deat "
                + " FROM " + "AnnoFactDetail" + " as deat"
                + " INNER JOIN deat.organism as org "
                + " WHERE deat.detailType = 'UniProtKB'";

        List<FactDetailLocation> deats = DataManager.getDefault().createQuery(queryString);
        //add to map
        for (FactDetailLocation d : deats) {
            goaLookup.put(d.getDetailValue(), d);
        }
    }

    private FactDetailLocation lookupGOA(String goa) {
        FactDetailLocation deet = goaLookup.get(goa);
        return deet;
    }

    private void readFile(File file) {
        List<Persister> persisterList = new LinkedList<Persister>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                //split line
                String[] ss = line.split("\t");

                //lookup goa
                if (!ss[0].equals("No Data")) {
                    FactDetailLocation deet = lookupGOA(ss[0]);
                    if (deet != null) {
                        //lookup category
                        if (!ss[1].equals("No Data")) {
                            FeatureCluster cluster = getCategory(ss[1], ss[2]);
                            
                            //persist
                            FactFeature fact = new FactFeature();
                            fact.setOrganismId(deet.getOrganismId());
                            fact.setAssembledUnitId(deet.getAssembledUnitId());
                            fact.setFeatureId(deet.getFeatureId());
                            FactFeaturePersister persister = FactFeaturePersister.instantiate();
                            persisterList.add(persister);
                            persister.setup(cluster, method, fact, CLUSTER_ENTITY_NAME, METHOD_ENTITY_NAME, FACT_ENTITY_NAME);
                        }
                    }
                }
            }
            mgr.persist(persisterList);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                br.close();
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private FeatureCluster getCategory(String main, String sub) {
        FeatureCluster f = catLookup.get(main + "_" + sub);
        if (f == null) {
            f = new FeatureCluster();
            catLookup.put(main + "_" + sub, f);
            f.setClusterCategory(CATEGORY);
            f.setClusterType(CATEGORY_TYPE);
            f.setClusterName(sub);
            f.setValueOfCustomField(CUSTOM_MAIN_CATEGORY, main);
        }
        return f;
    }

    public void run(String folderName) {
        mgr = DataManager.getDefault();
        method = new AnnotationMethod();
        method.setMethodSourceType("CMR_Classifications");
        method.setRunDate(new Date());
        method.setLoadDate(method.getRunDate());
        method.setMethodCategory("Classification");

        loadGOA();

        File folder = new File(folderName);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                readFile(file);
            }
        }
    }
}
