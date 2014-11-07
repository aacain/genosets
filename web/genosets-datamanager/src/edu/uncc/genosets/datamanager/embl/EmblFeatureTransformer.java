/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.embl;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.embl.EmblParser.FeatureTableItem;
import edu.uncc.genosets.datamanager.entity.FactLocation;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.Location;
import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author aacain
 */
public abstract class EmblFeatureTransformer {

    protected FeatureTableItem item;
    protected final int nucLength;
    protected final Map<String, List<FactLocation>> factMap;
    protected final String source;

    public EmblFeatureTransformer(String source, Map<String, List<FactLocation>> factMap, FeatureTableItem item, int nucLength) {
        this.source = source;
        this.factMap = factMap;
        this.item = item;
        this.nucLength = nucLength;
    }

    public abstract Feature getFeature();

    public abstract Location getLocation();

    public abstract String getSequence();

    public abstract FactLocation getFact();

    public abstract List<String[]> getDetailList();

    public abstract Map<String, List<String>> getGenoSetsDbxref();

    public static EmblFeatureTransformer instantiate(String source, Map<String, List<FactLocation>> factMap, FeatureTableItem item, int nucLength) {
        return new EmblFeatureTransformerImpl(source, factMap, item, nucLength);
    }

    public static class EmblFeatureTransformerImpl extends EmblFeatureTransformer {

        private FactLocation fact;
        //private Feature feature;
        //private Location location;
        private List<String[]> detailList = new LinkedList<String[]>();
        private String protSequence;
        private Map<String, List<String>> genosetsDbxref = new HashMap<String, List<String>>();

        public EmblFeatureTransformerImpl(String source, Map<String, List<FactLocation>> factMap, FeatureTableItem item, int nucLength) {
            super(source, factMap, item, nucLength);
            parseFeature();
            parseLocation();
        }

        private void parseFeature() {
            fact = new FactLocation();
            fact.setFeatureType(item.getFeatureType());

            //save attributes
            Map<String, List<StringBuilder>> atts = item.getAttributes();
            Set<Entry<String, List<StringBuilder>>> entrySet = atts.entrySet();
            for (Entry<String, List<StringBuilder>> entry : entrySet) {
                String attKey = entry.getKey();
                List<StringBuilder> valueList = entry.getValue();
                if (("translation").equals(attKey)) {
                    protSequence = valueList.get(0).toString();
                } else {
                    String dType = attKey;
                    if (("product").equals(attKey)) {
                        fact.setProduct(valueList.get(0).toString());
                    } else if ("locus_tag".equals(attKey)) {
                        fact.setPrimaryName(valueList.get(0).toString());
                    }
                    //create a Details list
                    for (StringBuilder stringBuilder : valueList) {
                        detailList.add(new String[]{dType, stringBuilder.toString()});
                        //get dbXref and add db individually
                        if (dType.equals("db_xref")) {
                            String[] dbSplit = stringBuilder.toString().split(":");
                            if (dbSplit.length == 2) {
                                if (dbSplit[0].startsWith("GenoSets")) {
                                    String[] split = dbSplit[1].split("_");
                                    List<String> get = genosetsDbxref.get(split[0]);
                                    if (get == null) {
                                        get = new LinkedList<String>();
                                        genosetsDbxref.put(split[0], get);
                                    }
                                    get.add(split[0]);
                                }
                                String[] dbs = dbSplit[0].split("/");
                                for (String string : dbs) {
                                    detailList.add(new String[]{string, dbSplit[1]});
                                }
                            }
                        }
                    }
                }
            }
        }

        private void parseLocation() {
            Location currentLocation = new Location();
            int min = 0;
            int max = 0;
            currentLocation.setIsForward(true);

            //TODO: Fix this method, doesn't work for all locations
            String locString = item.getLocation();
            String[] ss = new String[2];
            try {
                if (locString.contains("join")) {
                    if (locString.contains(":")) {
                        int firstBeg = locString.indexOf(':') + 1;
                        int firstEnd = locString.indexOf("..");
                        int lastBeg = locString.lastIndexOf(':') + 1;
                        int lastEnd = locString.lastIndexOf(")");
                        ss[0] = locString.substring(firstBeg, firstEnd);
                        ss[1] = locString.substring(lastBeg, lastEnd);
                    } else {
                        int seperator = locString.indexOf("..");
                        int start = seperator - 1;
                        while (start >= 0 && Character.isDigit(locString.charAt(start - 1))) {
                            start = start - 1;
                        }
                        ss[0] = locString.substring(start, seperator);
                        //get last
                        int end = locString.length() - 1;
                        while (end >= 0 && !Character.isDigit(locString.charAt(end))) {
                            end = end - 1;
                        }
                        start = end;
                        while (start >= 0 && Character.isDigit(locString.charAt(start - 1))) {
                            start = start - 1;
                        }
                        ss[1] = locString.substring(start, end + 1);
                    }
                    //set the max first in case there is an exception, 
                    //the nucleotide length wont have an invalid value.
                    max = Integer.parseInt(ss[1]);
                    min = Integer.parseInt(ss[0]);
                    if (min > max) { //then it spans the origin
                        int length = this.nucLength - min + 1;
                        length = length + max;
                        currentLocation.setNucleotideLength(length);
                    }
                    if (locString.contains("complement")) {
                        currentLocation.setIsForward(Boolean.FALSE);
                    } else {
                        currentLocation.setIsForward(Boolean.TRUE);
                    }
                    StringBuilder message = new StringBuilder("Joined location ");
                    message.append(fact.getPrimaryName() == null ? item.getLocation() : fact.getPrimaryName());
                    message.append(" at position ").append(locString).append(" The position as been set to ").append(min).append(",").append(max).append(".");
                    TaskLogFactory.getDefault().log("Joined location not supported.","EMBL " + source, message.toString(), TaskLog.WARNING, new Date());
                } else if (locString.startsWith("complement")) {
                    currentLocation.setIsForward(false);
                    int first = locString.indexOf("(");
                    int second = locString.indexOf(")");
                    int last = locString.lastIndexOf(".");
                    ss[0] = locString.substring(first + 1, last - 1);
                    ss[1] = locString.substring(last + 1, second);
                } else {
                    currentLocation.setIsForward(true);
                    int last = locString.lastIndexOf(".");
                    ss[0] = locString.substring(0, last - 1);
                    ss[1] = locString.substring(last + 1);
                }
                //TODO: not handled location less than or greater than
                if (ss[0].startsWith("<")) {
                    ss[0] = ss[0].substring(1);
                } else if (ss[0].startsWith(">")) {
                    ss[0] = ss[0].substring(1);
                }
                if (ss[1].startsWith("<")) {
                    ss[1] = ss[1].substring(1);
                } else if (ss[1].startsWith(">")) {
                    ss[1] = ss[1].substring(1);
                }
                //set the max first in case there is an exception, 
                //the nucleotide length wont have an invalid value.
                max = Integer.parseInt(ss[1]);
                min = Integer.parseInt(ss[0]);


            } catch (Exception e) {
                //Exceptions.printStackTrace(e);
                StringBuilder message = new StringBuilder();
                message.append("The location for this feature could not be parsed. ").append(fact.getPrimaryName() == null ? item.getLocation() : fact.getPrimaryName()).append(" at position ").append(locString).append(" The position as been set to ").append(min).append(",").append(max).append(".");
                TaskLogFactory.getDefault().log("Unsupported postion warning", "EMBL " + source, message.toString(), TaskLog.WARNING, new Date());
            } finally {
                currentLocation.setMinPosition(min);
                currentLocation.setMaxPosition(max);
                if (currentLocation.getNucleotideLength() == null) {
                    currentLocation.setNucleotideLength(max - min + 1);
                }
                if (currentLocation.getIsForward()) {
                    currentLocation.setStartPosition(min);
                    currentLocation.setEndPosition(max);
                } else {
                    currentLocation.setStartPosition(max);
                    currentLocation.setEndPosition(min);
                }
                //first lookup facts by locus
                if (fact.getPrimaryName() != null) {
                    List<FactLocation> facts = factMap.get(fact.getPrimaryName());
                    if (facts == null) {
                        facts = new LinkedList<FactLocation>();
                        factMap.put(fact.getPrimaryName(), facts);
                        fact.setFeature(new Feature());
                        fact.setLocation(currentLocation);
                    } else {
                        for (FactLocation byLocus : facts) {
                            fact.setFeature(byLocus.getFeature());
                            if (currentLocation.getMinPosition().equals(byLocus.getLocation().getMinPosition())
                                    && currentLocation.getMaxPosition().equals(byLocus.getLocation().getMaxPosition())
                                    && currentLocation.getIsForward().equals(byLocus.getLocation().getIsForward())) {
                                fact.setLocation(byLocus.getLocation());
                                fact.setFeature(byLocus.getFeature());
                            }
                        }
                        if (fact.getLocation() == null) {
                            fact.setLocation(currentLocation);
                            fact.setFeature(new Feature());
                        }
                    }
                    facts.add(fact);
                } else {
                    fact.setFeature(new Feature());
                    fact.setLocation(currentLocation);
                }

                //lookup existing locations
                List<String> locationIds = genosetsDbxref.get("LocationId");
                Location existingLocation = null;
                Location unmatchedLocation = null;
                if (locationIds != null) {
                    for (String string : locationIds) {
                        int locId = Integer.parseInt(string);
                        List<? extends Location> locsWithFeature = MyBuilder.getLocationsWithFeature(currentLocation, locId);
                        if (locsWithFeature != null) {
                            for (Location l : locsWithFeature) {
                                existingLocation = l;
                                break;
                            }
                        }
                        //not found, so lets get the unmatch location
                        unmatchedLocation = MyBuilder.getExistingLocation(locId);
                    }
                }//end existing lookup
                if (existingLocation != null) {
                    fact.setLocation(existingLocation);
                    fact.getFeature().setFeatureId(existingLocation.getFeatureId());
                    fact.getFeature().setFeatureId(existingLocation.getFeatureId());
                    fact.getFeature().setAssembledUnitId(existingLocation.getAssembledUnitId());
                    fact.getFeature().setOrganismId(existingLocation.getOrganismId());
                } else if (unmatchedLocation != null) {
                    fact.setLocation(currentLocation);
                    currentLocation.setFeatureId(unmatchedLocation.getFeatureId());
                    currentLocation.setAssembledUnitId(unmatchedLocation.getAssembledUnitId());
                    currentLocation.setOrganismId(unmatchedLocation.getOrganismId());
                    fact.getFeature().setFeatureId(unmatchedLocation.getFeatureId());
                    fact.getFeature().setAssembledUnitId(unmatchedLocation.getAssembledUnitId());
                    fact.getFeature().setOrganismId(unmatchedLocation.getOrganismId());
                }
                //update the feature attributes
                if(fact.getProduct() != null){
                    fact.getFeature().setProduct(fact.getProduct());
                }
                if(fact.getPrimaryName() != null){
                    fact.getFeature().setPrimaryName(fact.getPrimaryName());
                }
            }
        }

        @Override
        public Feature getFeature() {
            return fact.getFeature();
        }

        @Override
        public Location getLocation() {
            return fact.getLocation();
        }

        @Override
        public List<String[]> getDetailList() {
            return detailList;
        }

        @Override
        public String getSequence() {
            return protSequence;
        }

        @Override
        public Map<String, List<String>> getGenoSetsDbxref() {
            return genosetsDbxref;
        }

        @Override
        public FactLocation getFact() {
            return this.fact;
        }
    }

    private static class MyBuilder implements QueryCreator {

        public static List<? extends Location> getLocationsWithFeature(Location mylocation, int dbxrefLocationId) {
            StringBuilder bldr = new StringBuilder();
            bldr.append("SELECT l FROM Location as l, Location as L2 WHERE l2.locationId = ").append(dbxrefLocationId);
            bldr.append(" AND l.feature = l2.feature AND l.minPosition = ").append(mylocation.getMinPosition());
            bldr.append(" AND l.maxPosition = ").append(mylocation.getMaxPosition());
            bldr.append(" AND l.isForward = ").append(mylocation.getIsForward());
            return DataManager.getDefault().createQuery(bldr.toString(), Location.class);
        }

        public static Location getExistingLocation(int dbxrefLocationId) {
            return (Location) DataManager.getDefault().get(Location.DEFAULT_NAME, dbxrefLocationId);
        }
    }
}
