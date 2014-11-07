/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.entity;

/**
 *
 * @author aacain
 */
public class FactDetailLocation extends CustomizableEntity implements java.io.Serializable {

    public static final String DEFAULT_NAME = "AnnoFactDetail";
    private Integer factId;

    //associations
    private Organism organism;
    private AssembledUnit assembledUnit;
    private Feature feature;
    private FeatureCluster featureCluster;
    private AnnotationMethod annotationMethod;
    private MolecularSequence featureSequence;
    private FactLocation parentFact;
    private Location location;



    //association ids
    private Integer featureId;
    private Integer organismId;
    private Integer featureClusterId;
    private Integer annotationMethodId;
    private Integer assembledUnitId;
    private Integer featureSequenceId;
    private Integer parentFactId;
    private Integer locationId;

    private String detailType;
    private String detailValue;
    private Integer detailOrder;

    public Integer getFactId() {
        return factId;
    }

    public void setFactId(Integer factId) {
        this.factId = factId;
    }


    
    public String getDetailType() {
        return detailType;
    }

    public void setDetailType(String detailType) {
        this.detailType = detailType;
    }

    public String getDetailValue() {
        return detailValue;
    }

    public void setDetailValue(String detailValue) {
        this.detailValue = detailValue;
    }

    public Integer getDetailOrder() {
        return detailOrder;
    }

    public void setDetailOrder(Integer detailOrder) {
        this.detailOrder = detailOrder;
    }

    

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

    public FactLocation getParentFact() {
        return parentFact;
    }

    public void setParentFact(FactLocation parentFact) {
        this.parentFact = parentFact;
    }

    public Integer getParentFactId() {
        return parentFactId;
    }

    public void setParentFactId(Integer parentFactId) {
        this.parentFactId = parentFactId;
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

    @Override
    public String getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    public Integer getId() {
        return this.factId;
    }
}
