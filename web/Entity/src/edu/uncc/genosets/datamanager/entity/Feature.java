package edu.uncc.genosets.datamanager.entity;
// Generated Oct 3, 2010 3:29:53 PM by Hibernate Tools 3.2.1.GA

import java.util.HashSet;
import java.util.Set;

/**
 * Feature generated by hbm2java
 */
public class Feature extends CustomizableEntity implements java.io.Serializable {

    public static final String DEFAULT_NAME = "Feature";
    public static final String PROP_minPosition = "minPosition";
    public static final String PROP_maxPosition = "maxPosition";
    private Integer featureId; //id property
    //association ids
    private Integer organismId;
    private Integer assembledUnitId;


    //association entities
    private Organism organism;
    private AssembledUnit assembledUnit;

    //values

    private String primaryName;
    private String featureType;
    private String product;

    public Feature() {
    }

    public AssembledUnit getAssembledUnit() {
        return assembledUnit;
    }

    public void setAssembledUnit(AssembledUnit assembledUnit) {
        this.assembledUnit = assembledUnit;
    }

    public Integer getAssembledUnitId() {
        return assembledUnitId;
    }

    public void setAssembledUnitId(Integer assembledUnitId) {
        this.assembledUnitId = assembledUnitId;
    }

    public Integer getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Integer featureId) {
        this.featureId = featureId;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public Organism getOrganism() {
        return organism;
    }

    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    public Integer getOrganismId() {
        return organismId;
    }

    public void setOrganismId(Integer organismId) {
        this.organismId = organismId;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public void setPrimaryName(String primaryName) {
        this.primaryName = primaryName;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }
    

    @Override
    public String getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    public Integer getId() {
        return this.featureId;
    }
}
