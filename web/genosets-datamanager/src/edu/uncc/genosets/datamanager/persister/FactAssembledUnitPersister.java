/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datamanager.persister;

import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.AssembledUnitAquisition;
import edu.uncc.genosets.datamanager.entity.MolecularSequence;
import edu.uncc.genosets.datamanager.entity.Organism;
import org.hibernate.StatelessSession;
/**
 * To use this persister, you should set all the values that
 * you know using the setter methods.
 * The fact can be just an empty fact and the id's will be set later.
 *
 * @author aacain
 */
public class FactAssembledUnitPersister extends FactPersister<AssembledUnitAquisition>{
    
    private Organism organism;
    private AssembledUnit assUnit;
    private String sequence;

    public void setAssUnit(AssembledUnit assUnit) {
        this.assUnit = assUnit;
    }

    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
    
    public static FactAssembledUnitPersister instantiate(){
        return new FactAssembledUnitPersister();
    }
    
    @Override
    public void persist(StatelessSession session) {
        if (method.getAnnotationMethodId() == null) {
            session.insert(this.methodEntityName, this.method);
        }

        //set fact values
        fact.setAnnotationMethodId(method.getAnnotationMethodId());

        //set known associations
        if (fact.getOrganismId() == null) {
            if(organism.getOrganismId() == null){
                session.insert(Organism.DEFAULT_NAME, organism);
            }
        }
        fact.setOrganismId(organism.getOrganismId());
        
        boolean insertedAssUnit = false;
        if (fact.getAssembledUnitId() == null) {
            if(assUnit.getAssembledUnitId() == null){
                assUnit.setOrganism(organism);
                assUnit.setOrganismId(organism.getOrganismId());
                assUnit.setAssembledUnitLength(sequence == null ? null : sequence.length());
                session.insert(AssembledUnit.DEFAULT_NAME, assUnit);
                insertedAssUnit = true;
            }
        }
        fact.setAssembledUnitId(assUnit.getAssembledUnitId());
        
        if(sequence != null && insertedAssUnit){
            MolecularSequence seq = new MolecularSequence();
            seq.setForwardSequence(sequence);
            seq.setMolecularSequenceId(assUnit.getAssembledUnitId());
            session.insert(MolecularSequence.DEFAULT_NAME, seq);
        }
        
        //TODO: change this based when the mapping file changes
        fact.setOrganism(organism);
        fact.setAssembledUnit(assUnit);
        fact.setAnnotationMethod(method);
        
        
        session.insert(factEntityName, fact);
    }
    
}
