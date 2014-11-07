/*
 * 
 * 
 */
package edu.uncc.genosets.embl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class AccessionLookup {

    private static int notFound = 0;
    private static int found = 0;
    private static Set<String> notFoundSet = new HashSet<String>();

    public static Map<String, String> lookup(String gbAccession) {
        Map<String, String> map = null;
        try {
            URL u = new URL("http://www.uniprot.org/uniprot/?query=" + gbAccession + "&format=tab");
            try {
                // Connect
                HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
                BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                //read the header line
                String line = rd.readLine();
                if (line != null) {
                    String[] header = line.split("\t");
                    //get second line
                    line = rd.readLine();
                    if (line != null) {
                        found++;
                        map = new HashMap<String, String>();
                        String[] values = line.split("\t");
                        for (int i = 0; i < values.length; i++) {
                            map.put(header[i], values[i]);
                        }
                    } else {
                        notFoundSet.add(gbAccession);
                        notFound++;
                        System.out.println("No line result for " + gbAccession);
                    }
                } else { //header line is null
                    notFoundSet.add(gbAccession);
                    notFound++;
                    System.out.println("No header result for " + gbAccession);
                }
                rd.close();

                return map;
            } catch (IOException ex) {
                ex.printStackTrace();
                return map;
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return map;
        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        FileInputStream fstream = new FileInputStream("/Users/shataviamorrison/Desktop/e1.proteinID.txt");
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        int count = 0;



        Set<String> featureSet = new HashSet<String>();
        while ((strLine = br.readLine()) != null) {
            String[] ss = strLine.split("\t");
            count = count + 1;
            if (ss.length == 1) {
                //   System.out.print("@@@@@@@@@@@@@@@@@@@@@@@@@@"+count+"\n");
                Map<String, String> lookup = AccessionLookup.lookup(ss[0]);
                if (lookup.isEmpty()) {
                    featureSet.add(ss[1]);
                } else {

                    //   Map<String, String> lookup = AccessionTest.lookup("YP_001501125.1");
                    if (lookup != null) {

                        //  writer.write(count+"\t"+strLine+"\t"+lookup.get("Accession")+"\t"+lookup.get("Protein names")+"\t"+lookup.get("Organism")+"\t"+lookup.get("Gene names"));
                        //    System.out.println(count+"\t"+strLine+"\t"+lookup.get("Accession")+"\t"+lookup.get("Protein names")+"\t"+lookup.get("Organism")+"\t"+lookup.get("Gene names"));
                        System.out.println(ss[1] + "\t" + lookup.get("Accession") + "\t" + lookup.get("Protein names") + "\t" + count);
                        // System.out.println(count);
                        //       System.out.println(lookup.get("Accession"));//take this
                        //      System.out.println(lookup.get("Entry name"));
                        //     System.out.println(lookup.get("Status"));
                        //    System.out.println(lookup.get("Protein names"));//take this
                        //   System.out.println(lookup.get("Gene names"));
                        //  System.out.println(lookup.get("Organism"));
                        // System.out.println(lookup.get("Length"));
                    }

                }//end reading input lines
            }else{
                System.out.println("Error: not enough legumes in the beanstock !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            }
        }
        System.out.println("Found total " + found);
        System.out.println("Not found total " + notFound);

        System.out.println("List of not founds: ");
        for (String string : notFoundSet) {
            System.out.println(string);
        }

        System.out.println("List of feature id's not found: ");
        for (String string : featureSet) {
            System.out.println(string);
        }
    }
}
