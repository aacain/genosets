/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.orthomcl;

import edu.uncc.genosets.bioio.Fasta;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.*;
import edu.uncc.genosets.datamanager.persister.FactLocationPersister;
import edu.uncc.genosets.datamanager.persister.Persister;
import edu.uncc.genosets.taskmanager.AbstractTask;
import edu.uncc.genosets.taskmanager.TaskManager;
import edu.uncc.genosets.taskmanager.TaskManagerFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * Responsible for loading orthomcl files into the database.
 *
 * @author aacain
 */
public class LoadOrthoMcl {

    public static void loadOrthoMcl(AnnotationMethod method, File fastaFolder, File groupsFile) {
        TaskManager mgr = TaskManagerFactory.getDefault();
        mgr.addPendingTask(new OrthoMclTask(method, fastaFolder, groupsFile));
    }

    public static class OrthoMclTask extends AbstractTask {

        private final AnnotationMethod method;
        private final File fastaFolder;
        private final File groupsFile;

        public OrthoMclTask(AnnotationMethod method, File fastaFolder, File groupsFile) {
            super("Loading OrthoMCL files");
            this.method = method;
            this.fastaFolder = fastaFolder;
            this.groupsFile = groupsFile;
        }

        @Override
        public void performTask() {
            try {
                //open the files
                HashSet<String> idSet = new HashSet<String>();
                FileObject fasta = FileUtil.createFolder(fastaFolder);
                FileObject groups = FileUtil.createData(groupsFile);
                //get all the indexes from the fasta files
                if (fasta != null) {
                    for (FileObject fo : fasta.getChildren()) {
                        if (fo.getExt().equals("faa") || fo.getExt().equals("fasta")) {
                            Fasta fastaFile = Fasta.parse(fo.getInputStream());
                            for (Fasta.FastaItem fastaItem : fastaFile.getItems()) {
                                idSet.add(fastaItem.getId());
                            }
                        }
                    }
                }
                //parse the groups file
                List<OrthoItem> parseGroups = parseGroups(groups.getInputStream());
                //remove items from idSet so that remaining set is singleton clusters
                for (OrthoItem o : parseGroups) {
                    for (String id : o.getLocationList()) {
                        idSet.remove(id);
                    }
                }
                //add the singleton clusters to the list
                int i = 0;
                for (String string : idSet) {
                    OrthoItem item = new OrthoItem();
                    item.setClusterId("Singleton" + i);
                    i++;
                    item.getLocationList().add(string);
                    parseGroups.add(item);
                }
                //now persist
                List<Persister> persisterList = new ArrayList(parseGroups.size());
                for (OrthoItem ortho : parseGroups) {
                    FeatureCluster cluster = new FeatureCluster();
                    cluster.setClusterName(ortho.getClusterId());
                    cluster.setClusterType("OrthoMCL");
                    cluster.setClusterCategory("Ortholog");
                    for (String string : ortho.getLocationList()) {
                        //get the location id
                        String[] ss = string.split("\\|");
                        int locId = Integer.parseInt(ss[1]);
                        //look up the location id so that we can get the other values
                        try {
                            Location loc = (Location) DataManager.getDefault().get(Location.DEFAULT_NAME, locId);
                            FactLocationPersister factPersister = FactLocationPersister.instantiate();
                            FactLocation fact = new FactLocation();
                            fact.setLocationId(loc.getLocationId());
                            fact.setFeatureId(loc.getFeatureId());
                            fact.setAssembledUnitId(loc.getAssembledUnitId());
                            fact.setOrganismId(loc.getOrganismId());
                            factPersister.setup(cluster, method, fact, FeatureCluster.DEFAULT_NAME, AnnotationMethod.DEFAULT_NAME, "OrthoFact");
                            persisterList.add(factPersister);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
                DataManager.getDefault().persist(persisterList);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        private List<OrthoItem> parseGroups(InputStream is) throws IOException {
            BufferedReader br = null;
            br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            List<OrthoItem> items = new LinkedList<OrthoItem>();
            while ((line = br.readLine()) != null) {
                String[] ss = line.split(" ");
                if (ss.length > 0) {
                    OrthoItem item = new OrthoItem();
                    List<String> locationList = item.getLocationList();
                    //remove the : from the cluster id
                    item.setClusterId(ss[0].substring(0, ss[0].length() - 1));
                    for (int i = 1; i < ss.length; i++) {
                        locationList.add(ss[i]);
                    }
                    items.add(item);
                }
            }
            return items;
        }

        @Override
        public void uninitialize() {
        }

        @Override
        public void logErrors() {
        }

        @Override
        public Organism getOrganismDependency() {
            return null;
        }

        @Override
        public void setOrganismDependency(Organism org) {
        }
    }

    public static class OrthoItem {

        private String clusterId;
        private List<String> locationList = new LinkedList<String>();

        public String getClusterId() {
            return clusterId;
        }

        public void setClusterId(String clusterId) {
            this.clusterId = clusterId;
        }

        public List<String> getLocationList() {
            return locationList;
        }

        public void setLocationList(List<String> locationList) {
            this.locationList = locationList;
        }
    }

    public static class MyQueryBuilder implements QueryCreator {
    }
}
