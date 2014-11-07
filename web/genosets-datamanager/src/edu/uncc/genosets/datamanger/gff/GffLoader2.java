package edu.uncc.genosets.datamanger.gff;

import edu.uncc.genosets.datamanager.api.DataLoadException;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.AssembledUnitAquisition;
import edu.uncc.genosets.datamanager.entity.FactDetailLocation;
import edu.uncc.genosets.datamanager.entity.FactLocation;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.persister.AssembledUnitMapping;
import edu.uncc.genosets.datamanager.persister.FactAssembledUnitPersister;
import edu.uncc.genosets.datamanager.persister.FactLocationDetailPersister;
import edu.uncc.genosets.datamanager.persister.FactLocationPersister;
import edu.uncc.genosets.datamanager.persister.LocationMapping;
import edu.uncc.genosets.datamanager.persister.Persister;
import edu.uncc.genosets.taskmanager.AbstractTask;
import edu.uncc.genosets.taskmanager.TaskException;
import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import edu.uncc.genosets.util.gff.ReadGff;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.LogFactory;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.RNASequence;
import org.biojava3.core.sequence.transcription.Frame;
import org.biojava3.core.sequence.transcription.TranscriptionEngine;

/**
 *
 * @author aacain
 */
public class GffLoader2 {

    private List<ReadGff.FeatureExt> features;
    private HashMap<String, StringBuilder> seqMapFromFile;
    private final Organism organism;
    private final AnnotationMethod method;
    private final TranscriptionEngine engine;
    private final HashMap<Integer, List<Location>> locationEndMap = new HashMap<Integer, List<Location>>(); //key = end position, value=list of locations with that end position
    private HashMap<Integer, Location> locationIdMap = new HashMap<Integer, Location>();  //key = locationid, value = location
    private final Set<String> toTranslateType;
    private final LocationMapping locationMapping;
    private final AssembledUnitMapping assUnitMapping;

    public GffLoader2(Organism organism, AnnotationMethod method, Set<String> toTranslateType) {
        this(organism, method, toTranslateType, 11);
    }

    public GffLoader2(Organism organism, AnnotationMethod method, Set<String> toTranslateType, int translationTable) {
        this(organism, method, toTranslateType, translationTable, null, null);
    }

    public GffLoader2(Organism organism, AnnotationMethod method, Set<String> toTranslateType, int translationTable, AssembledUnitMapping assUnitMapping, LocationMapping locationMapping) {
        this.organism = organism;
        this.method = method;
        this.toTranslateType = toTranslateType;
        this.assUnitMapping = assUnitMapping;
        this.locationMapping = locationMapping;
        TranscriptionEngine.Builder b = new TranscriptionEngine.Builder();
        b.table(translationTable);
        b.translateNCodons(true);
        engine = b.build();
    }

    public void parse(InputStream is) {
        ReadGff readGff = new ReadGff(is);
        try {
            this.features = readGff.parse();
            this.seqMapFromFile = readGff.getSequenceMap();
        } catch (IOException ex) {
            LogFactory.getLog(GffLoader2.class).error("Could not load file: " + method.getMethodName(), ex);
            TaskLogFactory.getDefault().log("GFF3 parse error", method.getMethodName(), "Could not load file: " + method.getMethodName(), TaskLog.ERROR, new Date());
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
            }
        }
    }

    public void persist() throws DataLoadException {
        HashMap<Integer, List<Location>> endMap = new HashMap<Integer, List<Location>>();

        //create a lookup map for all locations by end position
//        DataManager mgr = DataManager.getDefault();
//        List<Location> locs = mgr.createQuery("select loc from Location as loc WHERE loc.organismId = " + organism.getOrganismId());
//        for (Location l : locs) {
//            locationIdMap.put(l.getLocationId(), l);
//            List<Location> endLocs = locationEndMap.get(l.getEndPosition());
//            if (endLocs == null) {
//                endLocs = new LinkedList<Location>();
//                locationEndMap.put(l.getEndPosition(), endLocs);
//            }
//            endLocs.add(l);
//        }
        HashMap<String, AssembledUnit> assUnitForMethod = new HashMap<String, AssembledUnit>();

        //create all necessary persist objects
        FeatureCluster cluster = new FeatureCluster();
        cluster.setClusterName(method.getMethodName());
        List<FactLocationPersister> featurePersisters = new LinkedList<FactLocationPersister>();
        List<FactLocationDetailPersister> detailPersisters = new LinkedList<FactLocationDetailPersister>();
        for (ReadGff.FeatureExt feature : features) {
            if (!feature.gffId.equals(feature.seqId)) { //make sure it is not the source object
                FactLocation fact = new FactLocation();
                FactLocationPersister p = new FactLocationPersister();
                featurePersisters.add(p);
                p.setup(cluster, method, fact, "FeatureCluster", AnnotationMethod.DEFAULT_NAME, "AnnoFact");
                fact.setFeature(feature);
                fact.setFeatureType(feature.getFeatureType());
                fact.setPrimaryName(feature.getPrimaryName());
                fact.setProduct(feature.getProduct());
                //lookup in location mapping
                Location location = feature.loc;
                if (this.locationMapping != null) {
                    location = this.locationMapping.lookup(feature.gffId, feature.loc, Boolean.TRUE);
                }
                fact.setLocationId(location.getLocationId());
                p.setLocation(location);
                fact.setFeatureId(location.getFeatureId());
                //set the feature
                if (feature.getFeatureId() == null) {
                    feature.setFeatureId(location.getFeatureId());
                    fact.setFeatureId(location.getFeatureId());
                    p.setFeature(feature);
                }

                //lookup assUnit from mapping file
                if (assUnitMapping != null) {
                    AssembledUnit assFromMappingFile = assUnitMapping.lookup(feature.seqId);
                    if (assFromMappingFile != null) {
                        fact.setAssembledUnitId(assFromMappingFile.getAssembledUnitId());
                        p.setAssUnit(assFromMappingFile);
                        assUnitForMethod.put(feature.seqId, assFromMappingFile);
                    }
                }
                //create a new assunit if necessary
                if (fact.getAssembledUnitId() == null) {
                    //assunit not mapped, see if created new
                    AssembledUnit newlyCreatedAssUnit = assUnitForMethod.get(feature.seqId);
                    if (newlyCreatedAssUnit == null) {
                        newlyCreatedAssUnit = new AssembledUnit();
                        newlyCreatedAssUnit.setAssembledUnitName(feature.seqId);
                        assUnitForMethod.put(feature.seqId, newlyCreatedAssUnit);
                    }
                    p.setAssUnit(newlyCreatedAssUnit);
                    fact.setAssembledUnit(newlyCreatedAssUnit);
                }

                //set the organism
                fact.setOrganismId(organism.getOrganismId());
                p.setOrganism(organism);

                //lookup the protein sequence and remove it (this allows us to have only the nucleotide sequences in the final sequence map)
                StringBuilder seq = seqMapFromFile.remove(feature.gffId);
                if (seq == null && toTranslateType != null) {//then translate
                    if (feature.getFeatureType() != null && toTranslateType.contains(feature.getFeatureType())) {
                        StringBuilder nucSeq = seqMapFromFile.get(feature.seqId);
                        if (nucSeq == null) {
                            throw new DataLoadException("Could not find the nucleotide sequence for id: " + feature.seqId);
                        } else if (feature.loc.getMinPosition() - 1 < 0 || feature.loc.getMaxPosition() - 1 > nucSeq.length()) {
                            throw new DataLoadException("Invalid range for feature " + feature.gffId + " on sequence " + feature.seqId);
                        } else {
                            try {
                                String subNuc = nucSeq.substring(feature.loc.getMinPosition() - 1, feature.loc.getMaxPosition());
                                subNuc = subNuc.replaceAll("[^AGCTagct]", "N");
                                DNASequence dna = new DNASequence(subNuc);
                                RNASequence rna = dna.getRNASequence(engine, feature.loc.getIsForward() ? Frame.ONE : Frame.REVERSED_ONE);
                                ProteinSequence prot = rna.getProteinSequence(engine);
                                seq = new StringBuilder(prot.getSequenceAsString());
                            } catch (Exception ex) {
                                LogFactory.getLog(GffLoader2.class).error("Could not translate sequence.", ex);
                                TaskLogFactory.getDefault().log("Error persisting GFF3 file", method.getMethodName(), "Error translating sequence for " + feature.getPrimaryName(), TaskLog.ERROR, new Date());
                            }
                        }
                    }
                }

                //set the sequence if it is a new feature
                if (fact.getLocationId() == null) {
                    p.setSequence(seq == null ? null : seq.toString());
                }
                //add the details
                for (FactDetailLocation deat : feature.details) {
                    detailPersisters.add(FactLocationDetailPersister.instantiate(fact, deat, FactDetailLocation.DEFAULT_NAME));
                }
            }
        }//end feature list iteration

        //get through the list of sequences and add persisters
        //persist assunits        
        List<FactAssembledUnitPersister> assPersister = new LinkedList<FactAssembledUnitPersister>();
        for (Map.Entry<String, StringBuilder> entry : seqMapFromFile.entrySet()) {
            AssembledUnit assUnit = assUnitForMethod.get(entry.getKey());
            if (assUnit == null) {//no features on this assUnit
                //lookup in mapping
                if (assUnitMapping != null) {
                    assUnit = assUnitMapping.lookup(entry.getKey());
                }
                if (assUnit == null) {
                    assUnit = new AssembledUnit();
                    assUnit.setAssembledUnitName(entry.getKey());
                    assUnitForMethod.put(entry.getKey(), assUnit);
                }
            }
            FactAssembledUnitPersister assP = new FactAssembledUnitPersister();
            assPersister.add(assP);
            AssembledUnitAquisition fact = new AssembledUnitAquisition();
            assP.setup(null, method, fact, null, AnnotationMethod.DEFAULT_NAME, AssembledUnitAquisition.DEFAULT_NAME);
            assP.setOrganism(organism);
            assP.setAssUnit(assUnit);
            fact.setAssembledUnitId(assUnit.getAssembledUnitId());
            fact.setOrganismId(organism.getOrganismId());
            fact.setAssembledUnitName(entry.getKey());
            //set the sequence if assUnit isn't in db already
            if (fact.getAssembledUnitId() == null) {
                StringBuilder seq = seqMapFromFile.get(assUnit.getAssembledUnitName());
                assP.setSequence(seq == null ? null : seq.toString());
                assUnit.setAssembledUnitLength(seq == null ? null : seq.length());
                assUnit.setSequenceLength(assUnit.getAssembledUnitLength());
            }
        }

        try {
            List<Persister> persister = new ArrayList<Persister>(assPersister);
            persister.addAll(featurePersisters);
            persister.addAll(detailPersisters);
            DataManager.getDefault().persist(persister);
            DataManager.getDefault().persist(assPersister);
            TaskLogFactory.getDefault().log("Persisted " + method.getMethodName() + " to database " + DataManager.getDefault().getDatabaseName(), method.getMethodName(), "Persisted " + method.getMethodName() + " to database " + DataManager.getDefault().getDatabaseName(), TaskLog.INFO, new Date());
        } catch (Exception ex) {
            TaskLogFactory.getDefault().log("Could not persist " + method.getMethodName(), method.getMethodName(), ex.getMessage(), TaskLog.ERROR, new Date());
            throw new DataLoadException("Error persisting " + method.getMethodName(), ex);
        }
    }

    private static class EndLookup {

        HashMap<String, HashMap<Integer, List<Location>>> endMap = new HashMap<String, HashMap<Integer, List<Location>>>();
    }

    private static class ExistingAssembledUnits implements QueryCreator {

        private static HashMap<String, AssembledUnit> getAssembledUnits(Organism organism) {
            //get all the current assembled units
            DataManager mgr = DataManager.getDefault();
            List<? extends AssembledUnit> asses = mgr.createQuery("select ass from AssembledUnit as ass WHERE ass.organismId = " + organism.getOrganismId(), AssembledUnit.class);
            //create a lookup map for assembled units
            HashMap<String, AssembledUnit> allOrgsAsses = new HashMap<String, AssembledUnit>();
            for (AssembledUnit ass : asses) {
                allOrgsAsses.put(ass.getAssembledUnitName(), ass);
            }
            return allOrgsAsses;
        }
    }

    public static class GffTask extends AbstractTask {

        GffLoader2 loader;
        InputStream is;
        Organism org;

        public GffTask(Organism organism, AnnotationMethod method, Set<String> toTranslateType, InputStream is, String name) {
            super(name);
            loader = new GffLoader2(organism, method, toTranslateType, 11);
            this.is = is;
            this.org = organism;
        }

        public GffTask(Organism organism, AnnotationMethod method, Set<String> toTranslateType, int translationTable, InputStream is, String name) {
            super(name);
            loader = new GffLoader2(organism, method, toTranslateType, translationTable);
            this.is = is;
            this.org = organism;
        }

        public GffTask(Organism organism, AnnotationMethod method, Set<String> toTranslateType, int translationTable, InputStream is, AssembledUnitMapping assMapping, LocationMapping locMapping, String name) {
            super(name);
            loader = new GffLoader2(organism, method, toTranslateType, translationTable, assMapping, locMapping);
            this.is = is;
            this.org = organism;
        }

        @Override
        public void performTask() throws TaskException {
            try {
                loader.parse(is);
                loader.persist();
            } catch (DataLoadException ex) {
                throw new TaskException(ex);
            }
        }

        @Override
        public void uninitialize() {
        }

        @Override
        public void logErrors() {
        }

        @Override
        public Organism getOrganismDependency() {
            return this.org;
        }

        @Override
        public void setOrganismDependency(Organism org) {
        }
    }
}
