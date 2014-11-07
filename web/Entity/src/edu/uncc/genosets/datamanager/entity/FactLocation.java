/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.entity;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class FactLocation extends CustomizableEntity implements java.io.Serializable {

    public static final String DEFAULT_NAME = "FactLocation";
    private Integer factId;
    //associations
    private Organism organism;
    private AssembledUnit assembledUnit;
    private Feature feature;
    private FeatureCluster featureCluster;
    private AnnotationMethod annotationMethod;
    private MolecularSequence featureSequence;
    private Location location;
    private Set<FactDetailLocation> annoFactDetails = new HashSet(0);
    //association ids
    private Integer featureId;
    private Integer organismId;
    private Integer featureClusterId;
    private Integer annotationMethodId;
    private Integer assembledUnitId;
    private Integer featureSequenceId;
    private Integer locationId;
    //values
    private String primaryName;
    private String featureType;
    private String product;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public MolecularSequence getFeatureSequence() {
        return featureSequence;
    }

    public void setFeatureSequence(MolecularSequence featureSequence) {
        this.featureSequence = featureSequence;
    }

    public Integer getFeatureSequenceId() {
        return featureSequenceId;
    }

    public void setFeatureSequenceId(Integer featureSequenceId) {
        this.featureSequenceId = featureSequenceId;
    }

    public Integer getAnnotationMethodId() {
        return annotationMethodId;
    }

    public void setAnnotationMethodId(Integer annotationMethodId) {
        this.annotationMethodId = annotationMethodId;
    }

    public Integer getAssembledUnitId() {
        return assembledUnitId;
    }

    public void setAssembledUnitId(Integer assembledUnitId) {
        this.assembledUnitId = assembledUnitId;
    }

    public Integer getFeatureClusterId() {
        return featureClusterId;
    }

    public void setFeatureClusterId(Integer featureClusterId) {
        this.featureClusterId = featureClusterId;
    }

    public Integer getOrganismId() {
        return organismId;
    }

    public void setOrganismId(Integer organismId) {
        this.organismId = organismId;
    }

    public Integer getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Integer featureId) {
        this.featureId = featureId;
    }

    public Integer getFactId() {
        return factId;
    }

    public void setFactId(Integer factId) {
        this.factId = factId;
    }

    public Organism getOrganism() {
        return this.organism;
    }

    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    public Feature getFeature() {
        return this.feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public FeatureCluster getFeatureCluster() {
        return this.featureCluster;
    }

    public void setFeatureCluster(FeatureCluster featureCluster) {
        this.featureCluster = featureCluster;
    }

    public AnnotationMethod getAnnotationMethod() {
        return this.annotationMethod;
    }

    public void setAnnotationMethod(AnnotationMethod annotationMethod) {
        this.annotationMethod = annotationMethod;
    }

    public AssembledUnit getAssembledUnit() {
        return this.assembledUnit;
    }

    public void setAssembledUnit(AssembledUnit assembledUnit) {
        this.assembledUnit = assembledUnit;
    }

    public Set<FactDetailLocation> getAnnoFactDetails() {
        return annoFactDetails;
    }

    public void setAnnoFactDetails(Set<FactDetailLocation> annoFactDetails) {
        this.annoFactDetails = annoFactDetails;
    }

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
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
        return this.factId;
    }
}
