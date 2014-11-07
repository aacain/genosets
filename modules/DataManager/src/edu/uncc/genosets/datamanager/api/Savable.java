/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.datamanager.api;

import java.io.IOException;
import java.io.Serializable;
import org.openide.filesystems.FileObject;

/**
 *
 * @author aacain
 */
public interface Savable extends Serializable{
    /** Saves the settings for this object after the
     * user closes the window
     * @param fo - the file object that represents this savable
     * @throws IOException 
     */
    public void save(FileObject fo) throws IOException;
    /**
     * 
     * @param set - the download set that this object is a part of
     * @param fo - the file object to save to
     */
    public void load(DownloadSet set, FileObject fo);
}
