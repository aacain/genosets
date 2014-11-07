/*
 */
package edu.uncc.genosets.util.gff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Remove GFF3 accession prefix. If the accession id in the first column is
 * in the form sid|781146|accn|AFSX01000001 , the id will be changed to AFSX01000001.
 * This script removes the leading information and leaves just the
 * last id as the accession for the seqId and for the fasta ids.
 * 
 * @author aacain
 */
public class ConvertPatricGff {

    private final File file;

    public ConvertPatricGff(File file) {
        this.file = file;
    }

    /**
     * Removes leading identifier from seqId column
     *
     * @param outputfile
     */
    public void convert(File outputfile) {
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new FileReader(file));
            try {
                outputfile.createNewFile();
                bw = new BufferedWriter(new FileWriter(outputfile));
                String line = null;
                boolean onFasta = Boolean.FALSE;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(">")) {
                        onFasta = Boolean.TRUE;
                        String longId = line.substring(1).split("[\\s]+")[0];
                        String[] longIdSplit = longId.split("\\|");
                        bw.write(">");
                        bw.write(longIdSplit[longIdSplit.length - 1]);
                        bw.newLine();
                    }
                    else if (line.startsWith("#") || onFasta  || line.equals("")) {//just write the line
                        bw.write(line);
                        bw.newLine();                       
                    }else{
                        String[] lineSplit = line.split("\\t");
                        if(lineSplit.length != 9){
                            throw new RuntimeException(new IOException("Error parsing line: " + line));
                        }
                        String[] seqIdSplit = lineSplit[0].split("\\|");
                        String newId = seqIdSplit[seqIdSplit.length-1];
                        bw.write(newId);
                        for(int i = 1; i < lineSplit.length; i++){
                            bw.write("\t");
                            bw.write(lineSplit[i]);
                        }
                        bw.newLine();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ConvertPatricGff.class.getName()).log(Level.SEVERE, "Error converting file.", ex);
            } finally {
                try {
                    br.close();
                    br = null;
                } catch (IOException ex) {
                }
                try {
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException ex) {
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConvertPatricGff.class.getName()).log(Level.SEVERE, "Could not find input file " + file.getName(), ex);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                }
            }
        }
    }
}
