/*
 * Copyright (C) 2015 Aurora Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.uncc.genosets.embl.goa;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.FactDetailLocation;
import edu.uncc.genosets.datamanager.entity.FactFeature;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.persister.FactFeaturePersister;
import edu.uncc.genosets.datamanager.persister.Persister;
import edu.uncc.genosets.geneontology.api.GeneOntology;
import edu.uncc.genosets.geneontology.api.GoTermPersister;
import edu.uncc.genosets.taskmanager.SimpleTask;
import edu.uncc.genosets.taskmanager.TaskException;
import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
public class GeneOntologyGoaImpl extends SimpleTask {
    protected static final String URL = "http://www.ebi.ac.uk/QuickGO/GAnnotation?tax=VARIABLE&format=tsv&limit=1000000&gz=true";
    private final GoTermPersister termPersister;
    private final AnnotationMethod method;
    private final Organism organism;

    public GeneOntologyGoaImpl(GoTermPersister persister, AnnotationMethod method, Organism organism) {
        super(organism.getStrain());
        this.termPersister = persister;
        this.method = method;
        this.organism = organism;
    }

    @Override
    public void performTask() throws TaskException {
        try {
            HashMap<String, FactDetailLocation> goaMap = GOAQuery.getGOA(organism);
            if (goaMap != null && !goaMap.isEmpty()) {
                String taxonId = getTaxonId(goaMap);
                if (taxonId != null) {
                    //get the file annotations
                    FileAnnotation fileAnnotation = getFileAnnotation(getURL(taxonId));
                    //persist only the annotations that are included in the assunit set
                    List<Persister> persisters = loadAnnotations(fileAnnotation, goaMap);
                    DataManager.getDefault().persist(persisters);
                    //log as complete
                    TaskLogFactory.getDefault().log("Persisted GOA annotations for organism " + organism.getStrain() + "(" + organism.getOrganismId() + ")", this.getClass().toString(), "Success", TaskLog.INFO, new Date());
                }
            } else { //no GOA annotations
                //Log as warning
                TaskLogFactory.getDefault().log("Could not find load GOA annotations.  No annotations exist for organism " + organism.getStrain() + "(" + organism.getOrganismId() + ")", this.getClass().toString(), "No UniProtKB id is found for organisms.", TaskLog.WARNING, new Date());
            }
        } catch (Exception ex) {
            //Log as error
            TaskLogFactory.getDefault().log("Error loading GOA annotations for organism " + organism.getStrain() + "(" + organism.getOrganismId() + ")", this.getClass().toString(), "Exception during loading" + ex.getLocalizedMessage(), TaskLog.ERROR, new Date());
            Exceptions.printStackTrace(ex);
        }
    }

    private String getURL(String taxonId) {
        return URL.replace("VARIABLE", taxonId);
    }

    private String getTaxonId(Map<String, FactDetailLocation> goaMap) {
        String goa = null;
        for (String goaId : goaMap.keySet()) {
            StringBuilder urlString = new StringBuilder("http://www.ebi.ac.uk/QuickGO/GAnnotation?protein=").append(goaId).append("&format=tsv&gz=true");
            FileAnnotation fileAnnotation = getFileAnnotation(urlString.toString());
            Integer taxonIndex = fileAnnotation.getColumnByName(FileAnnotation.TAXON);
            List<String[]> values = fileAnnotation.getValues();
            if (values != null && values.size() > 0) {
                String[] line = values.get(0);
                String taxon = line[taxonIndex];
                if (taxon != null) {
                    return taxon;
                }
            }
            if (goa == null) {
                goa = goaId;
            }
        }
        TaskLogFactory.getDefault().log("Could not find GOA annotations for file that includes protein: " + goa, "GeneOntology.class", "Could not find GOA annotations for file that includes protein: " + goa, TaskLog.ERROR, new Date());
        return null;
    }

    /**
     * Creates persisters for the go annotations for this feature
     *
     * @param annos
     * @param usingAlternateTaxon
     */
    private List<Persister> loadAnnotations(FileAnnotation annos, Map<String, FactDetailLocation> goaMap) {
        List<Persister> persisters = null;
        if (annos != null) {
            if (annos.getValues() != null && !annos.getValues().isEmpty()) {
                persisters = new LinkedList<Persister>();
                //load annotations
                int id = annos.getColumnByName(FileAnnotation.ID_COLUMN);
                int goId = annos.getColumnByName(FileAnnotation.GO_ID_COLUMN);
                //int goName = annos.getColumnByName(FileAnnotation.GO_NAME_COLUMN);
                List<String[]> values = annos.getValues();
                for (String[] v : values) {
                    //lookup feature
                    FactDetailLocation feature = goaMap.get(v[id]);
                    if (feature != null) {
                        FeatureCluster cluster = termPersister.getGoTerm(v[goId]);
                        if (cluster == null) {
                            Logger.getLogger("edu.uncc.genosets.geneontology.GeneOntologyImpl").log(Level.SEVERE, "Could not find go term " + v[goId]);
                        } else {
                            persisters.add(load(feature, cluster));
                        }
                    }
                }
            }
        }
        return persisters;
    }

    /**
     * Gets the Fact feature persister for this
     *
     * @param feature
     * @param cluster
     * @return
     */
    private FactFeaturePersister load(FactDetailLocation feature, FeatureCluster cluster) {
        FactFeaturePersister p = FactFeaturePersister.instantiate();
        FactFeature fact = new FactFeature();
        //set what we know
        fact.setFeatureId(feature.getFeatureId());
        fact.setAssembledUnitId(feature.getAssembledUnitId());
        fact.setOrganismId(feature.getOrganismId());

        //setup persister
        fact.setFeatureClusterId(cluster.getFeatureClusterId());
        p.setup(cluster, method, fact, GeneOntology.ENTITY_NAME_CLUSTER, AnnotationMethod.DEFAULT_NAME, GeneOntology.ENTITY_NAME_FACT);
        return p;
    }

    /**
     * Looks up the GOA annotations from the EMBL website for this tax id
     *
     * @param urlString
     * @return
     */
    private FileAnnotation getFileAnnotation(String urlString) {
        FileAnnotation anno = new FileAnnotation();
        BufferedReader rd = null;
        GZIPInputStream gz = null;

        //GZIPInputStream rd = null;
        try {
            URL u = new URL(urlString);
            // Connect
            HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
            // Get data
            //rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            gz = new GZIPInputStream(urlConnection.getInputStream());
            rd = new BufferedReader(new InputStreamReader(gz));


            //read the first line for column definitions and store in anno
            anno.loadColumns(rd.readLine());

            //read and store all annotations
            String line;
            while ((line = rd.readLine()) != null) {
                anno.addValue(line);
            }

        } catch (Exception e) {
            NotifyDescriptor d = new NotifyDescriptor.Message("Cannot find any GOA annotations associated with a genome.",
                    NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
            //Exceptions.printStackTrace(e);
        } finally {
            try {
                if (rd != null) {
                    rd.close();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return anno;
    }

    private static class FileAnnotation {

        private final static String ID_COLUMN = "ID";
        private final static String GO_ID_COLUMN = "GO ID";
        private final static String GO_NAME_COLUMN = "GO Name";
        private final static String TAXON = "Taxon";
        private HashMap<String, Integer> columnMap;
        private List<String[]> values;

        public void loadColumns(String columnLine) {
            if (columnMap == null) {
                columnMap = new HashMap<String, Integer>();
            }
            String[] split = columnLine.split("\t");
            int i = 0;
            for (String c : split) {
                columnMap.put(c, new Integer(i));
                i++;
            }
        }

        public List<String[]> getValues() {
            if (values == null) {
                values = new LinkedList<String[]>();
            }
            return values;
        }

        public void addValue(String line) {
            String[] split = line.split("\t");
            if (values == null) {
                values = new LinkedList<String[]>();
            }
            values.add(split);
        }

        public Integer getColumnByName(String name) {
            if (columnMap == null) {
                return null;
            } else {
                return columnMap.get(name);
            }
        }
    }

    private static class GOAQuery implements QueryCreator {

        static HashMap<String, FactDetailLocation> getGOA(Organism org) {
            HashMap<String, FactDetailLocation> goaLookup = new HashMap<String, FactDetailLocation>();
            StringBuilder queryString = new StringBuilder("SELECT deat FROM ");
            queryString.append("AnnoFactDetail").append(" as deat ");
            queryString.append(" WHERE ");
            queryString.append(" deat.detailType = 'UniProtKB' ");
            queryString.append(" AND ");
            queryString.append(" deat.organismId = ").append(org.getOrganismId());
            queryString.append(" GROUP BY deat.detailValue ");
            List<FactDetailLocation> deats = DataManager.getDefault().createQuery(queryString.toString());

            //add to the goa id and the detail to the map
            for (FactDetailLocation d : deats) {
                goaLookup.put(d.getDetailValue(), d);
            }
            return goaLookup;
        }
    }
}
