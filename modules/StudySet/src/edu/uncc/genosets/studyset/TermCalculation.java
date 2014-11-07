/*
 * 
 * 
 */
package edu.uncc.genosets.studyset;

import edu.uncc.genosets.geneontology.obo.Obo;
import java.io.Serializable;
import java.util.HashSet;

/**
 *
 * @author aacain
 */
public class TermCalculation implements Serializable{

    private String termId;
    private int popTotal;
    private int popTerm;
    private int studyTotal;
    private int studyTerm;
    private int popFamily;
    private int studyFamily;
    private int numParents;
    private boolean trivial;
    private Double pValue;
    private Double pAdjusted;
    private Double pMin;
    private HashSet<Integer> featureIds = new HashSet<Integer>();

    public TermCalculation(String termId, int popTotal, int popTerm, int studyTotal, int studyTerm, int popFamily, int studyFamily, int numParents, boolean trivial, Double pValue, Double pAdjusted, Double pMin) {
        this.termId = termId;
        this.popTotal = popTotal;
        this.popTerm = popTerm;
        this.studyTotal = studyTotal;
        this.studyTerm = studyTerm;
        this.popFamily = popFamily;
        this.studyFamily = studyFamily;
        this.numParents = numParents;
        this.trivial = trivial;
        this.pValue = pValue;
        this.pAdjusted = pAdjusted;
        this.pMin = pMin;
    }

    public String getGoName() {
        return GoTerm.getGOName(termId);
    }
    
    public void addFeature(int featureId){
        featureIds.add(featureId);
    }

    public HashSet<Integer> getFeatureIds(){
        return featureIds;
    }

    public int getNumParents() {
        return numParents;
    }

    public Double getpAdjusted() {
        return pAdjusted;
    }

    public Double getpMin() {
        return pMin;
    }

    public Double getpValue() {
        return pValue;
    }

    public int getPopFamily() {
        return popFamily;
    }

    public int getPopTerm() {
        return popTerm;
    }

    public int getPopTotal() {
        return popTotal;
    }

    public int getStudyFamily() {
        return studyFamily;
    }

    public int getStudyTerm() {
        return studyTerm;
    }

    public int getStudyTotal() {
        return studyTotal;
    }

    public String getTermId() {
        return termId;
    }

    public boolean isTrivial() {
        return trivial;
    }
}
