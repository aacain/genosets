
package edu.uncc.genosets.util.geneontology;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author lucy
 */
public class GoAnnotationFileFormat {

    private final List<Annotation> annotations;
    public static final int FORMAT_1_0 = 1;
    public static final int FORMAT_2_0 = 2;

    public GoAnnotationFileFormat() {
        this.annotations = new LinkedList<Annotation>();
    }

    public GoAnnotationFileFormat(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    /**
     * Reads GAF 1.0 or 2.0 format. Closes the input stream when finished
     *
     * @param in
     * @param format
     * @throws IOException
     */
    public static GoAnnotationFileFormat readAnnotations(InputStream in, int format) throws IOException {
        GoAnnotationFileFormat gaf = new GoAnnotationFileFormat();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("!")) {
                    String[] split = line.split("\t");
                    if (format == FORMAT_2_0) {
                        if (split.length == 17) {
                            gaf.annotations.add(new Annotation(split[0], split[1], split[2], split[3], split[4], split[5].split("\\|"), split[6], split[7], split[8], split[9], split[10].split("\\|"), split[11], split[12].split("\\|"), split[13], split[14], split[15], split[16]));
                        } else {
                            throw new IOException("Error reading GAF 2.0 File: incorrect number of columns");
                        }
                    } else if (format == FORMAT_1_0) {
                        if (split.length == 15) {
                            gaf.annotations.add(new Annotation(split[0], split[1], split[2], split[3], split[4], split[5].split("\\|"), split[6], split[7], split[8], split[9], split[10].split("\\|"), split[11], split[12].split("\\|"), split[13], split[14], "", ""));
                        } else {
                            throw new IOException("Error reading GAF 1.0 File: incorrect number of columns");
                        }
                    }
                }
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return gaf;
    }

    public static void writeAnnotations(File file, GoAnnotationFileFormat gaf) throws IOException {
        BufferedWriter wr = null;
        try {
            wr = new BufferedWriter(new FileWriter(file));
            wr.append("!gaf-version: 2.0");
            wr.newLine();
            for (Annotation anno : gaf.annotations) {
                wr.append(anno.db).append("\t");
                wr.append(anno.id).append("\t");
                wr.append(anno.dbObjectSymbol).append("\t");
                wr.append(anno.qualifier).append("\t");
                wr.append(anno.goIdentifier).append("\t");
                int i = 0;
                for (String string : anno.dbReference) {
                    i++;
                    if (i > 1) {
                        wr.append("|");
                    }
                    wr.append(string);
                }
                wr.append("\t");
                wr.append(anno.evidenceCode).append("\t");
                wr.append(anno.withOrFrom).append("\t");
                wr.append(anno.aspect).append("\t");
                wr.append(anno.dbObjectName).append("\t");
                i = 0;
                for (String string : anno.dbObjectSynonym) {
                    i++;
                    if (i > 1) {
                        wr.append("|");
                    }
                    wr.append(string);
                }
                wr.append("\t");
                wr.append(anno.dbObjectType).append("\t");
                i = 0;
                for (String string : anno.taxon) {
                    i++;
                    if (i > 1) {
                        wr.append("|");
                    }
                    wr.append(string);
                }
                wr.append("\t");
                wr.append(anno.date).append("\t");
                wr.append(anno.assignedBy).append("\t");
                wr.append(anno.annotationExtension).append("\t");
                wr.append(anno.geneProductFormId);
                wr.newLine();
            }
        } finally {
            if (wr != null) {
                wr.close();
            }
        }
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public static class Annotation {

        private String id;
        private String evidenceCode;
        private String goIdentifier;
        private String db;
        private String qualifier = "";
        private String dbObjectSymbol;
        private String[] dbReference = {""};
        private String withOrFrom = "";
        private String aspect;
        private String dbObjectName = "";
        private String[] dbObjectSynonym = {""};
        private String dbObjectType;
        private String[] taxon = {""};
        private String date;
        private String assignedBy;
        private String annotationExtension = "";
        private String geneProductFormId = "";

        public Annotation(String id, String evidenceCode, String goIdentifier) {
            this.id = id;
            this.evidenceCode = evidenceCode;
            this.goIdentifier = goIdentifier;
            this.db = "";
            this.dbObjectSymbol = "";
            this.aspect = "";
            this.dbObjectType = "";
            this.date = "";
            this.assignedBy = "";
        }

        public Annotation(String db, String id, String dbObjectSymbol, String evidenceCode, String goIdentifier, String[] dbReference, String aspect, String dbObjectType, String[] taxon, String date, String assignedBy) {
            this.id = id;
            this.evidenceCode = evidenceCode;
            this.goIdentifier = goIdentifier;
            this.db = db;
            this.dbObjectSymbol = dbObjectSymbol;
            this.dbReference = dbReference;
            this.aspect = aspect;
            this.dbObjectType = dbObjectType;
            this.taxon = taxon;
            this.date = date;
            this.assignedBy = assignedBy;
        }

        public Annotation(String db, String id, String dbObjectSymbol, String qualifier, String goIdentifier, String[] dbReference, String evidenceCode, String withOrFrom, String aspect, String dbObjectName, String[] dbObjectSynonym, String dbObjectType, String[] taxon, String date, String assignedBy, String annotationExtension, String geneProductFormId) {
            this.id = id;
            this.evidenceCode = evidenceCode;
            this.goIdentifier = goIdentifier;
            this.db = db;
            this.qualifier = qualifier;
            this.dbObjectSymbol = dbObjectSymbol;
            this.dbReference = dbReference;
            this.withOrFrom = withOrFrom;
            this.aspect = aspect;
            this.dbObjectName = dbObjectName;
            this.dbObjectSynonym = dbObjectSynonym;
            this.dbObjectType = dbObjectType;
            this.taxon = taxon;
            this.date = date;
            this.assignedBy = assignedBy;
            this.annotationExtension = annotationExtension;
            this.geneProductFormId = geneProductFormId;
        }

        public String getEvidenceCode() {
            return evidenceCode;
        }

        public String getGoIdentifier() {
            return goIdentifier;
        }

        public String getId() {
            return id;
        }

        public String getAnnotationExtension() {
            return annotationExtension;
        }

        /**
         *
         * @param annotationExtension(optional)
         */
        public void setAnnotationExtension(String annotationExtension) {
            this.annotationExtension = annotationExtension;
        }

        public String getAspect() {
            return aspect;
        }

        /**
         *
         * @param aspect (required)
         */
        public void setAspect(String aspect) {
            this.aspect = aspect;
        }

        public String getAssignedBy() {
            return assignedBy;
        }

        /**
         *
         * @param assignedBy (required)
         */
        public void setAssignedBy(String assignedBy) {
            this.assignedBy = assignedBy;
        }

        public String getDate() {
            return date;
        }

        /**
         *
         * @param date (required)
         */
        public void setDate(String date) {
            this.date = date;
        }

        public String getDb() {
            return db;
        }

        /**
         *
         * @param db (required)
         */
        public void setDb(String db) {
            this.db = db;
        }

        public String getDbObjectName() {
            return dbObjectName;
        }

        /**
         *
         * @param dbObjectName (optional)
         */
        public void setDbObjectName(String dbObjectName) {
            this.dbObjectName = dbObjectName;
        }

        public String getDbObjectSymbol() {
            return dbObjectSymbol;
        }

        /**
         *
         * @param dbObjectSymbol (required)
         */
        public void setDbObjectSymbol(String dbObjectSymbol) {
            this.dbObjectSymbol = dbObjectSymbol;
        }

        public String[] getDbObjectSynonym() {
            return dbObjectSynonym;
        }

        /**
         *
         * @param dbObjectSynonym (optional)
         */
        public void setDbObjectSynonym(String[] dbObjectSynonym) {
            this.dbObjectSynonym = dbObjectSynonym;
        }

        public String getDbObjectType() {
            return dbObjectType;
        }

        /**
         *
         * @param dbObjectType (required)
         */
        public void setDbObjectType(String dbObjectType) {
            this.dbObjectType = dbObjectType;
        }

        public String[] getDbReference() {
            return dbReference;
        }

        /**
         *
         * @param dbReference (required)
         */
        public void setDbReference(String[] dbReference) {
            this.dbReference = dbReference;
        }

        public String getGeneProductFormId() {
            return geneProductFormId;
        }

        /**
         *
         * @param geneProductFormId (optional)
         */
        public void setGeneProductFormId(String geneProductFormId) {
            this.geneProductFormId = geneProductFormId;
        }

        public String getQualifier() {
            return qualifier;
        }

        /**
         *
         * @param qualifier (optional)
         */
        public void setQualifier(String qualifier) {
            this.qualifier = qualifier;
        }

        public String[] getTaxon() {
            return taxon;
        }

        /**
         *
         * @param taxon (required)
         */
        public void setTaxon(String[] taxon) {
            this.taxon = taxon;
        }

        public String getWithOrFrom() {
            return withOrFrom;
        }

        /**
         *
         * @param withOrFrom (optional)
         */
        public void setWithOrFrom(String withOrFrom) {
            this.withOrFrom = withOrFrom;
        }
    }
    
    public static void main(String[] args) throws IOException{
        FileInputStream is = new FileInputStream("C:\\Users\\lucy\\Downloads\\gene_association.goa");
        GoAnnotationFileFormat gaf = GoAnnotationFileFormat.readAnnotations(is, FORMAT_1_0);
        List<Annotation> annotations1 = gaf.getAnnotations();
    }
}
