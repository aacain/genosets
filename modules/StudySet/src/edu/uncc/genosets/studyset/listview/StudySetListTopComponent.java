/*
 * Copyright (C) 2013 Aurora Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * */
package edu.uncc.genosets.studyset.listview;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetManager.StudySetManagerFactory;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import org.openide.util.LookupEvent;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.*;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//edu.uncc.genosets.studyset.listview//StudySetList//EN",
        autostore = false)
@TopComponent.Description(preferredID = "StudySetListTopComponent",
        iconBase = "edu/uncc/genosets/studyset/resources/dna-icon.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@TopComponent.OpenActionRegistration(displayName = "#CTL_StudySetListAction",
        preferredID = "StudySetListTopComponent")
@ActionID(category = "Window",
        id = "edu.uncc.genosets.studyset.StudySetListTopComponent")
@ActionReferences(value = {
    @ActionReference(path = "Menu/Window", position = 220),
    @ActionReference(path = "Toolbars/Explore", position = 100),
    @ActionReference(path = "StudySet/Nodes/Actions", position = 300)
})
@NbBundle.Messages({
    "CTL_StudySetListAction=List View"
})
public final class StudySetListTopComponent extends TopComponent implements ExplorerManager.Provider, LookupListener, FocusChangeListener {

    private ExplorerManager em;
    private InstanceContent ic;
    private StudySet studySet;
    private Lookup.Result<StudySet> studySetResult;
    private PropertyChangeListener dbChangeListener;
    private PropertyChangeListener exploredContextListener;
    private Map<String, GroupHierarchy> hierarchyMap = new HashMap<String, GroupHierarchy>();
    private Map<String, String[]> propertyColumns = new HashMap<String, String[]>();
//    private Node[] previousSelection;
//    private VetoableChangeListener vetoableListener;

    public StudySetListTopComponent() {
        setName(NbBundle.getMessage(StudySetListTopComponent.class, "CTL_StudySetListTopComponent"));
        setToolTipText(NbBundle.getMessage(StudySetListTopComponent.class, "HINT_StudySetListTopComponent"));

        //initialize ExplorerManager and create lookup with instanceContent
        this.em = new ExplorerManager();
        ActionMap map = this.getActionMap();
        this.ic = new InstanceContent();
        Lookup lookup = new ProxyLookup(ExplorerUtils.createLookup(this.em, map), new AbstractLookup(this.ic));
        this.associateLookup(lookup);
        //add self to instance content so nodes can see the top component they are active in
        ic.add(this);
    }

    private synchronized void setDefaultHierarchy() {
        if (hierarchyMap.isEmpty()) {
            List<String> focusEntities = GroupHierarchy.getFocusEntities();
            for (String focus : focusEntities) {
                GroupHierarchy defaultHierarchy = GroupHierarchy.getDefaultHierarchy(focus);
                updateHierMap(focus, defaultHierarchy);
            }
        }
    }

    private synchronized void updateHierMap(String focus, GroupHierarchy hierarchy) {
        if (hierarchy != null) {
            hierarchyMap.put(focus, hierarchy);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new OutlineView();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();

        OutlineView view = (OutlineView)jScrollPane1;
        view.getOutline().setRootVisible(Boolean.FALSE);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jToolBar1.setRollover(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        //listen for study set changes
        studySetResult = Utilities.actionsGlobalContext().lookupResult(StudySet.class);
        studySetResult.addLookupListener(this);

        //listen for database changes
        dbChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                dbChanged(evt);
            }
        };
        DataManager.getDefault().addPropertyChangeListener(WeakListeners.create(PropertyChangeListener.class, dbChangeListener, DataManager.getDefault()));

//        //listen on explored context
//        this.exploredContextListener = new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                explorerManagerChanged(evt);
//            }
//        };
//        this.em.addPropertyChangeListener(WeakListeners.propertyChange(exploredContextListener, this.em));
//
//        //listen on explored context
//        this.vetoableListener = new VetoableChangeListener() {
//            @Override
//            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
//                vetoableExplorerManagerChanged(evt);
//            }
//        };
//        this.em.addVetoableChangeListener(WeakListeners.create(VetoableChangeListener.class, vetoableListener, this.em));

        setDefaultHierarchy();

        //initialize swing components
        initComponents();
        jPanel1.add(new BreadCrumbComponent(), BorderLayout.CENTER);
        //groupingButton.addActionListener(new GroupHierarchyWizardAction());

        resultChanged(null);
    }

    @Override
    public void componentClosed() {
        //remove listeners
        this.studySetResult.removeLookupListener(this);
        this.em.removePropertyChangeListener(exploredContextListener);
        DataManager.getDefault().removePropertyChangeListener(dbChangeListener);

    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        assert hierarchyMap != null;
        for (Map.Entry<String, GroupHierarchy> entry : hierarchyMap.entrySet()) {
            StringBuilder bldr = new StringBuilder();
            List<String> hierarchyList = entry.getValue().getHierarchyList();
            for (int i = 0; i < hierarchyList.size(); i++) {
                if (i > 0) {
                    bldr.append(",");
                }
                bldr.append(hierarchyList.get(i));
            }
            p.put(entry.getKey(), bldr.toString());
        }
        for (Map.Entry<String, String[]> entry : propertyColumns.entrySet()) {
            StringBuilder bldr = new StringBuilder();
            String[] value = entry.getValue();
            for (int i = 0; i < value.length; i++) {
                if (i > 0) {
                    bldr.append(",");
                }
                bldr.append(value[i]);
            }
            p.put(entry.getKey() + "_focus", bldr.toString());
        }
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        if (version.equals("1.0")) {
            List<String> focusEntities = GroupHierarchy.getFocusEntities();
            for (String entity : focusEntities) {
                String hierarchyProperty = p.getProperty(entity);
                if (hierarchyProperty != null) {
                    updateHierMap(entity, new GroupHierarchy(new ArrayList(Arrays.asList(hierarchyProperty.split(",")))));
                } else {
                    updateHierMap(entity, GroupHierarchy.getDefaultHierarchy(entity));
                }
                String focusProperty = p.getProperty(entity + "_focus");
                if (focusProperty != null) {
                    String[] focs = focusProperty.split(",");
                    propertyColumns.put(entity, focs);
                }
            }
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return this.em;
    }

//    private void explorerManagerChanged(PropertyChangeEvent evt) {
//        if (("nodeChange").equals(evt.getPropertyName())) {
//            if (previousSelection != null) {
//                
//                try {
//                    this.em.setSelectedNodes(previousSelection);
//                } catch (PropertyVetoException ex) {
//                }
//            }
//        } else if (("rootContext").equals(evt.getPropertyName()) || ("exploredContext").equals(evt.getPropertyName())) {
//            previousSelection = null;
//        }
//    }
//
//    private void vetoableExplorerManagerChanged(PropertyChangeEvent evt) {
//        if (("selectedNodes").equals(evt.getPropertyName())) {
//            previousSelection = (Node[]) evt.getNewValue();
//        }
//    }
    @Override
    public void resultChanged(LookupEvent ev) {
        StudySet oldSet = this.studySet;
        for (StudySet study : studySetResult.allInstances()) {
            this.studySet = study;
        }
        if (this.studySet == null) {
            studySet = StudySetManagerFactory.getDefault().getSelectedStudySet();
        }

        if (studySet != null && oldSet != this.studySet) {
            String[] properties = null;
            if (studySet.getFocusEntity().equals(FocusEntity.getEntity("AssembledUnit"))) {
                GroupHierarchy hierarchy = hierarchyMap.get("AssembledUnit");
                EntityNode root = new EntityNode(studySet, hierarchy);
                em.setRootContext(root);
                properties = propertyColumns.get(studySet.getFocusEntity().getEntityName());
                if (properties == null) {
                    properties = OrganismNode.getNodeProperties();
                }
            } else if (studySet.getFocusEntity().equals(FocusEntity.getEntity("Organism"))) {
                GroupHierarchy hierarchy = hierarchyMap.get("Organism");
                EntityNode root = new EntityNode(studySet, hierarchy);
                em.setRootContext(root);
                properties = propertyColumns.get(studySet.getFocusEntity().getEntityName());
                if (properties == null) {
                    properties = OrganismNode.getNodeProperties();
                }
            } else if (studySet.getFocusEntity().equals(FocusEntity.getEntity("Location"))) {
                GroupHierarchy hierarchy = hierarchyMap.get("Location");
                EntityNode root = new EntityNode(studySet, hierarchy);
                em.setRootContext(root);
                properties = propertyColumns.get(studySet.getFocusEntity().getEntityName());
                if (properties == null) {
                    properties = OrganismNode.getNodeProperties();
                }
            } else if (studySet.getFocusEntity().equals(FocusEntity.getEntity("Feature"))) {
                GroupHierarchy hierarchy = hierarchyMap.get("Feature");
                EntityNode root = new EntityNode(studySet, hierarchy);
                em.setRootContext(root);
                properties = propertyColumns.get(studySet.getFocusEntity().getEntityName());
                if (properties == null) {
                    properties = OrganismNode.getNodeProperties();
                }
            } else {
                AbstractNode root = new AbstractNode(Children.LEAF);
                root.setName("root");
                em.setRootContext(root);
                properties = new String[]{};
            }
            propertyColumns.put(studySet.getFocusEntity().getEntityName(), properties);
            OutlineView view = (OutlineView) jScrollPane1;
            view.setPropertyColumns(properties);
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

    @Override
    public void focusChanged(FocusChangedEvent evt) {
        OutlineView view = (OutlineView) jScrollPane1;
        view.setPropertyColumns(evt.getProperties());
        if (studySet != null) {
            propertyColumns.put(studySet.getFocusEntity().getEntityName(), evt.getProperties());
        }
    }

    public final class GroupHierarchyWizardAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new GroupHierarchyWizardPanel1(hierarchyMap));
            String[] steps = new String[panels.size()];
            for (int i = 0; i < panels.size(); i++) {
                Component c = panels.get(i).getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                }
            }
            WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
            // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
            wiz.setTitleFormat(new MessageFormat("{0}"));
            wiz.setTitle("...dialog title...");
            if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
                // do something
            }
        }
    }
}
