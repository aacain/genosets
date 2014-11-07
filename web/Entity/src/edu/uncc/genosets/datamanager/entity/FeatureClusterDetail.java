/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.entity;

/**
 *
 * @author aacain
 */
public class FeatureClusterDetail extends CustomizableEntity implements java.io.Serializable{

    public static final String DEFAULT_NAME = "FeatureClusterDetail";
    private Integer featureClusterDetailId;
    //association ids
    private Integer featureId;
    private Integer organismId;
    private Integer featureClusterId;
    private Integer annotationMethodId;
    private Integer assembledUnitId;
    private Integer clusterFactId;
    //associations
    private FeatureClusterClassification clusterFact;
    private Organism organism;
    private AssembledUnit assembledUnit;
    private Feature feature;
    private FeatureCluster featureCluster;
    private AnnotationMethod annotationMethod;

    //values
    private String detailType;
    private String detailValue;

    public FeatureClusterDetail() {
    }

    public AnnotationMethod getAnnotationMethod() {
        return annotationMethod;
    }

    public void setAnnotationMethod(AnnotationMethod annotationMethod) {
        this.annotationMethod = annotationMethod;
    }

    public Integer getAnnotationMethodId() {
        return annotationMethodId;
    }

    public void setAnnotationMethodId(Integer annotationMethodId) {
        this.annotationMethodId = annotationMethodId;
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

    public FeatureClusterClassification getClusterFact() {
        return clusterFact;
    }

    public void setClusterFact(FeatureClusterClassification clusterFact) {
        this.clusterFact = clusterFact;
    }

    public Integer getClusterFactId() {
        return clusterFactId;
    }

    public void setClusterFactId(Integer clusterFactId) {
        this.clusterFactId = clusterFactId;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
    }

    public FeatureCluster getFeatureCluster() {
        return featureCluster;
    }

    public void setFeatureCluster(FeatureCluster featureCluster) {
        this.featureCluster = featureCluster;
    }

    public Integer getFeatureClusterDetailId() {
        return featureClusterDetailId;
    }

    public void setFeatureClusterDetailId(Integer featureClusterDetailId) {
        this.featureClusterDetailId = featureClusterDetailId;
    }

    public Integer getFeatureClusterId() {
        return featureClusterId;
    }

    public void setFeatureClusterId(Integer featureClusterId) {
        this.featureClusterId = featureClusterId;
    }

    public Integer getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Integer featureId) {
        this.featureId = featureId;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FeatureClusterDetail other = (FeatureClusterDetail) obj;
        if (this.featureClusterDetailId != other.featureClusterDetailId && (this.featureClusterDetailId == null || !this.featureClusterDetailId.equals(other.featureClusterDetailId))) {
            return false;
        }
        if (this.featureId != other.featureId && (this.featureId == null || !this.featureId.equals(other.featureId))) {
            return false;
        }
        if (this.organismId != other.organismId && (this.organismId == null || !this.organismId.equals(other.organismId))) {
            return false;
        }
        if (this.featureClusterId != other.featureClusterId && (this.featureClusterId == null || !this.featureClusterId.equals(other.featureClusterId))) {
            return false;
        }
        if (this.annotationMethodId != other.annotationMethodId && (this.annotationMethodId == null || !this.annotationMethodId.equals(other.annotationMethodId))) {
            return false;
        }
        if (this.assembledUnitId != other.assembledUnitId && (this.assembledUnitId == null || !this.assembledUnitId.equals(other.assembledUnitId))) {
            return false;
        }
        if (this.clusterFactId != other.clusterFactId && (this.clusterFactId == null || !this.clusterFactId.equals(other.clusterFactId))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.featureClusterDetailId != null ? this.featureClusterDetailId.hashCode() : 0);
        hash = 37 * hash + (this.featureId != null ? this.featureId.hashCode() : 0);
        hash = 37 * hash + (this.organismId != null ? this.organismId.hashCode() : 0);
        hash = 37 * hash + (this.featureClusterId != null ? this.featureClusterId.hashCode() : 0);
        hash = 37 * hash + (this.annotationMethodId != null ? this.annotationMethodId.hashCode() : 0);
        hash = 37 * hash + (this.assembledUnitId != null ? this.assembledUnitId.hashCode() : 0);
        hash = 37 * hash + (this.clusterFactId != null ? this.clusterFactId.hashCode() : 0);
        return hash;
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_NAME;
    }
    
    @Override
    public Integer getId() {
        return this.featureClusterDetailId;
    }
}
