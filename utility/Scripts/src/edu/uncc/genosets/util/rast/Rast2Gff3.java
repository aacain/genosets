/*
 * Copyright (C) 2014 Aurora Cain
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
package edu.uncc.genosets.util.rast;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import edu.uncc.genosets.datamanager.entity.Location;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aacain
 */
public class Rast2Gff3 {

    public void process(String inputFileName, String outputFileName) throws IOException {
        File input = new File(inputFileName);
        File outfile = new File(outputFileName);
        outfile.createNewFile();
        BufferedWriter bw = null;
        BufferedReader br = null;
        HashMap<String, List<Location>> map = new HashMap<String, List<Location>>();
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outfile)));
            br = new BufferedReader(new FileReader(input));
            String line = null;
            while ((line = br.readLine()) != null) {
                String ss[] = line.split("\t");
                if (ss.length == 3) {
                    //split the first column by periods                 
                    String[] names = ss[0].split("\\.");
                    String type = names[names.length - 2];
                    String id = names[names.length -1];
                    String function = ss[2];
                    if(type.equals("peg")){
                        type = "CDS";
                    }else{
                        type = "RNA";
                    }

                    //split the second column by underscore
                    String[] locations = ss[1].split("_");
                    if (locations.length >= 3) {
                        StringBuilder contigName = new StringBuilder();
                        Location loc = new Location();
                        loc.setEndPosition(Integer.parseInt(locations[locations.length - 1]));
                        loc.setStartPosition(Integer.parseInt(locations[locations.length - 2]));
                        if (loc.getStartPosition() > loc.getEndPosition()) {
                            loc.setIsForward(Boolean.FALSE);
                            loc.setMinPosition(loc.getEndPosition());
                            loc.setMaxPosition(loc.getStartPosition());
                        } else {
                            loc.setIsForward(Boolean.TRUE);
                            loc.setMinPosition(loc.getStartPosition());
                            loc.setMaxPosition(loc.getEndPosition());
                        }
                        for (int i = locations.length - 3; i > 0 && locations.length > 3; i--) {
                            contigName.insert(0, locations[i]);
                            contigName.insert(0, "_");
                        }
                        contigName.insert(0, locations[0]);
                        List<Location> locList = map.get(contigName.toString());
                        if(locList == null){
                            locList = new LinkedList<Location>();
                            map.put(contigName.toString(), locList);
                        }
                        locList.add(loc);
                        //write it out
                        
                        StringBuilder outline = new StringBuilder();
                        outline.append(contigName.toString()).append("\t");
                        outline.append("RAST").append("\t");
                        outline.append(type).append("\t");
                        outline.append(loc.getMinPosition()).append("\t");
                        outline.append(loc.getMaxPosition()).append("\t");
                        outline.append(".").append("\t");
                        if(loc.getIsForward()){
                            outline.append("+").append("\t");
                        }else{
                            outline.append("-").append("\t");
                        }
                        outline.append("0").append("\t");
                        outline.append("product=").append(function);
                        System.out.println(outline.toString());
                        bw.write(outline.toString());
                       } else {
                        throw new IOException("Invalid number of entries for location fild (column 2)");
                    }
                } else {
                    throw new IOException("wrong number of columns");
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Rast2Gff3.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            br.close();
        }
    }

    public static void main(String[] args) throws Exception {
        String detailedHelp = " ";
        SimpleJSAP jsap = new SimpleJSAP("Rast2GFF", detailedHelp, new Parameter[]{
            new FlaggedOption("input", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'i', "in", "input file of accessions"),
            new FlaggedOption("output", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, 'o', "out", "output file to write")
        });
        JSAPResult config = jsap.parse(args);
        if (jsap.messagePrinted()) {
            System.exit(1);
        }
        String input = config.getString("input");
        String output = config.getString("output");
        Rast2Gff3 me = new Rast2Gff3();
        me.process(input, output);
    }
}
