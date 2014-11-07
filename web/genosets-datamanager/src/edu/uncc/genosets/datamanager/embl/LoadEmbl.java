/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.embl;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.*;
import edu.uncc.genosets.datamanager.exceptions.RemoteServiceException;
import edu.uncc.genosets.datamanager.persister.FactAssembledUnitPersister;
import edu.uncc.genosets.datamanager.persister.FactLocationDetailPersister;
import edu.uncc.genosets.datamanager.persister.FactLocationPersister;
import edu.uncc.genosets.datamanager.persister.Persister;
import edu.uncc.genosets.datamanager.taxonomy.TaxonomyLookupFactory;
import edu.uncc.genosets.taskmanager.AbstractTask;
import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import edu.uncc.genosets.taskmanager.TaskManager;
import edu.uncc.genosets.taskmanager.TaskManagerFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author aacain
 */
public abstract class LoadEmbl {

    public abstract void start(String accessionId, AnnotationMethod method);

    public static LoadEmbl instantiate() {
        return new LoadEmblImpl();
    }

    public static class LoadEmblImpl extends LoadEmbl {

        @Override
        public void start(String accessionId, AnnotationMethod method) {
            ET_Task task = new ET_Task(accessionId, method);
            TaskManager mgr = TaskManagerFactory.getDefault();
            mgr.addPendingTask(task);
        }

        private static class ET_Task extends AbstractTask {

            private String accessionId;
            private final AnnotationMethod method;

            public ET_Task(String accessionId, AnnotationMethod method) {
                super("Downloading " + accessionId);
                this.accessionId = accessionId;
                this.method = method;
            }

            @Override
            public void performTask() {
                //get from embl
                EmblClient client = EmblClient.instantiate();
                String emblString = client.getEmblFile(accessionId);
                if (emblString != null) {
                    EmblTransformer transformer = EmblTransformer.instantiate();
                    transformer.transform(emblString, method);

                    //start persist task with org dependancy
                    PersistTask persistTask = new PersistTask(transformer);
                    TaskManager mgr = TaskManagerFactory.getDefault();
                    mgr.addPendingTask(persistTask);
                } else {
                    throw new RuntimeException("Could not download " + accessionId);
                }
            }

            @Override
            public void uninitialize() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void logErrors() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Organism getOrganismDependency() {
                return null;
            }

            @Override
            public void setOrganismDependency(Organism org) {
            }
        }

        public static class PersistTask extends AbstractTask {

            private final EmblTransformer trans;
            private final Organism organism;
            private final Date date;
            private final AnnotationMethod method;

            public PersistTask(EmblTransformer trans) {
                super("Persisting ");
                this.method = trans.getAnnotationMethod();
                this.setName("Persisting " + this.method.getMethodName());
                this.rank = 1;
                this.trans = trans;
                this.organism = trans.getOrganism();
                this.date = trans.getDate();
            }

            @Override
            public void performTask() {
                if (trans.getType().equals("SET")) {
                    LogFactory.getLog(LoadEmbl.class).warn("Could not load file: " + trans.getAnnotationMethod().getMethodName() + "because it is a set. You should include the range instead.");
                    TaskLogFactory.getDefault().log("Could not load file: " + trans.getAnnotationMethod().getMethodName() + "because it is a set. You should include the range instead.", "EMBL Load", "Could not load file: " + trans.getAnnotationMethod().getMethodName() + "because it is a set. You should include the range instead.", TaskLog.ERROR, new Date());
                    return;
                }
                //set method date
                this.method.setLoadDate(new Date());
                this.method.setMethodSourceType(trans.getType());
                //lookup organism taxonomy
                //TODO: fix this.  EMBL is overloaded with requests (I think).
                if (organism.getOrganismId() == null) {
                    if (organism.getTaxonomyIdentifier() != null) {
                        Collection<? extends edu.uncc.genosets.datamanager.taxonomy.TaxonomyLookup> taxLookups = TaxonomyLookupFactory.getTaxonomyLookups();
                        boolean found = false;
                        for (Iterator<? extends edu.uncc.genosets.datamanager.taxonomy.TaxonomyLookup> it = taxLookups.iterator(); !found && it.hasNext();) {
                            edu.uncc.genosets.datamanager.taxonomy.TaxonomyLookup taxLookup = it.next();
                            try {
                                found = taxLookup.lookupByTaxId(organism);
                            } catch (RemoteServiceException ex) {
                                LogFactory.getLog(LoadEmbl.class).info("Taxonomy remote service is unavailable", ex);
                            }
                        }
                    }
                }


                //get sequence date
                trans.getTransformers();
                this.setName("Persisting " + method.getMethodName());
                //persist assembledunit
                AssembledUnitAquisition fact = new AssembledUnitAquisition();
                fact.setAssembledUnitName(trans.getAssembledUnitName());
                FactAssembledUnitPersister assP = new FactAssembledUnitPersister();
                assP.setup(null, method, fact, null, AnnotationMethod.DEFAULT_NAME, AssembledUnitAquisition.DEFAULT_NAME);
                assP.setOrganism(organism);
                assP.setAssUnit(trans.getAssembledUnit());
                assP.setSequence(trans.getAssSequence());

                DataManager mgr = DataManager.getDefault();
                mgr.persist(Collections.singletonList(assP));
                TaskLogFactory.getDefault().log("Persisted assembled units for " + trans.getAssembledUnit().getAssembledUnitName(), trans.getAssembledUnit().getAssembledUnitName(), "Persisted assembled units for " + trans.getAssembledUnit().getAssembledUnitName(), TaskLog.INFO, new Date());
                

                List<Persister> persisterList = new LinkedList<Persister>();
                //persist features                
                FeatureCluster cluster = new FeatureCluster();
                cluster.setClusterCategory("Annotation");
                cluster.setClusterType("EMBL");
                cluster.setClusterName(trans.getAssembledUnit().getAssembledUnitName());
                for (EmblFeatureTransformer fTran : trans.getTransformers()) {
                    FactLocation lFact = fTran.getFact();
                    FactLocationPersister lp = FactLocationPersister.instantiate();
                    persisterList.add(lp);
                    lp.setup(cluster, method, lFact, FeatureCluster.DEFAULT_NAME, AnnotationMethod.DEFAULT_NAME, "AnnoFact");

                    //set what we know in fact
                    lFact.setOrganismId(organism.getOrganismId());
                    lFact.setAssembledUnitId(trans.getAssembledUnit().getAssembledUnitId());

                    //set what we don't know
                    lp.setFeature(lFact.getFeature());
                    lp.setLocation(lFact.getLocation());
                    lp.setSequence(fTran.getSequence());
                    if(lFact.getFeatureId() == null && lFact.getFeature() == null){
                        System.out.println("Here");
                    }
                    //create details
                    List<String[]> detailList = fTran.getDetailList();
                    if (detailList != null) {
                        int i = 1;
                        for (String[] strings : detailList) {
                            FactDetailLocation detail = new FactDetailLocation();
                            FactLocationDetailPersister dp = FactLocationDetailPersister.instantiate(lFact, detail, "AnnoFactDetail");
                            detail.setDetailType(strings[0]);
                            detail.setDetailValue(strings[1]);
                            detail.setDetailOrder(i);
                            persisterList.add(dp);
                            i++;
                        }
                    }
                }
                mgr.persist(persisterList);
                TaskLogFactory.getDefault().log("Persisted features for " + trans.getAssembledUnit().getAssembledUnitName(), trans.getAssembledUnit().getAssembledUnitName(), "Persisted features for " + trans.getAssembledUnit().getAssembledUnitName(), TaskLog.INFO, new Date());
                
            }

            @Override
            public void uninitialize() {
                
            }

            @Override
            public void logErrors() {
                
            }

            @Override
            public Organism getOrganismDependency() {
                return organism;
            }

            @Override
            public void setOrganismDependency(Organism org) {
            }
        }
    }
}
