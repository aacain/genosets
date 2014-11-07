/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datamanager.fasta;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Stores a fasta file attributes. The beginning of the file contains header
 * items. These begin with a # character The Fasta items do not have the >
 * character in the id
 *
 * @author aacain
 */
public class Fasta {

    private List<String> header;
    private List<FastaItem> items;

    public List<String> getHeader() {
        return header;
    }

    public void setHeader(List<String> header) {
        this.header = header;
    }

    public List<? extends FastaItem> getItems() {
        return items;
    }

    public void setItems(List<FastaItem> items) {
        this.items = items;
    }

    /**
     * Reads the file and creates a new Fasta Adds each entry as a fasta item.
     * The id has the > character removed. If the file contains a header denoted
     * with # before the first fasta item is read, then that will be stored in
     * the fasta header variable Ignores lines that begin with # that are not in
     * the header portion of the file Closes the input stream when finished.
     *
     *
     * @param reader
     * @return list of Fasta.FastaItem
     * @throws IOException
     */
    public static Fasta parse(String asString) throws IOException {
        Fasta fasta = null;
        fasta = new Fasta();
        LinkedList<FastaItem> myItems = new LinkedList<FastaItem>();
        fasta.items = myItems;
        String[] lines = asString.split("\n");
        boolean onFasta = false;
        for (String line : lines) {
            if (line.startsWith(">")) {
                onFasta = true;
                FastaItem item = new FastaItem();
                item.setId(line.substring(1, line.length()));
                myItems.add(item);
            } else if (line.startsWith("#")) {
                //then we are on a comment
                if (onFasta == false) {
                    if (fasta.header == null) {
                        fasta.header = new LinkedList<String>();
                    }
                    fasta.header.add(line);
                }
            } else {
                FastaItem item = myItems.getLast();
                item.appendSequence(line.trim());
            }
        }
        return fasta;
    }

    /**
     * Reads the file and creates a new Fasta Adds each entry as a fasta item.
     * The id has the > character removed. If the file contains a header denoted
     * with # before the first fasta item is read, then that will be stored in
     * the fasta header variable Ignores lines that begin with # that are not in
     * the header portion of the file Closes the input stream when finished.
     *
     *
     * @param reader
     * @return list of Fasta.FastaItem
     * @throws IOException
     */
    public static Fasta parse(InputStream reader) throws IOException {
        BufferedReader br = null;
        Fasta fasta = null;
        try {
            br = new BufferedReader(new InputStreamReader(reader));
            fasta = new Fasta();
            LinkedList<FastaItem> myItems = new LinkedList<FastaItem>();
            fasta.items = myItems;
            String line = null;
            boolean onFasta = false;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(">")) {
                    onFasta = true;
                    FastaItem item = new FastaItem();
                    item.setId(line.substring(1, line.length()));
                    myItems.add(item);
                } else if (line.startsWith("#")) {
                    //then we are on a comment
                    if (onFasta == false) {
                        if (fasta.header == null) {
                            fasta.header = new LinkedList<String>();
                        }
                        fasta.header.add(line);
                    }
                } else {
                    FastaItem item = myItems.getLast();
                    item.appendSequence(line.trim());
                }
            }
        } finally {
            br.close();
        }
        return fasta;
    }

    /**
     * Writes fasta to the output stream. The header items will be written to
     * the beginning of the file. If the header lines do not start with a #,
     * then one will be added. The id line will be in the format
     * >idPrefix|fastaItemId. All sequence lines will be no more than 60
     * characters in length.
     *
     * Closes the output stream when finished.
     *
     * @param out
     * @param fasta
     * @param idPrefix or null if no prefix is to be added
     * @throws IOException
     */
    public static void createFasta(OutputStream out, Fasta fasta, final String idPrefix) throws IOException {
        int allTotal = 0;
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new OutputStreamWriter(out));
            if (fasta.header != null) {
                for (String string : fasta.header) {
                    if (!string.startsWith("#")) {
                        br.write('#');
                    }
                    br.write(string);
                    br.newLine();
                }
            }
            if (fasta.items != null) {
                for (FastaItem fastaItem : fasta.items) {
                    br.write(">");
                    if (idPrefix != null) {
                        br.write(idPrefix);
                        br.write("|");
                    }
                    br.write(fastaItem.getId());
                    br.newLine();
                    StringBuilder bldr = new StringBuilder(fastaItem.getSequence());
                    int running = 0;
                    for (int i = 0; i < bldr.length(); i = i + 60) {
                        String line = bldr.substring(i, (i + 60) < bldr.length() ? (i + 60) : bldr.length());
                        running = running + line.length();
                        br.write(line);
                        br.newLine();
                    }
                    allTotal = allTotal + running;
                }
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            br.close();
        }
    }

    /**
     * Writes fasta to the file. The header items will be written to the
     * beginning of the file. If the header lines do not start with a #, then
     * one will be added. The id line will be in the format
     * >idPrefix|fastaItemId. All sequence lines will be no more than 60
     * characters in length.
     *
     *
     * @param file to write to
     * @param appendFile - if output should be appended to the file.
     * @param fasta
     * @param idPrefix or null if no prefix is to be added
     * @throws IOException
     */
    public static void createFasta(File file, boolean appendFile, Fasta fasta, final String idPrefix) throws IOException {
        int allTotal = 0;
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new FileWriter(file, appendFile));
            if (fasta.header != null) {
                for (String string : fasta.header) {
                    if (!string.startsWith("#")) {
                        br.write('#');
                    }
                    br.write(string);
                    br.newLine();
                }
            }
            if (fasta.items != null) {
                for (FastaItem fastaItem : fasta.items) {
                    br.write(">");
                    if (idPrefix != null) {
                        br.write(idPrefix);
                        br.write("|");
                    }
                    br.write(fastaItem.getId());
                    br.newLine();
                    StringBuilder bldr = new StringBuilder(fastaItem.getSequence());
                    int running = 0;
                    for (int i = 0; i < bldr.length(); i = i + 60) {
                        String line = bldr.substring(i, (i + 60) < bldr.length() ? (i + 60) : bldr.length());
                        running = running + line.length();
                        br.write(line);
                        br.newLine();
                    }
                    allTotal = allTotal + running;
                }
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public static class FastaItem {

        private String id;
        private StringBuilder sequence;

        public FastaItem() {
        }

        public FastaItem(String id, String sequence) {
            this.id = id;
            this.sequence = new StringBuilder(sequence);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSequence() {
            return sequence.toString();
        }

        public void setSequence(String sequence) {
            this.sequence = new StringBuilder(sequence);
        }

        public void appendSequence(String sequence) {
            if (this.sequence == null) {
                this.sequence = new StringBuilder();
            }
            this.sequence.append(sequence);
        }
    }
}
