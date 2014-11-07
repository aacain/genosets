/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.download;

import edu.uncc.genosets.datamanager.api.FactType;
import edu.uncc.genosets.datanavigator.FactFlavor;
import java.io.File;
import java.util.Collection;
import javax.swing.ActionMap;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.ChoiceView;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

public final class CreateDownloadSetVisualPanel1 extends JPanel implements ExplorerManager.Provider {

    private ExplorerManager em = new ExplorerManager();
    private Result<FactType> lookupResult;
    private ChangeSupport cs = new ChangeSupport(this);

    /**
     * Creates new form CreateDownloadSetVisualPanel1
     */
    public CreateDownloadSetVisualPanel1() {
        //get FactFlavors
        Collection<? extends FactType> factTypes = Lookup.getDefault().lookupAll(FactType.class);
        AbstractNode root = new AbstractNode(Children.create(new FactTypeFactory(factTypes), true));
        em.setRootContext(root);

        ActionMap map = this.getActionMap();
        Lookup lookup = ExplorerUtils.createLookup(this.em, map);
        lookupResult = lookup.lookupResult(FactType.class);
        lookupResult.addLookupListener(new LookupListener() {

            @Override
            public void resultChanged(LookupEvent ev) {
                updateDescription(ev);
                cs.fireChange();
            }
        });

        initComponents();

        setNameText.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                cs.fireChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                cs.fireChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                cs.fireChange();
            }
        });

        locationText.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                cs.fireChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                cs.fireChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                cs.fireChange();
            }
        });

    }

    @Override
    public String getName() {
        return "Setup";
    }
    
    private void updateDescription(LookupEvent ev){
        Collection<? extends FactType> factTypes = lookupResult.allInstances();
        FactType fact = null;
        for (FactType factType : factTypes) {
            fact = factType;
        }
        if(fact != null){
            factDetailArea.setText(fact.getDescription());
            return;
        }
        factDetailArea.setText("");
    }

    public void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener l) {
        cs.removeChangeListener(l);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        setNameText = new javax.swing.JTextField();
        jComboBox1 = new ChoiceView();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        locationText = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        factDetailArea = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(CreateDownloadSetVisualPanel1.class, "CreateDownloadSetVisualPanel1.jLabel1.text")); // NOI18N

        setNameText.setText(org.openide.util.NbBundle.getMessage(CreateDownloadSetVisualPanel1.class, "CreateDownloadSetVisualPanel1.setNameText.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(CreateDownloadSetVisualPanel1.class, "CreateDownloadSetVisualPanel1.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(CreateDownloadSetVisualPanel1.class, "CreateDownloadSetVisualPanel1.jLabel3.text")); // NOI18N

        locationText.setText(org.openide.util.NbBundle.getMessage(CreateDownloadSetVisualPanel1.class, "CreateDownloadSetVisualPanel1.locationText.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(browseButton, org.openide.util.NbBundle.getMessage(CreateDownloadSetVisualPanel1.class, "CreateDownloadSetVisualPanel1.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        factDetailArea.setColumns(20);
        factDetailArea.setEditable(false);
        factDetailArea.setLineWrap(true);
        factDetailArea.setRows(5);
        factDetailArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(factDetailArea);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(CreateDownloadSetVisualPanel1.class, "CreateDownloadSetVisualPanel1.jLabel4.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(setNameText, javax.swing.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                                    .addComponent(locationText)
                                    .addComponent(jComboBox1, 0, 302, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(browseButton))
                            .addComponent(jScrollPane1))
                        .addGap(16, 16, 16))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(setNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(locationText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(33, 33, 33)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        //The default dir to use if no value is stored
        File home = new File(System.getProperty("user.home") + File.separator + "lib");
        //Now build a file chooser and invoke the dialog in one line of code
        //"libraries-dir" is our unique key
        File toAdd = new FileChooserBuilder("download-dir").setTitle("Select Download Location").
                setDefaultWorkingDirectory(home).setApproveText("Add").showOpenDialog();
        //Result will be null if the user clicked cancel or closed the dialog w/o OK
        if (toAdd != null) {
            //do something
            locationText.setText(toAdd.getAbsolutePath());
        }
    }//GEN-LAST:event_browseButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JTextArea factDetailArea;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField locationText;
    private javax.swing.JTextField setNameText;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return this.em;
    }

    String getSetName() {
        return setNameText.getText();
    }

    FactType getFactType() {
        for (FactType t : lookupResult.allInstances()) {
            return t;
        }
        return null;
    }

    String getLocationText() {
        return locationText.getText();
    }
}