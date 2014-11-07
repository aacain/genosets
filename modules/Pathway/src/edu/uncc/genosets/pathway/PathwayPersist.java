/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.pathway;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.FactLocation;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.datamanager.persister.FactLocationPersister;
import edu.uncc.genosets.datamanager.persister.Persister;
import edu.uncc.genosets.taskmanager.SimpleTask;
import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author lucy
 */
public class PathwayPersist extends SimpleTask{

    HashMap<String, FeatureCluster> pathwayMap;
    private final File file;
    private final int pathwayColumn;
    private final int pathwayNameColumn;
    private final int locationColumn;
    private final AnnotationMethod method;
    
    public PathwayPersist(AnnotationMethod method, File file, int pathwayColumn, int pathwayNameColumn, int locationColumn) {
        super("Persisting " + method.getMethodName());
        this.method = method;
        this.file = file;
        this.pathwayColumn = pathwayColumn;
        this.pathwayNameColumn = pathwayNameColumn;
        this.locationColumn = locationColumn;
    }

    
    public List<Entry> readFile(File file, int pathwayColumn, int pathwayNameColumn, int locationColumn) throws IOException {
        ArrayList<Entry> entryList = new ArrayList<Entry>();
        //get existing pathways
        if(pathwayMap == null){
            getExistingPathways();
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("#") && !line.equals("")) {
                    String[] split = line.split("\t");
                    FeatureCluster fc = pathwayMap.get(split[pathwayColumn]);
                    if(fc == null){
                        fc = new FeatureCluster();
                        fc.setClusterName(split[pathwayColumn]);
                        Map<String, Object> props = fc.getCustomProperties();
                        props.put("pathwayName", split[pathwayNameColumn]);
                        pathwayMap.put(fc.getClusterName(), fc);
                    }
                    Entry entry = new Entry();
                    entryList.add(entry);
                    entry.pathway = fc;
                    entry.locationId = Integer.parseInt(split[locationColumn]);
                }
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return entryList;
    }
    
    private void persist(List<Entry> entryList, AnnotationMethod method){
        List<Persister> persisterList = new ArrayList<Persister>(entryList.size());
        for (Entry entry : entryList) {
            FactLocationPersister persister = FactLocationPersister.instantiate();
            persisterList.add(persister);
            //Lookup the location
            Location loc = Query.getLocation(entry.locationId);
            if(loc == null){
                System.out.println(loc);
            }
            FactLocation fact = new FactLocation();
            fact.setEntityName(NbBundle.getMessage(PathwayPersist.class, "Pathway.FactEntityName"));
            fact.setOrganismId(loc.getOrganismId());
            fact.setAssembledUnitId(loc.getAssembledUnitId());
            fact.setFeatureId(loc.getFeatureId());
            fact.setLocationId(loc.getLocationId());
            persister.setup(entry.pathway, method, fact, NbBundle.getMessage(PathwayPersist.class, "Pathway.ClusterEntityName"), AnnotationMethod.DEFAULT_NAME, fact.getEntityName());
        }
        DataManager.getDefault().persist(persisterList);
    }
    
    private HashMap<String, FeatureCluster> getExistingPathways(){
        pathwayMap = new HashMap<String, FeatureCluster>();
        for (FeatureCluster fc : Query.getPathwayClusters()) {
            pathwayMap.put(fc.getClusterName(), fc);
        }
        return pathwayMap;
    }

    @Override
    public void performTask() {
        try {
            List<Entry> parse = readFile(file, pathwayColumn, pathwayNameColumn, locationColumn);
            persist(parse, method);
        } catch (IOException ex) {
            TaskLogFactory.getDefault().log("Unable to persist file.", "edu.uncc.genosets.pathway.ReadPathwayFile", ex.getMessage(), TaskLog.ERROR, new Date());
        }
    }
    
    private static class Query implements QueryCreator{
        private static Location getLocation(Integer id){
            return (Location)DataManager.getDefault().get(Location.DEFAULT_NAME, id);
        }
        
        private static List<? extends FeatureCluster> getPathwayClusters(){
            return DataManager.getDefault().createQuery("SELECT fc FROM ClusterPathway as fc", FeatureCluster.class);
        }
    }
    
    private static class Entry{
        FeatureCluster pathway;
        int locationId;
    }
}
