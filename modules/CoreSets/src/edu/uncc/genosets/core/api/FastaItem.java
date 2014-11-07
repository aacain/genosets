/*
 * 
 * 
 */

package edu.uncc.genosets.core.api;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.Organism;

/**
 *
 * @author aacain
 */
public class FastaItem {
    private Organism organism;
    private AnnotationMethod assemblyMethod;
    private AnnotationMethod geneMethod;

    public FastaItem(Organism organism, AnnotationMethod assemblyMethod, AnnotationMethod geneMethod) {
        this.organism = organism;
        this.assemblyMethod = assemblyMethod;
        this.geneMethod = geneMethod;
    }

    public AnnotationMethod getAssemblyMethod() {
        return assemblyMethod;
    }

    public void setAssemblyMethod(AnnotationMethod assemblyMethod) {
        this.assemblyMethod = assemblyMethod;
    }

    public AnnotationMethod getGeneMethod() {
        return geneMethod;
    }

    public void setGeneMethod(AnnotationMethod geneMethod) {
        this.geneMethod = geneMethod;
    }

    public Organism getOrganism() {
        return organism;
    }

    public void setOrganism(Organism organism) {
        this.organism = organism;
    }
}
