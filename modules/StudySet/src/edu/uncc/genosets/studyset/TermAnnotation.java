/*
 * 
 * 
 */

package edu.uncc.genosets.studyset;

import java.io.Serializable;

/**
 *
 * @author aacain
 */
public class TermAnnotation implements Serializable{
    private String termId;
    private Integer featureId;
    private boolean lowestLevel;

    public Integer getFeatureId() {
        return featureId;
    }

    public boolean isLowestLevel() {
        return lowestLevel;
    }

    public String getTermId() {
        return termId;
    }
}
