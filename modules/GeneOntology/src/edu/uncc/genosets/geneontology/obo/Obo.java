/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.geneontology.obo;

import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author lucy
 */
public class Obo {

    private final HashMap<String, Term> termMap;
    private final OboDataObject obodao;

    public Obo(OboDataObject obodao, HashMap<String, Term> termMap) {
        this.obodao = obodao;
        this.termMap = termMap;
    }

    public Term getTerm(String goId) {
        return termMap.get(goId);
    }

    public Collection<Term> getTerms() {
        return termMap.values();
    }

    public OboDataObject getObodao() {
        return obodao;
    }
}
