/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.geneontology.api;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.persister.Persister;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.StatelessSession;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.util.Exceptions;

/**
 *
 * @author aacain
 */
public class GoTermPersister {

    private HashMap<String, FeatureCluster> goMap;

    public static synchronized GoTermPersister instantiate(InputStream is) {
        return new GoTermPersister(is);
    }

    public static synchronized GoTermPersister instantiate(InputStream is, ProgressHandle handle) {
        return new GoTermPersister(is, handle);
    }

    public FeatureCluster getGoTerm(String goid) {
        return goMap.get(goid);
    }

    private GoTermPersister(InputStream oboIs) {
        goMap = new HashMap<String, FeatureCluster>(); //lookup current features
        List<FeatureCluster> createQuery = GoTermQueryCreator.createQuery();
        for (FeatureCluster fc : createQuery) {
            goMap.put(fc.getClusterName(), fc);
        }
        //parse file
        List<FeatureCluster> toAdd = parseFile(oboIs, null);
        GoTermPersisterImpl termPersister = new GoTermPersisterImpl(toAdd);
        DataManager.getDefault().persist(Collections.singletonList(termPersister));
    }

    private GoTermPersister(InputStream oboIs, ProgressHandle handle) {
        handle.switchToDeterminate(100);
        goMap = new HashMap<String, FeatureCluster>(); //lookup current features
        handle.progress("Querying existing Go terms.", 20);
        List<FeatureCluster> createQuery = GoTermQueryCreator.createQuery();
        for (FeatureCluster fc : createQuery) {
            goMap.put(fc.getClusterName(), fc);
        }
        //parse file
        handle.progress("Parsing obo file.", 50);
        List<FeatureCluster> toAdd = parseFile(oboIs, handle);
        handle.progress("Adding new terms to database.", 80);
        GoTermPersisterImpl termPersister = new GoTermPersisterImpl(toAdd);
        DataManager.getDefault().persist(Collections.singletonList(termPersister));
        handle.progress(80);
    }

    /**
     * Parses the terms in the file
     *
     * @param obo
     * @return the clusters that do not exist in the database
     */
    private List<FeatureCluster> parseFile(InputStream is, ProgressHandle handle) {
        DecimalFormat decimals = new DecimalFormat("0.0");
        BufferedReader rd = null;
        List<FeatureCluster> toAdd = new LinkedList<FeatureCluster>();
        try {
            rd = new BufferedReader(new InputStreamReader(is));
            String line = null;
            FeatureCluster current = null;
            int bytesRead = 0;
            while ((line = rd.readLine()) != null) {
                bytesRead = bytesRead + line.getBytes().length;
                double mbRead = (double) bytesRead / (double) (1024 * 1024);
                if (handle != null) {
                    handle.progress("Parsing OBO file. " + decimals.format(mbRead) + "MB read...");
                }
                if ("[Term]".equals(line)) {
                    current = new FeatureCluster();
                } else if (line.startsWith("id:")) {
                    current = goMap.get(line.split("id: ")[1]);
                    if (current == null) {
                        current = new FeatureCluster();
                        current.setClusterName(line.split("id: ")[1]);
                        current.setValueOfCustomField(GeneOntology.PROP_CLUSTER_GOID, current.getClusterName());
                        goMap.put(current.getClusterName(), current);
                        toAdd.add(current);
                    }
                } else if (line.startsWith(("name:"))) {
                    current.setValueOfCustomField(GeneOntology.PROP_CLUSTER_GONAME, line.split("name: ")[1]);
                } else if (line.startsWith("alt_id:")) {
                    String[] ss = line.split(" ");
                    goMap.put(ss[1], current);
                } else if (line.startsWith("[Typedef]")) {
                    break;
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                rd.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return toAdd;
    }

    private static class GoTermQueryCreator implements QueryCreator {

        @SuppressWarnings("unchecked")
        static List<FeatureCluster> createQuery() {
            return DataManager.getDefault().createQuery("SELECT c from Cluster_GoTerm as c");
        }
    }

    private static class GoTermPersisterImpl implements Persister {

        private final List<FeatureCluster> toAdd;

        public GoTermPersisterImpl(List<FeatureCluster> toAdd) {
            this.toAdd = toAdd;
        }

        @Override
        public void persist(StatelessSession session) {
            for (FeatureCluster fc : toAdd) {
                fc.setClusterType(GeneOntology.CLUSTER_TYPE);
                fc.setClusterCategory(GeneOntology.CLUSTER_CATEGORY);
                session.insert(GeneOntology.ENTITY_NAME_CLUSTER, fc);
            }
        }
    }
}
