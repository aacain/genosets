/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.embl;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aacain
 */
public class EmblParserImpl extends EmblParser {

    private final static Integer RELEASE_VERSION = 106;
    private static final String ACCESSION = "ID";
    private static final String PROJECT = "PR";
    private static final String DT_DATE = "DT";
    private static final String SOURCE_KEY = "source";
    private static final String DB_XREF = "db_xref";
    private static final String TAXON = "taxon";
    public static final String DATE_DATE = "date";
    public static final String VERSION_STRING = "version";
    private final Map<String, List<FeatureTableItem>> featureTableMap = new HashMap<String, List<FeatureTableItem>>();
    //key is line type, and list of values in order
    private final Map<String, List<String>> headerMap = new HashMap();
    private String nucSequence;
    private String taxon;
    private String assUnitName;
    private String project;
    private String contigLocation;

    public EmblParserImpl() {
    }

    @Override
    public void parse(File file, AnnotationMethod method) throws IOException {
        parse(convertFileToString(file), method);
    }

    private String convertFileToString(File file) throws IOException {
        StringBuilder bldr = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = reader.readLine()) != null) {
            bldr.append(line).append("\n");
        }

        reader.close();
        return bldr.toString();
    }

    @Override
    public void parse(String string, AnnotationMethod method) {

        String[] lineArray = string.split("\\n");
        string = null;
        StringBuilder nuc = new StringBuilder();
        FeatureTableItem featureItem = null;
        StringBuilder previousAttribute = new StringBuilder();
        String currentAttributeType = "";
        final String LOCATION = "LOCATION";
        boolean onSequence = false;
        StringBuilder contigLoc = new StringBuilder();

        for (String line : lineArray) {//read all lines
            try {
                if (line.length() > 5) {  //not a blank line
                    String lineType = line.substring(0, 5).trim();
                    if (lineType.length() == 0) { //on sequence
                        //add nuc sequence to string build
                        //but take off position field on the end and whitespace
                        nuc.append(line.substring(5, 70).replace(" ", ""));
                    } else { //not on sequence or contig
                        //Now check to see if we are on the featuretable or header
                        if (lineType.equals("FT")) {
                            //get the key and qualifier
                            String featureKey = line.substring(5, 20).trim();
                            String qualifierUnparsed = line.substring(21, line.length()).trim();
                            if (featureKey.length() > 0) { //on new feature, has a key
                                //reset variables
                                previousAttribute = new StringBuilder();
                                currentAttributeType = LOCATION;
                                //get the feature type
                                List<FeatureTableItem> keyList = featureTableMap.get(featureKey);
                                if (keyList == null) {
                                    keyList = new LinkedList();
                                    //add to map
                                    featureTableMap.put(featureKey, keyList);
                                }
                                featureItem = new FeatureTableItem();
                                keyList.add(featureItem);
                                featureItem.setFeatureType(featureKey);
                                //set location values
                                featureItem.setLocation(qualifierUnparsed);
                            } else { //on previous feature
                                //parse qualifier and see if starts with regex "/"
                                if (qualifierUnparsed.startsWith("/")) { //then new qualifier
                                    //split line to seperate "=" and remove quotes
                                    String[] qualifierSplit = qualifierUnparsed.split("=");
                                    //get qualifier and remove "/"
                                    String qualifierType = qualifierSplit[0].replace("/", "");
                                    currentAttributeType = qualifierType;
                                    if (qualifierSplit.length != 2) {
                                        previousAttribute = new StringBuilder();
                                    } else {
                                        //remove quotes from value
                                        previousAttribute = new StringBuilder(qualifierSplit[1].replace("\"", ""));
                                    }
                                    //add attribute to list
                                    List<StringBuilder> attributes = featureItem.getAttributes().get(qualifierType);
                                    if (attributes == null) {
                                        attributes = new LinkedList();
                                        featureItem.getAttributes().put(qualifierType, attributes);
                                    }
                                    attributes.add(previousAttribute);
                                } else { //continues for previous qualifier or location
                                    //remove quotes from value
                                    String cleanedValue = qualifierUnparsed.replace("\"", "");
                                    if (currentAttributeType.equals(LOCATION)) {
                                        featureItem.setLocation(featureItem.getLocation().concat(cleanedValue));
                                    } else {//else on attribute line
                                        previousAttribute.append(cleanedValue);
                                    }
                                }
                            }
                        } else if (lineType.equals("SQ")) {
                            onSequence = true;
                        } else if (lineType.equals("CO")) { //on contig
                            contigLoc.append(line.substring(5));
                        } else { //add it to the details list
                            List<String> headerAtt = headerMap.get(lineType);
                            if (headerAtt == null) {
                                headerAtt = new LinkedList();
                                headerMap.put(lineType, headerAtt);
                            }
                            headerAtt.add(line.substring(5));
                        }
                    }
                }//end not a blank line
            } catch (Exception e) {
                LoggerFactory.getLogger(EmblParserImpl.class).error("Error parsing EMBL file for " + method.getMethodName(), e);
            }
        }//all lines read
        nucSequence = nuc.toString();
        contigLocation = contigLoc.toString();
    }

    private void parseSourceInfo() {
        try {
            //see if taxon id exists in source
            List<FeatureTableItem> sourceList = this.getFeatureTableMap().get(SOURCE_KEY);
            if (sourceList != null) {
                FeatureTableItem sourceItem = sourceList.get(0);
                List<StringBuilder> dbrefList = sourceItem.getAttributes().get(DB_XREF);
                for (StringBuilder dbref : dbrefList) {
                    String toString = dbref.toString();
                    if (toString.contains(TAXON)) {
                        //split
                        String[] sArray = toString.split(":");
                        taxon = sArray[1].trim();
                    } else {
                        taxon = "";
                    }
                }
                //get assembled unit name by chromosome or plasmid
                List<StringBuilder> chromosome = sourceItem.getAttributes().get("chromosome");
                if (chromosome != null && chromosome.size() > 0) {
                    assUnitName = "chromosome " + chromosome.get(0).toString();
                } else {
                    List<StringBuilder> plasmid = sourceItem.getAttributes().get("plasmid");
                    if (plasmid != null && plasmid.size() > 0) {
                        assUnitName = "plasmid " + plasmid.get(0).toString();
                    }
                }
            }
            //now get project id
            List<String> projectLine = headerMap.get(PROJECT);
            if (projectLine != null) {
                String[] ss = projectLine.get(0).split(":");
                project = ss[1].replace(";", "");
            } else {
                project = "";
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(EmblParserImpl.class).error("Error parsing source information in EMBL file", e);
        }
    }

    @Override
    public String getProject() {
        if (project == null) {
            parseSourceInfo();
        }
        return project;
    }

    @Override
    public String getTaxon() {
        if (taxon == null) {
            parseSourceInfo();
        }
        return taxon;
    }

    @Override
    public String getAssUnitName() {
        return this.assUnitName;
    }

    @Override
    public Map<String, List<String>> getHeaderMap() {
        return headerMap;
    }

    @Override
    public Map<String, List<FeatureTableItem>> getFeatureTableMap() {
        return featureTableMap;
    }

    @Override
    public String getNucSequence() {
        return nucSequence;
    }

    @Override
    public Integer getEmblFormatRelease() {
        return RELEASE_VERSION;
    }

    @Override
    public Integer getExistingAssUnit() {
        //see if taxon id exists in source
        Integer assId = null;
        List<FeatureTableItem> sourceList = this.getFeatureTableMap().get(SOURCE_KEY);
        if (sourceList != null) {
            FeatureTableItem sourceItem = sourceList.get(0);
            List<StringBuilder> dbrefList = sourceItem.getAttributes().get(DB_XREF);
            for (StringBuilder dbref : dbrefList) {
                String toString = dbref.toString();
                if (toString.contains("GenoSets") && toString.contains("AssembledUnitId")) {
                    //split
                    String[] sArray = toString.split(":");
                    String[] split = sArray[1].split("_");
                    assId = Integer.parseInt(split[1]);
                }
            }
        }
        return assId;
    }

    @Override
    public String getContigLocation() {
        return contigLocation;
    }
}
