/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.parsetsbridge;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.parsetsbridge.property.GenoSetsController;
import edu.uncc.genosets.parsetsbridge.property.GenoSetsDataSet;
import edu.uncc.genosets.parsetsbridge.property.GenoSetsDimensionHandle;
import edu.uncc.genosets.parsetsbridge.property.ParsetsDatabaseHandler;
import edu.uncc.genosets.studyset.StudySetManager;
import edu.uncc.parsets.parsets.ParSetsView;
import edu.uncc.parsets.util.osabstraction.AbstractOS;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.SoftReference;
import java.util.List;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressRunnable;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.WeakListeners;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
//@ConvertAsProperties(dtd = "-//edu.uncc.genosets.parsetsbridge//ParSets//EN",
//autostore = false)
@TopComponent.Description(preferredID = "ParSetsTopComponent",
        //iconBase="edu/uncc/genosets/datanavigator/resources/organism16.png", 
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
//@ActionID(category = "Window", id = "edu.uncc.genosets.parsetsbridge.ParSetsTopComponentAction")
//@ActionReference(path = "Menu/Window",
//position = 10)
@TopComponent.OpenActionRegistration(displayName = "#CTL_ParSetsTopComponent",
        preferredID = "ParSetsTopComponent")
@NbBundle.Messages({
    "CTL_ParSetsTopComponent=Parallel Sets",
    "HINT_ParSetsTopComponent=This is the Parallel Sets Window"
})
public final class ParSetsTopComponent extends TopComponent implements ExplorerManager.Provider {

    private static SoftReference<ParSetsTopComponent> instance;
    private GenoSetsDataSet ds;
    private GenoSetsController controller;
    private ExplorerManager em;
    private PropertyChangeListener dbChangeListener;
    private FocusEntity focusEntity;

    public ParSetsTopComponent() {
        this(null);
    }

    public ParSetsTopComponent(GenoSetsDataSet ds) {
        setName(NbBundle.getMessage(ParSetsTopComponent.class, "CTL_ParSetsTopComponent"));
        setToolTipText(NbBundle.getMessage(ParSetsTopComponent.class, "HINT_ParSetsTopComponent"));
        this.ds = ds;
        controller = new GenoSetsController();
        if (this.ds == null) {
            this.ds = query();
        }
        controller.setDataSet(this.ds);
        this.em = new ExplorerManager();
        AbstractOS.determineOS();
        initComponents();
        Children children = new RootDimensionChildren(controller);
        AbstractNode node = new AbstractNode(children);
        node.setDisplayName("All");
        em.setRootContext(node);
        ActionMap map = this.getActionMap();
        InputMap keys = this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        Lookup lookup = ExplorerUtils.createLookup(this.em, map);
        this.associateLookup(lookup);
        ((OutlineView) sideBarPane).getOutline().setRootVisible(Boolean.FALSE);
        dbChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(StudySetManager.PROP_DB_CHANGE)) {
                    dbChanged(evt);
                }
            }
        };
        StudySetManager.StudySetManagerFactory.getDefault().addPropertyChangeListener(WeakListeners.propertyChange(dbChangeListener, StudySetManager.StudySetManagerFactory.getDefault()));
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    private GenoSetsDataSet query() {
        final FocusEntity entity = FocusEntity.getEntity(NbPreferences.forModule(GenoSetsDataSet.class).get("FocusEntity", "Feature"));
        ProgressRunnable<GenoSetsDataSet> r = new ProgressRunnable<GenoSetsDataSet>() {
            @Override
            public GenoSetsDataSet run(ProgressHandle handle) {
                //OrthologTableCreator.initialize();
                handle.setDisplayName("Loading dimensions");
                GenoSetsDataSet myds = new GenoSetsDataSet(entity);
                controller.setDataSet(myds);
                List<GenoSetsDimensionHandle> dims = ParsetsDatabaseHandler.setupRootProperties(controller);
                myds.setDimensions(dims);
                return myds;
            }
        };
        return ProgressUtils.showProgressDialogAndRun(r, "Querying", true);
    }

    public static ParSetsTopComponent findInstance() {
        ParSetsTopComponent ps = null;
        if (instance != null) {
            ps = instance.get();
        }
        if (ps == null) {
            ps = new ParSetsTopComponent();
            instance = new SoftReference<ParSetsTopComponent>(ps);
        }
        return ps;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        sideBarPane = new OutlineView("Dimensions");
        parSetsPanel = this.controller.getView();
        jToolBar1 = new javax.swing.JToolBar();
        changeModeCheckBox = new javax.swing.JCheckBox();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        ribbonStyleCheckBox = new javax.swing.JCheckBox();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 0), new java.awt.Dimension(20, 32767));
        jPanel2 = new javax.swing.JPanel();
        focusComboBox = new javax.swing.JComboBox();

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setLeftComponent(sideBarPane);

        javax.swing.GroupLayout parSetsPanelLayout = new javax.swing.GroupLayout(parSetsPanel);
        parSetsPanel.setLayout(parSetsPanelLayout);
        parSetsPanelLayout.setHorizontalGroup(
            parSetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 236, Short.MAX_VALUE)
        );
        parSetsPanelLayout.setVerticalGroup(
            parSetsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 309, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(parSetsPanel);

        jToolBar1.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(changeModeCheckBox, org.openide.util.NbBundle.getMessage(ParSetsTopComponent.class, "ParSetsTopComponent.changeModeCheckBox.text")); // NOI18N
        changeModeCheckBox.setFocusable(false);
        changeModeCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        changeModeCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        changeModeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeModeCheckBoxActionPerformed(evt);
            }
        });
        jToolBar1.add(changeModeCheckBox);
        jToolBar1.add(filler2);

        org.openide.awt.Mnemonics.setLocalizedText(ribbonStyleCheckBox, org.openide.util.NbBundle.getMessage(ParSetsTopComponent.class, "ParSetsTopComponent.ribbonStyleCheckBox.text")); // NOI18N
        ribbonStyleCheckBox.setFocusable(false);
        ribbonStyleCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ribbonStyleCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        ribbonStyleCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ribbonStyleCheckBoxActionPerformed(evt);
            }
        });
        jToolBar1.add(ribbonStyleCheckBox);
        jToolBar1.add(filler1);

        focusComboBox.setModel(new javax.swing.DefaultComboBoxModel(FocusEntity.getEntities().toArray()));
        focusComboBox.getModel().setSelectedItem(this.ds.getFocusEntity());
        focusComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                focusComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 42, Short.MAX_VALUE)
                .addComponent(focusComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 243, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(focusComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jToolBar1.add(jPanel2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 542, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 316, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addGap(33, 33, 33)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void changeModeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeModeCheckBoxActionPerformed
        ParSetsView view = (ParSetsView) parSetsPanel;
        view.changeState();
    }//GEN-LAST:event_changeModeCheckBoxActionPerformed

    private void ribbonStyleCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ribbonStyleCheckBoxActionPerformed
        ParSetsView view = (ParSetsView) parSetsPanel;
        view.changeBarState();
    }//GEN-LAST:event_ribbonStyleCheckBoxActionPerformed

    private void focusComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_focusComboBoxActionPerformed
        if (this.ds.getFocusEntity() != this.focusComboBox.getModel().getSelectedItem()) {
            //this.ds.propertyChange(new PropertyChangeEvent(this, GenoSetsDataSet.PROP_FOCUS_ENTITY_CHANGED, this.ds.getFocusEntity(), this.focusComboBox.getModel().getSelectedItem()));
            FocusEntity e = (FocusEntity) this.focusComboBox.getModel().getSelectedItem();
            this.controller.setFocusEntity(e);
        }
    }//GEN-LAST:event_focusComboBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox changeModeCheckBox;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JComboBox focusComboBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel parSetsPanel;
    private javax.swing.JCheckBox ribbonStyleCheckBox;
    private javax.swing.JScrollPane sideBarPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
        DataManager.getDefault().removePropertyChangeListener(dbChangeListener);
        instance = null;
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return this.em;
    }

    private void dbChanged(PropertyChangeEvent evt) {
        final TopComponent tc = this;
        if (StudySetManager.PROP_DB_CHANGE.equals(evt.getPropertyName())) {
            WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
                @Override
                public void run() {
                    tc.close();
                }
            });
        }
    }
}
