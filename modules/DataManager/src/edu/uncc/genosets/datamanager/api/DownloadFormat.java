/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.datamanager.api;

import java.io.Serializable;

/**
 *
 * @author aacain
 */
public interface DownloadFormat extends Serializable{
    public static final String WIZARD_DOWNLOAD_FORMAT_OBJECT = "WIZARD_DOWNLOAD_FORMAT_OBJECT";
    public static final String WIZARD_DOWNLOAD_SET_OBJECT = "WIZARD_DOWNLOAD_SET_OBJECT";
    /**
     * The name of the folder in which files are to be downloaded for this format
     * @return name of the folder for this format
     */
    public String getFormatFolderName();
    public void download() throws DownloadException;
}
