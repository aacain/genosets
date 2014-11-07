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
package edu.uncc.genosets.util.commandline;

import edu.uncc.genosets.util.gff.MapGff;
import edu.uncc.genosets.util.gff.ReadGff;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 *
 * @author aacain
 */
public class Main {
    private String command;
    private HashMap<String, String> params = new HashMap<String, String>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Main run = new Main();
        try {
            run.parseArgs(args);
            run.run();
        } catch (RuntimeException ex) {
            System.err.println(ex.getMessage());
            run.printHelp();
        }
    }

    private void parseArgs(String[] args) {
        for (String a : args) {
            if (a.startsWith("--")) {
                int indexOf = a.indexOf("=");
                try {
                    params.put(a.substring(0, indexOf), a.substring(indexOf + 1));
                } catch (StringIndexOutOfBoundsException ex) {
                    throw new RuntimeException("Could not read argument: " + a);
                }
            } else {
                if (isCommand(a)) {
                    this.command = a;
                } else {
                    throw new RuntimeException("Unknown command: " + a);
                }
            }
        }
        if (this.command == null) {
            throw new RuntimeException("No command specified");
        }
    }
    
    private boolean isCommand(String command) {
        return ("mapGff".equals(command));
    }
    
    private void run() throws FileNotFoundException, IOException {
        if ("mapGff".equals(command)) {
            ReadGff gff1 = new ReadGff(new FileInputStream(params.get("--in1")));
            ReadGff gff2 = new ReadGff(new FileInputStream(params.get("--in2")));
            OutputStream is = new FileOutputStream(params.get("--out"));
            MapGff gffmapper = new MapGff(gff1, gff2);
            gffmapper.map(params.get("--out"));
        }
    }

    private void printHelp() {
        System.out.println("Usage:");
        System.out.println("\tmapGff --in1=<file1> --in2=<file2> --out=<outfile>");
    }
}
