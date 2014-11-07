package edu.uncc.genosets.datamanager.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class StudySetEntity extends CustomizableEntity {

    private static final String DEFAULT_NAME = "StudySetEntity";
    private Integer studySetId;
    private String entityTable;
    private String studySetName;
    private String studySetDescription;
    private Date loadDate = new Date();
    private Date modifiedDate = new Date();
    private Set<FactStudySet> factStudySets;
    private Set<Integer> idSet;

    public Integer getStudySetId() {
        return studySetId;
    }

    public void setStudySetId(Integer studySetId) {
        this.studySetId = studySetId;
    }

    public String getEntityTable() {
        return entityTable;
    }

    public void setEntityTable(String entityTable) {
        this.entityTable = entityTable;
    }

    public String getStudySetName() {
        return studySetName;
    }

    public void setStudySetName(String studySetName) {
        this.studySetName = studySetName;
    }

    public String getStudySetDescription() {
        return studySetDescription;
    }

    public void setStudySetDescription(String studySetDescription) {
        this.studySetDescription = studySetDescription;
    }

    protected Set<FactStudySet> getFactStudySets() {
        return factStudySets;
    }

    public void setFactStudySets(Set<FactStudySet> factStudySets) {
        this.factStudySets = factStudySets;
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

    public Set<Integer> getIdSet() {
        return idSet;
    }

    public void setIdSet(Set<Integer> idSet) {
        this.idSet = idSet;
    }

    public void addFactStudySet(FactStudySet fact) {
        fact.setStudySet(this);
        if (this.getFactStudySets() == null) {
            this.setFactStudySets(new HashSet());
        }
        this.getFactStudySets().add(fact);
    }
    public void removeFactStudySet(FactStudySet fact) {
        fact.setStudySet(this);
        if (this.getFactStudySets() == null) {
            this.setFactStudySets(new HashSet());
        }
        this.getFactStudySets().remove(fact);
    }
    

    @Override
    public Integer getId() {
        return studySetId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.studySetId != null ? this.studySetId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StudySetEntity other = (StudySetEntity) obj;
        if (this.studySetId != other.studySetId && (this.studySetId == null || !this.studySetId.equals(other.studySetId))) {
            return false;
        }
        return true;
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_NAME;
    }
}
