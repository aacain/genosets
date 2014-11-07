package edu.uncc.genosets.datamanager.entity;
// Generated Oct 3, 2010 3:29:53 PM by Hibernate Tools 3.2.1.GA

import java.util.Collection;
import java.util.LinkedList;

/**
 * FeatureClusterClassification generated by hbm2java
 */
public class FeatureClusterClassification extends CustomizableEntity implements java.io.Serializable {

    public static final String DEFAULT_NAME = "FeatureClusterClassification";
    private Integer clusterClassificationId;
    //associations
    private Organism organism;
    private AssembledUnit assembledUnit;
    private Feature feature;
    private FeatureCluster featureCluster;
    private AnnotationMethod annotationMethod;
    //fact data
    private Integer totalFeatureInClusterByMethod;
    //association ids
    private Integer featureId;
    private Integer organismId;
    private Integer featureClusterId;
    private Integer annotationMethodId;
    private Integer assembledUnitId;
    //unmapped associations
    private Collection<FeatureClusterDetail> details;

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

    public FeatureClusterClassification() {
    }

    public FeatureClusterClassification(Organism organism, Feature feature, FeatureCluster featureCluster, AnnotationMethod annotationMethod, AssembledUnit assembledUnit, Integer totalFeatureInClusterByMethod) {
        this.organism = organism;
        this.feature = feature;
        this.featureCluster = featureCluster;
        this.annotationMethod = annotationMethod;
        this.assembledUnit = assembledUnit;
        this.totalFeatureInClusterByMethod = totalFeatureInClusterByMethod;
    }

    public Integer getClusterClassificationId() {
        return this.clusterClassificationId;
    }

    public void setClusterClassificationId(Integer clusterClassificationId) {
        this.clusterClassificationId = clusterClassificationId;
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

    public Integer getTotalFeatureInClusterByMethod() {
        return this.totalFeatureInClusterByMethod;
    }

    public void setTotalFeatureInClusterByMethod(Integer totalFeatureInClusterByMethod) {
        this.totalFeatureInClusterByMethod = totalFeatureInClusterByMethod;
    }

    public Collection<FeatureClusterDetail> getDetails() {
        return details;
    }

    public void setDetails(Collection<FeatureClusterDetail> details) {
        this.details = details;
    }

    public void addDetails(FeatureClusterDetail detail) {
        if (this.details == null) {
            this.details = new LinkedList<FeatureClusterDetail>();
        }
        this.details.add(detail);
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    public Integer getId() {
        return this.clusterClassificationId;
    }
}
