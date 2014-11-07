/*
 * This script looks up the accession ranges given the GenBank Accession or
 * GenBank Accession prefix.  This will lookup both completed projects
 * and assemblies.  The GenBank accessions are either 6, 12, or 8 characters long.
 * If 6 or 12 characters, it is a sequencing project.  If given 6 or 12 character
 * accession, this will lookup the master sequencing project and return the
 * accession ranges for that project.
 * 
 * If given 8 character accession, this is a complete sequence (i.e. the same
 * accession).  
 * 
 * Each line may contain multiple accessions, seperated by a ','.  In this case
 * each looked up accession will be output as a new line.
 * 
 * 
 * Example input:
 * ANIK01
 * ACJJ00000000
 * CP000777,CP000778,CP000779
 * CP000348,CP000349
 * ANMU01
 * AKWJ02
 * 
 * Example output:
 * ANIK01000001-ANIK01000125
 * ACJJ01000001-ACJJ01000045
 * CP000777
 * CP000778
 * CP000779
 * CP000348
 * CP000349
 * ANMU01000001-ANMU01000196
 * AKWJ02000001-AKWJ02000039
 * 
 * Copyright (C) 2013 Aurora Cain
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
package edu.uncc.genosets.util;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.Switch;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author aacain
 */
public class GenBankAccessionLookup {

    private String outfileName;
    private String infileName;
    private static final String CON = "CON";
    private static final String WGS = "WGS";
    private static final String STD = "STD";

    public GenBankAccessionLookup() {
    }

    public GenBankAccessionLookup(String input, String output) {
        this.outfileName = output;
        this.infileName = input;
    }

//    public List<String> getAllAccessions(final String accession) throws IOException {
//        List<String> list = new LinkedList<String>();
//        //see if accession contains commas
//        String[] split = accession.split(",");
//        for (String acc : split) {
//            if (acc.length() == 6 || acc.length() == 12) {//must be ena project
//                String updatedAccession = acc.length() == 12 ? acc : acc.concat("000000");
//                String shortAccession = acc.substring(0, 6);
//                String urlString = "http://www.ebi.ac.uk/ena/data/view/" + updatedAccession + "&display=xml";
//                URL url = new URL(urlString);
//                try {
//                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//                    Document document = builder.parse(url.openStream());
//                    XPath xpath = XPathFactory.newInstance().newXPath();
//                    Node node = null;
//                    if (!wgsOnly) {
//                        node = (Node) xpath.evaluate("ROOT/entry/xref[@db='ENA-CON']/@id", document, XPathConstants.NODE);
//                    }
//                    if (node == null) {
//                        node = (Node) xpath.evaluate("ROOT/entry/xref[@db='ENA-WGS']/@id", document, XPathConstants.NODE);
//                    }
//                    Integer start, end;
//                    if (node != null) {
//                        start = parseStartRange(node.getNodeValue());
//                        end = parseEndRange(node.getNodeValue());
//                        for (int i = start; i <= end; i++) {
//                            list.add(shortAccession + String.format("%06d", i));
//                        }
//                    } else {
//                        throw new IOException("Result invalid for url " + urlString);
//                    }
//                } catch (ParserConfigurationException ex) {
//                    Logger.getLogger(GenBankAccessionLookup.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (SAXException ex) {
//                    Logger.getLogger(GenBankAccessionLookup.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (XPathExpressionException ex) {
//                    Logger.getLogger(GenBankAccessionLookup.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            } else {
//                list.add(acc);
//            }
//        }
//        return list;
//    }

    private Integer parseStartRange(String range) {
        String[] ranges = range.split("-");
        String endIndexString = ranges[0].substring(6);
        Integer end = null;
        try {
            end = Integer.parseInt(endIndexString);
        } catch (NumberFormatException ex) {
            return null;
        }
        return end;
    }

    private Integer parseEndRange(String range) {
        String[] ranges = range.split("-");
        String endIndexString = ranges[1].substring(6);
        Integer end = null;
        try {
            end = Integer.parseInt(endIndexString);
        } catch (NumberFormatException ex) {
            return null;
        }
        return end;
    }

    public HashMap<String, StringBuilder> getAccessionsRanges(final String accession) throws IOException {
        HashMap<String, StringBuilder> map = new HashMap<String, StringBuilder>();
        //see if accession contains commas
        String[] split = accession.split(",");
        for (String acc : split) {
            if (acc.length() == 6 || acc.length() == 12) {// must be WGS or CON
                String updatedAccession = acc.length() == 12 ? acc : acc.substring(0, 4).concat("000000");
                String shortAccession = acc.substring(0, 6);
                String urlString = "http://www.ebi.ac.uk/ena/data/view/" + updatedAccession + "&display=xml";
                URL url = new URL(urlString);
                try {
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = builder.parse(url.openStream());
                    XPath xpath = XPathFactory.newInstance().newXPath();
                    Node conNode = (Node) xpath.evaluate("ROOT/entry/xref[@db='ENA-CON']/@id", document, XPathConstants.NODE);
                    Node wgsNode = (Node) xpath.evaluate("ROOT/entry/xref[@db='ENA-WGS']/@id", document, XPathConstants.NODE);
                    if (conNode == null && wgsNode == null) {
                        throw new IOException("Result invalid for url " + urlString);
                    }
                    if (conNode != null) {
                        StringBuilder entry = map.get(CON);
                        if (entry == null) {
                            entry = new StringBuilder();
                        } else if (entry.length() > 0) {
                            entry.append(',');
                        }
                        entry.append(conNode.getNodeValue());
                        map.put(CON, entry);
                    }
                    if (wgsNode != null) {
                        StringBuilder entry = map.get(WGS);
                        if (entry == null) {
                            entry = new StringBuilder();
                        } else if (entry.length() > 0) {
                            entry.append(',');
                        }
                        entry.append(wgsNode.getNodeValue());
                        map.put(WGS, entry);
                    }
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(GenBankAccessionLookup.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SAXException ex) {
                    Logger.getLogger(GenBankAccessionLookup.class.getName()).log(Level.SEVERE, null, ex);
                } catch (XPathExpressionException ex) {
                    Logger.getLogger(GenBankAccessionLookup.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else { //complete project
                StringBuilder entry = map.get(STD);
                if (entry == null) {
                    entry = new StringBuilder();
                } else if (entry.length() > 0) {
                    entry.append(',');
                }
                entry.append(acc);
                map.put(STD, entry);
            }
        }
        return map;
    }

    private void readFile(String filename) throws IOException {
        File f = new File(filename);
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(f));
        String line = null;
        BufferedWriter out;
        BufferedWriter err = null;
        if (this.outfileName != null) {
            File outfile = new File(this.outfileName);
            outfile.createNewFile();
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile)));
        } else {
            out = new BufferedWriter(new OutputStreamWriter(System.out));
            err = new BufferedWriter(new OutputStreamWriter(System.err));
        }
        try {
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) {
                    try {
                        HashMap<String, StringBuilder> accessionsRanges = getAccessionsRanges(line);
                        out.write(line);
                        out.write('\t');
                        writeColumn(out, accessionsRanges, STD);
                        out.write('\t');
                        writeColumn(out, accessionsRanges, CON);
                        out.write('\t');
                        writeColumn(out, accessionsRanges, WGS);
                        out.newLine();
                    } catch (IOException ex) { //error reading accession write to error
                        if (err == null) {
                            File errfile;
                            int lastIndexOf = this.outfileName.lastIndexOf('.');
                            if (lastIndexOf < 0) {
                                lastIndexOf = this.outfileName.length();
                            } else {
                                lastIndexOf = lastIndexOf - 1;
                            }
                            errfile = new File(this.outfileName.substring(0, lastIndexOf) + ".err");
                            err = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(errfile)));
                            try {
                                errfile.createNewFile();
                            } catch (IOException ex1) {
                                err = new BufferedWriter(new OutputStreamWriter(System.err));
                            }
                        }
                        err.write(line);
                        err.newLine();
                    }
                }
            }
        } finally {
            out.close();
            if (err != null) {
                err.close();
            }
        }
    }
    
    private static void writeColumn(BufferedWriter out, HashMap<String, StringBuilder> map, String column) throws IOException{
        StringBuilder value = map.get(column);
        if(value != null){
            out.write(value.toString());
        }else{
            out.write("");
        }
    }

    public static void main(String[] args) throws Exception {
        String detailedHelp = " * This script looks up the accession ranges given the GenBank Accession or\n"
                + "GenBank Accession prefix.  This will lookup both completed projects\n"
                + "and assemblies.  The GenBank accessions are either 6, 12, or 8 characters long.\n"
                + "If 6 or 12 characters, it is a sequencing project.  If given 6 or 12 character\n"
                + "accession, this will lookup the master sequencing project and return the\n"
                + "accession ranges for that project.\n"
                + " \n"
                + " If given 8 character accession, this is a complete sequence (i.e. the same\n"
                + "accession).  \n"
                + "\n"
                + "Each line may contain multiple accessions, seperated by a ','.  In this case\n"
                + "each looked up accession will be output as a new line.\n"
                + "\n"
                + "\n"
                + "Example input:\n"
                + "ANIK01\n"
                + "ACJJ00000000\n"
                + "CP000777,CP000778,CP000779\n"
                + "CP000348,CP000349\n"
                + "ANMU01\n"
                + "AKWJ02\n"
                + "\n"
                + "Example output:\n"
                + "ANIK01000001-ANIK01000125\n"
                + "ACJJ01000001-ACJJ01000045\n"
                + "CP000777\n"
                + "CP000778\n"
                + "CP000779\n"
                + "CP000348\n"
                + "CP000349\n"
                + "ANMU01000001-ANMU01000196";
        SimpleJSAP jsap = new SimpleJSAP("ToGenBank", detailedHelp, new Parameter[]{
            new FlaggedOption("input", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'i', "in", "input file of accessions"),
            new FlaggedOption("output", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'o', "out", "output file to write, if accessions cannot be "
            + "found, a file with the same name and .err extension will"
            + " be created. ")
        });
        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) {
            System.exit(1);
        }
        String input = config.getString("input");
        String output = config.getString("output");
        GenBankAccessionLookup lookup = new GenBankAccessionLookup(input, output);
        lookup.readFile(input);
    }
}
