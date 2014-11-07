/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.gffloader.download;

import edu.uncc.genosets.bioio.Gff;
import edu.uncc.genosets.bioio.Gff.GffFeature;
import edu.uncc.genosets.bioio.Gff.Region;
import edu.uncc.genosets.datamanager.api.*;
import edu.uncc.genosets.datamanager.entity.*;
import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author aacain
 */
@NbBundle.Messages("AnnoGffFormat_displayName=GFF3 Format")
public class AnnoGffFormat implements DownloadFormat, Editable, Savable {

    protected final static String PROP_MUGSY_FORMAT = "PROP_MUGSY_FORMAT";
    protected final static String PROP_INCLUDE_FASTA = "PROP_INCLUDE_FASTA";
    protected final static String PROP_MINIMAL_DETAILS = "PROP_MINIMAL_DETAILS";
    protected final static String PROP_ORIGINAL_ASSUNIT_IDS = "PROP_ORIGINAL_ASSUNIT_IDS";
    protected final static String PROP_CREATE_ASS_MAPPING = "PROP_CREATE_ASS_MAPPING";
    private static final String ITERATOR_PATH = "Templates/Downloads/edu-uncc-genosets-datamanager-api-AnnotationFactType/FASTA Protein (faa)";
    private transient DownloadSet ds;
    private String myDirPath = "";
    private transient boolean mugsyFormat = false;
    private transient boolean includeFasta = true;
    private transient boolean minimalDetails = false;
    private transient boolean useOriginalAssIds = false;
    private transient boolean createAssMapping = false;

    public AnnoGffFormat() {
    }

    public AnnoGffFormat(DownloadSet ds) {
        this.ds = ds;
    }

    public boolean isMugsyFormat() {
        return mugsyFormat;
    }

    public void setIsMugsyFormat(boolean isMugsy) {
        this.mugsyFormat = isMugsy;
    }

    public boolean isIncludeFasta() {
        return includeFasta;
    }

    public void setIncludeFasta(boolean includeFasta) {
        this.includeFasta = includeFasta;
    }

    public boolean isMinimalDetails() {
        return minimalDetails;
    }

    public void setMinimalDetails(boolean minimalDetails) {
        this.minimalDetails = minimalDetails;
    }

    public boolean isUseOriginalAssIds() {
        return useOriginalAssIds;
    }

    public void setUseOriginalAssIds(boolean useOriginalAssIds) {
        this.useOriginalAssIds = useOriginalAssIds;
    }

    public boolean isCreateAssMapping() {
        return createAssMapping;
    }

    public void setCreateAssMapping(boolean createAssMapping) {
        this.createAssMapping = createAssMapping;
    }

    @Override
    public String getFormatFolderName() {
        return NbBundle.getMessage(AnnoGffFormat.class, "AnnoGffFormat_displayName");
    }

    @Override
    public void download() throws DownloadException {
        try {
            FileObject root = ds.getRootFileObject();
            //create a unique name for the folder
            FileObject myDir = createFreeFolder(root, 0);
            myDirPath = myDir.getPath();
            //create files
            Set<AnnotationMethod> methods = new HashSet<AnnotationMethod>(ds.getMethods());
            List<Object[]> results = Query.createQuery(methods);
            HashMap<Integer, List<Object[]>> map = groupOrganisms(results);
            createFiles(myDir, ds.getMethods());
        } catch (Exception ex) {
            throw new DownloadException(ex);
        }
    }

    private HashMap<Integer, List<Object[]>> groupOrganisms(List<Object[]> results) {
        HashMap<Integer, List<Object[]>> map = new HashMap<Integer, List<Object[]>>();
        for (Object[] objs : results) {
            Feature feature = (Feature) objs[Query.IDX_FEATURE];
            List<Object[]> get = map.get(feature.getOrganismId());
            if (get == null) {
                get = new LinkedList<Object[]>();
                map.put(feature.getOrganismId(), get);
            }
            get.add(objs);
        }
        return map;
    }

    private void createFiles(FileObject myDir, List<? extends AnnotationMethod> methods) {
        //HashMap<Integer, List<Object[]>> groups;
        //create mapping file if necessary
        File assMapping = null;
        if (isCreateAssMapping()) {
            assMapping = FileUtil.toFile(createFreeFile(myDir, "assembledUnitMapping", "map", 0));
        }

        for (AnnotationMethod method : methods) {
            HashMap<Integer, List<AssembledUnitAquisition>> orgToAssMap = AssUnitByMethodQuery.createQuery(method.getAnnotationMethodId());
            List<Object[]> results = Query.createQuery(Collections.singletonList(method));
            HashMap<Integer, List<Object[]>> orgGroups = groupOrganisms(results);

            for (Map.Entry<Integer, List<Object[]>> entry : orgGroups.entrySet()) {
                Integer orgId = entry.getKey();
                List<Object[]> orgValues = entry.getValue();
                //now sort by assembledUnit
                HashMap<Integer, List<Object[]>> assGroups = new HashMap<Integer, List<Object[]>>();
                for (Object[] objs : orgValues) {
                    Feature feature = (Feature) objs[Query.IDX_FEATURE];
                    List<Object[]> get = assGroups.get(feature.getAssembledUnitId());
                    if (get == null) {
                        get = new LinkedList<Object[]>();
                        assGroups.put(feature.getAssembledUnitId(), get);
                    }
                    get.add(objs);
                }

                Gff gff = new Gff();
                List<Region> regions = new ArrayList<Region>(assGroups.size());
                gff.setRegionList(regions);
                for (Map.Entry<Integer, List<Object[]>> assEntry : assGroups.entrySet()) {
                    Integer assId = assEntry.getKey();
                    List<Object[]> objs = assEntry.getValue();
                    AssembledUnit assembledUnit = Query.getAssembledUnit(assId);
                    List<GffFeature> featureList = new ArrayList<GffFeature>(objs.size());
                    Region region = new Region(assId.toString(), "0", assembledUnit.getAssembledUnitLength() == null ? "0" : assembledUnit.getAssembledUnitLength().toString(), featureList);
                    regions.add(region);
                    for (Object[] obj : objs) {
                        Feature feature = (Feature) obj[Query.IDX_FEATURE];
                        Integer locId = (Integer) obj[Query.IDX_LOCID];
                        String min = Integer.toString((Integer) obj[Query.IDX_MINPOSITION]);
                        String max = Integer.toString((Integer) obj[Query.IDX_MAXPOSITION]);
                        String strand = "+";
                        Boolean tmp = (Boolean) obj[Query.IDX_STRAND];
                        if (tmp != null) {
                            strand = tmp.equals(Boolean.FALSE) ? "-" : "+";
                        }
                        List<String> attributes = new LinkedList<String>();
                        attributes.add("ID=" + locId.toString());
                        attributes.add("locus_tag=" + feature.getPrimaryName());
                        if (feature.getPrimaryName() != null) {
                            attributes.add("old_name=" + feature.getPrimaryName());
                        }
                        String featureType = feature.getFeatureType();
                        if (isMugsyFormat() && featureType.equals("CDS")) {
                            featureType = "gene";
                        }
                        GffFeature item = new GffFeature(useOriginalAssIds ? assembledUnit.getAssembledUnitName() : assId.toString(), "GenoSets", featureType, min, max, ".", strand, ".", attributes);
                        featureList.add(item);
                    }
                }
                //get the organism
                Organism org = Query.getOrganism(orgId);
                String fileName = org.getStrain().replaceAll("[^(a-zA-Z0-9)]{1,}", "_");
                fileName = fileName.concat(method.getMethodName().replaceAll("[^(a-zA-Z0-9)]{1,}", "_"));
                FileObject fileObject = createFreeFile(myDir, fileName, "gff", 0);
                try {
                    Gff.create(FileUtil.toFile(fileObject), Boolean.TRUE, gff);
                    //write fasta if necessary
                    if (includeFasta) {
                        writeFasta(FileUtil.toFile(fileObject), orgToAssMap.get(org.getOrganismId()));
                    }
                } catch (IOException ex) {
                    TaskLogFactory.getDefault().log("Unable to create file for " + fileName, "AnnoGffFormat", "Unable to create file for " + fileName, TaskLog.ERROR, new Date());
                    Exceptions.printStackTrace(ex);
                }
                //write the mapping file
                try {
                    //write the assMapping if necessary
                    writeAssUnitMapping(assMapping, orgToAssMap.get(org.getOrganismId()));
                } catch (IOException ex) {
                    Exceptions.printStackTrace(new IOException("Could not write to assembled unit mapping file.  The other files should be okay", ex));
                }
            }//end of file group
        }//end of method
    }

    private void writeFasta(File file, Collection<AssembledUnitAquisition> asses) throws IOException {
        List<Integer> assIds = new ArrayList<Integer>(asses.size());
        for (AssembledUnitAquisition f : asses) {
            assIds.add(f.getAssembledUnitId());
        }
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new FileWriter(file, Boolean.TRUE));
            List<Object[]> createQuery = NucSequenceQuery.createQuery(assIds);
            br.write("##FASTA");
            br.newLine();
            for (Object[] objects : createQuery) {
                MolecularSequence ms = (MolecularSequence) objects[0];
                AssembledUnit ass = (AssembledUnit) objects[1];
                br.write(">");
                if (useOriginalAssIds) {
                    br.write(ass.getAssembledUnitName());
                } else {
                    br.write(ass.getAssembledUnitId());
                }
                br.newLine();
                StringBuilder bldr = new StringBuilder(ms.getForwardSequence());
                int running = 0;
                for (int i = 0; i < bldr.length(); i = i + 60) {
                    String line = bldr.substring(i, (i + 60) < bldr.length() ? (i + 60) : bldr.length());
                    running = running + line.length();
                    br.write(line);
                    br.newLine();
                }
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    private void writeAssUnitMapping(File file, Collection<AssembledUnitAquisition> assFacts) throws IOException {
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new FileWriter(file, Boolean.TRUE));
            for (AssembledUnitAquisition assFact : assFacts) {
                if (assFact.getAssembledUnitName() != null) {
                    br.write(assFact.getAssembledUnitId().toString());
                    br.write("\t");
                    br.write(assFact.getAssembledUnitName());
                    br.newLine();
                }
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                }
            }
        }

    }

    public FileObject createFreeFolder(FileObject root, int num) {
        if (num < 10) {
            num++;
            try {
                return FileUtil.createFolder(root, FileUtil.findFreeFolderName(root, getFormatFolderName()));
            } catch (IOException ex) {
                return createFreeFolder(root, num);
            }
        }
        return null;
    }

    public FileObject createFreeFile(FileObject dir, String name, String ext, int num) {
        if (num < 10) {
            num++;
            try {
                return FileUtil.createData(dir, FileUtil.findFreeFileName(dir, name, ext) + "." + ext);
            } catch (IOException ex) {
                return createFreeFile(dir, name, ext, num);
            }
        }
        return null;
    }

    @Override
    public String getIteratorPath() {
        return ITERATOR_PATH;
    }

    @Override
    public void save(FileObject fo) throws IOException {
        fo.setAttribute("originalFile", "DownloadFormat/edu-uncc-genosets-gffloader-download-AnnoGffFormat.instance");
    }

    @Override
    public void load(DownloadSet set, FileObject fo) {
        this.ds = set;
    }

    private static class Query implements QueryCreator {

        static final int IDX_FEATURE = 0;
        static final int IDX_MINPOSITION = 1;
        static final int IDX_MAXPOSITION = 2;
        static final int IDX_STRAND = 3;
        static final int IDX_LOCID = 4;

        static List<Object[]> createQuery(Collection<? extends AnnotationMethod> methods) {
            StringBuilder bldr = new StringBuilder("SELECT feature, l.minPosition, l.maxPosition, l.isForward, l.locationId, fact.primaryName, fact.featureType, fact.product from Feature as feature, Location as l, AnnoFact as fact");
            bldr.append(" WHERE feature.featureId = fact.featureId AND l.locationId = fact.locationId AND(");
            int i = 0;
            for (AnnotationMethod m : methods) {
                if (i != 0) {
                    bldr.append(" OR ");
                }
                bldr.append(" fact.annotationMethodId = ").append(m.getAnnotationMethodId());
                i++;
            }
            bldr.append(") ORDER BY fact.locationId");

            return DataManager.getDefault().createQuery(bldr.toString());
        }

        static Organism getOrganism(Integer organismId) {
            return (Organism) DataManager.getDefault().get(Organism.DEFAULT_NAME, organismId);
        }

        static AssembledUnit getAssembledUnit(Integer assUnitId) {
            return (AssembledUnit) DataManager.getDefault().get(AssembledUnit.DEFAULT_NAME, assUnitId);
        }
    }

    private static class NucSequenceQuery implements QueryCreator {

        static List<Object[]> createQuery(Collection<Integer> asses) {
            StringBuilder inQuery = new StringBuilder("(");
            for (Integer ass : asses) {
                inQuery.append(ass.toString()).append(",");
            }
            inQuery.replace(inQuery.length() - 1, inQuery.length(), ")");
            return DataManager.getDefault().createQuery("SELECT m, ass FROM MolecularSequence as m, AssembledUnit as ass WHERE ass.assembledUnitId = m.molecularSequenceId and m.molecularSequenceId in " + inQuery.toString());
        }
    }

    private static class AssUnitByMethodQuery implements QueryCreator {

        static HashMap<Integer, List<AssembledUnitAquisition>> createQuery(Integer methodId) {
            HashMap<Integer, List<AssembledUnitAquisition>> orgToAssMap = new HashMap<Integer, List<AssembledUnitAquisition>>();
            List<Object[]> createQuery = DataManager.getDefault().createQuery("SELECT f, org.organismId FROM AssembledUnitAquisition as f, Organism as org, AssembledUnit as ass WHERE ass.assembledUnitId = f.assembledUnitId AND org.organismId = f.organismId AND f.annotationMethodId = " + methodId);
            assert !createQuery.isEmpty();
            for (Object[] objects : createQuery) {
                List<AssembledUnitAquisition> get = orgToAssMap.get((Integer) objects[1]);
                if (get == null) {
                    get = new LinkedList<AssembledUnitAquisition>();
                    orgToAssMap.put((Integer) objects[1], get);
                }
                get.add((AssembledUnitAquisition) objects[0]);
            }
            return orgToAssMap;
        }
    }
}
