/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.geneontology.obo;

import java.util.*;

/**
 *
 * @author aacain
 */
public class Term {
    private String goId;
    private String name;
    private Boolean isObsolete = Boolean.FALSE;
    private HashMap<String, List<TermEdge>> parentsMap = new HashMap<String, List<TermEdge>>();

    /**
     * Constructor for term.  Default value for isObsolete is False.
     */
    public Term() {
    }

    
    public Term(String goId) {
        this.goId = goId;
    }

    public String getGoId(){
        return this.goId;
    }

    public void setGoId(String goId) {
        this.goId = goId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
        
    
    public Collection<? extends TermEdge> getParentsEdges(){
        HashSet<TermEdge> termEdges = new HashSet<TermEdge>();
        for (List<TermEdge> list : parentsMap.values()) {
            termEdges.addAll(list);
        }
        return termEdges;
    }
    
    public void addParent(Term parent, String relationshipType){
        TermEdge edge = new TermEdge(parent, this, relationshipType);
        List<TermEdge> get = parentsMap.get(parent.getGoId());
        if(get == null){
            get = new LinkedList<TermEdge>();
            parentsMap.put(parent.getGoId(), get);
        }
        get.add(edge);
    }

    /**
     * Sets this obsolete flag
     * @param obsolete - sets this term as the obsolete value if this parameter
     * is not equal to null. If this value is null, then it does not change.
     */
    void setIsObsolete(Boolean obsolete) {
        if(!(obsolete == null)){
            this.isObsolete = obsolete;
        }
    }

    /**
     * Getter for isObsolete
     * @return if this term is obsolete
     */
    public Boolean getIsObsolete() {
        return isObsolete;
    }
    
    
}
