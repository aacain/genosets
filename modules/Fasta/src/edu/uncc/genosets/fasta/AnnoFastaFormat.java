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
import java.io.Serializable;
import java.util.*;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

/**
 *
 * @author aacain
 */
public class AnnoFastaFormat implements DownloadFormat, Editable, Savable {

    private static final String ITERATOR_PATH = "Templates/Downloads/edu-uncc-genosets-datamanager-api-AnnotationFactType/FASTA Protein (faa)";
    private transient DownloadSet ds;
    private transient boolean filePerOrganism;
    private transient boolean filePerMethod;
    private transient boolean prefixOrganism;
    private String myDirPath = "";
    

    public AnnoFastaFormat(){
        
    }
    
    public AnnoFastaFormat(DownloadSet ds) {
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

    public boolean isPrefixOrganism() {
        return prefixOrganism;
    }

    public void setPrefixOrganism(boolean prefixOrganism) {
        this.prefixOrganism = prefixOrganism;
    }

    public String getMyDirPath() {
        return myDirPath;
    }

    @Override
    public String getIteratorPath() {
        return ITERATOR_PATH;
    }

    @Override
    public String getFormatFolderName() {
        return NbBundle.getMessage(AnnoFastaProteinWizardIterator.class, "AnnoFastaProteinWizardIterator_displayName");
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
            List<Object[]> results = MyQueryCreator.createQuery(methods, this.isFilePerMethod());
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
                FastaItem item = new FastaItem(((Integer) obj[MyQueryCreator.LOC]).toString(), (String) obj[MyQueryCreator.SEQ]);
                itemsList.add(item);
            }
            Fasta fasta = new Fasta();
            fasta.setItems(itemsList);

            FileObject fo = null;
            if (isPrefixOrganism()) {
                String prefix = createPrefix(i, 4);
                fo = createFreeFile(myDir, prefix, "faa", 0);
                try {
                    Fasta.createFasta(fo.getOutputStream(), fasta, prefix);
                } catch (IOException ex) {
                    throw new DownloadException(ex);
                }
            } else {
                if (vByKey != null && !vByKey.isEmpty()) {
                    StringBuilder bldr = new StringBuilder();
                    if (isFilePerOrganism()) {
                        Organism org = MyQueryCreator.getOrganismName((Integer) vByKey.get(0)[MyQueryCreator.ORG]);
                        bldr.append(org.getStrain() != null ? org.getStrain().replace("\\s{1,}", "_") : org.getOrganismId());
                        if (isFilePerMethod()) {
                            bldr.append("_");
                        }
                    }
                    if (isFilePerMethod()) {
                        AnnotationMethod method = MyQueryCreator.getMethodName((Integer) vByKey.get(0)[MyQueryCreator.METHOD]);
                        bldr.append(method.getMethodName());
                    }
                    //create the file
                    fo = createFreeFile(myDir, bldr.toString(), "faa", 0);
                    try {
                        Fasta.createFasta(fo.getOutputStream(), fasta, null);
                    } catch (IOException ex) {
                        throw new DownloadException(ex);
                    }
                }
            }
            i++;
        }
    }

    private String createPrefix(int num, int totalChars) {
        StringBuilder bldr = new StringBuilder(Integer.toString(num));
        int digitsToAdd = totalChars - bldr.length();
        for (int i = 0; i < digitsToAdd; i++) {
            bldr.insert(0, '0');
        }
        return bldr.toString();
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
                return FileUtil.createData(dir, FileUtil.findFreeFileName(dir, name, ext) + ".faa");
            } catch (IOException ex) {
                return createFreeFile(dir, name, ext, num);
            }
        }
        return null;
    }

    @Override
    public void save(FileObject fo) throws IOException {
       fo.setAttribute("originalFile", "DownloadFormat/edu-uncc-genosets-fasta-AnnoFastaFormat.instance");
       //save the settings
       fo.setAttribute("filePerOrganism", isFilePerOrganism());
       fo.setAttribute("filePerMethod", isFilePerMethod());
       fo.setAttribute("prefixOrganism", isPrefixOrganism());
       
//        ObjectOutputStream fOut = null;
//        try {
//            fOut = new ObjectOutputStream(new BufferedOutputStream(fo.getOutputStream()));
//            fOut.writeObject(this);
//            fOut.close();
//        } catch (IOException ex) {
//            fOut.close();
//            throw ex;
//        }
    }

    @Override
    public void load(DownloadSet ds, FileObject fo) {
        this.ds = ds;
        Object fpo, fpm, fprefO;
        fpo = fo.getAttribute("filePerOrganism");
        fpm = fo.getAttribute("filePerMethod");
        fprefO = fo.getAttribute("prefixOrganism");
        if(fpo != null) this.setFilePerOrganism((Boolean)fpo);
        if(fpm != null) this.setFilePerMethod((Boolean)fpm);
        if(fprefO != null) this.setPrefixOrganism((Boolean)fprefO);
    }

    static class MyQueryCreator implements QueryCreator, Serializable {

        final static int LOC = 0;
        final static int METHOD = 1;
        final static int ORG = 2;
        final static int SEQ = 3;

        static List<Object[]> createQuery(Collection<? extends AnnotationMethod> methods, boolean byMethod) {
            StringBuilder bldr = new StringBuilder("SELECT l.locationId, f.annotationMethodId, l.organismId, seq.forwardSequence");
            bldr.append(" FROM Location as l, AnnoFact as f, ProteinSequence as seq ").append(" WHERE l.locationId = f.locationId AND seq.molecularSequenceId = l.locationId AND (");
            int i = 0;
            for (AnnotationMethod m : methods) {
                if (i != 0) {
                    bldr.append(" OR ");
                }
                bldr.append(" f.annotationMethodId = ").append(m.getAnnotationMethodId());
                i++;
            }
            bldr.append(")").append("GROUP BY l.locationId");
            if(byMethod){
                bldr.append(", f.annotationMethodId");
            }

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
