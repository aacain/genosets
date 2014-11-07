/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.entity;

/**
 *
 * @author aacain
 */
public class Location extends CustomizableEntity{

    public static final String DEFAULT_NAME = "Location";
    public static final String PROP_minPosition = "minPosition";
    public static final String PROP_maxPosition = "maxPosition";
    private Integer locationId; //id property
    //association ids
    private Integer organismId;
    private Integer assembledUnitId;
    private Integer proteinSequenceId;
    private Integer assembledSequenceId;
    private Integer featureId;

    //association entities
    private MolecularSequence proteinSequence;
    private MolecularSequence assembledSequence;
    private Organism organism;
    private AssembledUnit assembledUnit;
    private Feature feature;

    //values
    private Integer minPosition;
    private Integer maxPosition;
    private Integer startPosition;
    private Integer endPosition;
    private Boolean isForward;
    private Integer gcCount;
    private Integer nucleotideLength;
    private Float gcPercent;
    private String primaryName;
    private String featureType;
    private String product;

    public Location() {
    }

    public Integer getAssembledSequenceId() {
        return assembledSequenceId;
    }

    public void setAssembledSequenceId(Integer assembledSequenceId) {
        this.assembledSequenceId = assembledSequenceId;
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

    public Integer getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Integer endPosition) {
        this.endPosition = endPosition;
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
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

    public Integer getGcCount() {
        return gcCount;
    }

    public void setGcCount(Integer gcCount) {
        this.gcCount = gcCount;
    }

    public Float getGcPercent() {
        return gcPercent;
    }

    public void setGcPercent(Float gcPercent) {
        this.gcPercent = gcPercent;
    }

    public Boolean getIsForward() {
        return isForward;
    }

    public void setIsForward(Boolean isForward) {
        this.isForward = isForward;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getMaxPosition() {
        return maxPosition;
    }

    public void setMaxPosition(Integer maxPosition) {
        this.maxPosition = maxPosition;
    }

    public Integer getMinPosition() {
        return minPosition;
    }

    public void setMinPosition(Integer minPosition) {
        this.minPosition = minPosition;
    }

    public Integer getNucleotideLength() {
        return nucleotideLength;
    }

    public void setNucleotideLength(Integer nucleotideLength) {
        this.nucleotideLength = nucleotideLength;
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

    public Integer getProteinSequenceId() {
        return proteinSequenceId;
    }

    public void setProteinSequenceId(Integer proteinSequenceId) {
        this.proteinSequenceId = proteinSequenceId;
    }

    public Integer getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    public MolecularSequence getAssembledSequence() {
        return assembledSequence;
    }

    public void setAssembledSequence(MolecularSequence assembledSequence) {
        this.assembledSequence = assembledSequence;
    }

    public MolecularSequence getProteinSequence() {
        return proteinSequence;
    }

    public void setProteinSequence(MolecularSequence proteinSequence) {
        this.proteinSequence = proteinSequence;
    }
    

    @Override
    public String getDefaultName() {
        return DEFAULT_NAME;
    }
    
    @Override
    public Integer getId() {
        return this.locationId;
    }

}
