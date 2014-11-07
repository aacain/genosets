
package edu.uncc.genosets.datamanager.entity;

import java.lang.ref.SoftReference;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


/**
 *
 * @author aacain
 */
@Entity(name="StudySetExtended")
@Table(name="studyset")
public class StudySetExtended extends StudySetDatabase {
    private static final String DEFAULT_NAME = "StudySetExtended";
    Set<Integer> idSet;
    Set<FactStudySet> factStudySets;
    
   
    public StudySetExtended(){
        super();
    }
    
    @ElementCollection
    @CollectionTable(name="fact_studyset", joinColumns=@JoinColumn(name="StudySetId"))
    @Column(name="EntityId")
    public Set<Integer> getIdSet() {
        return idSet;
    }

    public void setIdSet(Set<Integer> idSet) {
        this.idSet = idSet;
    }
    
    @OneToMany(fetch= FetchType.LAZY, mappedBy = "studySet")
    @Fetch(FetchMode.SELECT)
    public Set<FactStudySet> getFactStudySets() {
        return factStudySets;
    }

    public void setFactStudySets(Set<FactStudySet> factStudySet) {
        this.factStudySets = factStudySet;
    }

    @Override
    @Transient
    public String getDefaultName() {
        return DEFAULT_NAME;
    }

}
