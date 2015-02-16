/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.embl.interproscan;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.Location;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class Interproscan {

    public HashMap<Location, Set<String>> getAnnotations(File file) throws IOException {
        HashMap<String, Set<String>> readTSV = readTSV(file);
        return getAnnotationMap(readTSV);

    }

    private HashMap<String, Set<String>> readTSV(File file) throws IOException {
        HashMap<String, Set<String>> annoStringMap = new HashMap<String, Set<String>>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (!line.equals("") && !line.startsWith("#")) {
                    String[] split = line.split("\\t");
                    if (split.length == 14) {
                        Set<String> gos = annoStringMap.get(split[0]);
                        if (gos == null) {
                            gos = new HashSet<String>();
                            annoStringMap.put(split[0], gos);
                        }
                        String[] goSplit = split[13].split("\\|");
                        for (String go : goSplit) {
                            gos.add(go);
                        }
                    }
                }
            }
        } finally {
            br.close();
        }
        return annoStringMap;
    }

    private HashMap<Location, Set<String>> getAnnotationMap(HashMap<String, Set<String>> stringMap) {
        HashMap<String, Location> locationLookup = LocationLookup.getLocationLookup(stringMap.keySet());
        HashMap<Location, Set<String>> annoMap = new HashMap<Location, Set<String>>(stringMap.size());
        for (Map.Entry<String, Set<String>> entry : stringMap.entrySet()) {
            Location loc = locationLookup.get(entry.getKey());
            annoMap.put(loc, entry.getValue());
        }
        return annoMap;
    }

    private static class LocationLookup implements QueryCreator {

        static HashMap<String, Location> getLocationLookup(Set<String> locationIdSet) {
            HashMap<String, Location> locLookup = new HashMap<String, Location>();
            if(locationIdSet.isEmpty()){
                return locLookup;
            }
            StringBuilder bldr = new StringBuilder("SELECT l FROM Location as l WHERE l.locationId IN (");
            for (String locId : locationIdSet) {
                bldr.append(locId).append(",");
            }
            bldr.replace(bldr.length() - 1, bldr.length(), ")");
            List<? extends Location> locList = null;
            try {
                locList = DataManager.getDefault().createQuery(bldr.toString(), Location.class);
            } catch (Exception ex) {
            }
            for (Location location : locList) {
                locLookup.put(location.getLocationId().toString(), location);
            }
            return locLookup;
        }
    }
}
