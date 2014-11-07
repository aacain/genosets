/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.fasta.load;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author aacain
 */
public class OrganismChildFactory extends ChildFactory<Organism>{
    
    private DataManager mgr;
    
    public OrganismChildFactory(){
        this.mgr = DataManager.getDefault();
    }

    @Override
    protected boolean createKeys(List<Organism> toPopulate) {
        toPopulate.addAll(mgr.getOrganisms());
        return true;
    }

    @Override
    protected Node createNodeForKey(Organism key) {
        return new OrganismNode(key);
    }
    
    
    public static class OrganismNode extends AbstractNode{
        private Organism organism;
        
        public OrganismNode(Organism organism){
            super(Children.LEAF);
            this.organism = organism;
            this.setDisplayName(organism.getSpecies() + organism.getStrain()==null ? "" : (" Strain " + organism.getStrain()) + " Project:" + organism.getProjectId());
            this.setName(organism.getOrganismId().toString());
        }
        
        public Organism getOrganism(){
            return this.organism;
        }
    }

}
