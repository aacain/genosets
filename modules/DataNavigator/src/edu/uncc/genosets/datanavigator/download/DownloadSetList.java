/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.download;

import edu.uncc.genosets.datamanager.api.DownloadSet;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;

/**
 *
 * @author aacain
 */
public class DownloadSetList {

    private List<DownloadSet> downloadSets;
    private final ChangeSupport cs = new ChangeSupport(this);

    public DownloadSetList(List<? extends DownloadSet> downloadSets) {
        if (downloadSets == null) {
            this.downloadSets = new ArrayList<DownloadSet>();
        } else {
            this.downloadSets = new ArrayList<DownloadSet>(downloadSets);
        }
    }

    public synchronized List<? extends DownloadSet> list() {
        return new ArrayList(downloadSets);
    }

    public synchronized void add(DownloadSet c) {
        downloadSets.add(c);
        cs.fireChange();
    }

    public synchronized void remove(DownloadSet c) {
        downloadSets.remove(c);
        cs.fireChange();
    }

    public synchronized void addChangeListener(ChangeListener l) {
        this.cs.addChangeListener(l);
    }

    public synchronized void removeChangeListener(ChangeListener l) {
        this.cs.removeChangeListener(l);
    }

    private class DatabaseChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            //get download sets
        }
    }
}
