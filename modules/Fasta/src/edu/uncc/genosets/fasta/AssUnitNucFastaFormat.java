/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.fasta;

import edu.uncc.genosets.bioio.Fasta;
import edu.uncc.genosets.bioio.Fasta.FastaItem;
import edu.uncc.genosets.datamanager.api.*;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.io.IOException;
import java.util.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author aacain
 */
public class AssUnitNucFastaFormat implements DownloadFormat, Savable, Editable {

    private static final String ITERATOR_PATH = "Templates/Downloads/edu-uncc-genosets-datamanager-api-AssUnitFactType/FASTA Nucleotide (fna)";
    private boolean filePerOrganism;
    private boolean filePerMethod;
    private final DownloadSet ds;
    
    
    public AssUnitNucFastaFormat(DownloadSet ds) {
        this.ds = ds;
    }

    public boolean isFilePerMethod() {
        return filePerMethod;
    }

    public void setFilePerMethod(boolean filePerMethod) {
        this.filePerMethod = filePerMethod;
    }

    public boolean isFilePerOrganism() {
        return filePerOrganism;
    }

    public void setFilePerOrganism(boolean filePerOrganism) {
        this.filePerOrganism = filePerOrganism;
    }

    @Override
    public String getFormatFolderName() {
        // return the name that is to be displayed to the user
        return NbBundle.getMessage(AssUnitNucFastaWizardIterator.class, "AssUnitNucFastaWizardIterator_displayName");
    }

    @Override
    public void download() throws DownloadException {
        //download files
        try {
            FileObject root = ds.getRootFileObject();
            //create a unique name for the folder
            FileObject myDir = createFreeFolder(root, 0);
            //create files
            Set<AnnotationMethod> methods = new HashSet<AnnotationMethod>(ds.getMethods());
            List<Object[]> results = MyQueryCreator.createQuery(methods);
            if (results == null || results.isEmpty()) {
                throw new DownloadException();
            }
            HashMap<String, List<Object[]>> group = group(results);
            createFiles(myDir, group);
        } catch (Exception ex) {
            throw new DownloadException(ex);
        }
    }

    private HashMap<String, List<Object[]>> group(List<Object[]> result) {
        HashMap<String, List<Object[]>> map = new HashMap<String, List<Object[]>>();
        for (Object[] obj : result) {
            StringBuilder key = new StringBuilder();
            if (filePerOrganism) {
                key.append((Integer) obj[MyQueryCreator.ORG]);
                key.append("\t");
            }
            if (filePerMethod) {
                key.append((Integer) obj[MyQueryCreator.METHOD]);
                key.append("\t");
            }
            List<Object[]> get = map.get(key.toString());
            if (get == null) {
                get = new LinkedList<Object[]>();
                map.put(key.toString(), get);
            }
            get.add(obj);
        }
        return map;
    }

    private void createFiles(FileObject myDir, HashMap<String, List<Object[]>> group) throws DownloadException {
        int i = 0;
        for (Map.Entry<String, List<Object[]>> entry : group.entrySet()) {
            String key = entry.getKey();
            List<Object[]> vByKey = entry.getValue();
            //convert list to fasta
            List<FastaItem> itemsList = new LinkedList<FastaItem>();
            for (Object[] obj : vByKey) {
                Integer assId = (Integer)obj[MyQueryCreator.ASS];
                String seq = (String)obj[MyQueryCreator.SEQ];
                //System.out.println(assId + "\t" + seq.length());
                FastaItem item = new FastaItem(((Integer) obj[MyQueryCreator.ASS]).toString(), (String) obj[MyQueryCreator.SEQ]);
                itemsList.add(item);
            }
            Fasta fasta = new Fasta();
            fasta.setItems(itemsList);

            FileObject fo = null;
            if (vByKey != null && !vByKey.isEmpty()) {
                StringBuilder bldr = new StringBuilder();
                if (isFilePerOrganism()) {
                    Organism org = MyQueryCreator.getOrganismName((Integer) vByKey.get(0)[MyQueryCreator.ORG]);
                    bldr.append(org.getStrain() != null ? org.getStrain().replaceAll("[\\s{1,}.\\/]", "_") : org.getOrganismId());
                    if (isFilePerMethod()) {
                        bldr.append("_");
                    }
                }
                if (isFilePerMethod()) {
                    AnnotationMethod method = MyQueryCreator.getMethodName((Integer) vByKey.get(0)[MyQueryCreator.METHOD]);
                    bldr.append(method.getMethodName());
                }
                //create the file
                fo = createFreeFile(myDir, bldr.toString(), "fna", 0);
                try {
                    Fasta.createFasta(fo.getOutputStream(), fasta, null);
                } catch (IOException ex) {
                    throw new DownloadException(ex);
                }
            }

            i++;
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
                return FileUtil.createData(dir, FileUtil.findFreeFileName(dir, name, ext) + ".fna");
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
        fo.setAttribute("originalFile", "DownloadFormat/edu-uncc-genosets-fasta-AnnoFastaFormat.instance");
        //save the settings
        fo.setAttribute("filePerOrganism", isFilePerOrganism());
        fo.setAttribute("filePerMethod", isFilePerMethod());
    }

    @Override
    public void load(DownloadSet set, FileObject fo) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    static class MyQueryCreator implements QueryCreator {

        final static int ASS = 0;
        final static int METHOD = 1;
        final static int ORG = 2;
        final static int SEQ = 3;

        static List<Object[]> createQuery(Collection<? extends AnnotationMethod> methods) {
            StringBuilder bldr = new StringBuilder("SELECT ass.assembledUnitId, f.annotationMethodId, ass.organismId, seq.forwardSequence ");
            bldr.append(" FROM AssembledUnit as ass, AssembledUnitAquisition as f, MolecularSequence as seq ").append(" WHERE f.assembledUnitId = ass.assembledUnitId AND seq.molecularSequenceId = ass.assembledUnitId AND (");
            int i = 0;
            for (AnnotationMethod m : methods) {
                if (i != 0) {
                    bldr.append(" OR ");
                }
                bldr.append(" f.annotationMethodId = ").append(m.getAnnotationMethodId());
                i++;
            }
            bldr.append(")");

            return DataManager.getDefault().createQuery(bldr.toString());
        }

        static Organism getOrganismName(Integer id) {
            return (Organism) DataManager.getDefault().get("Organism", id);
        }

        static AnnotationMethod getMethodName(Integer id) {
            return (AnnotationMethod) DataManager.getDefault().get("AnnotationMethod", id);
        }
    }
}
