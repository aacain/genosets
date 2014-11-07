package edu.uncc.genosets.mugsyannotator;

import edu.uncc.genosets.mugsyannotator.Graph.Contig;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aacain
 */
public class MugsyParser {

    private File directory;
    private HashMap<String, String> contigToOrgMap = new HashMap<String, String>(); //contigId, organism
    private HashMap<String, Contig> contigIdToContigMap = new HashMap<String, Contig>(); //contigId, contig
    private List<Cluster> clusterList;

    public MugsyParser(File directory) {
        this.directory = directory;
    }

    private void readFastaFiles() {
        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".fna")) {
                readFastaFile(file);
            }
        }
    }

    private void readFastaFile(File file) {
        String org = file.getName().substring(0, file.getName().indexOf('.'));
        BufferedReader reader = null;
        try {
            InputStream is = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            Contig contig = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                } else if (line.startsWith(">")) {
                    contig = new Contig();
                    contig.seqId = line.substring(1);
                    contigToOrgMap.put(contig.seqId, org);
                } else {
                    contig.length = contig.length + line.length();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MugsyParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(MugsyParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void readDetailOutput() {
        File outfile = null;
        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".details")) {
                outfile = file;
            }
        }
        BufferedReader reader = null;
        try {
            clusterList = new LinkedList<Cluster>();
            InputStream is = new FileInputStream(outfile);
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            Cluster currentCluster = null;
            boolean keepGoing = true;
            List<Gene> createdOrfs = new LinkedList<Gene>();
            while ((line = reader.readLine()) != null && keepGoing) {
                if (line.startsWith(">")) { //on new cluster
                    String[] ss = line.split("\\s+");
                    currentCluster = new Cluster();
                    clusterList.add(currentCluster);
                    currentCluster.id = ss[0].substring(1);
                    currentCluster.numSeq = Integer.parseInt(ss[1].substring(ss[1].indexOf("=") + 1, ss[1].length()));
                    currentCluster.numGenes = Integer.parseInt(ss[2].substring(ss[2].indexOf("=") + 1, ss[2].length()));
                    String[] cc = ss[3].substring(ss[3].indexOf("=") + 1, ss[3].length()).split(";");
                    currentCluster.queryLocus = ss[4].split("=")[1];
                    //add created orfs
                    for (int i = 0; i < createdOrfs.size(); i++) {
                        Gene gene = createdOrfs.get(i);
                        gene.locusTags = Collections.singletonList(currentCluster.id + "_" + i);
                        String org = contigToOrgMap.get(gene.contig);
                        List<Gene> orgList = currentCluster.geneByOrgList.get(org);
                        if (orgList == null) {
                            orgList = new LinkedList<Gene>();
                            currentCluster.geneByOrgList.put(org, orgList);
                        }
                        orgList.add(gene);
                    }
                    createdOrfs = new LinkedList<Gene>();
                } else if (line.startsWith("#")) {
                    if (line.startsWith("#NEWORF")) {
                        Gene g = new Gene();
                        createdOrfs.add(g);
                        String[] ss = line.split("\\s");
                        g.contig = ss[1];
                        String[] ll = ss[2].split(",");
                        g.min = Integer.parseInt(ll[0]);
                        g.max = Integer.parseInt(ll[1]);
                        g.isPositive = ll[3].equals("+") ? true : false;
                        g.createdOrf = true;
                    }
                } else if (line.startsWith("Class legend")) {
                    keepGoing = false;
                } else if (currentCluster != null) { //must be on entry
                    String[] ss = line.split("\t");
                    if (ss.length > 1) {
                        Gene gene = new Gene();
                        gene.contig = ss[2];
                        String org = contigToOrgMap.get(gene.contig);
                        List<Gene> orgList = currentCluster.geneByOrgList.get(org);
                        if (orgList == null) {
                            orgList = new LinkedList<Gene>();
                            currentCluster.geneByOrgList.put(org, orgList);
                        }
                        orgList.add(gene);
                        gene.locusTags = Arrays.asList(ss[0].split(","));
                        for (String string : gene.locusTags) {
                            if (currentCluster.queryLocus.equals(string)) {
                                currentCluster.queryGene = gene;
                            }
                        }
                        String[] rr = ss[6].split("-");
                        gene.min = Integer.parseInt(rr[0]);
                        gene.max = Integer.parseInt(rr[1]);
                        if (ss[7].equals("+")) {
                            gene.isPositive = true;
                        }
                        String[] aa = ss[9].split(";");
                        for (String a : aa) {
                            if (a.matches("CX")) {
                                //invalid translation
                                gene.translationError = true;
                            } else if (a.matches("CE[0234]")) {
                                gene.endCode = a.charAt(2);
                            } else if (a.matches("CS[0234]")) {
                                gene.startCode = a.charAt(2);
                            } else {
                                String[] att = a.split("=");
                                if (att.length > 1) {
                                    if (att[0].equals("startcodon")) {
                                        String[] cc = att[1].split("\\.");
                                        List<String> codons = gene.startCodons.get(cc[1]);
                                        if (codons == null) {
                                            codons = new LinkedList<String>();
                                            gene.startCodons.put(cc[1], codons);
                                        }
                                        codons.add(cc[0]);
                                    } else if (att[0].equals("stopcodon")) {
                                        String[] cc = att[1].split("\\.");
                                        List<String> codons = gene.stopCodons.get(cc[1]);
                                        if (codons == null) {
                                            codons = new LinkedList<String>();
                                            gene.stopCodons.put(cc[1], codons);
                                        }
                                        codons.add(cc[0]);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MugsyParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(MugsyParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        MugsyParser parser = new MugsyParser(new File(args[0]));
        parser.readFastaFiles();
        parser.readDetailOutput();
    }

    public List<Cluster> run() {
        readFastaFiles();
        readDetailOutput();
        return clusterList;
    }

    public void createClusterFile(OutputStream out) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(out));
            for (Cluster cluster : clusterList) {
                for (List<Gene> genes : cluster.geneByOrgList.values()) {
                    for (Gene gene : genes) {
                        for (String locusTag : gene.locusTags) {
                            writer.write(cluster.id);
                            writer.write("\t");
                            writer.write(locusTag);
                            writer.write("\t");
                            writer.write(gene.createdOrf ? "TRUE" : "FALSE");
                            writer.newLine();
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(MugsyParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(MugsyParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static class Cluster {

        String id;
        int numSeq;
        int numGenes;
        String queryLocus;
        Gene queryGene;
        HashMap<String, List<Gene>> geneByOrgList = new HashMap<String, List<Gene>>(); //organism, geneList
    }

    public static class Gene {

        List<String> locusTags;
        String contig;
        int min;
        int max;
        boolean isPositive;
        boolean createdOrf = false;
        HashMap<String, List<String>> startCodons = new HashMap<String, List<String>>(); //WGA_id, List position
        HashMap<String, List<String>> stopCodons = new HashMap<String, List<String>>(); //WGA_id, List position
        char startCode = '1';
        char endCode = '1';
        boolean translationError = false;
    }
}
