/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.mugsyannotator;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.FactDetailLocation;
import edu.uncc.genosets.datamanager.entity.FactLocation;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.datamanager.persister.FactLocationPersister;
import edu.uncc.genosets.mugsyannotator.MugsyParser.Cluster;
import edu.uncc.genosets.taskmanager.SimpleTask;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author aacain
 */
public class LoadMugsy extends SimpleTask {

    private FileObject directory;
    private AnnotationMethod method;
    public static final String METHOD_ENTITY_NAME = AnnotationMethod.DEFAULT_NAME;
    public static final String CLUSTER_ENTITY_NAME = FeatureCluster.DEFAULT_NAME;
    public static final String FACT_LOCATION_ENTITY_NAME = "OrthoFact";
    public static final String CLUSTER_CATEGORY = "Ortholog";
    public static final String CLUSTER_TYPE = "OrthoMCL";
    public static final String METHOD_SOURCE_TYPE = "MugsyAnnotator";

    public LoadMugsy(FileObject directory, AnnotationMethod method) {
        super("Load Mugsy " + directory.getName());
        this.directory = directory;
        this.method = method;
    }

    private void load() {
        //get lookup map
        DataManager mgr = DataManager.getDefault();
        List<Object[]> createQuery = mgr.createQuery("SELECT f, l FROM Feature as f, Location as l WHERE f.featureId = l.featureId");
        HashMap<String, Location> featureMap = new HashMap<String, Location>();
        //create a lookup map so we can lookup by primary name
        for (Object[] objects : createQuery) {
            Feature f = (Feature) objects[0];
            Location l = (Location) objects[1];
            featureMap.put(f.getPrimaryName(), l);
        }

        //create cluster persisters
        List<FactLocationPersister> persisters = new LinkedList<FactLocationPersister>();
        MugsyParser parser = new MugsyParser(FileUtil.toFile(directory));
        List<Cluster> clusters = parser.run();
        HashMap<String, Boolean> alreadyLookup = new HashMap<String, Boolean>();
        for (Cluster cluster : clusters) {
            FeatureCluster fc = new FeatureCluster();
            fc.setClusterName(cluster.id);
            fc.setClusterCategory(CLUSTER_CATEGORY);
            fc.setClusterType(CLUSTER_TYPE);
            //lookup the location by locus tag
            for (List<MugsyParser.Gene> list : cluster.geneByOrgList.values()) {
                for (MugsyParser.Gene gene : list) {
                    if (!gene.createdOrf) {
                        for (String locus : gene.locusTags) {
                            Location loc = featureMap.get(locus);
                            if (loc == null) {
                                //if (alreadyLookup.get(gene.contig) == null) {
                                //    alreadyLookup.put(gene.contig, Boolean.TRUE);
                                    System.out.println("Lookup: " + locus);
                                    //lookup by locus and add to lookup
                                    List<FactDetailLocation> deats = mgr.createQuery("SELECT d from AnnoFactDetail as d WHERE d.detailValue = '" + locus + "'");
                                    FactDetailLocation deat = null;
                                    for (FactDetailLocation d : deats) {
                                        deat = d;
                                    }
                                    if (deat != null) {
                                        System.out.println("Found " + locus);
                                        List<Object[]> locs = mgr.createQuery("SELECT l, d FROM Location as l, AnnoFactDetail as d, AnnoFact as f WHERE l.locationId = f.locationId AND d.parentFactId = f.factId AND l.organismId =  " + deat.getOrganismId() + " AND d.detailType = '" + deat.getDetailType() + "'");
                                        for (Object[] objects : locs) {
                                            featureMap.put(((FactDetailLocation) objects[1]).getDetailValue(), (Location) objects[0]);
                                        }
                                        loc = featureMap.get(locus);
                                        if (loc != null) {
                                            persisters.add(setupPersister(fc, loc));
                                        }
                                    }
                                //}
                            } else {
                                persisters.add(setupPersister(fc, loc));
                            }
                        }
                    }
                }
            }
        }
        mgr.persist(persisters);
    }

    private FactLocationPersister setupPersister(FeatureCluster cluster, Location l) {
        FactLocationPersister p = FactLocationPersister.instantiate();
        FactLocation fact = new FactLocation();
        p.setup(cluster, method, fact,
                CLUSTER_ENTITY_NAME,
                METHOD_ENTITY_NAME,
                FACT_LOCATION_ENTITY_NAME);
        //set what we dont know
        p.setCluster(cluster);
        p.setMethod(method);
        //set what we know
        fact.setLocationId(l.getLocationId());
        fact.setFeatureId(l.getFeatureId());
        fact.setAssembledUnitId(l.getAssembledUnitId());
        fact.setOrganismId(l.getOrganismId());
        return p;
    }

    @Override
    public void performTask() {
        load();
    }
}
