/*
 * 
 * 
 */
package edu.uncc.genosets.orthomcl;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.FactLocation;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.persister.FactLocationPersister;
import edu.uncc.genosets.datamanager.persister.Persister;
import edu.uncc.genosets.taskmanager.AbstractTask;
import edu.uncc.genosets.taskmanager.TaskManager;
import edu.uncc.genosets.taskmanager.TaskManagerFactory;
import edu.uncc.genosets.taskmanager.api.*;
import java.io.*;
import java.util.*;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author aacain
 */
public class OrthoMcl {

    public static final String METHOD_ENTITY_NAME = AnnotationMethod.DEFAULT_NAME;
    public static final String CLUSTER_ENTITY_NAME = FeatureCluster.DEFAULT_NAME;
    public static final String FACT_LOCATION_ENTITY_NAME = "OrthoFact";
    public static final String CLUSTER_CATEGORY = "Ortholog";
    public static final String CLUSTER_TYPE = "OrthoMCL";
    public static final String METHOD_SOURCE_TYPE = "OrthoMCL";
    List<AbstractProgramStep> steps;
    private HashMap<String, AbstractProgramParameter> paramLookup = new HashMap<String, AbstractProgramParameter>();
    private final String VERSION = "2.0";

    public OrthoMcl() {
    }

    private void initSteps() {
        steps = new LinkedList<AbstractProgramStep>();
        createConfigStep();
        createBlastSteps();
//        createInstallSchemaStep();
//        createFilterFastaStep();
//        createBlastStep();
//        createBlastParserStep();
    }

    private ProgramStep createConfigStep() {
        AbstractProgramStep step = new AbstractProgramStep();
        steps.add(step);
        step.setName("Configure database.");
        step.setCommand(null);
        step.setDescription("Creates a file of the datbase configurations");

        List<AbstractProgramParameter> params = new LinkedList<AbstractProgramParameter>();
        AbstractProgramParameter param = new AbstractProgramParameter(step);
        params.add(param);
        step.setProgramParameters(params);
        param.setName("Database Vendor");
        param.setParameterDescription("either 'oracle' or 'mysql' used by orthomclInstallSchema, orthomclLoadBlast, orthomclPairs");
        param.setParameterDefaultValue("mysql");
        param.setOptional(false);
        param.setIsUserRequired(false);


        param = new AbstractProgramParameter(step);
        params.add(param);
        step.setProgramParameters(params);
        param.setName("Connect string");
        param.setParameterDescription("the string required");
        param.setParameterDefaultValue("dbi:MySql:orthomcl");
        param.setOptional(false);
        param.setIsUserRequired(false);

        param = new AbstractProgramParameter(step);
        params.add(param);
        step.setProgramParameters(params);
        param.setName("dbLogin");
        param.setParameterDescription("login name");
        param.setParameterDefaultValue("uncc");
        param.setOptional(false);
        param.setIsUserRequired(true);

        param = new AbstractProgramParameter(step);
        params.add(param);
        step.setProgramParameters(params);
        param.setName("dbPassword");
        param.setParameterDescription("database password");
        param.setParameterDefaultValue("uncc");
        param.setOptional(false);
        param.setIsUserRequired(true);

        param = new AbstractProgramParameter(step);
        params.add(param);
        step.setProgramParameters(params);
        param.setName("percentMatchCutoff");
        param.setParameterDescription("blast similarities with percent match less than this value are ignored");
        param.setParameterDefaultValue("50");
        param.setOptional(false);
        param.setIsUserRequired(true);

        param = new AbstractProgramParameter(step);
        params.add(param);
        step.setProgramParameters(params);
        param.setName("evalueExponentCutoff");
        param.setParameterDescription("evalue cutoff greater are ignored");
        param.setParameterDefaultValue("-5");
        param.setOptional(false);
        param.setIsUserRequired(true);

        //TODO: the rest of the step

        //add params to lookup
        for (AbstractProgramParameter p : params) {
            paramLookup.put(p.getName(), param);
        }

        return step;
    }

    private ProgramStep createBlastSteps() {
        AbstractProgramStep step = new AbstractProgramStep();
        steps.add(step);
        step.setName("Blast");
        step.setCommand("blatp");
        step.setDescription("Blast step");

        List<AbstractProgramParameter> params = new LinkedList<AbstractProgramParameter>();
        AbstractProgramParameter param = new AbstractProgramParameter(step);
        params.add(param);
        step.setProgramParameters(params);
        param.setName("Blast e-value");
        param.setParameterDescription("blast value");
        param.setParameterDefaultValue("e-5");
        param.setOptional(false);
        param.setIsUserRequired(true);

        for (AbstractProgramParameter p : params) {
            paramLookup.put(p.getName(), param);
        }

        return step;
    }

    public OrthoMcl getDefault() {
        OrthoMcl impl = new OrthoMcl();
        impl.initSteps();
        return impl;
    }

    public List<AbstractProgramStep> getSteps() {
        return steps;
    }

    public void run(FileObject folder) {
        BufferedWriter writer = null;
        try {
            FileObject fastaFolder = folder.getFileObject("fasta");
            FileObject[] children = fastaFolder.getChildren();
            List<String> fileNames = new LinkedList<String>();
            for (FileObject fo : children) {
                fileNames.add(fo.getName());
            }
            FileObject config = createConfigFile(folder);
            FileObject script = folder.createData("orthoscript", "sh");

            writer = new BufferedWriter(new PrintWriter(script.getOutputStream()));
            writer.write("orthomclInstallSchema " + config.getNameExt());
            writer.newLine();
            writer.write("orthomclFilterFasta " + fastaFolder.getNameExt() + " 10 20");
            writer.newLine();
            writer.write("makeblastdb -in " + folder.getNameExt() + "/goodProteins.fasta -out " + "ortho.db");
            writer.newLine();
            writer.write("blastp -query " + folder.getNameExt() + "/goodProteins.fasta -out " + folder.getNameExt() + ".blast -db " + folder.getNameExt() + "ortho.db -evalue 1e-5 -outfmt 6");
            writer.newLine();
            writer.write("orthomclLoadBlast " + folder.getNameExt() + ".blast");
            writer.newLine();
            writer.write("orthomclPair");
            writer.newLine();
            writer.write("orthomclDumpPairsFile");
            writer.newLine();
            writer.write("mcl");
            writer.newLine();
            writer.write("orthomclMclToGroups");
            writer.newLine();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private FileObject createConfigFile(FileObject folder) {
        BufferedWriter writer = null;
        FileObject config = null;
        try {
            //configure database file
            config = folder.createData("orthomcl", "config");
            writer = new BufferedWriter(new PrintWriter(config.getOutputStream()));
            writer.write("dbVendor=mysql");
            writer.newLine();
            writer.write("dbConnectString=dbi:mysql:" + folder.getName());
            writer.newLine();
            writer.write("dbPassword=uncc");
            writer.newLine();
            writer.write("dbLogin=uncc");
            writer.newLine();
            writer.write("dbPassword=uncc");
            writer.newLine();
            writer.write("similarSequencesTable=SimilarSequences");
            writer.newLine();
            writer.write("orthologTable=Ortholog");
            writer.newLine();
            writer.write("inParalogTable=InParalog");
            writer.newLine();
            writer.write("coOrthologTable=CoOrtholog");
            writer.newLine();
            writer.write("interTaxonMatchView=InterTaxonMatch");
            writer.newLine();
            writer.write("percentMatchCutoff=50");
            writer.newLine();
            writer.write("evalueExponentCutoff=-5");
            writer.newLine();
            writer.write("oracleIndexTblSpc=NONE");
            writer.newLine();
            
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                writer.close();              
            } catch (IOException ex) {
            }        
        }
        return config;
    }

    public void load(File file, AnnotationMethod method) {
        LoadOrthoTask task = new LoadOrthoTask(file, method);
        TaskManager mgr = TaskManagerFactory.getDefault();
        mgr.addPendingTask(task);
    }

    public void load(File file, String methodName, String methodDescription) {
        AnnotationMethod method = new AnnotationMethod();
        method.setRunDate(new Date());
        method.setMethodVersion(getVersion());
        method.setMethodCategory(OrthoMcl.CLUSTER_CATEGORY);
        method.setMethodType(OrthoMcl.CLUSTER_TYPE);
        method.setMethodSourceType(OrthoMcl.METHOD_SOURCE_TYPE);
        method.setMethodName(methodName);
        method.setMethodDescription(methodDescription);

        load(file, method);
    }

    public String getVersion() {
        return VERSION;
    }

    private static class LoadOrthoTask extends AbstractTask {

        private final File file;
        private final AnnotationMethod method;
        final static int LOCATION_ID = 0;
        final static int FEATURE_ID = 1;
        final static int ASSUNIT_ID = 2;
        final static int ORG_ID = 3;
        final HashSet<String> uniqueIdSet = new HashSet<String>();

        public LoadOrthoTask(File file, AnnotationMethod method) {
            super("Load OrthoMCL");
            this.file = file;
            this.method = method;
        }

        @Override
        public void performTask() {
            //load the fasta ids
            loadFastaIds();

            File groupsFile = new File(file, "groups.txt");
            List<Persister> persisters = new LinkedList<Persister>();
            try {
                BufferedReader reader = new BufferedReader(new FileReader(groupsFile));
                String line = null;
                //Read each line and create a new Cluster for each line
                while ((line = reader.readLine()) != null) {
                    String[] split = line.split(": ");
                    if (split.length == 2) {
                        String cId = split[0];
                        FeatureCluster cluster = createCluster(cId);
                        //Now get all the features
                        String[] ss = split[1].split(" ");
                        //now we have each location
                        for (String uniqueId : ss) {
                            //remove from set
                            uniqueIdSet.remove(uniqueId);
                            String[] split1 = uniqueId.split("\\|");
                            String wholeId = split1[1];
                            String[] idArray = wholeId.split(";");
                            persisters.add(setupPersister(cluster, idArray));
                        }
                    }
                }//end reading lines
                //add unique genes
                addUnique(persisters);
                DataManager mgr = DataManager.getDefault();
                mgr.persist(persisters);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        private void addUnique(List<Persister> persisters) {
            int i = 0;
            for (String uniqueId : uniqueIdSet) {
                FeatureCluster cluster = createCluster("unique:" + i++);
                String[] split1 = uniqueId.split("\\|");
                String wholeId = split1[1];
                String[] idArray = wholeId.split(";");
                persisters.add(setupPersister(cluster, idArray));
            }
        }

        private void loadFastaIds() {
            //read each fasta file and add ids
            File fastaDir = new File(file, "fasta");
            File[] listFiles = fastaDir.listFiles();
            if (listFiles != null) {
                for (File f : listFiles) {
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new FileReader(f));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith(">")) {
                                uniqueIdSet.add(line.replace(">", ""));
                            }
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        try {
                            reader.close();
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        }

        private FeatureCluster createCluster(String clusterName) {
            FeatureCluster cluster = new FeatureCluster();
            cluster.setClusterName(clusterName);
            cluster.setClusterCategory(OrthoMcl.CLUSTER_CATEGORY);
            cluster.setClusterType(OrthoMcl.CLUSTER_TYPE);

            return cluster;
        }

        private FactLocationPersister setupPersister(FeatureCluster cluster, String[] ids) {
            FactLocationPersister p = FactLocationPersister.instantiate();
            FactLocation fact = new FactLocation();
            p.setup(cluster, method, fact,
                    OrthoMcl.CLUSTER_ENTITY_NAME,
                    OrthoMcl.METHOD_ENTITY_NAME,
                    OrthoMcl.FACT_LOCATION_ENTITY_NAME);
            //set what we dont know
            p.setCluster(cluster);
            p.setMethod(method);
            //set what we know
            fact.setLocationId(Integer.parseInt(ids[LOCATION_ID]));
            fact.setFeatureId(Integer.parseInt(ids[FEATURE_ID]));
            fact.setAssembledUnitId(Integer.parseInt(ids[ASSUNIT_ID]));
            fact.setOrganismId(Integer.parseInt(ids[ORG_ID]));
            return p;
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
}
