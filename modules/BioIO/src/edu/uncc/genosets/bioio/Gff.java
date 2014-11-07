/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.bioio;

import java.io.*;
import java.util.List;

/**
 *
 * @author aacain
 */
public class Gff {

    private List<Region> regionList;
    private final static String VERSION_LINE = "##gff-version 3";
    private final static String DETAIL_LINE = "##sequence-region ";

    public List<Region> getRegionList() {
        return regionList;
    }

    public void setRegionList(List<Region> regionList) {
        this.regionList = regionList;
    }


    public static void create(File file, boolean appendFile, Gff gff) throws IOException {
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new FileWriter(file, appendFile));
            if (gff.getRegionList() != null) {
                for (Region region : gff.getRegionList()) {
                    //write the header
                    br.append(VERSION_LINE);
                    br.newLine();
                    br.append(DETAIL_LINE).append(region.getSeqId()).append(" ").append(region.start).append(" ").append(region.end);
                    br.newLine();
                    //now write all of the features
                    if (region.getFeatureList() != null) {
                        for (GffFeature f : region.getFeatureList()) {
                            br.append(f.seqId).append("\t");
                            br.append(f.source).append("\t");
                            br.append(f.type).append("\t");
                            br.append(f.start).append("\t");
                            br.append(f.end).append("\t");
                            br.append(f.score).append("\t");
                            br.append(f.strand).append("\t");
                            br.append(f.phase).append("\t");
                            //now append attributes seperated by semicolon
                            if (f.attributes != null) {
                                int i = 0;
                                for (String att : f.attributes) {
                                    if (i > 0) {
                                        br.append(";");
                                    }
                                    br.append(att);
                                    i++;
                                }
                            }
                            br.newLine();
                        }
                    }
                    br.append("###");
                    br.newLine();
                }
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public static class Region {

        String seqId;
        String start;
        String end;
        List<GffFeature> featureList;

        public Region() {
        }

        public Region(String seqId, String start, String end, List<GffFeature> featureList) {
            this.seqId = seqId;
            this.start = start;
            this.end = end;
            this.featureList = featureList;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public List<GffFeature> getFeatureList() {
            return featureList;
        }

        public void setFeatureList(List<GffFeature> featureList) {
            this.featureList = featureList;
        }

        public String getSeqId() {
            return seqId;
        }

        public void setSeqId(String seqId) {
            this.seqId = seqId;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }
    }

    public static class GffFeature {

        String seqId;
        String source;
        String type;
        String start;
        String end;
        String score;
        String strand;
        String phase;
        List<String> attributes;

        public GffFeature() {
        }

        public GffFeature(String seqId, String source, String type, String start, String end, String score, String strand, String phase, List<String> attributes) {
            this.seqId = seqId;
            this.source = source;
            this.type = type;
            this.start = start;
            this.end = end;
            this.score = score;
            this.strand = strand;
            this.phase = phase;
            this.attributes = attributes;
        }

        public String getSeqId() {
            return seqId;
        }

        public void setSeqId(String seqId) {
            this.seqId = seqId;
        }

        public List<String> getAttributes() {
            return attributes;
        }

        public void setAttributes(List<String> attributes) {
            this.attributes = attributes;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public String getPhase() {
            return phase;
        }

        public void setPhase(String phase) {
            this.phase = phase;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getStrand() {
            return strand;
        }

        public void setStrand(String strand) {
            this.strand = strand;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Attributes {

        String tag;
        String value;

        public Attributes() {
        }

        public Attributes(String tag, String value) {
            this.tag = tag;
            this.value = value;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
