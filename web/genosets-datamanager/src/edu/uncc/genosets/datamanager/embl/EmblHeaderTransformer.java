/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.embl;

import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;


/**
 *
 * @author aacain
 */
public abstract class EmblHeaderTransformer {

    protected final EmblParser parser;

    public EmblHeaderTransformer(EmblParser parser) {
        this.parser = parser;
    }

    public static EmblHeaderTransformer instantiate(EmblParser parser) {
        return new EmblHeaderTransformerImpl(parser);
    }

    public abstract Organism getOrganism();

    public abstract AssembledUnit getAssembledUnit();

    public abstract String getAssembledSequence();

    public abstract Date getDate();

    public abstract String getVersion();
    
    public abstract String getType();
    
    public abstract String getAssUnitName();
    
    public abstract String getContigLocation();

    public static class EmblHeaderTransformerImpl extends EmblHeaderTransformer {

        private Organism organism;
        private AssembledUnit assUnit;
        private String assSequence;
        private Date date;
        private String version;
        private String type;
        private String assUnitName;

        public EmblHeaderTransformerImpl(EmblParser parser) {
            super(parser);
            transformOrganism();
            transformAssembledUnit();
            transformDate();
        }

        private void transformOrganism() {
            String project = parser.getProject();
            String taxon = parser.getTaxon();
            Integer taxonId = null;
            if (taxon != null) {
                try{
                    taxonId = Integer.parseInt(taxon);
                }catch(NumberFormatException ex){
                    
                }
            }
            organism = new Organism();
            organism.setTaxonomyIdentifier(taxonId);
            organism.setProjectId(project);
        }

        private void transformAssembledUnit() {
            this.assUnit = new AssembledUnit();
            Map<String, List<String>> headerMap = parser.getHeaderMap();
            List<String> id = headerMap.get("ID");
            String idLine = id.get(0);
            String[] split = idLine.split(";");
            assUnit.setAssembledUnitName(split[0].trim());
            assUnit.setRepUnitName(parser.getAssUnitName());
            this.type = split[4].trim();
            String[] sv = split[1].split("\\s{1,}SV\\s{1,}");
            version = sv[1];
            this.assUnitName = assUnit.getAssembledUnitName() + "." + version;
            assUnit.setAccessionVersion(this.assUnitName);
            if (assUnit.getRepUnitName() != null) {
                if (assUnit.getRepUnitName().contains("chromosome")) {
                    assUnit.setReplicatingUnitType("chromosome");
                } else if (assUnit.getRepUnitName().contains("plasmid")) {
                    assUnit.setReplicatingUnitType("plasmid");
                }
                assUnit.setAssembledUnitType(assUnit.getReplicatingUnitType());
            }
        }

        private void transformDate() {
            DateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
            Map<String, List<String>> headerMap = parser.getHeaderMap();
            List<String> dt = headerMap.get("DT");
            if (dt != null) {
                for (String string : dt) {
                    String ss[] = string.split(" ");
                    if (ss.length > 0) {
                        try {
                            this.date = fmt.parse(ss[0].trim());
                        } catch (ParseException ex) {
                           LoggerFactory.getLogger(EmblHeaderTransformer.class).error("Could not parse header", ex);
                        }
                    }
                }
            }
        }

        @Override
        public Organism getOrganism() {
            return organism;
        }

        @Override
        public AssembledUnit getAssembledUnit() {
            return assUnit;
        }

        @Override
        public String getAssembledSequence() {
            if (assSequence == null) {
                assSequence = parser.getNucSequence();
            }
            return assSequence;
        }

        @Override
        public Date getDate() {
            return this.date;
        }

        @Override
        public String getVersion() {
            return this.version;
        }

        @Override
        public String getType() {
            return this.type;
        }

        @Override
        public String getAssUnitName() {
            return this.assUnitName;
        }

        @Override
        public String getContigLocation() {
           return parser.getContigLocation();
        }
    }
}
