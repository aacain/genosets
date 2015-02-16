/*
 * Copyright (C) 2014 Aurora Cain
 *
 * This program rastIs free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program rastIs distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.uncc.genosets.datamanager.rast;

import edu.uncc.genosets.connections.Connection;
import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.datamanager.api.DataLoadException;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.DatabaseMigrationException;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.AssembledUnitAquisition;
import edu.uncc.genosets.datamanager.entity.FactLocation;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.datamanager.entity.MolecularSequence;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.fasta.Fasta;
import edu.uncc.genosets.datamanager.fasta.Fasta.FastaItem;
import edu.uncc.genosets.datamanager.hibernate.HibernateUtil;
import edu.uncc.genosets.datamanager.persister.FactAssembledUnitPersister;
import edu.uncc.genosets.datamanager.persister.FactLocationPersister;
import edu.uncc.genosets.datamanager.persister.Persister;
import edu.uncc.genosets.taskmanager.AbstractTask;
import edu.uncc.genosets.taskmanager.TaskException;
import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.RNASequence;
import org.biojava3.core.sequence.transcription.Frame;
import org.biojava3.core.sequence.transcription.TranscriptionEngine;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aacain
 */
public class RastLoader extends AbstractTask {

    private final Organism organism;
    private final AnnotationMethod rastMethod;
    private final InputStream rastIs;
    private final AnnotationMethod fastaMethod;
    private final InputStream fastaIs;
    private final TranscriptionEngine translationEngine;
    private final Comparator locComparator;
    private final Comparator<FactLocation> exactComparator;
    private final Comparator<FactLocation> partialComparator;
    private boolean lookupByAssName;
    private HashMap<String, AssembledUnit> seqMap = new HashMap<String, AssembledUnit>();

    public RastLoader(Organism organism, AnnotationMethod rastMethod, InputStream rastIs, AnnotationMethod fastaMethod, InputStream fastaIs, int translationTable, boolean lookupByAssName) {
        super("Loading RAST for organism: " + organism.getStrain());
        this.organism = organism;
        this.rastMethod = rastMethod;
        this.rastIs = rastIs;
        this.fastaMethod = fastaMethod;
        this.fastaIs = fastaIs;
        TranscriptionEngine.Builder b = new TranscriptionEngine.Builder();
        b.table(translationTable);
        b.translateNCodons(true);
        this.translationEngine = b.build();
        this.locComparator = SeqLookup.getLocationComparator();
        this.exactComparator = new Comparator<FactLocation>() {
            @Override
            public int compare(FactLocation o1, FactLocation o2) {
                Integer val = o1.getLocation().getEndPosition().compareTo(o2.getLocation().getEndPosition());
                if (val == 0) {
                    return o1.getLocation().getStartPosition().compareTo(o2.getLocation().getStartPosition());
                }
                return val;
            }
        };
        this.partialComparator = new Comparator<FactLocation>() {
            @Override
            public int compare(FactLocation o1, FactLocation o2) {
                return o1.getLocation().getEndPosition().compareTo(o2.getLocation().getEndPosition());
            }
        };

        this.lookupByAssName = lookupByAssName;
    }

    private List<FactAssembledUnitPersister> createFastaPersisters(HashMap<String, AssembledUnit> fasta) {
        FeatureCluster cluster = new FeatureCluster();
        List<FactAssembledUnitPersister> persisters = new ArrayList<FactAssembledUnitPersister>(fasta.size());
        for (Map.Entry<String, AssembledUnit> entry : fasta.entrySet()) {
            AssembledUnitAquisition fact = new AssembledUnitAquisition(organism, null, fastaMethod, entry.getValue());
            fact.setAssembledUnitName(entry.getValue().getAssembledUnitName());
            FactAssembledUnitPersister p = FactAssembledUnitPersister.instantiate();
            p.setup(cluster, fastaMethod, fact, FeatureCluster.DEFAULT_NAME, AnnotationMethod.DEFAULT_NAME, "AssembledUnitAquisition");
            p.setEntities(organism, entry.getValue(), entry.getValue().getMolecularSequence().getForwardSequence());
            persisters.add(p);
        }

        return persisters;
    }

    public void run() throws DataLoadException, IOException {
        LinkedList<Persister> persisters = new LinkedList<Persister>();
        List<FactAssembledUnitPersister> fastaPersisters = null;
        if (this.fastaIs != null) {
            this.seqMap = parseFasta();
            fastaPersisters = createFastaPersisters(this.seqMap);
            persisters.addAll(fastaPersisters);
        }

        List<FactLocation> rastFactList = createRastFactList();
        List<FactLocationPersister> rastPersisters = createRastPersister(rastFactList);
        persisters.addAll(rastPersisters);
        if (fastaPersisters != null) {
            for (FactAssembledUnitPersister f : fastaPersisters) {
                f.getFact().getAssembledUnit().setMolecularSequence(null);
            }
        }
        
        DataManager.getDefault().persist(persisters);

    }

    /**
     *
     * @return @throws DataLoadException
     */
    private List<FactLocation> createRastFactList() throws DataLoadException {
        List<FactLocation> facts = new LinkedList<FactLocation>();

        FeatureCluster featureCluster = new FeatureCluster();
        featureCluster.setClusterName(rastMethod.getMethodName());
        try {
            BufferedReader br = null;
            br = new BufferedReader(new InputStreamReader(rastIs));
            String line = null;
            List<FactLocation> existingList = new LinkedList<FactLocation>();
            while ((line = br.readLine()) != null) {
                String ss[] = line.split("\t");
                if (ss.length == 3) { //test the number of columns
                    //split the first column by periods to get the type            
                    String[] names = ss[0].split("\\.");
                    String type = names[names.length - 2];
                    String id = names[names.length - 1];
                    String function = ss[2];
                    if (type.equals("peg")) {
                        type = "CDS";
                    } else {
                        type = "RNA";
                    }
                    String primaryName = ss[0];


                    //try to split the 2nd column on semicolon
                    String[] semicolonSplit = ss[1].split(";");
                    String longContigName = null;
                    Location loc = new Location();
                    if (semicolonSplit.length > 1) { //mac/linux format
                        String[] contigNameArray = semicolonSplit[0].split(":");
                        longContigName = contigNameArray[0];
                        if (contigNameArray.length > 1) {
                            TaskLogFactory.getDefault().log("Contig name is unusual (" + semicolonSplit[0] + ").", RastLoader.class.toString(), "Contig name is unusual (" + semicolonSplit[0] + "). Used " + contigNameArray[0], TaskLog.WARNING, new Date());
                        }
                        String locationsString = semicolonSplit[semicolonSplit.length - 1];
                        String[] locations = locationsString.split("_");
                        if (locations.length == 3) {
                            loc.setEndPosition(Integer.parseInt(locations[2]));
                            loc.setStartPosition(Integer.parseInt(locations[1]));
                        } else {
                            throw new DataLoadException("Could not parse start and end position for line: " + line);
                        }
                    } else {  //windows format
                        //split the second column by underscore
                        String[] locations = ss[1].split("_");
                        if (locations.length >= 3) {
                            StringBuilder contigName = new StringBuilder();
                            loc.setEndPosition(Integer.parseInt(locations[locations.length - 1]));
                            loc.setStartPosition(Integer.parseInt(locations[locations.length - 2]));

                            for (int i = locations.length - 3; i > 0 && locations.length > 3; i--) {
                                contigName.insert(0, locations[i]);
                                contigName.insert(0, "_");
                            }
                            contigName.insert(0, locations[0]);
                            longContigName = contigName.toString();
                        }



                        //                        List<Location> locList = map.get(contigName.toString());
                        //                        if (locList == null) {
                        //                            locList = new LinkedList<Location>();
                        //                            map.put(contigName.toString(), locList);
                        //                        }
                        //                        locList.add(loc);

                    }//end windows format
                    if (loc.getStartPosition() > loc.getEndPosition()) {
                        loc.setIsForward(Boolean.FALSE);
                        loc.setMinPosition(loc.getEndPosition());
                        loc.setMaxPosition(loc.getStartPosition());
                    } else {
                        loc.setIsForward(Boolean.TRUE);
                        loc.setMinPosition(loc.getStartPosition());
                        loc.setMaxPosition(loc.getEndPosition());
                    }

                    loc.setFeatureType(type);
                    loc.setProduct(function);
                    loc.setPrimaryName(primaryName);

                    AssembledUnit assUnit = this.seqMap.get(longContigName);
                    if (lookupByAssName) {
                        if (assUnit == null) {
                            assUnit = SeqLookup.lookupByAssName(longContigName);
                            if (assUnit == null) {
                                throw new DataLoadException("Could not find sequence for " + longContigName + " in database.");
                            }
                        }
                    } else if (assUnit == null) {
                        assUnit = new AssembledUnit();
                    }
                    this.seqMap.put(longContigName, assUnit);

                    Feature feature = new Feature();
                    feature.setFeatureType(type);
                    feature.setProduct(function);
                    feature.setPrimaryName(primaryName);
                    FactLocation fact = new FactLocation();
                    fact.setAssembledUnit(assUnit);
                    fact.setOrganism(organism);
                    fact.setFeature(feature);
                    fact.setLocation(loc);
                    fact.setAnnotationMethod(rastMethod);
                    fact.setFeatureCluster(featureCluster);
                    fact.setProduct(function);
                    fact.setFeatureType(type);
                    fact.setPrimaryName(primaryName);


                    updateMatches(existingList, fact);
                    facts.add(fact);
                } else { //wrong number of column in line
                    throw new DataLoadException("wrong number of columns");
                }
            }
        } catch (IOException ex) {
            throw new DataLoadException("IO Error.");
        }

        return facts;
    }

    private List<FactLocationPersister> createRastPersister(List<FactLocation> rastFactList) throws DataLoadException {
        List<FactLocationPersister> persisters = new LinkedList<FactLocationPersister>();
        for (FactLocation fact : rastFactList) {
            if (fact.getAssembledUnit().getMolecularSequence() == null) {
                throw new DataLoadException("Could not find sequence for " + fact.getAssembledUnit().getAssembledUnitName());
            }
            MolecularSequence mole = fact.getAssembledUnit().getMolecularSequence();
            FactLocationPersister p = FactLocationPersister.instantiate();
            p.setup(fact.getFeatureCluster(), rastMethod, fact, FeatureCluster.DEFAULT_NAME, AnnotationMethod.DEFAULT_NAME, "AnnoFact");
            p.setEntities(fact.getOrganism(), fact.getAssembledUnit(), fact.getFeature(), fact.getLocation());
            if (fact.getAssembledUnit() != null && mole.getMolecularSequenceId() != null) {
                fact.getAssembledUnit().setAssembledUnitId(mole.getMolecularSequenceId());
            }
            //create sequence
            try {
                if (fact.getLocation().getFeatureType().equals("CDS")) {
                    String subNuc = mole.getForwardSequence().substring(fact.getLocation().getMinPosition() - 1, fact.getLocation().getMaxPosition());
                    subNuc = subNuc.replaceAll("[^AGCTagct]", "N");
                    DNASequence dna = new DNASequence(subNuc);
                    RNASequence rna = dna.getRNASequence(translationEngine, fact.getLocation().getIsForward() ? Frame.ONE : Frame.REVERSED_ONE);
                    ProteinSequence prot = rna.getProteinSequence(translationEngine);
                    p.setSequence(prot.getSequenceAsString());
                }
            } catch (Exception ex) {
                throw new DataLoadException("Could not create protein sequence", ex);
            }
            persisters.add(p);
        }
        return persisters;
    }

//    private void updateMatches(HashMap<String, List<FactLocation>> factMap, String assUnitId, FactLocation fact) {
//        List<FactLocation> factList = factMap.get(assUnitId);
//        if (factList == null) {
//            if(this.lookupByAssName){
//                //lookup existing facts
//                
//            }else{
//               factList = new LinkedList<FactLocation>(); 
//            }
//            factMap.put(assUnitId, factList);
//        }
//
//        //search by exact location
//        int exactMatchIndex = Collections.binarySearch(factList, fact, this.exactComparator);
//        if (exactMatchIndex >= 0) { //get the exact match
//            FactLocation matchingFact = factList.get(exactMatchIndex);
//            //update the feature
//            fact.setFeature(matchingFact.getFeature());
//            //update the location if identical
//            fact.setLocation(matchingFact.getLocation());
//        } else { //search by partial match
//            int partialMatchIndex = Collections.binarySearch(factList, fact, this.partialComparator);
//            if (partialMatchIndex >= 0) { //partial match found
//                FactLocation partialMatch = factList.get(partialMatchIndex);
//                //update the feature
//                fact.setFeature(partialMatch.getFeature());
//            }
//        }
//        if (exactMatchIndex < 0) {
//            factList.add(-exactMatchIndex - 1, fact);
//        } else {
//            factList.add(exactMatchIndex, fact);
//        }
//    }
    private void updateMatches(List<FactLocation> factList, FactLocation fact) {
        if (factList == null) {
            if(this.lookupByAssName){
                
            }else{
                factList = new LinkedList<FactLocation>();
            }
        }
        

        //search by exact location
        int exactMatchIndex = Collections.binarySearch(factList, fact, this.exactComparator);
        if (exactMatchIndex >= 0) { //get the exact match
            FactLocation matchingFact = factList.get(exactMatchIndex);
            //update the feature
            fact.setFeature(matchingFact.getFeature());
            //update the location if identical
            fact.setLocation(matchingFact.getLocation());
        } else { //search by partial match
            int partialMatchIndex = Collections.binarySearch(factList, fact, this.partialComparator);
            if (partialMatchIndex >= 0) { //partial match found
                FactLocation partialMatch = factList.get(partialMatchIndex);
                //update the feature
                fact.setFeature(partialMatch.getFeature());
            }
        }
        if (exactMatchIndex < 0) {
            factList.add(-exactMatchIndex - 1, fact);
        } else {
            factList.add(exactMatchIndex, fact);
        }
    }

    /**
     *
     * @return @throws IOException
     */
    private HashMap<String, AssembledUnit> parseFasta() throws IOException {
        HashMap<String, AssembledUnit> fastaMap = new HashMap<String, AssembledUnit>();
        Fasta parse;
        try {
            parse = Fasta.parse(this.fastaIs);
            List<? extends FastaItem> items = parse.getItems();
            for (FastaItem f : items) {
                MolecularSequence mole = new MolecularSequence();
                mole.setForwardSequence(f.getSequence());
                AssembledUnit assUnit = new AssembledUnit();
                assUnit.setAssembledUnitName(f.getId());
                assUnit.setMolecularSequence(mole);
                fastaMap.put(f.getId(), assUnit);
            }
        } finally {
            try {
                this.fastaIs.close();
            } catch (IOException ex) {
            }
        }

        return fastaMap;
    }

    @Override
    public void performTask() throws TaskException {
        try {
            run();


        } catch (DataLoadException ex) {
            Logger.getLogger(RastLoader.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RastLoader.class
                    .getName()).log(Level.SEVERE, null, ex);
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
        return this.organism;
    }

    @Override
    public void setOrganismDependency(Organism org) {
    }

    private static class SeqLookup implements QueryCreator {

        private static AssembledUnit lookupAssUnit(String seqId) {
            try {
                int id = Integer.parseInt(seqId);
                return (AssembledUnit) DataManager.getDefault().get(AssembledUnit.DEFAULT_NAME, id);
            } catch (NumberFormatException ex) {
                return null;
            }
        }

        private static AssembledUnit lookupByAssName(String assUnitName) {
            StringBuilder query = new StringBuilder("SELECT a, m FROM AssembledUnit as a, MolecularSequence as m WHERE a.assembledUnitId = m.molecularSequenceId AND a.assembledUnitName = '");
            query.append(assUnitName).append("'");
            List<Object[]> createQuery = DataManager.getDefault().createQuery(query.toString());
            for (Object[] objects : createQuery) {
                AssembledUnit assUnit = (AssembledUnit) objects[0];
                assUnit.setMolecularSequence((MolecularSequence) objects[1]);
                return assUnit;
            }
            return null;
        }

        private static List<? extends Location> getLocList(AssembledUnit assUnit) {
            if (assUnit.getAssembledUnitId() == null) {
                return null;
            }
            StringBuilder bldr = new StringBuilder("SELECT l from Location as l WHERE l.assembledUnitId = ");
            bldr.append(assUnit.getAssembledUnitId());
            bldr.append("order by l.endPosition, l.startPosition, l.isForward");
            return DataManager.getDefault().createQuery(bldr.toString(), Location.class);
        }

        private static Comparator getLocationComparator() {
            return new Comparator<Location>() {
                @Override
                public int compare(Location o1, Location o2) {
                    int val = o1.getEndPosition().compareTo(o1.getEndPosition());
                    if (val != 0) {
                        return val;
                    }
                    val = o1.getStartPosition().compareTo(o2.getStartPosition());
                    if (val != 0) {
                        return val;
                    }
                    val = o1.getIsForward().compareTo(o2.getIsForward());
                    if (val != 0) {
                        return val;
                    }
                    return 0;
                }
            };
        }
    }

    public static void main(String[] args) throws FileNotFoundException, DataLoadException, IOException, InvalidConnectionException, DatabaseMigrationException {
        try {
            DataManager.openConnection(new Connection("myConnection", "myConnection", "localhost:3306/rast", "uncc", "uncc", false, Connection.TYPE_DIRECT_DB, false));
        } catch (InvalidConnectionException ex) {
            HibernateUtil.createDb("localhost", "3306", "rast", "uncc", "uncc");
            DataManager.openConnection(new Connection("myConnection", "myConnection", "localhost:3306/rast", "uncc", "uncc", false, Connection.TYPE_DIRECT_DB, false));
        }

        //testNoLookup();
        //testWithLookup();
        runAll();
    }
    
    private static void runAll() throws IOException{
        File rast = null;
        File fasta = null;
        
        String orgName = "6666667.6722";
        rast = new File("C:\\Users\\lucy\\Dropbox\\29_genomes_new\\JY1305-assemblies\\RASTgenomes\\" + orgName + "\\output.txt");
        fasta = new File(rast.getParentFile(), "contigs");
        runNoLookup(rast, fasta, orgName);
        
        orgName = "6666667.6726";
        rast = new File("C:\\Users\\lucy\\Dropbox\\29_genomes_new\\JY1305-assemblies\\RASTgenomes\\" + orgName + "\\output.txt");
        fasta = new File(rast.getParentFile(), "contigs");
        runNoLookup(rast, fasta, orgName);
        
        orgName = "6666667.6727";
        rast = new File("C:\\Users\\lucy\\Dropbox\\29_genomes_new\\JY1305-assemblies\\RASTgenomes\\" + orgName + "\\output.txt");
        fasta = new File(rast.getParentFile(), "contigs");
        runNoLookup(rast, fasta, orgName);
        
        orgName = "6666667.6730";
        rast = new File("C:\\Users\\lucy\\Dropbox\\29_genomes_new\\JY1305-assemblies\\RASTgenomes\\" + orgName + "\\output.txt");
        fasta = new File(rast.getParentFile(), "contigs");
        runNoLookup(rast, fasta, orgName);
        
        orgName = "6666667.6731";
        rast = new File("C:\\Users\\lucy\\Dropbox\\29_genomes_new\\JY1305-assemblies\\RASTgenomes\\" + orgName + "\\output.txt");
        fasta = new File(rast.getParentFile(), "contigs");
        runNoLookup(rast, fasta, orgName);
        
        orgName = "6666667.6732";
        rast = new File("C:\\Users\\lucy\\Dropbox\\29_genomes_new\\JY1305-assemblies\\RASTgenomes\\" + orgName + "\\output.txt");
        fasta = new File(rast.getParentFile(), "contigs");
        runNoLookup(rast, fasta, orgName);
        
        orgName = "6666667.6733";
        rast = new File("C:\\Users\\lucy\\Dropbox\\29_genomes_new\\JY1305-assemblies\\RASTgenomes\\" + orgName + "\\output.txt");
        fasta = new File(rast.getParentFile(), "contigs");
        runNoLookup(rast, fasta, orgName);
    }
    
    private static void runNoLookup(File rastFile, File fastaFile, String organismShortName) throws IOException{
        InputStream rastIs = new FileInputStream(rastFile);
        InputStream fastaIs = new FileInputStream(fastaFile);
        Organism org = new Organism();
        org.setStrain(organismShortName);
        RastLoader loader = new RastLoader(org, new AnnotationMethod(), rastIs, new AnnotationMethod(), fastaIs, 11, false);
        loader.run();
    }

    private static void testWithLookup() throws FileNotFoundException, DataLoadException, IOException {
        InputStream rastIs = new FileInputStream("C:\\Users\\lucy\\Desktop\\testData\\rast\\RAST-Archive\\6666667.6722.txt");
        RastLoader loader = new RastLoader(new Organism(), new AnnotationMethod(), rastIs, new AnnotationMethod(), null, 11, true);
        loader.run();
    }

    private static void testNoLookup() throws FileNotFoundException, DataLoadException, IOException {
        InputStream rastIs = new FileInputStream("C:\\Users\\lucy\\Desktop\\testData\\rast\\RAST-Archive\\6666667.6722.txt");
        InputStream fastaIs = new FileInputStream("C:\\Users\\lucy\\Dropbox\\29_genomes_new\\JY1305-assemblies\\RASTgenomes\\6666667.6722\\contigs");
        RastLoader loader = new RastLoader(new Organism(), new AnnotationMethod(), rastIs, new AnnotationMethod(), fastaIs, 11, false);
        loader.run();
    }
}
