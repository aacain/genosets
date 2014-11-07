
package edu.uncc.genosets.studyset;

/**
 * Adds services to a study set. 
 * This class is used to decouple the details that a study set can have
 *  Also See EnrichmentServiceProvider for example.
 * @author aacain
 */
public interface StudySetServiceProvider {
    /**
     * Initialize the service.  Usually used to add objects to the InstanceContent
     * of the studyset.  
     * This method should never use the StudySetManager.class as it will
     * recursively call this method.
     * @param database
     * @param studySet 
     */
    public void initialize(String database, StudySet studySet);
    /**
     * Delete all associated information from this studySEt.
     * @param database
     * @param studySet 
     */
    public void studySetDeleted(String database, StudySet studySet);
}
