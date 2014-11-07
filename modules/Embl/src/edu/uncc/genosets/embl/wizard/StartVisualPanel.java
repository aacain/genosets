/*
 * 
 * 
 */

/*
 * StartVisualPanel.java
 *
 * Created on Oct 9, 2010, 12:46:57 PM
 */
package edu.uncc.genosets.embl.wizard;

import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author aacain
 */
public class StartVisualPanel extends javax.swing.JPanel {

    private String currentSourceType;

    /** Creates new form StartVisualPanel */
    public StartVisualPanel() {
        initComponents();

        projectsRadioButton.setSelected(true);
        currentSourceType = WizardConstants.SELECTED_PROJECT;
        fileText.setEnabled(false);
        browseFileButton.setEnabled(false);
        accessionNumberText.setEnabled(false);
    }


    public JTextField getAccessionNumberText() {
        return accessionNumberText;
    }

    public JRadioButton getAccessionRadioButton() {
        return accessionRadioButton;
    }

    public JRadioButton getFileRadioButton() {
        return fileRadioButton;
    }

    public JTextField getFileText() {
        return fileText;
    }

    public JRadioButton getProjectsRadioButton() {
        return projectsRadioButton;
    }

    protected void fireChange(String newSourceType) {
        String oldSourceType = currentSourceType;
        currentSourceType = newSourceType;
        this.firePropertyChange(WizardConstants.PROP_SOURCE_TYPE, oldSourceType, newSourceType);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        projectsRadioButton = new javax.swing.JRadioButton();
        accessionRadioButton = new javax.swing.JRadioButton();
        fileRadioButton = new javax.swing.JRadioButton();
        accessionNumberText = new javax.swing.JTextField();
        fileText = new javax.swing.JTextField();
        browseFileButton = new javax.swing.JButton();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(StartVisualPanel.class, "StartVisualPanel.jLabel1.text")); // NOI18N

        buttonGroup1.add(projectsRadioButton);
        projectsRadioButton.setSelected(true);
        projectsRadioButton.setText(org.openide.util.NbBundle.getMessage(StartVisualPanel.class, "StartVisualPanel.projectsRadioButton.text")); // NOI18N
        projectsRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectsRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(accessionRadioButton);
        accessionRadioButton.setText(org.openide.util.NbBundle.getMessage(StartVisualPanel.class, "StartVisualPanel.accessionRadioButton.text")); // NOI18N
        accessionRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                accessionRadioButtonActionPerformed(evt);
            }
        });

        buttonGroup1.add(fileRadioButton);
        fileRadioButton.setText(org.openide.util.NbBundle.getMessage(StartVisualPanel.class, "StartVisualPanel.fileRadioButton.text")); // NOI18N
        fileRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileRadioButtonActionPerformed(evt);
            }
        });

        accessionNumberText.setText(org.openide.util.NbBundle.getMessage(StartVisualPanel.class, "StartVisualPanel.accessionNumberText.text")); // NOI18N

        fileText.setText(org.openide.util.NbBundle.getMessage(StartVisualPanel.class, "StartVisualPanel.fileText.text")); // NOI18N

        browseFileButton.setText(org.openide.util.NbBundle.getMessage(StartVisualPanel.class, "StartVisualPanel.browseFileButton.text")); // NOI18N
        browseFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseFileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fileRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fileText, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(browseFileButton))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(accessionRadioButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(accessionNumberText, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE))
                            .addComponent(projectsRadioButton))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(projectsRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(accessionRadioButton)
                    .addComponent(accessionNumberText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fileRadioButton)
                    .addComponent(fileText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseFileButton))
                .addContainerGap(184, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void browseFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseFileButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_browseFileButtonActionPerformed

    private void accessionRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_accessionRadioButtonActionPerformed
        fileText.setEnabled(false);
        browseFileButton.setEnabled(false);
        this.fireChange(WizardConstants.BY_ACCESSION);
    }//GEN-LAST:event_accessionRadioButtonActionPerformed

    private void fileRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileRadioButtonActionPerformed
        fileText.setEnabled(true);
        browseFileButton.setEnabled(true);
        accessionNumberText.setEnabled(false);
        this.fireChange(WizardConstants.FROM_FILE);
    }//GEN-LAST:event_fileRadioButtonActionPerformed

    private void projectsRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectsRadioButtonActionPerformed
        fileText.setEnabled(false);
        browseFileButton.setEnabled(false);
        accessionNumberText.setEnabled(false);
        this.fireChange(WizardConstants.SELECTED_PROJECT);
}//GEN-LAST:event_projectsRadioButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField accessionNumberText;
    private javax.swing.JRadioButton accessionRadioButton;
    private javax.swing.JButton browseFileButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton fileRadioButton;
    private javax.swing.JTextField fileText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton projectsRadioButton;
    // End of variables declaration//GEN-END:variables
}
