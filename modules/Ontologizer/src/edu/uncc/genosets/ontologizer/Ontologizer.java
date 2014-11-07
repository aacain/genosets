/*
 * 
 * 
 */
package edu.uncc.genosets.ontologizer;

import edu.uncc.genosets.ontologizer.util.GeneAnnotations;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.wizards.NodeDialogPanel;
import edu.uncc.genosets.geneontology.obo.OboDataObject;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetManager;
import edu.uncc.genosets.studyset.StudySetManager.StudySetManagerFactory;
import edu.uncc.genosets.studyset.TermCalculation;
import java.io.*;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressRunnable;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author aacain
 */
public class Ontologizer {

    public final static String OUTPUT = "output";
    public final static String SAMPLES = "samples";
    public final static String PROCESSED_SAMPLES = "processedSamples";
    public final static String POP_FILE = "population";
    public final static String EXT_COMPLETE = "cmp";
    private final static String formatString = "#.##";

    public List<GoEnrichment> runOntologizer(final List<StudySet> studySets, final OntologizerParameters params) {
        try {          
            FileObject temp = FileUtil.getConfigFile("Temp");
            if (temp == null) {
                temp = FileUtil.getConfigRoot().createFolder("Temp");
            }
            final FileObject root = temp.createFolder(FileUtil.findFreeFolderName(temp, "GO_enrichment"));
            //start a new thread to run ontologizer
            ProgressRunnable<List<GoEnrichment>> runnable = new ProgressRunnable<List<GoEnrichment>>() {
                public List<GoEnrichment> run(ProgressHandle handle) {
                    if (validateAnnotationsByOrganisms(studySets)) {
                        try {
                            handle.switchToDeterminate(100);
                            handle.progress("Getting the obo file", 10);
                            //copy the obo file to the root directory
                            handle.progress("Copying the obo file", 20);
                            //FileObject obo = copyObo(handle, root, params.getObo());
                            FileObject obo = params.getObo().getFileObject(handle);
                            if (obo == null) {
                                return null;
                            }

                            //get the focusEntity
                            FocusEntity focusEntity = null;
                            for (StudySet studySet : studySets) {
                                focusEntity = studySet.getFocusEntity();
                            }
                            //create association file if it doesn't exist
                            handle.progress("Creating the association file", 60);
                            FileObject ass = GeneAnnotations.createAnnotationsFile(root, focusEntity, params);
                            //create population files
                            handle.progress("Creating the population file", 70);
                            FileObject pop = createPopulation(root, params);

                            //check to see which studysets have been analyzed
                            handle.progress("Creating the study set files", 80);
                            createStudySets(root, studySets);

                            //create output folder
                            FileObject output = root.getFileObject(OUTPUT);
                            if (output == null) {
                                output = root.createFolder(OUTPUT);
                            }
                            //run for each study set
                            FileObject studyDir = root.getFileObject(SAMPLES);
                            LinkedList<String> listArgs = new LinkedList<String>();
                            listArgs.add("-g");
                            listArgs.add(FileUtil.toFile(obo).getAbsolutePath());
                            listArgs.add("-a");
                            listArgs.add(FileUtil.toFile(ass).getAbsolutePath());
                            listArgs.add("-p");
                            listArgs.add(FileUtil.toFile(pop).getAbsolutePath());
                            listArgs.add("-s");
                            listArgs.add(FileUtil.toFile(studyDir).getAbsolutePath());
                            listArgs.add("-o");
                            listArgs.add(FileUtil.toFile(output).getAbsolutePath());
                            listArgs.add("-n");
                            listArgs.add("-m");
                            listArgs.add(params.getMtc());
                            listArgs.add("-c");
                            listArgs.add(params.getCalculation());
                            handle.progress("Running Ontologizer", 90);
                            ontologizer.OntologizerCMD.main(listArgs.toArray(new String[listArgs.size()]));
                            handle.progress("Reading results", 95);
                            FileObject copyResult = copyResult(root);
                            List<GoEnrichment> newResults = new LinkedList<GoEnrichment>();
                            StudySetManager stdyMgr = StudySetManagerFactory.getDefault();
                            for (FileObject studySetResultFo : copyResult.getChildren()) {
                                StudySet ss = stdyMgr.getStudySet(studySetResultFo.getName());
                                if (ss != null) {
                                    List<GoEnrichment> results = getResults(ss, studySetResultFo);
                                    newResults.addAll(results);
                                }
                            }
                            for (GoEnrichment go : newResults) {
                                FileObject studyFolder = copyResult.getFileObject(go.getStudySet().getUniqueName());
                                FileObject detailsFo = studyFolder.createData(go.getUniqueName(), "details");
                                OntologizerParameters.createDetailsFile(detailsFo, params);
                            }
                            return newResults;

                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                            return null;
                        }
                    }
                    return null;
                }
            };
            List<GoEnrichment> newResults = ProgressUtils.showProgressDialogAndRun(runnable, "Running Ontologizer", true);
            if (newResults != null) {
                //add them to the enrichments
                EnrichmentServiceProvider provider = Lookup.getDefault().lookup(EnrichmentServiceProvider.class);
                for (GoEnrichment go : newResults) {
                    provider.addEnrichment(go.getStudySet(), go);
                }
            }
            return newResults;
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private FileObject createPopulation(FileObject studyManagerRoot, final OntologizerParameters params) throws IOException {
        FileObject pop = studyManagerRoot.createData("population", "pop");
        HashSet<Integer> allIds = new HashSet<Integer>();
        if (params.getPopulationSets() != null) {
            for (StudySet ss : params.getPopulationSets()) {
                allIds.addAll(ss.getIdSet());
            }
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(pop.getOutputStream()));
            HashSet<Integer> idSet = allIds;
            for (Integer id : idSet) {
                writer.write(id.toString());
                writer.newLine();
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return pop;
    }

    private FileObject createStudySets(FileObject root, List<StudySet> studySets) {
        FileObject sampleDir = root.getFileObject(SAMPLES);
        FileObject resultDir = root.getFileObject(OUTPUT);
        try {
            if (sampleDir != null) {
                sampleDir.delete();
            }
            sampleDir = root.createFolder(SAMPLES);

            //create a file for each of the unanalyzed studysets
            for (StudySet set : studySets) {
                //create sample file
                FileObject fo = sampleDir.getFileObject(set.getUniqueName(), "txt");
                if (fo == null) {
                    fo = sampleDir.createData(set.getUniqueName(), "txt");
                }
                //create population file 
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new OutputStreamWriter(fo.getOutputStream()));
                    Set<Integer> idSet = set.getIdSet();
                    for (Integer id : idSet) {
                        writer.write(id.toString());
                        writer.newLine();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return sampleDir;
    }

    private FileObject copyResult(FileObject root) {
        //get the target folder
        FileObject target = null;
        try {
            target = FileUtil.getConfigFile(DataManager.getDefault().getConnectionId() + "/Ontologizer");
            if (target == null) {
                target = FileUtil.createFolder(FileUtil.getConfigRoot(), DataManager.getDefault().getConnectionId() + "/Ontologizer");
            }
            //copy the file
            if (root != null) {
                FileObject output = root.getFileObject(OUTPUT);
                if (output != null) {
                    output.refresh();
                    //copy the output
                    FileObject[] children = output.getChildren();
                    //iterate all result files
                    for (FileObject fo : children) {
                        //get the studyset instance
                        if (fo.getName().startsWith("anno-")) {
                            String[] ss = fo.getName().split("-");
                            String studyName = ss[1];
                            //get the target study set folder
                            FileObject ssTarget = target.getFileObject(studyName);
                            if (ssTarget == null) {
                                ssTarget = target.createFolder(studyName);
                            }
                            //copy the table and anno file
                            FileObject anno = fo.copy(ssTarget, FileUtil.findFreeFileName(ssTarget, fo.getName(), "anno"), "anno");
                            String tableName = fo.getName().replaceFirst("anno-", "table-");
                            FileObject tempTable = output.getFileObject(tableName, "txt");
                            if (tempTable != null) {
                                tempTable.copy(ssTarget, anno.getName(), "table");
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger("edu.uncc.genosets.ontologizer.Ontologizer.class").log(Level.WARNING, "Could not copy ontologizer files.");
        }
        return target;
    }

    /**
     * Gets all the go enrichments that have not already been added to the
     * enrichment service provider for the study set.
     *
     * @param studySet
     * @param studySetFolder - the folder object for the study set
     * @return list of goEnrichments that have not been added to the
     * EnrichmentServiceProvider or an empty list is there are none.
     */
    public static List<GoEnrichment> getResults(StudySet studySet, FileObject studySetResultFo) {
        EnrichmentServiceProvider provider = Lookup.getDefault().lookup(EnrichmentServiceProvider.class);
        List<GoEnrichment> enrichments = new LinkedList<GoEnrichment>();
        if (studySetResultFo != null) {
            //iterate all result files
            if (studySetResultFo.isFolder()) {
                for (FileObject goFo : studySetResultFo.getChildren()) {
                    if (goFo.getMIMEType().endsWith("/x-ontologizeranno")) {
                        GoEnrichment existing = provider.find(studySet, goFo.getName());
                        if (existing == null) {
                            GoEnrichment goEnrichment = new GoEnrichment(goFo.getName(), studySet, goFo);
                            enrichments.add(goEnrichment);
                        }
                    }
                }
            }
        }
        return enrichments;
    }

    private static Double parseLong(String s) {
        DecimalFormat format = new DecimalFormat(formatString);
        ParsePosition parsePosition = new ParsePosition(0);
        Number number = format.parse(s, parsePosition);
        if (number == null) {
            return null;
        } else {
            return new Double(number.doubleValue());
        }
    }

    private static void parseAnnoString(String featureId, StringBuilder annoString, GoEnrichment goEnrichment) {
        //get annotations
        HashMap<String, TermCalculation> termMap = goEnrichment.getTermCalculationMap();
        int annoStart = annoString.indexOf("annotations={");
        if (annoStart > -1) {
            annoStart = annoStart + 13;
            int annoEnd = annoString.indexOf("}");
            String annos = annoString.substring(annoStart, annoEnd);
            String[] ss = annos.split(",");
            for (String anno : ss) {
                String x = anno;
                TermCalculation term = termMap.get(x);
                term.addFeature(Integer.parseInt(featureId));
            }

            //get parental annotations
            int pStart = annoString.indexOf("parental_annotations={");
            if (pStart > -1) {
                pStart = pStart + 22;
                int pEnd = annoString.lastIndexOf("}");
                String pAnnos = annoString.substring(pStart, pEnd);
                String[] pp = pAnnos.split(",");
                for (String pAnno : pp) {
                    String y = pAnno;
                    TermCalculation term = termMap.get(y);
                    term.addFeature(Integer.parseInt(featureId));
                }
            }
        }
    }

    private boolean validateAnnotationsByOrganisms(List<StudySet> studySets) {
        final HashSet<Organism> studySetOrgs = new HashSet<Organism>(Query.getStudySetOrganisms(studySets));
        HashSet<Organism> annotatedOrgs = new HashSet<Organism>(Query.getAnnotatedOrganisms());
        studySetOrgs.removeAll(annotatedOrgs);
        if (studySetOrgs.isEmpty()) {
            return true;
        }
        
        ChildFactory childFactory = new ChildFactory<Organism>() {

            @Override
            protected boolean createKeys(List toPopulate) {
                toPopulate.addAll(studySetOrgs);
                return true;
            }

            @Override
            protected Node createNodeForKey(Organism key) {
               Node n = new AbstractNode(Children.LEAF);
               n.setName(key.getSpecies());
               return n;
            }
            
        };
        DialogDescriptor dd = new DialogDescriptor(new NodeDialogPanel("The following organisms do not have associated GO annotations. Continue?", new AbstractNode(Children.create(childFactory, false)), false), "Missing GO Annotations", true, DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.CANCEL_OPTION, null);
        Object notify = DialogDisplayer.getDefault().notify(dd);
        if (notify == NotifyDescriptor.OK_OPTION) {
            return true;
        }

        return false;
    }

    private static class Query implements QueryCreator {

        static List<? extends Organism> getStudySetOrganisms(List<StudySet> studySets) {
            FocusEntity focusEntity = null;
            for (StudySet studySet : studySets) {
                focusEntity = studySet.getFocusEntity();
                break;
            }
            StringBuilder bldr = new StringBuilder("SELECT o FROM Organism as o, Location as l ");
            bldr.append(" WHERE o.organismId = l.").append(focusEntity.getIdProperty());
            bldr.append(" GROUP BY o.organismId");
            return DataManager.getDefault().createQuery(bldr.toString(), Organism.class);
        }

        static List<? extends Organism> getAnnotatedOrganisms() {
            StringBuilder bldr = new StringBuilder("SELECT o FROM Organism as o, Fact_Feature_GoAnno as GOAnno ");
            bldr.append(" WHERE GOAnno.organismId = o.organismId");
            bldr.append(" GROUP BY o.organismId");

            return DataManager.getDefault().createQuery(bldr.toString(), Organism.class);
        }
    }
}
