/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.gffloader;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.persister.OrganismPersister;
import edu.uncc.genosets.gffloader.OrganismChildFactory.OrganismNode;
import java.awt.Dialog;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.Collections;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.api.javahelp.Help;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.ChoiceView;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.util.WeakListeners;

public final class GffWizardVisualPanel1 extends JPanel implements ExplorerManager.Provider, PropertyChangeListener {

    private ExplorerManager em;
    private File gffFile;
    private File assMappingFile;
    private File featureMappingFile;

    /**
     * Creates new form GffWizardVisualPanel1
     */
    public GffWizardVisualPanel1() {
        em = new ExplorerManager();
        initComponents();
        updateOrganisms(null);
        em.addPropertyChangeListener(WeakListeners.propertyChange(this, em));      
        featureMappingTextField.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                featureMappingTextField.setText("");
                featureMappingFile = null;
            }      
        });
        
        assMappingTextField.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                assMappingTextField.setText("");
                assMappingFile = null;
            }
            
        });
    }

    public File getAssMappingFile() {
        return assMappingFile;
    }

    public File getFeatureMappingFile() {
        return featureMappingFile;
    }
    
    private void propertyChanged() {
        firePropertyChange("PROP_xfjdsfjie", null, null);
    }

    private void updateOrganisms(Organism org) {
        Children children = Children.create(new OrganismChildFactory(), false);
        AbstractNode root = new AbstractNode(children);
        OrganismNode orgNode = null;
        if (org != null) {
            for (Node node : children.getNodes()) {
                if (node instanceof OrganismNode) {
                    OrganismNode orgNodeTemp = (OrganismNode) node;
                    if (orgNodeTemp.getOrganism().equals(org)) {
                        orgNode = orgNodeTemp;
                    }
                }
            }
        }
        this.em.setRootContext(root);
        if (orgNode != null) {
            try {
                this.em.setExploredContextAndSelection(root, new Node[]{orgNode});
                this.organismComboBox.setSelectedItem(orgNode);
            } catch (PropertyVetoException ex) {
            }
        }
        propertyChanged();
    }

    public Organism getOrganism() {
        for (Node node : em.getSelectedNodes()) {
            if (node instanceof OrganismNode) {
                return ((OrganismNode) node).getOrganism();
            }
        }
        return null;
    }

    public File getFile() {
        return this.gffFile;
    }

    @Override
    public String getName() {
        return "Select File & Organism:";
    }

    public JTextArea getTranslateTypeTextArea() {
        return translateTypeTextArea;
    }

    private Organism createOrganism(NewOrganismPanel orgPanel) {
        Organism org = new Organism();
        org.setKingdom(orgPanel.getKingdom());
        org.setPhylum(orgPanel.getPhlum());
        org.setTaxClass(orgPanel.getTaxClass());
        org.setTaxOrder(orgPanel.getOrder());
        org.setFamily(orgPanel.getFamily());
        org.setGenus(orgPanel.getFamily());
        org.setSpecies(orgPanel.getSpecies());
        org.setStrain(orgPanel.getStrain());
        org.setSample(orgPanel.getSample());
        org.setShortName(orgPanel.getShortName());
        org.setProjectId(orgPanel.getProject());
        return org;
    }

    @Override
    public void addNotify() {
        super.addNotify();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        gffFileTextField = new javax.swing.JTextField();
        fileButton = new javax.swing.JButton();
        organismComboBox = new ChoiceView();
        jLabel2 = new javax.swing.JLabel();
        newButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        translateTypeTextArea = new javax.swing.JTextArea();
        typeHelpButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        assMappingTextField = new javax.swing.JTextField();
        featureMappingTextField = new javax.swing.JTextField();
        assMappingFileButton = new javax.swing.JButton();
        featureMappingFileButton = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(GffWizardVisualPanel1.class, "GffWizardVisualPanel1.jLabel1.text")); // NOI18N

        gffFileTextField.setEditable(false);
        gffFileTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        gffFileTextField.setText(org.openide.util.NbBundle.getMessage(GffWizardVisualPanel1.class, "GffWizardVisualPanel1.gffFileTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(fileButton, org.openide.util.NbBundle.getMessage(GffWizardVisualPanel1.class, "GffWizardVisualPanel1.fileButton.text")); // NOI18N
        fileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(GffWizardVisualPanel1.class, "GffWizardVisualPanel1.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(newButton, org.openide.util.NbBundle.getMessage(GffWizardVisualPanel1.class, "GffWizardVisualPanel1.newButton.text")); // NOI18N
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(GffWizardVisualPanel1.class, "GffWizardVisualPanel1.jLabel3.text")); // NOI18N

        translateTypeTextArea.setColumns(20);
        translateTypeTextArea.setRows(5);
        jScrollPane1.setViewportView(translateTypeTextArea);

        typeHelpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/edu/uncc/genosets/gffloader/question.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(typeHelpButton, org.openide.util.NbBundle.getMessage(GffWizardVisualPanel1.class, "GffWizardVisualPanel1.typeHelpButton.text")); // NOI18N
        typeHelpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeHelpButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(GffWizardVisualPanel1.class, "GffWizardVisualPanel1.jLabel4.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(GffWizardVisualPanel1.class, "GffWizardVisualPanel1.jLabel5.text")); // NOI18N

        assMappingTextField.setEditable(false);
        assMappingTextField.setText(org.openide.util.NbBundle.getMessage(GffWizardVisualPanel1.class, "GffWizardVisualPanel1.assMappingTextField.text")); // NOI18N

        featureMappingTextField.setEditable(false);
        featureMappingTextField.setText(org.openide.util.NbBundle.getMessage(GffWizardVisualPanel1.class, "GffWizardVisualPanel1.featureMappingTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(assMappingFileButton, org.openide.util.NbBundle.getMessage(GffWizardVisualPanel1.class, "GffWizardVisualPanel1.assMappingFileButton.text")); // NOI18N
        assMappingFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assMappingFileButtonActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(featureMappingFileButton, org.openide.util.NbBundle.getMessage(GffWizardVisualPanel1.class, "GffWizardVisualPanel1.featureMappingFileButton.text")); // NOI18N
        featureMappingFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                featureMappingFileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(typeHelpButton, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(gffFileTextField)
                            .addComponent(organismComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(assMappingTextField)
                            .addComponent(featureMappingTextField))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(assMappingFileButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(newButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fileButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(featureMappingFileButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(gffFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(organismComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(newButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(assMappingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(assMappingFileButton))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(featureMappingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(featureMappingFileButton)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(typeHelpButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void fileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileButtonActionPerformed
    //The default dir to use if no value is stored
    File home = new File(System.getProperty("user.home"));
    //Now build a file chooser and invoke the dialog in one line of code
    //"libraries-dir" is our unique key
    File toAdd = new FileChooserBuilder("AddFileDirectory").setTitle("Select FASTA Folder").
            setDefaultWorkingDirectory(home).setApproveText("Open").setDirectoriesOnly(false).showOpenDialog();
    //Result will be null if the user clicked cancel or closed the dialog w/o OK
    if (toAdd != null) {
        //set the directory as the parent
        NbPreferences.forModule(FileChooserBuilder.class).put("AddFileDirectory", toAdd.getParent());
        //do something
        gffFile = toAdd;
        gffFileTextField.setText(gffFile.getAbsolutePath());
        propertyChanged();
    }
}//GEN-LAST:event_fileButtonActionPerformed

private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
    NewOrganismPanel orgPanel = new NewOrganismPanel();
    DialogDescriptor descriptor = new DialogDescriptor(new JScrollPane(orgPanel), "Create a new organism", true, DialogDescriptor.OK_CANCEL_OPTION, DialogDescriptor.OK_OPTION, null);
    Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
    dialog.setVisible(true);
    dialog.toFront();
    boolean cancelled = descriptor.getValue() != DialogDescriptor.OK_OPTION;
    if (!cancelled) {
        //add organism
        OrganismPersister persister = OrganismPersister.instantiate();
        Organism org = createOrganism(orgPanel);
        persister.setup(org);
        DataManager mgr = DataManager.getDefault();
        mgr.persist(Collections.singletonList(persister));
        updateOrganisms(org);
    }
}//GEN-LAST:event_newButtonActionPerformed

private void typeHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeHelpButtonActionPerformed
    Help h = Lookup.getDefault().lookup(Help.class);
    if (h != null) {
        h.showHelp(new HelpCtx("edu.uncc.genosets.gffloader.general"));
    }
}//GEN-LAST:event_typeHelpButtonActionPerformed

    private void assMappingFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_assMappingFileButtonActionPerformed
        //The default dir to use if no value is stored
        File home = new File(System.getProperty("user.home"));
        //Now build a file chooser and invoke the dialog in one line of code
        //"libraries-dir" is our unique key
        File assMapping = new FileChooserBuilder("AddFileDirectory_assMapping").setTitle("Select Assembled Unit Mapping File").
                setDefaultWorkingDirectory(home).setApproveText("Open").setDirectoriesOnly(false).showOpenDialog();
        //Result will be null if the user clicked cancel or closed the dialog w/o OK
        if (assMapping != null) {
            //set the directory as the parent
            NbPreferences.forModule(FileChooserBuilder.class).put("AddFileDirectory_assMapping", assMapping.getParent());
            //do something
            assMappingFile = assMapping;
            assMappingTextField.setText(assMappingFile.getAbsolutePath());
        }else{
            assMappingTextField.setText("");
            assMappingFile = null;
        }
    }//GEN-LAST:event_assMappingFileButtonActionPerformed

    private void featureMappingFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_featureMappingFileButtonActionPerformed
        //The default dir to use if no value is stored
        File home = new File(System.getProperty("user.home"));
        //Now build a file chooser and invoke the dialog in one line of code
        //"libraries-dir" is our unique key
        File toAdd = new FileChooserBuilder("AddFileDirectory_featureMapping").setTitle("Select Location Mapping File").
        setDefaultWorkingDirectory(home).setApproveText("Open").setDirectoriesOnly(false).showOpenDialog();
        //Result will be null if the user clicked cancel or closed the dialog w/o OK
        if (toAdd != null) {
            //set the directory as the parent
            NbPreferences.forModule(FileChooserBuilder.class).put("AddFileDirectory_featureMapping", toAdd.getParent());
            //do something
            featureMappingFile = toAdd;
            featureMappingTextField.setText(featureMappingFile.getAbsolutePath());
        }else{
            featureMappingTextField.setText("");
            featureMappingFile = null;
        }
    }//GEN-LAST:event_featureMappingFileButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton assMappingFileButton;
    private javax.swing.JTextField assMappingTextField;
    private javax.swing.JButton featureMappingFileButton;
    private javax.swing.JTextField featureMappingTextField;
    private javax.swing.JButton fileButton;
    private javax.swing.JTextField gffFileTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton newButton;
    private javax.swing.JComboBox organismComboBox;
    private javax.swing.JTextArea translateTypeTextArea;
    private javax.swing.JButton typeHelpButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return this.em;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        propertyChanged();
    }
}
