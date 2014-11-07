/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.embl;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author aacain
 */
public abstract class EmblParser {
    public abstract Integer getEmblFormatRelease();
    public abstract void parse(String emblString, AnnotationMethod method);
    public abstract void parse(File emblFile, AnnotationMethod method) throws IOException;
    public abstract String getProject();
    public abstract String getTaxon();
    public abstract Map<String, List<String>> getHeaderMap();
    public abstract Map<String, List<FeatureTableItem>> getFeatureTableMap();
    public abstract String getNucSequence();
    public abstract String getAssUnitName();
    public abstract Integer getExistingAssUnit();
    public abstract String getContigLocation();

    public static EmblParser instantiate(){
        return new EmblParserImpl();
    }

    public static class FeatureTableItem {
        private String featureType;
        private String location;
        private Map<String, List<StringBuilder>> attributes = new HashMap();

        public Map<String, List<StringBuilder>> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, List<StringBuilder>> attributes) {
            this.attributes = attributes;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getFeatureType() {
            return featureType;
        }

        public void setFeatureType(String featureType) {
            this.featureType = featureType;
        }
    }
}
