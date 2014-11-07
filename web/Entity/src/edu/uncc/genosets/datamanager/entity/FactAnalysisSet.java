package edu.uncc.genosets.datamanager.entity;

/**
 *
 * @author aacain
 */
public class FactAnalysisSet extends CustomizableEntity implements java.io.Serializable {

    public static final String DEFAULT_NAME = "FactAnalysisSet";
    private Integer factId;
    private Integer analysisSetId;
    private Integer entityId;
    private String entityTable;

    public FactAnalysisSet() {
    }

    
    public Integer getFactId() {
        return factId;
    }

    public void setFactId(Integer factId) {
        this.factId = factId;
    }

    public Integer getAnalysisSetId() {
        return analysisSetId;
    }

    public void setAnalysisSetId(Integer analysisSetId) {
        this.analysisSetId = analysisSetId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getEntityTable() {
        return entityTable;
    }

    public void setEntityTable(String entityTable) {
        this.entityTable = entityTable;
    }
    
    @Override
    public String getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    public Integer getId() {
        return factId;
    }
 
}
