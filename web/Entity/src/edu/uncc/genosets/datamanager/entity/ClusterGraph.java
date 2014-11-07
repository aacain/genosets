/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.entity;

/**
 *
 * @author aacain
 */
public class ClusterGraph {
    private Long graphId;

    private Integer parentId;
    private Integer childId;
    private Integer annotationMethodId;
    
    private FeatureCluster parent;
    private FeatureCluster child;
    private AnnotationMethod annotationMethod;

    private Integer level;
    private String relationType;

    public FeatureCluster getChild() {
        return child;
    }

    public void setChild(FeatureCluster child) {
        this.child = child;
    }

    public Integer getChildId() {
        return childId;
    }

    public void setChildId(Integer childId) {
        this.childId = childId;
    }

    public Long getGraphId() {
        return graphId;
    }

    public void setGraphId(Long graphId) {
        this.graphId = graphId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public FeatureCluster getParent() {
        return parent;
    }

    public void setParent(FeatureCluster parent) {
        this.parent = parent;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    
    
}
