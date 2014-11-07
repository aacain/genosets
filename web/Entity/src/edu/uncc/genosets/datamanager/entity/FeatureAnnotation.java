package edu.uncc.genosets.datamanager.entity;
// Generated Oct 3, 2010 3:29:53 PM by Hibernate Tools 3.2.1.GA

import java.util.HashSet;
import java.util.Set;

/**
 * FeatureAnnotation generated by hbm2java
 */
public class FeatureAnnotation extends CustomizableEntity implements java.io.Serializable {

    public static final String DEFAULT_NAME = "FeatureAnnotation";
    private Integer annotationId;
    private Organism organism;
    private Feature feature;
    private AnnotationMethod annotationMethod;
    private AssembledUnit assembledUnit;
    private Integer weight;
    private Set featureAnnotationDetails = new HashSet(0);
    private Set featureAnnotationDetails_1 = new HashSet(0);

    public FeatureAnnotation() {
    }

    public FeatureAnnotation(Organism organism, Feature feature, AnnotationMethod annotationMethod, AssembledUnit assembledUnit, Integer weight) {
        this.organism = organism;
        this.feature = feature;
        this.annotationMethod = annotationMethod;
        this.assembledUnit = assembledUnit;
        this.weight = weight;
    }

    public FeatureAnnotation(Organism organism, Feature feature, AnnotationMethod annotationMethod, AssembledUnit assembledUnit, Integer weight, Set featureAnnotationDetails, Set featureAnnotationDetails_1) {
        this.organism = organism;
        this.feature = feature;
        this.annotationMethod = annotationMethod;
        this.assembledUnit = assembledUnit;
        this.weight = weight;
        this.featureAnnotationDetails = featureAnnotationDetails;
        this.featureAnnotationDetails_1 = featureAnnotationDetails_1;
    }

    public Integer getAnnotationId() {
        return this.annotationId;
    }

    public void setAnnotationId(Integer annotationId) {
        this.annotationId = annotationId;
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

    public Integer getWeight() {
        return this.weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Set getFeatureAnnotationDetails() {
        return this.featureAnnotationDetails;
    }

    public void setFeatureAnnotationDetails(Set featureAnnotationDetails) {
        this.featureAnnotationDetails = featureAnnotationDetails;
    }

    public Set getFeatureAnnotationDetails_1() {
        return this.featureAnnotationDetails_1;
    }

    public void setFeatureAnnotationDetails_1(Set featureAnnotationDetails_1) {
        this.featureAnnotationDetails_1 = featureAnnotationDetails_1;
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_NAME;
    }
    
        @Override
    public Integer getId() {
        return this.annotationId;
    }
}
