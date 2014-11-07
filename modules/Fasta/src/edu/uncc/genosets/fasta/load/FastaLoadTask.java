/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.fasta.load;

import edu.uncc.genosets.bioio.Fasta;
import edu.uncc.genosets.bioio.Fasta.FastaItem;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.AssembledUnitAquisition;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.persister.FactAssembledUnitPersister;
import edu.uncc.genosets.taskmanager.AbstractTask;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import org.openide.util.Exceptions;

/**
 *
 * @author aacain
 */
public class FastaLoadTask extends AbstractTask {

    private Organism org;
    private final AnnotationMethod method;
    private final File file;

    public FastaLoadTask(Organism org, AnnotationMethod method, File file) {
        super("Loading fasta " + method.getMethodName());
        this.org = org;
        this.method = method;
        this.file = file;
    }

    @Override
    public void performTask() {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            List<FactAssembledUnitPersister> persisters = new LinkedList<FactAssembledUnitPersister>();
            Fasta parse = Fasta.parse(in);
            List<? extends FastaItem> items = parse.getItems();
            for (FastaItem f : items) {
                FactAssembledUnitPersister persister = FactAssembledUnitPersister.instantiate();
                persisters.add(persister);
                AssembledUnit ass = new AssembledUnit();
                ass.setAssembledUnitName(f.getId());
                ass.setAccessionVersion(f.getId());
                ass.setSequenceLength(f.getSequence().length());
                AssembledUnitAquisition fact = new AssembledUnitAquisition();
                persister.setFact(fact);
                persister.setFactEntityName(AssembledUnitAquisition.DEFAULT_NAME);
                persister.setMethodEntityName(AnnotationMethod.DEFAULT_NAME);
                persister.setAssUnit(ass);
                persister.setMethod(method);
                persister.setOrganism(org);
                persister.setSequence(f.getSequence());
            } 
            DataManager.getDefault().persist(persisters);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Override
    public void uninitialize() {
    }

    @Override
    public void logErrors() {
    }

    @Override
    public Organism getOrganismDependency() {
        return this.org;
    }

    @Override
    public void setOrganismDependency(Organism org) {
        this.org = org;
    }
}
