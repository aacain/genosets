package edu.uncc.genosets.datamanager.entity;
// Generated Oct 3, 2010 3:29:53 PM by Hibernate Tools 3.2.1.GA

/**
 * FeatureAnnotationDetail generated by hbm2java
 */
public class FeatureAnnotationDetail extends CustomizableEntity implements java.io.Serializable {

    public static final String DEFAULT_NAME = "FeatureAnnotationDetail";
    private Integer featureAnnotationDetailId;
    private Integer organismId;
    private Integer featureAnnotationId;
    private Integer featureId;
    private Integer annotationMethodId;
    private Integer detailTypeId;
    private Integer assembledUnitId;

    private Organism organism;
    private FeatureAnnotation featureAnnotation;
    private Feature feature;
    private AnnotationMethod annotationMethod;
    private FeatureDetailType featureDetailType;
    private AssembledUnit assembledUnit;
    private Integer numberOfDetailsCategoryPerFeature;
    private Integer numberOfDetailTypePerFeature;
    private String detailValue;

    public FeatureAnnotationDetail() {
    }

    public FeatureAnnotationDetail(Organism organism, FeatureAnnotation featureAnnotation, Feature feature, AnnotationMethod annotationMethod, FeatureDetailType featureDetailType, AssembledUnit assembledUnit, Integer numberOfDetailsCategoryPerFeature, Integer numberOfDetailTypePerFeature, String detailValue) {
        this.organism = organism;
        this.featureAnnotation = featureAnnotation;
        this.feature = feature;
        this.annotationMethod = annotationMethod;
        this.featureDetailType = featureDetailType;
        this.assembledUnit = assembledUnit;
        this.numberOfDetailsCategoryPerFeature = numberOfDetailsCategoryPerFeature;
        this.numberOfDetailTypePerFeature = numberOfDetailTypePerFeature;
        this.detailValue = detailValue;
    }

    public Integer getFeatureAnnotationDetailId() {
        return this.featureAnnotationDetailId;
    }

    public void setFeatureAnnotationDetailId(Integer featureAnnotationDetailId) {
        this.featureAnnotationDetailId = featureAnnotationDetailId;
    }

    public Organism getOrganism() {
        return this.organism;
    }

    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    public FeatureAnnotation getFeatureAnnotation() {
        return this.featureAnnotation;
    }

    public void setFeatureAnnotation(FeatureAnnotation featureAnnotation) {
        this.featureAnnotation = featureAnnotation;
    }

    public Feature getFeature() {
        return this.feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public AnnotationMethod getAnnotationMethod() {
        return this.annotationMethod;
    }

    public void setAnnotationMethod(AnnotationMethod annotationMethod) {
        this.annotationMethod = annotationMethod;
    }

    public FeatureDetailType getFeatureDetailType() {
        return this.featureDetailType;
    }

    public void setFeatureDetailType(FeatureDetailType featureDetailType) {
        this.featureDetailType = featureDetailType;
    }

    public AssembledUnit getAssembledUnit() {
        return this.assembledUnit;
    }

    public void setAssembledUnit(AssembledUnit assembledUnit) {
        this.assembledUnit = assembledUnit;
    }

    public Integer getNumberOfDetailsCategoryPerFeature() {
        return this.numberOfDetailsCategoryPerFeature;
    }

    public void setNumberOfDetailsCategoryPerFeature(Integer numberOfDetailsCategoryPerFeature) {
        this.numberOfDetailsCategoryPerFeature = numberOfDetailsCategoryPerFeature;
    }

    public Integer getNumberOfDetailTypePerFeature() {
        return this.numberOfDetailTypePerFeature;
    }

    public void setNumberOfDetailTypePerFeature(Integer numberOfDetailTypePerFeature) {
        this.numberOfDetailTypePerFeature = numberOfDetailTypePerFeature;
    }

    public String getDetailValue() {
        return this.detailValue;
    }

    public void setDetailValue(String detailValue) {
        this.detailValue = detailValue;
    }

    public Integer getOrganismId() {
        return organismId;
    }

    public void setOrganismId(Integer organismId) {
        this.organismId = organismId;
    }

    public Integer getAssembledUnitId() {
        return assembledUnitId;
    }

    public void setAssembledUnitId(Integer assembledUnitId) {
        this.assembledUnitId = assembledUnitId;
    }



    public Integer getDetailTypeId() {
        return detailTypeId;
    }

    public void setDetailTypeId(Integer detailTypeId) {
        this.detailTypeId = detailTypeId;
    }

    public Integer getFeatureAnnotationId() {
        return featureAnnotationId;
    }

    public void setFeatureAnnotationId(Integer featureAnnotationId) {
        this.featureAnnotationId = featureAnnotationId;
    }

    public Integer getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Integer featureId) {
        this.featureId = featureId;
    }

    public Integer getAnnotationMethodId() {
        return annotationMethodId;
    }

    public void setAnnotationMethodId(Integer annotationMethodId) {
        this.annotationMethodId = annotationMethodId;
    }

    

    @Override
    public String getDefaultName() {
        return DEFAULT_NAME;
    }
    
    @Override
    public Integer getId() {
        return this.featureAnnotationDetailId;
    }
}