/*
 * 
 * 
 */
package edu.uncc.genosets.golegacy;

import edu.uncc.genosets.datamanager.api.DataManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author aacain
 */
public class GoLegacyLoad {

    private File file;
    private File newFile;

    public GoLegacyLoad(File file, File newFile) {
        this.file = file;
        this.newFile = newFile;
    }

    public void run() {
        DataManager mgr = DataManager.getDefault();
        if (file != null) {
            //parse file
            parseFile();
        }
    }

    private void parseFile() {
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            HashMap<String, List<String>> locToGo = new HashMap<String, List<String>>();
            while ((line = reader.readLine()) != null) {
                String[] ss = line.split("\t");
                if (ss.length >= 5) {
                    String locString = ss[5];
                    for (int i = 6; i < ss.length; i++) {
                        String string = ss[i].trim();
                        if (string.startsWith("GO:")) {
                            List<String> goList = locToGo.get(locString);
                            if (goList == null) {
                                goList = new LinkedList<String>();
                                locToGo.put(locString, goList);
                            }
                            goList.add(string);
                        }
                    }
                }
            }

            //not create sql statement
            newFile.createNewFile();

            writer = new BufferedWriter(new FileWriter(newFile));
            String newLine = System.getProperty("line.separator");
            for (Entry<String, List<String>> entry : locToGo.entrySet()) {
                String locId = entry.getKey();
                for (String go : entry.getValue()) {
                    writer.write(locId + "\t" + go);
                    writer.write(newLine);
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                reader.close();
                writer.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
