/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator;

import edu.uncc.genosets.datamanager.api.FactType;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.awt.datatransfer.DataFlavor;
import java.util.List;

/**
 *
 * @author lucy
 */
public class Fact {
    public static final String TYPE_ANNOFACT = "Annotation";
    public static final String TYPE_ASSFACT = "Assembled Unit Acquisition";
    public static final String TYPE_GO = "Go Annotation";
    private final FactFlavor flavor;
    private final FactType factType;
    private List<AnnotationMethod> methods;
    private final String displayName;

    
    public Fact(FactType factType, FactFlavor flavor, String displayName){
        this.flavor = flavor;
        this.displayName = displayName;
        this.factType = factType;
    }

    void setMethods(List<AnnotationMethod> methods) {
        this.methods = methods;
    }


    public List<AnnotationMethod> getMethods() {
        return methods;
    }

    public String getDisplayName() {
        return displayName;
    }

    public DataFlavor getFlavor() {
        return flavor;
    }

    public FactType getFactType() {
        return factType;
    }
    
    
}
