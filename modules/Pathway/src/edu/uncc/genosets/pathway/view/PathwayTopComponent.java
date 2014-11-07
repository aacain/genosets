/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.pathway.view;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetManager;
import edu.uncc.genosets.studyset.StudySetManager.StudySetManagerFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.*;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//edu.uncc.genosets.pathway.view//Pathway//EN",
autostore = false)
@TopComponent.Description(preferredID = "PathwayTopComponent",
iconBase = "edu/uncc/genosets/pathway/resources/call_graph.png",
persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "edu.uncc.genosets.pathway.view.PathwayTopComponent")
@ActionReferences(value = {
    @ActionReference(path = "Menu/Window", position = 230)
})
@TopComponent.OpenActionRegistration(displayName = "#CTL_PathwayAction",
preferredID = "PathwayTopComponent")
@Messages({
    "CTL_PathwayAction=Pathway List",
    "CTL_PathwayTopComponent=Pathway List Window",
    "HINT_PathwayTopComponent=This is a Pathway List window"
})
public final class PathwayTopComponent extends TopComponent implements LookupListener, ExplorerManager.Provider {

    private ExplorerManager em;
    private final Lookup.Result<StudySet> resultSet;
    private final PropertyChangeListener dbChangeListener;
    private StudySet studySet = null;
    private StudySetManager mgr;

    public PathwayTopComponent() {
        em = new ExplorerManager();
        ActionMap map = this.getActionMap();
        Lookup lookup = ExplorerUtils.createLookup(this.em, map);
        this.associateLookup(lookup);
        resultSet = Utilities.actionsGlobalContext().lookupResult(StudySet.class);
        resultSet.addLookupListener(this);
        mgr = StudySetManagerFactory.getDefault();
        initComponents();
        setName(Bundle.CTL_PathwayTopComponent());
        setToolTipText(Bundle.HINT_PathwayTopComponent());
        resultChanged(null);
        dbChangeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                dbChanged(evt);
            }
        };
        DataManager.getDefault().addPropertyChangeListener(WeakListeners.create(PropertyChangeListener.class, dbChangeListener, DataManager.getDefault()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new BeanTreeView();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return this.em;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        StudySet oldSet = this.studySet;
        for (StudySet study : resultSet.allInstances()) {
            this.studySet = study;
        }

        if (this.studySet == null) {
            studySet = mgr.getSelectedStudySet();
        }

        if (studySet != null && oldSet != this.studySet) {
            AbstractNode root = new AbstractNode(Children.create(new PathwayNodeFactory(studySet), true));
            root.setName("root");
            em.setRootContext(root);
        }
    }

    private void dbChanged(PropertyChangeEvent evt) {
        final TopComponent tc = this;
        if (DataManager.PROP_DB_CHANGED.equals(evt.getPropertyName())) {
            WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

                @Override
                public void run() {
                    tc.close();
                }
            });
        }
    }
}