/*
 * Class to convert MugsyAnnotator output into cluster file and annotation file
 */
package edu.uncc.genosets.bioio;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aacain
 */
public class MugsyAnnotator {

    private final File inFile;
    private final File clusterFile;
    private final File annoFile;
    private final boolean includeNew;
    private boolean parsed = false;
    private List<Cluster> clusters;
    private List<Location> singletons;
    private List<NewOrf> newOrfs;

    public MugsyAnnotator(File inFile, File clusterFile, File annoFile, boolean includeNew) {
        this.inFile = inFile;
        this.clusterFile = clusterFile;
        this.annoFile = annoFile;
        this.includeNew = includeNew;
    }

    public void readInput() throws IOException {
        if (!parsed) {
            parsed = true;
            clusters = new LinkedList<Cluster>();
            singletons = new LinkedList<Location>();
            newOrfs = new LinkedList<NewOrf>();
            BufferedReader is = null;
            boolean doneReading = false;
            try {
                is = new BufferedReader(new FileReader(inFile));
                String line = null;
                Cluster currentCluster = null;
                while (!doneReading && (line = is.readLine()) != null) {
                    if (line.startsWith(">CLUSTER_")) {
                        currentCluster = getCluster(line);
                        clusters.add(currentCluster);
                    } else if (line.startsWith("#NEWORF")) {
                        NewOrf orf = getNewOrf(line);
                        newOrfs.add(orf);
                    } else if (line.startsWith("#SINGLETON")) {
                        singletons.add(addSingleton(line));
                    } else if (line.startsWith("#") || line.equals("")) {
                        //do nothing
                    } else if (line.startsWith("Class legend")) {
                        //done reading
                        doneReading = true;
                    } else {
                        //we are adding a location
                        addLocation(currentCluster, line);
                    }
                }
            } finally {
                is.close();
            }
        }
    }

    private HashMap<String, List<NewOrf>> sortNewOrfs() {
        HashMap<String, List<NewOrf>> map = new HashMap<String, List<NewOrf>>();
        for (NewOrf newOrf : newOrfs) {
            List<NewOrf> get;
            String key;
            if (newOrf.strand.equals("-")) {
                key = newOrf.getAssUnitId() + "_" + newOrf.getMin();
            } else {
                key = newOrf.getAssUnitId() + "_" + newOrf.getMax();
            }
            get = map.get(key);
            if (get == null) {
                get = new LinkedList<NewOrf>();
                map.put(key, get);
            }
            get.add(newOrf);
        }
        Comparator<NewOrf> c = new Comparator<NewOrf>() {

            @Override
            public int compare(NewOrf o1, NewOrf o2) {
                //reversed to sort descending
                int o1Val = o2.getLength();
                int o2Val = o1.getLength();
                return (o1Val < o2Val ? -1 : (o1Val == o2Val ? 0 : 1));
            }
        };
        for (List<NewOrf> list : map.values()) {
            Collections.sort(list, c);
        }
        return map;
    }

    private Cluster getCluster(String line) {
        String[] ss = line.split(" ");
        String[] numSeqs = ss[1].split("=");
        String[] numGenes = ss[2].split("=");
        String[] cc = ss[3].split("=");
        String[] classes = cc[1].split(";");
        Cluster cluster = new Cluster();
        cluster.setClusterId(ss[0].substring(1));
        cluster.setNumSeq(Integer.parseInt(numSeqs[1]));
        cluster.setNumGenes(Integer.parseInt(numGenes[1]));
        cluster.setClassification(Arrays.asList(classes));
        return cluster;
    }

    private NewOrf getNewOrf(String line) throws IOException {
        try {
            String[] ss = line.split(" ");
            NewOrf orf = new NewOrf();
            orf.setAssUnitId(ss[1]);
            String[] xx = ss[2].split(",");
            orf.setMin(xx[0]);
            orf.setMax(xx[1]);
            orf.setStrand(xx[3]);
            orf.setLength(Integer.parseInt(xx[2]));
            return orf;
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    private Location addSingleton(String line) {
        String[] ss = line.split(" ");
        Location loc = new Location();
        loc.setLocationId(ss[1]);
        return loc;
    }

    private void addLocation(Cluster cluster, String line) {
        String[] ss = line.split("\\t");
        String[] ll = ss[0].split(",");
        String[] strands = ss[7].split(",");
        String[] pp = ss[6].split("-");
        List<Location> locs = new ArrayList<Location>(ll.length);
        for (int i = 0; i < ll.length; i++) {
            Location l = new Location();
            l.setLocationId(ll[i]);
            l.setStrand(strands[i]);
            locs.add(l);
        }
        Gene gene = new Gene();
        gene.setMin(pp[0]);
        gene.setMax(pp[1]);
        gene.setLocations(locs);
        cluster.addGene(gene);
    }

    public void mugsy2cluster() throws IOException {
        if (!parsed) {
            readInput();
        }
        if (clusterFile.exists()) {
            clusterFile.delete();
        }
        clusterFile.createNewFile();
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(new FileWriter(clusterFile));
            for (Cluster cluster : clusters) {
                wr.append(cluster.clusterId).append(":");
                for (Gene gene : cluster.getGenes()) {
                    for (Location location : gene.getLocations()) {
                        wr.append(" xxxx|");
                        String[] ss = location.getLocationId().split("_");
                        if (ss.length == 2) {
                            wr.append(ss[1]);
                        } else {
                            wr.append(location.getLocationId());
                        }
                    }
                }
                wr.newLine();
            }
            //now write all singletons
            int index = 0;
            for (Location location : singletons) {
                String[] ss = location.getLocationId().split("_");
                if (ss.length == 2) {
                    wr.append("SINGLETON_" + index++).append(": xxxx|").append(ss[1]);
                }else{
                     wr.append("SINGLETON_" + index++).append(": xxxx|").append(location.getLocationId());
                }
                wr.newLine();
            }
        } finally {
            wr.close();
        }
    }

    public void mugsy2annotation() throws IOException {
        if (!parsed) {
            readInput();
        }
    }

    public static void printUsage() {
        System.out.println("Usage:");
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        String inName = null;
        String annoName = null;
        String clusterName = null;
        boolean includeNew = false;
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-c") || args[i].equals("--cluster")) {
                    i = i + 1;
                    if (i < args.length && !args[i].startsWith("-")) {
                        clusterName = args[i];
                    } else {
                        System.out.println("Invalid usage of " + args[i - 1]);
                        printUsage();
                        System.exit(-1);
                    }
                } else if (args[i].equals("-a") || args[i].equals("--anno")) {
                    i = i + 1;
                    if (i < args.length && !args[i].startsWith("-")) {
                        annoName = args[i];
                    } else {
                        System.out.println("Invalid usage of " + args[i - 1]);
                        printUsage();
                        System.exit(-1);
                    }
                } else if (args[i].equals("-i") || args[i].equals("--in")) {
                    i = i + 1;
                    if (i < args.length && !args[i].startsWith("-")) {
                        inName = args[i];
                    } else {
                        System.out.println("Invalid usage of " + args[i - 1]);
                        printUsage();
                        System.exit(-1);
                    }
                } else if (args[i].equals("-n") || args[i].equals("--new")) {
                    includeNew = true;
                }
            }
            if (inName == null) {
                System.out.println("No input file specified.");
                printUsage();
                System.exit(-1);
            }
            File inFile = new File(inName);
            File annoFile = null;
            File clusterFile = null;
            if (annoName != null) {
                annoFile = new File(annoName);
            }
            if (clusterName != null) {
                clusterFile = new File(clusterName);
            }
            MugsyAnnotator parser = new MugsyAnnotator(inFile, clusterFile, annoFile, includeNew);
            try {
                parser.readInput();
                parser.sortNewOrfs();
            } catch (IOException ex) {
                Logger.getLogger(MugsyAnnotator.class.getName()).log(Level.SEVERE, "Could not read input.", ex);
                System.out.println("Could not read input.");
                System.exit(-1);
            }
            if (annoFile != null) {
                try {
                    parser.mugsy2annotation();
                } catch (IOException ex) {
                    Logger.getLogger(MugsyAnnotator.class.getName()).log(Level.SEVERE, "Could not write annotation file.", ex);
                    System.out.println("Could not write annotation.");
                    System.exit(-1);
                }
            }
            if (clusterFile != null) {
                try {
                    parser.mugsy2cluster();
                } catch (IOException ex) {
                    Logger.getLogger(MugsyAnnotator.class.getName()).log(Level.SEVERE, "Could not write cluster file.", ex);
                    System.out.println("Could not write cluster file.");
                    System.exit(-1);
                }
            }
        } else {
            printUsage();
        }
    }

    private static class Cluster {

        String clusterId;
        int numSeq;
        int numGenes;
        List<String> classification = new LinkedList<String>();
        List<Gene> genes;

        public List<String> getClassification() {
            return classification;
        }

        public void setClassification(List<String> classification) {
            this.classification = classification;
        }

        public String getClusterId() {
            return clusterId;
        }

        public void setClusterId(String clusterId) {
            this.clusterId = clusterId;
        }

        public List<Gene> getGenes() {
            return genes;
        }

        public void addGene(Gene gene) {
            if (genes == null) {
                genes = new LinkedList<Gene>();
            }
            genes.add(gene);
        }

        public int getNumGenes() {
            return numGenes;
        }

        public void setNumGenes(int numGenes) {
            this.numGenes = numGenes;
        }

        public int getNumSeq() {
            return numSeq;
        }

        public void setNumSeq(int numSeq) {
            this.numSeq = numSeq;
        }
    }

    private static class Gene {

        List<Location> locations;
        String min;
        String max;
        String details;

        public List<Location> getLocations() {
            return locations;
        }

        public void setLocations(List<Location> locations) {
            this.locations = locations;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }
    }

    private static class Location {

        String locationId;
        String strand;

        public String getLocationId() {
            return locationId;
        }

        public void setLocationId(String locationId) {
            this.locationId = locationId;
        }

        public String getStrand() {
            return strand;
        }

        public void setStrand(String strand) {
            this.strand = strand;
        }
    }

    private static class NewOrf {

        String assUnitId;
        String min;
        String max;
        String strand;
        int length;

        public String getAssUnitId() {
            return assUnitId;
        }

        public void setAssUnitId(String assUnitId) {
            this.assUnitId = assUnitId;
        }

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getMin() {
            return min;
        }

        public void setMin(String min) {
            this.min = min;
        }

        public String getStrand() {
            return strand;
        }

        public void setStrand(String strand) {
            this.strand = strand;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }
    }
}
