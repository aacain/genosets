package edu.uncc.genosets.datamanager.entity;

import java.util.Date;

/**
 *
 * @author aacain
 */
public class AnalysisSet extends CustomizableEntity {

    private static final String DEFAULT_NAME = "AnalysisSet";
    private Integer analysisSetId;
    private String setType;
    private String setName;
    private String setDescription;
    private Date loadDate = new Date();
    private Date modifiedDate = new Date();

    public AnalysisSet() {
    }
    
    

    public Integer getAnalysisSetId() {
        return analysisSetId;
    }

    public void setAnalysisSetId(Integer analysisSetId) {
        this.analysisSetId = analysisSetId;
    }
    
    public String getSetType() {
        return setType;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public String getSetDescription() {
        return setDescription;
    }

    public void setSetDescription(String setDescription) {
        this.setDescription = setDescription;
    }

    public Date getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(Date loadDate) {
        this.loadDate = loadDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    

    @Override
    public Integer getId() {
        return analysisSetId;
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_NAME;
    }
}
