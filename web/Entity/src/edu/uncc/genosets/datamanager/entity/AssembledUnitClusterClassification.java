package edu.uncc.genosets.datamanager.entity;
// Generated Oct 3, 2010 3:29:53 PM by Hibernate Tools 3.2.1.GA

/**
 * AssembledUnitClusterClassification generated by hbm2java
 */
public class AssembledUnitClusterClassification extends CustomizableEntity implements java.io.Serializable {

    public static final String DEFAULT_NAME = "AssembledUnitClusterClassification";
    private Integer assembledUnitClusterClassificationId;
    private Organism organism;
    private AnnotationMethod annotationMethod;

    public AssembledUnitClusterClassification() {
    }

    public AssembledUnitClusterClassification(Organism organism, AnnotationMethod annotationMethod) {
        this.organism = organism;
        this.annotationMethod = annotationMethod;
    }

    public Integer getAssembledUnitClusterClassificationId() {
        return this.assembledUnitClusterClassificationId;
    }

    public void setAssembledUnitClusterClassificationId(Integer assembledUnitClusterClassificationId) {
        this.assembledUnitClusterClassificationId = assembledUnitClusterClassificationId;
    }

    public Organism getOrganism() {
        return this.organism;
    }

    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    public AnnotationMethod getAnnotationMethod() {
        return this.annotationMethod;
    }

    public void setAnnotationMethod(AnnotationMethod annotationMethod) {
        this.annotationMethod = annotationMethod;
    }

    @Override
    public String getDefaultName() {
        return DEFAULT_NAME;
    }

    @Override
    public Integer getId() {
        return assembledUnitClusterClassificationId;
    }
}