/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datamanager.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 *
 * @author aacain
 */
@Entity(name = "StudySetTest")
@Table(name = "studyset")
public class StudySetTest extends CustomizableEntity implements java.io.Serializable {

    private static final String DEFAULT_NAME = "edu.uncc.genosets.datamanager.entity.StudySetTest";
    @GenericGenerator(name = "table-hilo-generator", strategy = "org.hibernate.id.MultipleHiLoPerTableGenerator",
            parameters = {
        @Parameter(name = "max_lo", value = "9")
    })
    //@GeneratedValue(generator = "table-hilo-generator")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "table-hilo-generator")
    @Id
    @Column(name = "StudySetId")
    private Integer studySetId;
    @Column(name = "EntityTable")
    private String entityTable;
    @Column(name = "studySetName")
    private String studySetName;
    @Column(name = "studySetDescription")
    private String studySetDescription;
    @Column(name = "LoadDate")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date loadDate;
    @Column(name = "ModifiedDate")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date modifiedDate;

    public StudySetTest() {
    }

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
    public String getDefaultName() {
        return DEFAULT_NAME;
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
        final StudySetTest other = (StudySetTest) obj;
        if (this.studySetId != other.studySetId && (this.studySetId == null || !this.studySetId.equals(other.studySetId))) {
            return false;
        }
        return true;
    }
}
