/*
 * 
 * 
 */
package edu.uncc.genosets.fastabymethod.api;

import edu.uncc.genosets.datamanager.api.DataManager;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Set;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author aacain
 */
public class FastaCreator {

    public static final String EXT = "fasta";
    private FileObject directory;
    private DataManager mgr;

    public FastaCreator(FileObject directory) {
        mgr = Lookup.getDefault().lookup(DataManager.class);
        this.directory = directory;
    }

    public void createFiles(String fileName, Set<Integer> locationIds) {
        StringBuilder bldr = new StringBuilder("SELECT l.locationId, l.featureId, l.assembledUnitId, l.organismId, p.forwardSequence");
        bldr.append(" FROM ProteinSequence as p, Location as l WHERE l.locationId = p.molecularSequenceId AND p.molecularSequenceId IN ( ");
        for (Integer locId : locationIds) {
            bldr.append(locId.intValue()).append(",");
        }
        bldr.deleteCharAt(bldr.length() - 1);
        bldr.append(")");

        FileObject fo = null;
        BufferedWriter writer = null;
        try {
            fo = directory.getFileObject(fileName, EXT);
            if (fo != null) {
                fo.delete();
            }
            fo = directory.createData(fileName, EXT);
            writer = new BufferedWriter(new OutputStreamWriter(fo.getOutputStream()));
            List<Object[]> result = mgr.createQuery(bldr.toString());
            for (Object[] line : result) {
                writer.write(">" + fileName + "|"
                        + ((Integer) line[0]).toString()
                        + ";" + ((Integer) line[1]).toString()
                        + ";" + ((Integer) line[2]).toString()
                        + ";" + ((Integer) line[3]).toString());
                writer.newLine();
                writer.write((String) line[4]);
                writer.newLine();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }
}
