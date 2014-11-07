package edu.uncc.genosets.datamanager.entity;

/**
 *
 * @author aacain
 */
public class FactStudySet extends CustomizableEntity implements java.io.Serializable {

    public static final String DEFAULT_NAME = "FactStudySet";
    private Integer factId;
    private Integer studySetId;
    private Integer entityId;
    private StudySetEntity studySet;

    public FactStudySet(Integer entityId, StudySetEntity studySet) {
        this.entityId = entityId;
        this.studySet = studySet;
        this.studySetId = studySet == null ? null : studySet.getStudySetId();
    }

    public FactStudySet(Integer entityId) {
        this(entityId, null);
    }

    public FactStudySet() {
        this(null, null);
    }

    public Integer getFactId() {
        return factId;
    }

    public void setFactId(Integer factId) {
        this.factId = factId;
    }

    public Integer getStudySetId() {
        return studySetId;
    }

    public void setStudySetId(Integer studySetId) {
        this.studySetId = studySetId;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer EntityId) {
        this.entityId = EntityId;
    }

    public StudySetEntity getStudySet() {
        return studySet;
    }

    public void setStudySet(StudySetEntity studySet) {
        this.studySet = studySet;
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    public Integer getId() {
        return factId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.factId != null ? this.factId.hashCode() : 0);
        hash = 79 * hash + (this.entityId != null ? this.entityId.hashCode() : 0);
        hash = 79 * hash + (this.studySet != null ? this.studySet.hashCode() : 0);
        return hash;
    }



    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        //match by integer
        if (obj instanceof Integer) {
            Integer other = (Integer)obj;
            return this.entityId.equals(other);
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FactStudySet other = (FactStudySet) obj;
        if (this.factId != other.factId && (this.factId == null || !this.factId.equals(other.factId))) {
            return false;
        }
        return true;
    }
}
