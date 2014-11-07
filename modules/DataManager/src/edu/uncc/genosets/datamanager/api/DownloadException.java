/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datamanager.api;

/**
 *
 * @author aacain
 */
public class DownloadException extends Exception {

    public DownloadException() {
        super();
    }

    public DownloadException(String message) {
        super(message);
    }

    public DownloadException(Throwable t) {
        super(t);
    }
}
