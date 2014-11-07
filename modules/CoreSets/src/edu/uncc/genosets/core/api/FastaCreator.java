/*
 * 
 * 
 */

package edu.uncc.genosets.core.api;

import edu.uncc.genosets.datamanager.entity.Organism;
import java.util.List;
import org.openide.filesystems.FileObject;

/**
 *
 * @author aacain
 */
public interface FastaCreator {
    public static final int RESULT_SUCCESS = 0;
    public static final int RESULT_FAILURE = 1;
    public static final String FASTA_EXT = "fasta";

    /**
     * Creates a fasta file with the name as the filePrefix + .fasta and
     * appends the filePrefix to the beginning of the fasta identifier
     * followed by the feature id.
     * @param folder
     * @param filePrefix
     * @param organism
     * @param itemList
     * @return 
     */
    public int createFile(FileObject folder, String filePrefix, Organism organism, List<FastaItem> itemList);

    public int createFile(FileObject folder, String filePrefix, Organism organism);
}
