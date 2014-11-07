/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.embl;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.embl.EmblParser.FeatureTableItem;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.FactLocation;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.fasta.Fasta;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aacain
 */
public abstract class EmblTransformer {

    public static EmblTransformer instantiate() {
        return new EmblTransformerImpl();
    }

    public abstract void transform(String emblString, AnnotationMethod method);

    public abstract Organism getOrganism();

    public abstract AssembledUnit getAssembledUnit();

    public abstract String getAssSequence();

    public abstract Date getDate();

    public abstract List<EmblFeatureTransformer> getTransformers();

    public abstract AnnotationMethod getAnnotationMethod();

    public abstract String getType();

    public abstract String getAssembledUnitName();
    
    public abstract String getContigLocation();

    public static class EmblTransformerImpl extends EmblTransformer {

        private Organism organism;
        private AssembledUnit assUnit;
        private String assSequence;
        private List<EmblFeatureTransformer> transformers = new LinkedList<EmblFeatureTransformer>();
        private Date date;
        private AnnotationMethod method;
        private String type;
        private String assUnitName;
        private String contigLocation;

        @Override
        public void transform(String emblString, AnnotationMethod method) {
            this.method = method;
            EmblParser parser = EmblParser.instantiate();
            parser.parse(emblString, method);
            //load header
            type = loadHeader(parser);
            //load features
            loadFeatures(parser);
            //see if we have an existing assembledunit

        }

        /**
         * load the header for this embl file.
         *
         * @param parser
         * @return the type of file (STD, SET, WGS, ...)
         */
        private String loadHeader(EmblParser parser) {
            EmblHeaderTransformer headerTrans = EmblHeaderTransformer.instantiate(parser);
            organism = headerTrans.getOrganism();
            assUnit = headerTrans.getAssembledUnit();
            contigLocation = headerTrans.getContigLocation();
            
            Integer existingAssId = parser.getExistingAssUnit();
            if (existingAssId != null) {
                AssembledUnit existingAss = MyBuilder.getAssUnit(existingAssId);
                assUnit.setAssembledUnitId(existingAss.getAssembledUnitId());
                assUnit.setOrganismId(existingAss.getOrganismId());
                organism.setOrganismId(assUnit.getOrganismId());
            }
            this.date = headerTrans.getDate();
            if (this.method.getMethodName() == null) {
                this.method.setMethodName(assUnit.getAssembledUnitName() + "." + headerTrans.getVersion());
            }
            this.method.setMethodVersion(headerTrans.getVersion());
            this.method.setRunDate(date);
            this.assUnitName = headerTrans.getAssUnitName();

            assSequence = headerTrans.getAssembledSequence();
            if (assSequence == null || assSequence.length() == 0) {
                String fullSequence = EmblClient.instantiate().getFastaFile(assUnit.getAssembledUnitName() + "." + headerTrans.getVersion());
                if (fullSequence != null) {
                    try {
                        Fasta fasta = Fasta.parse(fullSequence);
                        assSequence = fasta.getItems().size() > 0 ? fasta.getItems().get(0).getSequence() : null;
                    } catch (IOException ex) {
                        Logger.getLogger(EmblTransformer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }
            return headerTrans.getType();
        }

        private void loadFeatures(EmblParser parser) {
            Map<String, List<FactLocation>> factMap = new HashMap<String, List<FactLocation>>();
            Collection<List<FeatureTableItem>> values = parser.getFeatureTableMap().values();
            for (List<FeatureTableItem> list : values) {
                for (FeatureTableItem item : list) {
                    transformers.add(EmblFeatureTransformer.instantiate(this.method.getMethodName(), factMap, item, assSequence.length()));
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
        public String getAssSequence() {
            return assSequence;
        }

        @Override
        public List<EmblFeatureTransformer> getTransformers() {
            return transformers;
        }

        @Override
        public Date getDate() {
            return this.date;
        }

        @Override
        public AnnotationMethod getAnnotationMethod() {
            return this.method;
        }

        @Override
        public String getType() {
            return this.type;
        }

        @Override
        public String getAssembledUnitName() {
            return this.assUnitName;
        }

        @Override
        public String getContigLocation() {
            return this.contigLocation;
        }
    }

    private static class MyBuilder implements QueryCreator {

        static AssembledUnit getAssUnit(Integer id) {
            return (AssembledUnit) DataManager.getDefault().get(AssembledUnit.DEFAULT_NAME, id);
        }
    }
}
