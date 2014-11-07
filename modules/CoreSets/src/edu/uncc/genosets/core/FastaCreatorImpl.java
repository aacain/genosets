/*
 * 
 * 
 */
package edu.uncc.genosets.core;

import edu.uncc.genosets.core.api.FastaCreator;
import edu.uncc.genosets.core.api.FastaItem;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aacain
 */
@ServiceProvider(service = FastaCreator.class)
public class FastaCreatorImpl implements FastaCreator {

    @Override
    public int createFile(FileObject folder, String filePrefix, Organism organism, List<FastaItem> itemList) {
        if (folder.canWrite() && folder.isFolder() && !folder.isLocked()) {
            FileObject fo = folder.getFileObject(filePrefix, FastaCreator.FASTA_EXT);
            if (fo == null) {
                try {
                    fo = folder.createData(filePrefix, FastaCreator.FASTA_EXT);
                    for (FastaItem item : itemList) {
                        addToFile(item, fo);
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                    return FastaCreator.RESULT_FAILURE;
                }
                return FastaCreator.RESULT_SUCCESS;
            }
            return FastaCreator.RESULT_FAILURE;
        } else {
            return FastaCreator.RESULT_FAILURE;
        }
    }

    private int addToFile(FastaItem item, FileObject fo) {
        final int LOCATION_ID = 0;
        final int FEATURE_ID = 1;
        final int ASSUNIT_ID = 2;
        final int ORGANISM_ID = 3;
        final int SEQ = 4;
        final String ORG_CODE = fo.getName();
        //TODO: remove dependancy

        String queryString = "";

        if (item.getAssemblyMethod() == null) {
            queryString = "select l.locationId, l.featureId, l.assembledUnitId, l.organismId, p.forwardSequence from Location as l, ProteinSequence as p "
                    + " where l.locationId = p.molecularSequenceId AND l.organismId = " + item.getOrganism().getOrganismId();


//            queryString = "select f.featureId, seq.forwardSequence "
//                    + " FROM Feature as f "
//                    + " inner join f.molecularSequenceByProteinSequence as seq "
//                    + " where f.organismId = " + item.getOrganism().getOrganismId();
        } else {
//            queryString = "select f.featureId, seq.forwardSequence "
//                    + " from AnnotationMethod as gMeth "
//                    + " inner join gMeth.featureAnnotations as fanno "
//                    + " inner join fanno.feature as f "
//                    + " inner join f.molecularSequenceByProteinSequence as seq "
//                    + " where gMeth.annotationMethodId = " + item.getGeneMethod().getAnnotationMethodId();
        }

        DataManager mgr = DataManager.getDefault();
        List<Object[]> seqList = mgr.createQuery(queryString);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new PrintWriter(fo.getOutputStream()));
            if (seqList != null) {
                for (Object[] objects : seqList) {
                    writer.write(">" + ORG_CODE + "|"
                            + ((Integer) objects[LOCATION_ID]).toString()
                            + ";" + ((Integer) objects[FEATURE_ID]).toString()
                            + ";" + ((Integer) objects[ASSUNIT_ID]).toString()
                            + ";" + ((Integer) objects[ORGANISM_ID]).toString());
                    writer.newLine();
                    writer.write((String) objects[SEQ]);
                    writer.newLine();
                }
            }
        } catch (FileAlreadyLockedException ex) {
            Exceptions.printStackTrace(ex);
            return FastaCreator.RESULT_FAILURE;
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return FastaCreator.RESULT_FAILURE;
        } finally {
            int result = RESULT_FAILURE;
            try {
                writer.close();
                result =  FastaCreator.RESULT_SUCCESS;
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
                result =  FastaCreator.RESULT_FAILURE;
            }
            return result;
        }
    }

    @Override
    public int createFile(FileObject folder, String filePrefix, Organism organism) {
        if (folder.canWrite() && folder.isFolder() && !folder.isLocked()) {
            FileObject fo = folder.getFileObject(filePrefix, FastaCreator.FASTA_EXT);
            if (fo == null) {
                try {
                    fo = folder.createData(filePrefix, FastaCreator.FASTA_EXT);
                    FastaItem item = new FastaItem(organism, null, null);
                    addToFile(item, fo);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                    return FastaCreator.RESULT_FAILURE;
                }
                return FastaCreator.RESULT_SUCCESS;
            }
            return FastaCreator.RESULT_FAILURE;
        } else {
            return FastaCreator.RESULT_FAILURE;
        }
    }
}
