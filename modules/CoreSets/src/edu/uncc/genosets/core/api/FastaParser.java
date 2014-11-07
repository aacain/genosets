/*
 * 
 * 
 */
package edu.uncc.genosets.core.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author aacain
 */
public class FastaParser {

    public static String IDENTIFIER = "IDENTIFIER";
    public static String SEQUENCE = "SEQUENCE";

    public List<HashMap<String, Object>> parseToMap(File file) throws IOException {
        List<HashMap<String, Object>> list = new LinkedList<HashMap<String, Object>>();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        String header = null;
        StringBuilder sequence = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(">")) {
                if (header != null) {
                    HashMap map = new HashMap<String, Object>();
                    map.put(IDENTIFIER, header);
                    map.put(SEQUENCE, sequence.toString());
                    list.add(map);
                    sequence = new StringBuilder();
                }
                header = line.substring(1);
            } else {
                sequence.append(line);
            }
        }
        //Add last
        if (header != null) {
            HashMap map = new HashMap<String, Object>();
            map.put(IDENTIFIER, header);
            map.put(SEQUENCE, sequence.toString());
            list.add(map);
        }
        reader.close();

        return list;
    }

    public List<String[]> parseToStringArray(File file) throws IOException {
        List<String[]> list = new LinkedList<String[]>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        String header = null;
        StringBuilder sequence = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(">")) {
                if (header != null) {
                    String[] ss = new String[2];
                    ss[0] = header;
                    ss[1] = sequence.toString();
                    list.add(ss);
                    sequence = new StringBuilder();
                }
                header = line.substring(1);
            } else {
                sequence.append(line);
            }
        }
        //Add last
        if (header != null) {
            String[] ss = new String[2];
            ss[0] = header;
            ss[1] = sequence.toString();
            list.add(ss);
        }
        reader.close();

        return list;
    }
}
