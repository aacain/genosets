/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datamanager.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 *
 * @author aacain
 */
@MappedSuperclass
public abstract class StudySetDatabase extends CustomizableEntity implements java.io.Serializable {

    private static final String DEFAULT_NAME = "StudySetDatabase";
    private Integer studySetId;
    private String entityTable;
    private String studySetName;
    private String studySetDescription;
    private Date loadDate;
    private Date modifiedDate;
    private Set<FactStudySet> studySetFacts = new HashSet(0);

    @GenericGenerator(name = "table-hilo-generator", strategy = "org.hibernate.id.MultipleHiLoPerTableGenerator",
            parameters = {
        @Parameter(value = "9", name = "max_lo")})
    @GeneratedValue(generator = "table-hilo-generator")
    @Id
    @Column(name = "StudySetId")
    public Integer getStudySetId() {
        return studySetId;
    }

    public void setStudySetId(Integer studySetId) {
        this.studySetId = studySetId;
    }

    @Column(name = "EntityTable")
    public String getEntityTable() {
        return entityTable;
    }

    public void setEntityTable(String entityTable) {
        this.entityTable = entityTable;
    }

    @Column(name = "studySetName")
    public String getStudySetName() {
        return studySetName;
    }

    public void setStudySetName(String studySetName) {
        this.studySetName = studySetName;
    }

    @Column(name = "studySetDescription")
    public String getStudySetDescription() {
        return studySetDescription;
    }

    public void setStudySetDescription(String studySetDescription) {
        this.studySetDescription = studySetDescription;
    }

    //    @OneToMany(fetch = FetchType.LAZY, mappedBy = "studySetId", targetEntity = edu.uncc.genosets.datamanager.entity.FactStudySet.class)
    @Transient
    public Set<FactStudySet> getStudySetFacts() {
        return studySetFacts;
    }

    public void setStudySetFacts(Set<FactStudySet> studySetFacts) {
        this.studySetFacts = studySetFacts;
    }

    @Column(name = "LoadDate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(Date loadDate) {
        this.loadDate = loadDate;
    }

    @Column(name = "ModifiedDate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Transient
    @Override
    public String getDefaultName() {
        return DEFAULT_NAME;
    }

    @Transient
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
        final StudySetDatabase other = (StudySetDatabase) obj;
        if (this.studySetId != other.studySetId && (this.studySetId == null || !this.studySetId.equals(other.studySetId))) {
            return false;
        }
        return true;
    }
    
    
}
