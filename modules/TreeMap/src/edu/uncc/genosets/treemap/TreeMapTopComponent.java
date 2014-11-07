/*
 * 
 * 
 */
package edu.uncc.genosets.treemap;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.geneontology.obo.OboDataObject;
import edu.uncc.genosets.ontologizer.GoEnrichment;
import edu.uncc.genosets.studyset.GoTerm;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.TermCalculation;
import edu.uncc.genosets.treemap.view.GenoSetsTreeMap;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;
import javax.swing.*;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.*;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * Top component which displays a treemap Listens for lookup changes to the
 * selected study set and for changes to the selected term. Adds selected go
 * term to global lookup
 *
 */
@ConvertAsProperties(dtd = "-//edu.uncc.genosets.treemap//TreeMap//EN",
autostore = false)
@NbBundle.Messages({
    "CTL_TreeMapTopComponent=GO Tree Map", "HINT_TreeMapTopComponent=GO Tree Map view of selected study set"
})
public final class TreeMapTopComponent extends TopComponent {

    private static TreeMapTopComponent instance;
    /**
     * path to the icon used by the component and its open action
     */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "TreeMapTopComponent";
    //custom components
    private GenoSetsTreeMap treeMap;
    private JFastLabel title;
    //instance content for my selected GoTerm
    private InstanceContent termInstanceContent = new InstanceContent();
    private GoTerm mySelectedGo;  //term selected in go treemap
    //listens for changes to selected go enrichment
    private GoEnrichment selectedEnrichment;
    private Lookup.Result<GoEnrichment> enrichmentResult;
    private LookupListener enrichmentLookupListener;
    private Lookup.Result<StudySet> studySetResult;
    private LookupListener studySetLookupListener;
    //listens for changes to termCalculation
    private TermCalculation selectedTermCalculation;
    private Lookup.Result<TermCalculation> termResult;
    private LookupListener termLookupListener;
    private NumberFormat numberInstance = NumberFormat.getNumberInstance();
    //for drill/down and up of go hierarchy
    private int selectedNodeIndex = 0;
    private ArrayList<NodeItem> selectedNodePath;
    private NodeItem rootNodeItem;
    //view variables
    boolean byPopulation = true;
    //private OboDataObject obodao;
    private PropertyChangeListener dbChangeListener;
    private boolean initialized = false;

    public TreeMapTopComponent() {
    }
    
    public TreeMapTopComponent(GenoSetsTreeMap treeMap, GoEnrichment enrichment){
        this.treeMap = treeMap;
        this.selectedEnrichment = enrichment;
        this.enrichmentResult = Utilities.actionsGlobalContext().lookupResult(GoEnrichment.class);
        this.studySetResult = Utilities.actionsGlobalContext().lookupResult(StudySet.class);
        this.associateLookup(new AbstractLookup(this.termInstanceContent));
        initLabel();
        Font textFont = new Font("Verdana", Font.BOLD, 20);

        //Create UIManager
        UIManager uim = new UIManager();

        //Set tooltiptext text font using created Font
        uim.put("ToolTip.font", textFont);

        setName(NbBundle.getMessage(TreeMapTopComponent.class, "CTL_TreeMapTopComponent"));
        setToolTipText(NbBundle.getMessage(TreeMapTopComponent.class, "HINT_TreeMapTopComponent"));
        this.treeMap.updateEnrichment(enrichment);
        this.treeMap.updatePValueCutoff(0.0100);
        rootNodeItem = this.treeMap.getSubrootNodeItem();
        selectedNodePath = new ArrayList<NodeItem>(10);
        selectedNodePath.add(rootNodeItem);
        selectedNodeIndex = 0;
        initComponents();
        updateUndoRedoButtons();
        dbChangeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                dbChanged(evt);
            }
        };
        DataManager.getDefault().addPropertyChangeListener(WeakListeners.create(PropertyChangeListener.class, dbChangeListener, DataManager.getDefault()));
    }


    private void setPValue() {
        double value = Double.parseDouble(pValueText.getText());
        this.treeMap.updatePValueCutoff(value);
        mainPanel.requestFocusInWindow();
    }

    private void initLabel() {
        title = new JFastLabel("                 ");
        title.setPreferredSize(new Dimension(450, 20));
        title.setVerticalAlignment(SwingConstants.TOP);
        title.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        title.setForeground(new Color(247, 67, 67));
        title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));

        treeMap.addControlListener(new ControlAdapter() {

            @Override
            public void itemEntered(VisualItem item, MouseEvent e) {
                NodeItem n = (NodeItem) item;
                Display d = (Display) e.getSource();
                //Lookup backingrow
                int sourceNodeRow = n.getInt(DAGTree.SOURCE_NODE_ROW);
                String goId = treeMap.getGraph().getNode(sourceNodeRow).getString(GOSchema.GO_TERM_ID);
                TermCalculation term = selectedEnrichment.getTermCalculationMap().get(goId);
                StringBuilder bldr = new StringBuilder(treeMap.getGraph().getNode(sourceNodeRow).getString(GOSchema.NAME));
                d.setToolTipText(bldr.toString());

                if (term != null) {
                    double percentage = (double) term.getStudyTerm() / (double) term.getPopTerm();
                    percentage = percentage * 100;
                    bldr.append(" Study Total: ").append(term.getStudyTerm()).append("  (").append(percentage).append("%)");
                }
                if (detailLabel != null) {
                    detailLabel.setText(bldr.toString());
                }
                //title.setText(item.getString(GOSchema.NAME) + " : " + selectedCount + " selected of " + originalCount + " total ");
                //Depth: " + item.get("depth"));
            }

            @Override
            public void itemClicked(VisualItem item, MouseEvent e) {
//                if (e.isPopupTrigger()) {
//                    selectedNode = (NodeItem)item;
//                    popup.show(treeMap, e.getX(), e.getY());

                int sourceNodeRow = ((NodeItem) item).getInt(DAGTree.SOURCE_NODE_ROW);
                String goId = treeMap.getGraph().getNode(sourceNodeRow).getString(GOSchema.GO_TERM_ID);
                setIc(goId, item.getString(GOSchema.NAME));
                treeMap.updateFocusTerm(item, true);
                if (e.getClickCount() > 1) {
                    updateSelectedNode((NodeItem) item);
                }
            }

            @Override
            public void itemExited(VisualItem item, MouseEvent e) {
                Display d = (Display) e.getSource();
                d.setToolTipText(null);
            }
        });
    }

    private void updateSelectedNode(NodeItem nodeItem) {
        treeMap.setSubLayout(nodeItem);
        treeMap.updateFocusTerm(nodeItem, true);
        selectedNodeIndex++;
        selectedNodePath.add(selectedNodeIndex, nodeItem);
        if (selectedNodePath.size() > selectedNodeIndex) {
            selectedNodePath.subList(selectedNodeIndex + 1, selectedNodePath.size()).clear();
        }
        updateUndoRedoButtons();
    }

    private void updateUndoRedoButtons() {
        if (selectedNodeIndex == 0) {
            undoButton.setEnabled(Boolean.FALSE);
        } else {
            undoButton.setEnabled(Boolean.TRUE);
        }
        if (selectedNodeIndex + 1 == selectedNodePath.size()) {
            redoButton.setEnabled(Boolean.FALSE);
        } else {
            redoButton.setEnabled(Boolean.TRUE);
        }
    }

    private void setIc(String goId, String goName) {
        if (goId != null) {
            if (mySelectedGo != null) {
                if (!mySelectedGo.getGoId().equals(goId)) {
                    this.termInstanceContent.remove(mySelectedGo);
                    GoTerm term = new GoTerm();
                    term.setGoId(goId);
                    mySelectedGo = term;
                    this.termInstanceContent.add(mySelectedGo);
                }
            } else {//previously selectedTerm is null
                GoTerm term = new GoTerm();
                term.setGoId(goId);
                mySelectedGo = term;
                this.termInstanceContent.add(mySelectedGo);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundPanel = new javax.swing.JPanel();
        detailLabel = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jPanel2 = new javax.swing.JPanel();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        homeButton = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jComboBox1 = new javax.swing.JComboBox();
        pValueLabel = new javax.swing.JLabel();
        pValueText = new JFormattedTextField(numberInstance);
        oboLabel = new javax.swing.JLabel();
        mainPanel = treeMap.getPanel();

        detailLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(detailLabel, org.openide.util.NbBundle.getMessage(TreeMapTopComponent.class, "TreeMapTopComponent.detailLabel.text")); // NOI18N

        javax.swing.GroupLayout backgroundPanelLayout = new javax.swing.GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(detailLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 779, Short.MAX_VALUE))
        );
        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(detailLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jToolBar1.setRollover(true);

        undoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/uncc/genosets/treemap/undo.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(undoButton, org.openide.util.NbBundle.getMessage(TreeMapTopComponent.class, "TreeMapTopComponent.undoButton.text")); // NOI18N
        undoButton.setToolTipText(org.openide.util.NbBundle.getMessage(TreeMapTopComponent.class, "TreeMapTopComponent.undoButton.toolTipText")); // NOI18N
        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });

        redoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/uncc/genosets/treemap/redo.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(redoButton, org.openide.util.NbBundle.getMessage(TreeMapTopComponent.class, "TreeMapTopComponent.redoButton.text")); // NOI18N
        redoButton.setToolTipText(org.openide.util.NbBundle.getMessage(TreeMapTopComponent.class, "TreeMapTopComponent.redoButton.toolTipText")); // NOI18N
        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoButtonActionPerformed(evt);
            }
        });

        homeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/uncc/genosets/treemap/root.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(homeButton, org.openide.util.NbBundle.getMessage(TreeMapTopComponent.class, "TreeMapTopComponent.homeButton.text")); // NOI18N
        homeButton.setToolTipText(org.openide.util.NbBundle.getMessage(TreeMapTopComponent.class, "TreeMapTopComponent.homeButton.toolTipText")); // NOI18N
        homeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeButtonActionPerformed(evt);
            }
        });

        jToolBar2.setRollover(true);
        jToolBar2.setToolTipText(org.openide.util.NbBundle.getMessage(TreeMapTopComponent.class, "TreeMapTopComponent.jToolBar2.toolTipText")); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Area by Population", "Area by Study Set" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });
        jToolBar2.add(jComboBox1);

        org.openide.awt.Mnemonics.setLocalizedText(pValueLabel, org.openide.util.NbBundle.getMessage(TreeMapTopComponent.class, "TreeMapTopComponent.pValueLabel.text")); // NOI18N

        pValueText.setEditable(false);
        pValueText.setText(org.openide.util.NbBundle.getMessage(TreeMapTopComponent.class, "TreeMapTopComponent.pValueText.text")); // NOI18N
        pValueText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pValueTextActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(oboLabel, org.openide.util.NbBundle.getMessage(TreeMapTopComponent.class, "TreeMapTopComponent.oboLabel.text_1")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(homeButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(undoButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(redoButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pValueLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pValueText, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(oboLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addGap(73, 73, 73))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(undoButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(redoButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(homeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(pValueText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(pValueLabel)
                        .addComponent(oboLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jToolBar1.add(jPanel2);

        mainPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                mainPanelComponentResized(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 362, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(backgroundPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backgroundPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void pValueTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pValueTextActionPerformed
        setPValue();
    }//GEN-LAST:event_pValueTextActionPerformed

    private void homeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeButtonActionPerformed
        selectedNodeIndex++;
        selectedNodePath.add(selectedNodeIndex, rootNodeItem);
        treeMap.setSubLayout(rootNodeItem);
        updateUndoRedoButtons();
    }//GEN-LAST:event_homeButtonActionPerformed

    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
        selectedNodeIndex--;
        treeMap.setSubLayout(selectedNodePath.get(selectedNodeIndex));
        updateUndoRedoButtons();
    }//GEN-LAST:event_undoButtonActionPerformed

    private void redoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoButtonActionPerformed
        selectedNodeIndex++;
        treeMap.setSubLayout(selectedNodePath.get(selectedNodeIndex));
        updateUndoRedoButtons();
    }//GEN-LAST:event_redoButtonActionPerformed

    private void mainPanelComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_mainPanelComponentResized
        if (initialized) {
            treeMap.resizeMe(mainPanel.getSize());
        }
    }//GEN-LAST:event_mainPanelComponentResized

private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
    String item = (String) jComboBox1.getSelectedItem();
    boolean oldPopulation = byPopulation;
    if (item.equals("Area by Population")) {
        byPopulation = true;
    } else {
        byPopulation = false;
    }

    if (oldPopulation != byPopulation) {
        treeMap.updateByPopulation(byPopulation);
    }



}//GEN-LAST:event_jComboBox1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JLabel detailLabel;
    private javax.swing.JButton homeButton;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JLabel oboLabel;
    private javax.swing.JLabel pValueLabel;
    private javax.swing.JTextField pValueText;
    private javax.swing.JButton redoButton;
    private javax.swing.JButton undoButton;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized TreeMapTopComponent getDefault() {
        if (instance == null) {
            instance = new TreeMapTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the TreeMapTopComponent instance. Never call {@link #getDefault}
     * directly!
     */
    public static synchronized TreeMapTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);


        if (win == null) {
            Logger.getLogger(TreeMapTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");


            return getDefault();
        }
        if (win instanceof TreeMapTopComponent) {
            return (TreeMapTopComponent) win;


        }
        Logger.getLogger(TreeMapTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");


        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public void componentOpened() {
        if(!initialized){
            initialized = true;
            treeMap.createVisualization((int)mainPanel.getSize().getWidth(), (int)mainPanel.getSize().getHeight());
        }
        this.enrichmentLookupListener = new LookupListener() {

            @Override
            public void resultChanged(LookupEvent ev) {
                goEnrichmentChanged();
            }
        };


        this.enrichmentResult.addLookupListener(
                this.enrichmentLookupListener);


        this.goEnrichmentChanged();

        this.studySetLookupListener = new LookupListener() {

            @Override
            public void resultChanged(LookupEvent ev) {
                studySetChanged();
            }
        };


        this.studySetResult.addLookupListener(
                this.studySetLookupListener);


        this.studySetChanged();

        //initialize term lookup

        this.termResult = Utilities.actionsGlobalContext().lookupResult(TermCalculation.class);

        this.termLookupListener = new LookupListener() {

            @Override
            public void resultChanged(LookupEvent ev) {
                termChanged();
            }
        };


        this.termResult.addLookupListener(termLookupListener);


        this.termInstanceContent.add(treeMap);
        //add lookup to treemap source
        GenoSetsTreeMapSource sourceLookup = Lookup.getDefault().lookup(GenoSetsTreeMapSource.class);

        sourceLookup.addLookup(
                this.getLookup());
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
        this.enrichmentResult.removeLookupListener(this.enrichmentLookupListener);
        this.termResult.removeLookupListener(this.termLookupListener);
        this.studySetResult.removeLookupListener(this.studySetLookupListener);

        this.termInstanceContent.remove(treeMap);
        GenoSetsTreeMapSource sourceLookup = Lookup.getDefault().lookup(GenoSetsTreeMapSource.class);
        sourceLookup.removeLookup(this.getLookup());
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    public void goEnrichmentChanged() {
        Collection<? extends GoEnrichment> allInstances = this.enrichmentResult.allInstances();
        for (GoEnrichment enrichment : allInstances) {
            if (selectedEnrichment != enrichment) {
                this.selectedEnrichment = enrichment;
                this.treeMap.updateEnrichment(enrichment);
            }
        }
    }

    private void termChanged() {
        TermCalculation oldTerm = this.selectedTermCalculation;
        for (TermCalculation goTerm : this.termResult.allInstances()) {
            this.selectedTermCalculation = goTerm;
        }
        boolean changed = false;
        if (oldTerm != null) {
            if (this.selectedTermCalculation != null) {
                if (!oldTerm.getTermId().equals(this.selectedTermCalculation.getTermId())) {
                    changed = true;
                }
            }
        } else {
            changed = true;
        }
        if (selectedTermCalculation != null) {
            if (changed) {
                treeMap.updateSelectedTerm(this.selectedTermCalculation.getTermId());
            }
        }
    }

    private void studySetChanged() {
        Collection<? extends StudySet> allInstances = studySetResult.allInstances();
        if (allInstances == null || allInstances.size() != 1) {
            return;
        }
        StudySet set = null;
        for (StudySet studySet : allInstances) {
            set = studySet;
            break;
        }
        if (set != null) {
            GoEnrichment enrichment = set.getLookup().lookup(GoEnrichment.class);
            if (enrichment == null) { //there is no default enrichment set, so update view
                this.selectedEnrichment = null;
                this.treeMap.updateEnrichment(null);
            }
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
