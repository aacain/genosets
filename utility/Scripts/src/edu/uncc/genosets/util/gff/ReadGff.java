/*
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
package edu.uncc.genosets.util.gff;

import edu.uncc.genosets.datamanager.entity.FactDetailLocation;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.Location;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aacain
 */
public class ReadGff {

    private HashMap<String, StringBuilder> seqMapFromFile = new HashMap<String, StringBuilder>();
    private List<FeatureExt> features = new LinkedList<FeatureExt>();
    private final InputStream is;

    public ReadGff(InputStream is) {
        this.is = is;
    }
    
    public List<FeatureExt> getFeatures(){
        return this.features;
    }
    
    public HashMap<String, StringBuilder> getSequenceMap(){
        return this.seqMapFromFile;
    }

    public List<FeatureExt> parse() throws IOException {
        //now read the file
        String line = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String fastaKey = null;
        StringBuilder translation = null;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("#") && !line.equals("")) {
                if (line.startsWith(">")) {
                    String[] split = line.split("[\\s]+", 2);
                    fastaKey = split[0].substring(1);
                    translation = new StringBuilder();
                    this.seqMapFromFile.put(fastaKey, translation);
                } else if (fastaKey != null) { //we are in fasta section
                    translation.append(line);
                } else { //we are still in gff section
                    //parse gff line
                    parseGffLine(line);
                }
            }
        }
        return this.features;
    }

    private void parseGffLine(String line) {
        String[] ss = line.split("\t");
        FeatureExt f = new FeatureExt();
        features.add(f);
        f.seqId = ss[0];
        f.source = ss[1];
        f.setFeatureType(ss[2]);
        Integer min = parseInt(ss[3]);
        Integer max = parseInt(ss[4]);

        if (min != null) {
            f.loc.setMinPosition(min);
        }
        if (max != null) {
            f.loc.setMaxPosition(max);
        }
        f.score = parseFloat(ss[5]);

        if (ss[6].equals("+")) {
            f.loc.setIsForward(Boolean.TRUE);
            f.loc.setStartPosition(min);
            f.loc.setEndPosition(max);
        } else if (ss[6].equals("-")) {
            f.loc.setIsForward(Boolean.FALSE);
            f.loc.setStartPosition(max);
            f.loc.setEndPosition(min);
        }

        //parse the attributes
        String[] aa = ss[8].split(";");
        int i = 1;
        for (String string : aa) {
            boolean addAttribute = true;
            String[] kv = string.split("=");
            try {
                kv[0] = URLDecoder.decode(kv[0], "UTF-8");
                kv[1] = URLDecoder.decode(kv[1], "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(this.getClass().toString()).log(Level.SEVERE, "Error parsing Unicode", ex);
            }
            if (kv[0].equals("locus_tag")) {
                f.setPrimaryName(kv[1]);
            } else if (kv[0].equals("ID")) {
                f.gffId = kv[1];
            } else if (kv[0].equals("product")) {
                f.setProduct(kv[1]);
            } else if (kv[0].equals("Dbxref")) {
                String[] maps = kv[1].split(":");
                if (maps.length == 2) {
                    if (maps[0].equals("GenoSets")) {
                        String[] split = maps[1].split("_");
                        XrefMapping m = new XrefMapping(split[0], split[1]);
                        f.mappingsGS.add(m);
                        addAttribute = false;
                    } else {
                        XrefMapping m = new XrefMapping(maps[0], maps[1]);
                        f.mappingsExternal.add(m);
                    }
                }
            }
            if (addAttribute) {
                FactDetailLocation detail = new FactDetailLocation();
                detail.setDetailType(kv[0]);
                detail.setDetailValue(kv[1]);
                detail.setDetailOrder(i);
                f.details.add(detail);
                i++;
            }
        }
        if (f.gffId == null) {
            f.gffId = f.getPrimaryName();
        }
        if (f.getPrimaryName() == null) {
            f.setPrimaryName(f.gffId);
        }

        //add the original id as a dbxref
        if (f.gffId != null) {
            FactDetailLocation detail = new FactDetailLocation();
            detail.setDetailType("Dbxref");
            detail.setDetailValue("SOURCE:" + f.gffId);
            detail.setDetailOrder(i);
            f.details.add(detail);
            i++;
        }

        f.phase = parseInt(ss[7]);
    }

    private Integer parseInt(String s) {
        if (s.equals(".")) {
            return null;
        }
        try {
            return Integer.parseInt(s);
        } catch (Exception ex) {
            return null;
        }
    }

    private Float parseFloat(String s) {
        if (s.equals(".")) {
            return null;
        }
        try {
            return Float.parseFloat(s);
        } catch (Exception ex) {
            return null;
        }
    }

    public static class FeatureExt extends Feature {

        public String gffId;
        public String seqId;
        public String source;
        public String type;
        public Integer phase;
        public Float score;
        public Location loc = new Location();
        public List<FactDetailLocation> details = new LinkedList<FactDetailLocation>();
        public List<XrefMapping> mappingsExternal = new LinkedList<XrefMapping>();
        public List<XrefMapping> mappingsGS = new LinkedList<XrefMapping>();
    }

    private static class XrefMapping {

        private String database;
        private String xref;

        public XrefMapping(String database, String xref) {
            this.database = database;
            this.xref = xref;
        }

        public String getDatabase() {
            return database;
        }

        public String getXref() {
            return xref;
        }
    }
    
    
}
