/*
 * 
 * 
 */
package edu.uncc.genosets.geneontology.api;

import bioio.GoAnnotationFileFormat;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.*;
import edu.uncc.genosets.datamanager.persister.FactFeaturePersister;
import edu.uncc.genosets.datamanager.persister.Persister;
import edu.uncc.genosets.taskmanager.AbstractTask;
import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

/**
 *
 * @author aacain
 */
public abstract class GeneOntology extends AbstractTask {
    //cluster properties

    public static final String CLUSTER_CATEGORY = "Annotation";
    public static final String CLUSTER_TYPE = "Gene Ontology";
    public static final String PROP_CLUSTER_GOID = "goId";
    public static final String PROP_CLUSTER_GONAME = "goName";
    //fact properties
    public static final String PROP_FACT_TERM = "goTerm";
    public static final String PROP_FACT_TERMID = "goTermId";
    //entity names
    public static final String ENTITY_NAME_FACT = "Fact_Feature_GoAnno";
    public static final String ENTITY_NAME_CLUSTER = "Cluster_GoTerm";
    public static final String UNCLASSIFIED_TERMID = "GS:0000000";
    public static final String ALL_TERMID = "GO:0000000";
    public static final String OBSOLETE_TERMID = "GO:xxxxxxx";

//    public static GeneOntology instantiate(GoTermPersister termPersister, AnnotationMethod method, Organism organism) {
//        return new GeneOntologyImpl(termPersister, method, organism);
//    }
//
//    public static GeneOntology instantiate(GoTermPersister termPersister, AnnotationMethod method, GoAnnotationFileFormat gaf, String fileName) {
//        return new GeneOntologyImpl(termPersister, method, gaf, fileName);
//    }
//    
//    private static class GeneOntologyImpl extends GeneOntology {
//
//        protected static final String URL = "http://www.ebi.ac.uk/QuickGO/GAnnotation?tax=VARIABLE&format=tsv&limit=1000000&gz=true";
//        private final AnnotationMethod method;
//        private final GoTermPersister termPersister;
//        private final GoAnnotationFileFormat gaf;
//        private String fileName;
//        private final Organism organism;
//
//        public GeneOntologyImpl(GoTermPersister persister, AnnotationMethod method, GoAnnotationFileFormat gaf, String fileName) {
//            this.setName("Persist GOA annotations: " + fileName);
//            this.termPersister = persister;
//            this.method = method;
//            this.method.setLoadDate(new Date());
//            this.method.setRunDate(this.method.getLoadDate());
//            this.organism = null;
//            this.gaf = gaf;
//            this.fileName = fileName;
//        }
//
//        public GeneOntologyImpl(GoTermPersister persister, AnnotationMethod method, Organism organism) {
//            this.setName("Persist GOA annotations: " + organism.getStrain() + "(" + organism.getOrganismId() + ")");
//            this.termPersister = persister;
//            this.method = method;
//            this.method.setLoadDate(new Date());
//            this.method.setRunDate(this.method.getLoadDate());
//            this.organism = organism;
//            this.gaf = null;
//        }
//
//        //start the job
//        protected void run() {
//            if (organism != null) {
//                try {
//                    HashMap<String, FactDetailLocation> goaMap = GOAQuery.getGOA(organism);
//                    if (goaMap != null && !goaMap.isEmpty()) {
//                        String taxonId = getTaxonId(goaMap);
//                        if (taxonId != null) {
//                            //get the file annotations
//                            FileAnnotation fileAnnotation = getFileAnnotation(getURL(taxonId));
//                            //persist only the annotations that are included in the assunit set
//                            List<Persister> persisters = loadAnnotations(fileAnnotation, goaMap);
//                            DataManager.getDefault().persist(persisters);
//                            //log as complete
//                            TaskLogFactory.getDefault().log("Persisted GOA annotations for organism " + organism.getStrain() + "(" + organism.getOrganismId() + ")", this.getClass().toString(), "Success", TaskLog.INFO, new Date());
//                        }
//                    } else { //no GOA annotations
//                        //Log as warning
//                        TaskLogFactory.getDefault().log("Could not find load GOA annotations.  No annotations exist for organism " + organism.getStrain() + "(" + organism.getOrganismId() + ")", this.getClass().toString(), "No UniProtKB id is found for organisms.", TaskLog.WARNING, new Date());
//                    }
//                } catch (Exception ex) {
//                    //Log as error
//                    TaskLogFactory.getDefault().log("Error loading GOA annotations for organism " + organism.getStrain() + "(" + organism.getOrganismId() + ")", this.getClass().toString(), "Exception during loading" + ex.getLocalizedMessage(), TaskLog.ERROR, new Date());
//                    Exceptions.printStackTrace(ex);
//                }
//            } else { //load gaf
//                try {
//                    List<Persister> persisters = runGaf();
//                    DataManager.getDefault().persist(persisters);
//                    TaskLogFactory.getDefault().log("Persisted GOA annotations for " + this.fileName, this.getClass().toString(), "Success", TaskLog.INFO, new Date());
//                } catch (Exception ex) {
//                    TaskLogFactory.getDefault().log("Error loading annotations from file: " + this.fileName, this.getClass().toString(), "Exception during loading" + ex.getLocalizedMessage(), TaskLog.ERROR, new Date());
//                    Exceptions.printStackTrace(ex);
//                }
//            }
////                if (includedAsses != null && !includedAsses.isEmpty()) {
////                    //get all the goa ids for the included assunits
////                    HashMap<String, FactDetailLocation> goaMap = getGOA();
////                    //lookup goa id to get taxon id
////                    if (goaMap != null && !goaMap.isEmpty()) {
////                        if (includedAsses != null) {
////                            //get the taxon id for a single goa
////                            String taxonId = getTaxonId(goaMap);
////                            if (taxonId != null) {
////                                //get the file annotations
////                                FileAnnotation fileAnnotation = getFileAnnotation(getURL(taxonId));
////                                //persist only the annotations that are included in the assunit set
////                                List<Persister> persisters = loadAnnotations(fileAnnotation, goaMap);
////                                DataManager.getDefault().persist(persisters);
////                            }
////                        }
////                    }
////                    else{ //no annotations found
////                        AssembledUnit a = null;
////                        for (AssembledUnit ass : includedAsses) {
////                            a = ass;
////                            break;
////                        }
////                        Organism org = (Organism)DataManager.getDefault().get(Organism.DEFAULT_NAME, a.getOrganismId());
////                        TaskLogFactory.getDefault().log("Could not find load GOA annotations.  No annotations exist for organism", this.getClass().toString(), "No UniProtKB id is found for organisms.", TaskLog.WARNING, new Date());
////                    }
////                } else {
////                    List<Persister> persisters = runGaf();
////                    DataManager.getDefault().persist(persisters);
////                }
//        }
//
//        private List<Persister> runGaf() {
//            List<Persister> persisters = new ArrayList<Persister>(gaf.getAnnotations().size());
//            for (GoAnnotationFileFormat.Annotation anno : gaf.getAnnotations()) {
//                try {
//                    String stringId = anno.getId();
//                    String[] splitIds = stringId.split("\\|");
//                    if (splitIds.length > 1) {
//                        stringId = splitIds[splitIds.length - 1];
//                    }
//                    Integer id = Integer.parseInt(stringId);
//                    Location l = (Location) DataManager.getDefault().get(Location.DEFAULT_NAME, id);
//                    FeatureCluster cluster = termPersister.getGoTerm(anno.getGoIdentifier());
//                    if (cluster != null) {
//                        FactFeaturePersister p = FactFeaturePersister.instantiate();
//                        FactFeature fact = new FactFeature();
//                        //set what we know
//                        fact.setFeatureId(l.getFeatureId());
//                        fact.setAssembledUnitId(l.getAssembledUnitId());
//                        fact.setOrganismId(l.getOrganismId());
//
//                        //setup persister
//                        fact.setFeatureClusterId(cluster.getFeatureClusterId());
//                        p.setup(cluster, method, fact, GeneOntology.ENTITY_NAME_CLUSTER, AnnotationMethod.DEFAULT_NAME, GeneOntology.ENTITY_NAME_FACT);
//                        persisters.add(p);
//                    } else {
//                        TaskLogFactory.getDefault().log("Could not find GO term: " + anno.getGoIdentifier(), GeneOntology.class.getName(), "The GO term was not found.  This annotation was not added.", TaskLog.WARNING, new Date());
//                    }
//                } catch (NumberFormatException ex) {
//                    TaskLogFactory.getDefault().log("Error loading GAF file: " + this.fileName, GeneOntology.class.getName(), ex.getMessage(), TaskLog.ERROR, new Date());
//                    Exceptions.printStackTrace(ex);
//                }
//            }
//            return persisters;
//        }
//
////        /**
////         * Adds all the goaId, featureDeatils to a map that are included for
////         * this organism.
////         *
////         * @return a hashmap of key=goaId, value=featureDetail
////         */
////        @SuppressWarnings("unchecked")
////        private HashMap<String, FactDetailLocation> getGOA() {
////            //get an organism id
////            if (includedAsses != null && !includedAsses.isEmpty()) {
////                HashMap<String, FactDetailLocation> goaLookup = new HashMap<String, FactDetailLocation>();
////                StringBuilder queryString = new StringBuilder("SELECT deat FROM ");
////                queryString.append("AnnoFactDetail").append(" as deat WHERE deat.detailType = 'UniProtKB' ");
////                int i = 0;
////                for (AssembledUnit ass : includedAsses) {
////                    if (i == 0) {
////                        queryString.append(" AND (deat.assembledUnitId  IN (");
////                    } else {
////                        queryString.append(", ");
////                    }
////                    queryString.append(ass.getAssembledUnitId());
////                    if (i == includedAsses.size() - 1) {
////                        queryString.append(")");
////                    }
////                    i++;
////                }
////                queryString.append(")");
////                queryString.append(" GROUP BY deat.detailValue ");
////                List<FactDetailLocation> deats = DataManager.getDefault().createQuery(queryString.toString());
////
////                //add to the goa id and the detail to the map
////                for (FactDetailLocation d : deats) {
////                    goaLookup.put(d.getDetailValue(), d);
////                }
////                return goaLookup;
////            } else {
////                return null;
////            }
////        }
//        private String getTaxonId(Map<String, FactDetailLocation> goaMap) {
//            String goa = null;
//            for (String goaId : goaMap.keySet()) {
//                StringBuilder urlString = new StringBuilder("http://www.ebi.ac.uk/QuickGO/GAnnotation?protein=").append(goaId).append("&format=tsv&gz=true");
//                FileAnnotation fileAnnotation = getFileAnnotation(urlString.toString());
//                Integer taxonIndex = fileAnnotation.getColumnByName(FileAnnotation.TAXON);
//                List<String[]> values = fileAnnotation.getValues();
//                if (values != null && values.size() > 0) {
//                    String[] line = values.get(0);
//                    String taxon = line[taxonIndex];
//                    if (taxon != null) {
//                        return taxon;
//                    }
//                }
//                if (goa == null) {
//                    goa = goaId;
//                }
//            }
//            TaskLogFactory.getDefault().log("Could not find GOA annotations for file that includes protein: " + goa, "GeneOntology.class", "Could not find GOA annotations for file that includes protein: " + goa, TaskLog.ERROR, new Date());
//            return null;
//        }
//
//        /**
//         * Looks up the GOA annotations from the EMBL website for this tax id
//         *
//         * @param urlString
//         * @return
//         */
//        private FileAnnotation getFileAnnotation(String urlString) {
//            FileAnnotation anno = new FileAnnotation();
//            BufferedReader rd = null;
//            GZIPInputStream gz = null;
//
//            //GZIPInputStream rd = null;
//            try {
//                URL u = new URL(urlString);
//                // Connect
//                HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
//                // Get data
//                //rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                gz = new GZIPInputStream(urlConnection.getInputStream());
//                rd = new BufferedReader(new InputStreamReader(gz));
//
//
//                //read the first line for column definitions and store in anno
//                anno.loadColumns(rd.readLine());
//
//                //read and store all annotations
//                String line;
//                while ((line = rd.readLine()) != null) {
//                    anno.addValue(line);
//                }
//
//            } catch (Exception e) {
//                NotifyDescriptor d = new NotifyDescriptor.Message("Cannot find any GOA annotations associated with a genome.",
//                        NotifyDescriptor.WARNING_MESSAGE);
//                DialogDisplayer.getDefault().notify(d);
//                //Exceptions.printStackTrace(e);
//            } finally {
//                try {
//                    if (rd != null) {
//                        rd.close();
//                    }
//                } catch (IOException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//            }
//            return anno;
//        }
//
//        /**
//         * Creates persisters for the go annotations for this feature
//         *
//         * @param annos
//         * @param usingAlternateTaxon
//         */
//        private List<Persister> loadAnnotations(FileAnnotation annos, Map<String, FactDetailLocation> goaMap) {
//            List<Persister> persisters = null;
//            if (annos != null) {
//                if (annos.getValues() != null && !annos.getValues().isEmpty()) {
//                    persisters = new LinkedList<Persister>();
//                    //load annotations
//                    int id = annos.getColumnByName(FileAnnotation.ID_COLUMN);
//                    int goId = annos.getColumnByName(FileAnnotation.GO_ID_COLUMN);
//                    //int goName = annos.getColumnByName(FileAnnotation.GO_NAME_COLUMN);
//                    List<String[]> values = annos.getValues();
//                    for (String[] v : values) {
//                        //lookup feature
//                        FactDetailLocation feature = goaMap.get(v[id]);
//                        if (feature != null) {
//                            FeatureCluster cluster = termPersister.getGoTerm(v[goId]);
//                            if (cluster == null) {
//                                Logger.getLogger("edu.uncc.genosets.geneontology.GeneOntologyImpl").log(Level.SEVERE, "Could not find go term " + v[goId]);
//                            } else {
//                                persisters.add(load(feature, cluster));
//                            }
//                        }
//                    }
//                }
//            }
//            return persisters;
//        }
//
//        /**
//         * Gets the Fact feature persister for this
//         *
//         * @param feature
//         * @param cluster
//         * @return
//         */
//        private FactFeaturePersister load(FactDetailLocation feature, FeatureCluster cluster) {
//            FactFeaturePersister p = FactFeaturePersister.instantiate();
//            FactFeature fact = new FactFeature();
//            //set what we know
//            fact.setFeatureId(feature.getFeatureId());
//            fact.setAssembledUnitId(feature.getAssembledUnitId());
//            fact.setOrganismId(feature.getOrganismId());
//
//            //setup persister
//            fact.setFeatureClusterId(cluster.getFeatureClusterId());
//            p.setup(cluster, method, fact, GeneOntology.ENTITY_NAME_CLUSTER, AnnotationMethod.DEFAULT_NAME, GeneOntology.ENTITY_NAME_FACT);
//            return p;
//        }
//
//        private String getURL(String taxonId) {
//            return URL.replace("VARIABLE", taxonId);
//        }
//
//        @Override
//        public void performTask() {
//            this.run();
//        }
//
//        @Override
//        public void uninitialize() {
//        }
//
//        @Override
//        public void logErrors() {
//        }
//
//        @Override
//        public Organism getOrganismDependency() {
//            return null;
//        }
//
//        @Override
//        public void setOrganismDependency(Organism org) {
//        }
//
//        private static class FileAnnotation {
//
//            private final static String ID_COLUMN = "ID";
//            private final static String GO_ID_COLUMN = "GO ID";
//            private final static String GO_NAME_COLUMN = "GO Name";
//            private final static String TAXON = "Taxon";
//            private HashMap<String, Integer> columnMap;
//            private List<String[]> values;
//
//            public void loadColumns(String columnLine) {
//                if (columnMap == null) {
//                    columnMap = new HashMap<String, Integer>();
//                }
//                String[] split = columnLine.split("\t");
//                int i = 0;
//                for (String c : split) {
//                    columnMap.put(c, new Integer(i));
//                    i++;
//                }
//            }
//
//            public List<String[]> getValues() {
//                if (values == null) {
//                    values = new LinkedList<String[]>();
//                }
//                return values;
//            }
//
//            public void addValue(String line) {
//                String[] split = line.split("\t");
//                if (values == null) {
//                    values = new LinkedList<String[]>();
//                }
//                values.add(split);
//            }
//
//            public Integer getColumnByName(String name) {
//                if (columnMap == null) {
//                    return null;
//                } else {
//                    return columnMap.get(name);
//                }
//            }
//        }
//    }
//
//    private static class GOAQuery implements QueryCreator {
//
//        static HashMap<String, FactDetailLocation> getGOA(Organism org) {
//            HashMap<String, FactDetailLocation> goaLookup = new HashMap<String, FactDetailLocation>();
//            StringBuilder queryString = new StringBuilder("SELECT deat FROM ");
//            queryString.append("AnnoFactDetail").append(" as deat ");
//            queryString.append(" WHERE ");
//            queryString.append(" deat.detailType = 'UniProtKB' ");
//            queryString.append(" AND ");
//            queryString.append(" deat.organismId = ").append(org.getOrganismId());
//            queryString.append(" GROUP BY deat.detailValue ");
//            List<FactDetailLocation> deats = DataManager.getDefault().createQuery(queryString.toString());
//
//            //add to the goa id and the detail to the map
//            for (FactDetailLocation d : deats) {
//                goaLookup.put(d.getDetailValue(), d);
//            }
//            return goaLookup;
//        }
//    }
}
