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
package edu.uncc.genosets.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *  Creates orthomcl script
 * 
 * 
 * @author aacain
 */
public class OrthoMCL {

    private static final String SIM_SEQUENCES = "similarSequences.txt";
    private static final String CLEANUP = "no";
    private static final String PREFIX = "orthomcl";
    private static final String START_INDEX = "1000";

//    
//orthomclInstallSchema config_file install_schema.log
//orthomclFilterFasta fasta 10 20
//orthomclBlastParser all.blast fasta > similarSequences.txt
//
//orthomclLoadBlast config_file similarSequences.txt
//orthomclPairs config_file orthomcl_pairs.log cleanup=no
//orthomclDumpPairsFiles config_file
//mcl mclInput --abc -I 1.5 -o mclOutput
//orthomclMclToGroups my_prefix 1000 < mclOutput > groups.txt
    public static void main(String[] args) {
        OrthoMCL orthomcl = new OrthoMCL();
        List<String> commands1 = orthomcl.getCommands("fasta", "config_file", "blast.out", null, null, null);
        for (String string : commands1) {
            System.out.println(string);
        }
    }
    private HashMap<String, Integer> commandMap;
    private ArrayList<String> commands;

    public List<String> getCommands(String fastaDirectory, String config_file, String blastFile, String similarSequences, String cleanup, String prefix) {

        //update to default if null
        similarSequences = similarSequences == null ? SIM_SEQUENCES : similarSequences;
        cleanup = cleanup == null ? CLEANUP : cleanup;
        prefix = prefix == null ? PREFIX : prefix;

        this.commandMap = new HashMap<String, Integer>(8);
        this.commands = new ArrayList<String>(8);
        StringBuilder bldr = new StringBuilder();
        int i = 0;

        bldr.append("orthomclInstallSchema ").append(config_file).append(" install_schema.log");
        commands.add(bldr.toString());
        commandMap.put("orthomclInstallSchema", i++);

        bldr = new StringBuilder();
        bldr.append("orthomclFilterFasta ").append(fastaDirectory).append(" ").append(10).append(" ").append(20);
        commands.add(bldr.toString());
        commandMap.put("orthomclFilterFasta", i++);

        bldr = new StringBuilder();
        bldr.append("orthomclBlastParser ").append(blastFile).append(" ").append(fastaDirectory).append(" > ").append(similarSequences);
        commands.add(bldr.toString());
        commandMap.put("orthomclBlastParser", i++);

        bldr = new StringBuilder();
        bldr.append("orthomclLoadBlast ").append(config_file).append(" ").append(similarSequences);
        commands.add(bldr.toString());
        commandMap.put("orthomclLoadBlast", i++);

        bldr = new StringBuilder();
        bldr.append("orthomclPairs ").append(config_file).append(" orthomcl_pairs.log cleanup=").append(cleanup);
        commands.add(bldr.toString());
        commandMap.put("orthomclPairs", i++);

        bldr = new StringBuilder();
        bldr.append("orthomclDumpPairsFiles ").append(config_file);
        commands.add(bldr.toString());
        commandMap.put("orthomclDumpPairsFiles", i++);

        bldr = new StringBuilder();
        bldr.append("mcl mclInput --abc -I 1.5 -o mclOutput");
        commands.add(bldr.toString());
        commandMap.put("mcl", i++);

        bldr = new StringBuilder();
        bldr.append("orthomclMclToGroups ").append(prefix).append(START_INDEX).append(" < mclOutput > groups.txt");
        commands.add(bldr.toString());
        commandMap.put("orthomclMclToGroups", i++);


        return commands;
    }

    public String createConfigFile(String database, String dbUser, String dbPassword, String percentMatchCutoff, String evalueExponentCutoff, String outputDirectory) throws IOException{

        String newLine = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        s.append("dbVendor=mysql").append(newLine)
                .append("dbConnectString=dbi:mysql:").append(database).append(";mysql_local_infile=1;").append(newLine)
                .append("dbLogin=").append(dbUser).append(newLine)
                .append("dbPassword=").append(dbPassword).append(newLine)
                .append("similarSequencesTable=SimilarSequences").append(newLine)
                .append("orthologTable=Ortholog").append(newLine)
                .append("inParalogTable=InParalog").append(newLine)
                .append("coOrthologTable=CoOrtholog").append(newLine)
                .append("interTaxonMatchView=InterTaxonMatch").append(newLine)
                .append("percentMatchCutoff=").append(percentMatchCutoff).append(newLine)
                .append("evalueExponentCutoff=").append(evalueExponentCutoff).append(newLine)
                .append("oracleIndexTblSpc=NONE").append(newLine);

        String fileName;
        if (outputDirectory == null) {
            fileName = "config_file";
        } else {
            fileName = outputDirectory + "config_file";
        }

        File outputFile = new File(fileName);
        outputFile.createNewFile();
        BufferedWriter out;
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile)));
        out.write(s.toString());
        out.flush();
        out.close();
        return s.toString();
    }
}
