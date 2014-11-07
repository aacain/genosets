/*
 * 
 * 
 */
package edu.uncc.genosets.core.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author aacain
 */

/*
 *
 */
public class Annotation {

    protected static final String m_ID = "PROP_ID";
    protected static final String CRITERIA = "CRITERIA";
    //criteria
    protected static final String ORTHOLOG_OTHER = "ORTHOLOG_OTHER";
    //feature properties
    /*
     * Set<Map<String, Object>>
     */
    protected static final String F_Clusters = "F_Clusters";
    protected Map<String, Integer> headerMap = new HashMap<String, Integer>();
    private HashMap<String, A_Feature> featureMap = new HashMap<String, A_Feature>();
    protected HashMap<String, A_Cluster> clusterMap = new HashMap<String, A_Cluster>();
    private HashSet<A_Cluster> clusters_set = new HashSet<A_Cluster>();
    private HashSet<A_Cluster> clusters_unused = new HashSet<A_Cluster>();
    private HashSet<A_Cluster> clusters_eliminated = new HashSet<A_Cluster>();

    /**
     *
     *
     * Feature (integer id) - all features that have the same 3'position have
     * the same feature id (they can have a different location id if there 5'
     * position differs)
     *
     * Location (integer id) - the location id is unique if the feature has
     * different 5'positions FeatureCluster (integer id) - the ortholog cluster
     * id 
     * 
     * GeneMark (0 or 1 for true or false) - if the location is predicted in
     * GeneMark 
     * 
     * Glimmer (0 or 1) - if the location is predicted in Glimmer 
     * 
     * is150
     * (0 or 1) - if the location is >= 150 
     * 
     * hasOrtholog (0 or 1) - if the
     * feature has an ortholog in another genome (we used the criteria indicate
     * that the feature was found in a reference genome) 
     * 
     * NucLength (integer) -
     * the nucleotide length of the location
     *
     * The file must contain a header column that on the first line of the file
     * that contains the columns names above (spelled as they appear above).
     * They can be in any order.
     *
     * @param inputFile
     * @param outputDirectory - directory the output should be written to
     */
    public void run(File inputFile, File outputDirectory) {
        //initialize
        readFile(inputFile);


        clusters_unused.addAll(clusterMap.values());

        //eliminate bad clusters (don't meet criteria)
        setNonCriteriaClusters();

        //set by elimination (if only 1 good cluster, then set)
        for (A_Feature feature : featureMap.values()) {
            if (feature.getKeepCluster() == null) {
                setFeatureWithOneCluster(feature);
            }
        }

        //compare by homology
        for (A_Feature feature : featureMap.values()) {
            if (feature.getKeepCluster() == null) {
                compareHomology(feature);

            }
        }

        //compare by annotation rank
        for (A_Feature feature : featureMap.values()) {
            if (feature.getKeepCluster() == null) {
                compareRank(feature);
            }
        }

        //best by largest
        for (A_Feature feature : featureMap.values()) {
            if (feature.getKeepCluster() == null) {
                this.compareSize(feature);
            }
        }

        //determine locations
        determineLocations();


        Set<A_Cluster> clusters = new HashSet<A_Cluster>();
        for (A_Cluster cluster : clusterMap.values()) {
            if (cluster.isKeepFlag() && !cluster.isAmbiguousFlag()) {
                clusters.add(cluster);
            }
        }


        print(outputDirectory);

        System.out.println("Cluster size: " + clusters.size());
    }

    /**
     * set clusters as bad if they don't meet criteria
     */
    private void setNonCriteriaClusters() {
        int i_geneMark = headerMap.get("GeneMark");
        int i_glimmer = headerMap.get("Glimmer");
        int i_150 = headerMap.get("is150");
        int i_ortholog = headerMap.get("hasOrtholog");

        for (A_Cluster cluster : clusterMap.values()) {
            if (!cluster.isIsTrash() && !cluster.isMeetsCriteria()) {
                boolean glimmer = false;
                boolean genemark = false;
                boolean ortholog = false;
                boolean is150 = false;
                for (List<A_Location> f_locs : cluster.getFeatureMap().values()) {
                    for (A_Location loc : f_locs) {
                        int rank = 0;
                        ArrayList<String> criteria = loc.getCriteria();
                        if (criteria.get(i_ortholog).equals("1")) {
                            ortholog = true;
                            rank = rank + 1000;
                        }
                        if (criteria.get(i_150).equals("1")) {
                            is150 = true;
                            rank = rank + 100;
                        }
                        if (criteria.get(i_geneMark).equals("1")) {
                            genemark = true;
                            rank = rank + 10;
                        }
                        if (criteria.get(i_glimmer).equals("1")) {
                            glimmer = true;
                            rank = rank + 1;
                        }
                        loc.setRank(rank);
                    }//visited all this features location for this cluster
                }
                //test to see if the feature meets the criteria and get rank
                int clusterRank = 0;
                if (ortholog) {
                    clusterRank = clusterRank + 1000;
                }
                if (is150) {
                    clusterRank = clusterRank + 100;
                }
                if (genemark) {
                    clusterRank = clusterRank + 10;
                }
                if (glimmer) {
                    clusterRank = clusterRank + 1;
                }
                if (ortholog || is150 || (glimmer & genemark)) {
                    cluster.setMeetsCriteria(true);
                }
                if (cluster.getRank() < clusterRank) {
                    cluster.setRank(clusterRank);
                }
            }//end of feature


            //now set non-keepers
            if (!cluster.isMeetsCriteria()) {
                setBad(cluster);
            }
        }
    }

    private int setFeatureWithOneCluster(A_Feature feature) {
        Set<A_Cluster> possibleClusters = new HashSet<A_Cluster>();
        for (A_Location location : feature.getLocations()) {
            if (location.getCluster().isMeetsCriteria() && !location.getCluster().isIsTrash()) {
                possibleClusters.add(location.getCluster());
            }
        }

        if (possibleClusters.size() == 1) {
            for (A_Cluster cluster : possibleClusters) {
                setGood(cluster);
            }
            return 1;
        }
        return 0;
    }

    private int compareHomology(A_Feature feature) {
        Integer i_ortholog = headerMap.get("hasOrtholog");
        Set<A_Location> locations = feature.getLocations();
        HashSet<A_Cluster> myGoodClusters = new HashSet<A_Cluster>();
        //test each cluster for this feature
        for (A_Location location : locations) {
            if (location.getCluster().isMeetsCriteria() && !location.getCluster().isIsTrash()) {
                int hasOrtholog = Integer.parseInt(location.getCriteria().get(i_ortholog));
                if (hasOrtholog > 0) {
                    myGoodClusters.add(location.getCluster());
                }
            }
        }

        if (myGoodClusters.size() == 1) {
            for (A_Cluster cluster : myGoodClusters) {
                setGood(cluster);
            }
            return 1;
        }

        //has ambiguities
        if (myGoodClusters.size() > 1) {
            for (A_Cluster cluster : myGoodClusters) {
                setAmbiguous(cluster);
                cluster.setAmbiguousFlag(true);
            }
            return -1;
        }

        //unknown
        return 0;
    }

    private int compareRank(A_Feature feature) {
        //get other possible clusters
        Set<A_Cluster> possibleClusters = new HashSet<A_Cluster>();
        int maxRank = 0;
        for (A_Location location : feature.getLocations()) {
            A_Cluster cluster = location.getCluster();
            if (!cluster.isIsTrash()) {
                if (cluster.getRank() > maxRank) {
                    maxRank = cluster.getRank();
                    for (A_Cluster badCluster : possibleClusters) {
                        setBad(badCluster);
                    }
                    possibleClusters.clear();
                    possibleClusters.add(cluster);
                } else if (cluster.getRank() == maxRank) {
                    possibleClusters.add(cluster);
                }
            }
        }

        if (possibleClusters.size() == 1) {
            for (A_Cluster cluster : possibleClusters) {
                setGood(cluster);
            }
            return 1;
        }


        return 0;
    }

    private void compareSize(A_Feature feature) {
        int i_nucLength = headerMap.get("NucLength");
        int maxRank = 0;
        int largest = 0;
        A_Cluster best = null;
        for (A_Location location : feature.getLocations()) {
            if (!location.getCluster().isIsTrash()) {
                int rank = location.getCluster().getRank();
                if (rank > maxRank) {
                    largest = Integer.parseInt(location.getCriteria().get(i_nucLength));
                    best = location.getCluster();
                } else if (rank == maxRank) {
                    int length = Integer.parseInt(location.getCriteria().get(i_nucLength));
                    if (length > largest) {
                        largest = length;
                        best = location.getCluster();
                    }
                }
            }
        }

        if (best != null) {
            setGood(best);
        }
    }

    private void determineLocations() {
        int i_length = headerMap.get("NucLength");
        for (A_Cluster cluster : clusterMap.values()) {
            if (cluster.isKeepFlag()) {
                for (List<A_Location> locList : cluster.getFeatureMap().values()) {
                    A_Location bestLocation = null;
                    int maxRank = 0;
                    int maxLength = 0;
                    for (A_Location location : locList) {
                        if (location.getRank() > maxRank) {
                            //then set
                            bestLocation = location;
                            maxRank = location.getRank();
                            maxLength = Integer.parseInt(location.getCriteria().get(i_length));
                        } else if (location.getRank() == maxRank) {
                            //look at sequence length and take the longest
                            int length = Integer.parseInt(location.getCriteria().get(i_length));
                            if (length > maxLength) {
                                bestLocation = location;
                                maxRank = location.getRank();
                                maxLength = length;
                            }
                        }
                    }
                    bestLocation.getFeature().setKeepLocation(bestLocation);
                }
            }
        }
    }

    private void print(File directory) {
        File clusterFile = new File(directory.getAbsolutePath() + "/cluster.txt");
        File locationFile = new File(directory.getAbsolutePath() + "/location.txt");
        BufferedWriter clusterWriter = null;
        BufferedWriter locWriter = null;
        try {
            //create files
            clusterFile.createNewFile();
            locationFile.createNewFile();
            //create writers
            clusterWriter = new BufferedWriter(new FileWriter(clusterFile));
            locWriter = new BufferedWriter(new FileWriter(locationFile));
            for (A_Cluster cluster : clusterMap.values()) {
                StringBuilder clusterBldr = new StringBuilder();
                clusterBldr.append(cluster.getId()).append("\t");
                if (cluster.isAmbiguousFlag()) {
                    clusterBldr.append("A");
                } else if (cluster.isKeepFlag()) {
                    clusterBldr.append("K");
                    //iterate all features and get the best location
                    StringBuilder locBldr = new StringBuilder();
                    for (A_Location location : cluster.getLocations()) {
                        A_Location keepLocation = location.getFeature().getKeepLocation();
                        if (keepLocation.equals(location)) {
                            locBldr.append(cluster.getId()).append("\t").append(location.getId()).append(System.getProperty("line.separator"));
                            locWriter.write(locBldr.toString());
                            locBldr = new StringBuilder();
                        }
                    }
                } else if (cluster.isIsTrash()) {
                    clusterBldr.append("E");
                } else {
                    clusterBldr.append("U");
                }
                clusterBldr.append(System.getProperty("line.separator"));
                clusterWriter.write(clusterBldr.toString());
            }
        } catch (Exception ex) {
            Logger.getLogger(Annotation.class.toString()).log(Level.SEVERE, "Error writing file.", ex);
        } finally {
            try {
                if (clusterWriter != null) {
                    clusterWriter.close();
                }
                if (locWriter != null) {
                    locWriter.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    private void setGood(A_Cluster goodCluster) {
        //remove good cluster from unused
        clusters_unused.remove(goodCluster);
        clusters_set.add(goodCluster);
        goodCluster.setKeepFlag(true);

        //now set this cluster for all the features in the cluster
        for (A_Feature feature : goodCluster.getFeatureMap().keySet()) {
            feature.setKeepCluster(goodCluster);
            //set all other clusters as bad
            Set<A_Location> locsForFeature = feature.getLocations();
            for (A_Location f_loc : locsForFeature) {
                A_Cluster f_cluster = f_loc.getCluster();
                if (!f_cluster.equals(goodCluster)) {
                    //then set as bad
                    setBad(f_cluster);
                }
            }
        }
    }

    private void setBad(A_Cluster cluster) {
        //remove good cluster from unused
        clusters_unused.remove(cluster);
        clusters_eliminated.add(cluster);
        cluster.setIsTrash(true);
    }

    private void setAmbiguous(A_Cluster ambigCluster) {
        clusters_unused.remove(ambigCluster);
        clusters_set.remove(ambigCluster);
        clusters_eliminated.remove(ambigCluster);
        ambigCluster.setAmbiguousFlag(true);

        //now set this cluster for all the features in the cluster
        for (A_Feature feature : ambigCluster.getFeatureMap().keySet()) {
            feature.setKeepCluster(null);
            //set all other clusters as ambig
            Set<A_Location> locsForFeature = feature.getLocations();
            for (A_Location f_loc : locsForFeature) {
                A_Cluster f_cluster = f_loc.getCluster();
                clusters_unused.remove(f_cluster);
                clusters_set.remove(f_cluster);
                clusters_eliminated.remove(f_cluster);
                f_cluster.setAmbiguousFlag(true);
            }
        }
    }

    private Set difference(Set x, Set y) {
        Set z = new HashSet(x);
        z.removeAll(y);

        return z;
    }

    private Set intersection(Set x, Set y) {
        Set z = new HashSet(x);
        z.retainAll(y);

        return z;
    }

    protected void readFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            int i = 0;
            ArrayList<String> header;
            while ((line = reader.readLine()) != null) {
                String[] ss = line.split("\t");
                if (i == 0) {//read header
                    header = new ArrayList<String>(ss.length);
                    for (int j = 0; j < ss.length; j++) {
                        header.add(ss[j]);
                        headerMap.put(ss[j], j);
                    }
                    i++;
                } else {
                    ArrayList<String> criteria = new ArrayList<String>(ss.length);
                    criteria.addAll(Arrays.asList(ss));
                    String f_id = criteria.get(headerMap.get("Feature"));
                    String l_id = criteria.get(headerMap.get("Location"));
                    String c_id = criteria.get(headerMap.get("FeatureCluster"));

                    A_Feature f = featureMap.get(f_id);
                    if (f == null) {
                        f = new A_Feature(f_id);
                        featureMap.put(f_id, f);
                    }
                    A_Cluster c = clusterMap.get(c_id);
                    if (c == null) {
                        c = new A_Cluster(c_id);
                        clusterMap.put(c_id, c);
                    }
                    A_Location l = new A_Location(l_id, f, c, criteria);
                    f.addLocation(l);
                    c.addLocation(l);
                }
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static class A_Feature {

        private String id;
        //Set<A_Cluster> clusters;
        private Set<A_Location> locations = new HashSet<A_Location>();
        private A_Location keepLocation = null;
        private A_Cluster keepCluster = null;

        public A_Feature(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public A_Cluster getKeepCluster() {
            return keepCluster;
        }

        public void setKeepCluster(A_Cluster keepCluster) {
            this.keepCluster = keepCluster;
        }

        public A_Location getKeepLocation() {
            return keepLocation;
        }

        public void setKeepLocation(A_Location keepLocation) {
            this.keepLocation = keepLocation;
        }

        public Set<A_Location> getLocations() {
            return locations;
        }

        public void setLocations(Set<A_Location> locations) {
            this.locations = locations;
        }

        public void addLocation(A_Location location) {
            locations.add(location);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final A_Feature other = (A_Feature) obj;
            if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 97 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }
    }

    protected static class A_Cluster {

        private String id;
        private Set<A_Location> locations = new HashSet<A_Location>();
        private boolean meetsCriteria = false;
        private Map<A_Feature, List<A_Location>> featureMap = new HashMap<A_Feature, List<A_Location>>();
        private boolean keepFlag = false;
        private boolean ambiguousFlag = false;
        private boolean isTrash = false;
        private int rank = 0;

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public A_Cluster(String id) {
            this.id = id;
        }

        public boolean isMeetsCriteria() {
            return meetsCriteria;
        }

        public void setMeetsCriteria(boolean meetsCriteria) {
            this.meetsCriteria = meetsCriteria;
        }

        public boolean isAmbiguousFlag() {
            return ambiguousFlag;
        }

        public void setAmbiguousFlag(boolean ambiguousFlag) {
            this.ambiguousFlag = ambiguousFlag;
        }

        public Map<A_Feature, List<A_Location>> getFeatureMap() {
            return featureMap;
        }

        public void setFeatureMap(Map<A_Feature, List<A_Location>> featureMap) {
            this.featureMap = featureMap;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isKeepFlag() {
            return keepFlag;
        }

        public void setKeepFlag(boolean keepFlag) {
            this.keepFlag = keepFlag;
        }

        public Set<A_Location> getLocations() {
            return locations;
        }

        public void setLocations(Set<A_Location> locations) {
            this.locations = locations;
        }

        public void addLocation(A_Location l) {
            locations.add(l);
            List<A_Location> locsByFeature = featureMap.get(l.getFeature());
            if (locsByFeature == null) {
                locsByFeature = new LinkedList<A_Location>();
                featureMap.put(l.getFeature(), locsByFeature);
            }
            locsByFeature.add(l);
        }

        public boolean isIsTrash() {
            return isTrash;
        }

        public void setIsTrash(boolean isTrash) {
            this.isTrash = isTrash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final A_Cluster other = (A_Cluster) obj;
            if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 11 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }
    }

    protected static class A_Location {

        private String id;
        private A_Feature feature;
        private A_Cluster cluster;
        private ArrayList<String> criteria;
        private List<String> evidence;
        private boolean isKept = false;
        private boolean isTrash = false;
        private int rank = 0;

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public A_Location(String id, A_Feature feature, A_Cluster cluster, ArrayList<String> criteria) {
            this.id = id;
            this.feature = feature;
            this.cluster = cluster;
            this.criteria = criteria;
        }

        public A_Cluster getCluster() {
            return cluster;
        }

        public void setCluster(A_Cluster cluster) {
            this.cluster = cluster;
        }

        public ArrayList<String> getCriteria() {
            return criteria;
        }

        public void setCriteria(ArrayList<String> criteria) {
            this.criteria = criteria;
        }

        public A_Feature getFeature() {
            return feature;
        }

        public void setFeature(A_Feature feature) {
            this.feature = feature;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isIsKept() {
            return isKept;
        }

        public void setIsKept(boolean isKept) {
            this.isKept = isKept;
        }

        public boolean isIsTrash() {
            return isTrash;
        }

        public void setIsTrash(boolean isTrash) {
            this.isTrash = isTrash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final A_Location other = (A_Location) obj;
            if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 83 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }
    }
}
