/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.persister;

import edu.uncc.genosets.datamanager.entity.CustomizableEntity;

/**
 * To use this persister, you should set all the values that
 * you know using the setter methods.
 * The fact can be just an empty fact and the id's will be set later.
 *
 * @author aacain
 */
public abstract class FactDetailPersister<F extends CustomizableEntity, E> implements Persister {
    protected F parentFact;
    protected E detailFact;
    protected String detailEntityName;

    public void setup(F parentFact, E detailFact, String detailEntityName){
        this.parentFact = parentFact;
        this.detailFact = detailFact;
        this.detailEntityName = detailEntityName;
    }

    public String getDetailEntityName() {
        return detailEntityName;
    }

    public void setDetailEntityName(String detailEntityName) {
        this.detailEntityName = detailEntityName;
    }

    public F getParentFact() {
        return parentFact;
    }

    public void setParentFact(F parentFact) {
        this.parentFact = parentFact;
    }

    public E getDetailFact() {
        return detailFact;
    }

    public void setDetailFact(E detailFact) {
        this.detailFact = detailFact;
    }
}
