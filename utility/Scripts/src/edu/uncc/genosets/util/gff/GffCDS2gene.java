/*
 * Converts the 
 * 
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

package edu.uncc.genosets.util.gff;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aacain
 */
public class GffCDS2gene {
    /**
     * 
     * @param input.gff output.gff
     */
    public static void main(String[] args){
        File infile = new File(args[0]);
        File outfile = new File(args[1]);
        if(infile.exists()){
            BufferedReader reader = null;
            BufferedWriter writer = null;
            try {
                HashMap<String, List<String[]>> sources = new HashMap<String, List<String[]>>();
                 reader = new BufferedReader(new FileReader(infile));
                String line = null;
                while((line = reader.readLine()) != null){
                    if(!line.startsWith("#")){
                        String ss[] = line.split("\t");
                        List<String[]> list = sources.get(ss[0]);
                        if(list == null){
                            list = new LinkedList<String[]>();
                            sources.put(ss[0], list);
                        }
                        list.add(ss);
                    }
                }//done reading file
                //print out new file  
                if(outfile.exists()){
                    outfile.delete();
                }
                outfile.createNewFile();
                writer = new BufferedWriter(new FileWriter(outfile));
                for (Map.Entry<String, List<String[]>> entry : sources.entrySet()) {
                    writer.write("##gff-version 3");
                    writer.newLine();
                    writer.write("##Type DNA " + entry.getKey());
                    writer.newLine();
                    for (String[] value : entry.getValue()) {
                        for (int i = 0; i < value.length; i++) {
                            String word = value[i];
                            if(i == 2 && value[i].equals("CDS")){
                                word = "gene";
                            }
                            writer.write(word);
                            if(i < value.length - 1){
                                writer.write('\t');
                            }
                        }
                        writer.newLine();
                    }
                    writer.write("###");
                    writer.newLine();
                }
            } catch (IOException ex) {              
               ex.printStackTrace(System.err);
            }finally{
                try {
                    reader.close();
                    writer.close();
                } catch (IOException ex) {
                     ex.printStackTrace(System.err);
                }
            }
        }else{
            System.exit(1);
        }
    }  

}
