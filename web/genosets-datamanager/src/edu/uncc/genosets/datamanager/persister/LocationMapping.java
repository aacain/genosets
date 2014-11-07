package edu.uncc.genosets.datamanager.persister;

import edu.uncc.genosets.datamanager.api.DataLoadException;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.Location;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aacain
 */
public class LocationMapping {

    Map<String, List<? extends Location>> gsLookup;
    Map<String, String> mapping;
    Comparator<Location> comparator;

    public LocationMapping() {
        comparator = new Comparator<Location>() {
            @Override
            public int compare(Location o1, Location o2) {
                int starts = o1.getStartPosition().compareTo(o2.getStartPosition());
                if (starts == 0) {
                    int ends = o1.getEndPosition().compareTo(o2.getEndPosition());
                    if (ends == 0) {
                        return o1.getIsForward().compareTo(o2.getIsForward());
                    }
                    return ends;
                }
                return starts;
            }
        };
    }

    public void readMappings(final String mappingString) throws DataLoadException {
        gsLookup = new HashMap<String, List<? extends Location>>();
        mapping = new HashMap<String, String>();
        String[] lines = mappingString.split("[\n\r]");
        List<String> ids = new ArrayList<String>(lines.length);
        for (String line : lines) {
            if (!line.startsWith("#") && !line.equals("")) {
                String[] ss = line.split("\\t");
                if (ss.length == 2) {
                    mapping.put(ss[1], ss[0]);
                    ids.add(ss[0]);
                } else {
                    throw new DataLoadException("Error reading location mapping file");
                }
            }
        }
        gsLookup = LocationQuery.queryAll(ids);   
    }

    public List<? extends Location> lookup(String newId) {
        String gsId = mapping.get(newId);
        if (gsId == null) {
            return null;
        }
        return gsLookup.get(gsId);
    }

    public Location lookup(String newId, Location newLocation, boolean addIfNew) {
        List<Location> lookup = (List<Location>) lookup(newId);
        if (lookup != null) {
            int binarySearch = Collections.binarySearch(lookup, newLocation, comparator);
            if (binarySearch >= 0) {
                return lookup.get(binarySearch);
            }
            newLocation.setFeatureId(lookup.get(0).getFeatureId());
            if (addIfNew) {
                lookup.add(-binarySearch - 1, newLocation);
            }
        }
        if (addIfNew) {
            if (lookup == null) {
                lookup = new LinkedList<Location>();
                lookup.add(newLocation);
                String gsId = mapping.get(newId);
                if (gsId != null) {
                    gsLookup.put(gsId, lookup);
                }
            }
        }
        return newLocation;
    }

    private static class LocationQuery implements QueryCreator {

        static List<? extends Location> query(String locationId) {
            return DataManager.getDefault().createQuery("SELECT l2 FROM Location AS l1, Location as l2 WHERE l1.featureId = l2.featureId AND l1.locationId = " + locationId + " ORDER BY l2.startPosition desc, l2.endPosition desc, l2.isForward", Location.class);
        }
        static HashMap<String, List<? extends Location>> queryAll(List<String> locationIds) {
            HashMap<String, List<? extends Location>> map = new HashMap<String, List<? extends Location>>();
            StringBuilder ids = new StringBuilder("(");
            for (String string : locationIds) {
                ids.append(string).append(",");
            }
            ids.setCharAt(ids.length()-1, ')');
            List<? extends Location> list = DataManager.getDefault().createQuery("SELECT l2 FROM Location AS l1, Location as l2 WHERE l1.featureId = l2.featureId AND l1.locationId in " + ids.toString() + " ORDER BY l2.startPosition desc, l2.endPosition desc, l2.isForward", Location.class);
            HashMap<Integer, List<Location>> featureToLocationMap = new HashMap<Integer, List<Location>>();
            HashMap<Integer, Integer> locationToFeatureMap = new HashMap<Integer, Integer>();
            for (Location location : list) {
                List<Location> relatedLocations = featureToLocationMap.get(location.getFeatureId());
                if(relatedLocations == null){
                    relatedLocations = new LinkedList<Location>();
                    featureToLocationMap.put(location.getFeatureId(), relatedLocations);
                }
                locationToFeatureMap.put(location.getLocationId(), location.getFeatureId());
                relatedLocations.add(location);
            }
            for (Map.Entry<Integer, Integer> entry : locationToFeatureMap.entrySet()) {
                map.put(entry.getKey().toString(), featureToLocationMap.get(entry.getValue()));
            }
            return map;
        }
    }
}
