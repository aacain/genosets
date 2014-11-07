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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author aacain
 */
public class ConvertText {

    public static void main(String[] args) {
        Option help = new Option("h", "help", false, "print this message");
        Options options1 = new Options();
        options1.addOption(help);

        Options options2 = new Options();
        options2.addOption(OptionBuilder.withArgName("file")
                .isRequired()
                .hasArg()
                .withDescription("input file")
                .withLongOpt("in")
                .create("i"));
        OptionGroup typeGroup = new OptionGroup();
        typeGroup.addOption(new Option("u", "unix", false, "(Default) Convert to Unix format."));
        typeGroup.addOption(new Option("d", "dos", false, "Convert to DOS format."));
        typeGroup.addOption(new Option("m", "mac", false, "Convert to MAC format."));
        options2.addOptionGroup(typeGroup);

        CommandLineParser parser = new GnuParser();
        ConvertText me = new ConvertText();
        try {
            CommandLine c1 = parser.parse(options1, args);
            if (c1.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("convertText", "", options2, "Use '-h' or '--help' to print this message", true);
            } else {
                me.run(args, options2);
            }
        } catch (ParseException ex) {
            me.run(args, options2);
        }
    }

    private void run(String[] args, Options options2) {
        try {
            CommandLineParser parser = new GnuParser();
            CommandLine c1 = parser.parse(options2, args);
            String infilename = c1.getOptionValue("in");
            File infile = new File(infilename);
            String lineSeparator = "\n";
            if(c1.hasOption("d")){
                lineSeparator = "\r\n";
            }else if(c1.hasOption("m")){
                lineSeparator = "\r";
            }
            convert(infile, lineSeparator);
        } catch (ParseException ex1) {
            System.err.println("Unexpected exception:" + ex1.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("convertText", "", options2, "Use '-h' or '--help' to print this message", true);
        }
    }

    private static void convert(File infile, String lineSeparator) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(infile));
            String line = null;
            try {
                while ((line = br.readLine()) != null) {
                    System.out.print(line + lineSeparator);
                }
            } catch (IOException ex) {
                System.err.println();
                Logger.getLogger(ConvertText.class.getName()).log(Level.SEVERE, "Could not read file. " + infile.getPath(), ex);
            }finally{
                try {
                    br.close();
                } catch (IOException ex) {
                    
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConvertText.class.getName()).log(Level.SEVERE, "File not found. " + infile.getPath(), ex);
        }
    }
}
