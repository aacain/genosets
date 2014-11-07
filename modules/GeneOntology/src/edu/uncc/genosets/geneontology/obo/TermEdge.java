/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.geneontology.obo;

/**
 *
 * @author aacain
 */
public class TermEdge {
    private Term parent;
    private Term child;
    private String relationshipType;

    public TermEdge() {
    }

    public TermEdge(Term parent, Term child, String relationshipType) {
        this.parent = parent;
        this.child = child;
        this.relationshipType = relationshipType;
    }

    public Term getChild() {
        return child;
    }

    public void setChild(Term child) {
        this.child = child;
    }

    public Term getParent() {
        return parent;
    }

    public void setParent(Term parent) {
        this.parent = parent;
    }

    public String getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(String relationshipType) {
        this.relationshipType = relationshipType;
    }
}
