/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.orthomcl;

import javax.swing.JTextField;

/**
 *
 * @author lucy
 */
public class GeneralOrthoMclSettingsVisualPanel extends javax.swing.JPanel {

    @Override
    public String getName() {
        return "OrthoMCL Settings";
    }

    /**
     * Creates new form GeneralOrthoMclSettingsVisualPanel
     */
    public GeneralOrthoMclSettingsVisualPanel() {
        initComponents();
    }

    public JTextField geteValueText() {
        return eValueText;
    }

    public JTextField getFilterLengthText() {
        return filterLengthText;
    }

    public JTextField getFilterStopCodons() {
        return filterStopCodons;
    }

    public JTextField getPercentMatchText() {
        return percentMatchText;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        filterLengthText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        filterStopCodons = new javax.swing.JTextField();
        percentMatchText = new javax.swing.JTextField();
        eValueText = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jTextArea1 = new javax.swing.JTextArea();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(GeneralOrthoMclSettingsVisualPanel.class, "GeneralOrthoMclSettingsVisualPanel.jLabel1.text")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(GeneralOrthoMclSettingsVisualPanel.class, "GeneralOrthoMclSettingsVisualPanel.jLabel2.text")); // NOI18N

        filterLengthText.setText(org.openide.util.NbBundle.getMessage(GeneralOrthoMclSettingsVisualPanel.class, "GeneralOrthoMclSettingsVisualPanel.filterLengthText.text")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(GeneralOrthoMclSettingsVisualPanel.class, "GeneralOrthoMclSettingsVisualPanel.jLabel3.text")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(GeneralOrthoMclSettingsVisualPanel.class, "GeneralOrthoMclSettingsVisualPanel.jLabel4.text")); // NOI18N

        jLabel5.setText(org.openide.util.NbBundle.getMessage(GeneralOrthoMclSettingsVisualPanel.class, "GeneralOrthoMclSettingsVisualPanel.jLabel5.text")); // NOI18N

        jLabel6.setText(org.openide.util.NbBundle.getMessage(GeneralOrthoMclSettingsVisualPanel.class, "GeneralOrthoMclSettingsVisualPanel.jLabel6.text")); // NOI18N

        filterStopCodons.setText(org.openide.util.NbBundle.getMessage(GeneralOrthoMclSettingsVisualPanel.class, "GeneralOrthoMclSettingsVisualPanel.filterStopCodons.text")); // NOI18N

        percentMatchText.setText(org.openide.util.NbBundle.getMessage(GeneralOrthoMclSettingsVisualPanel.class, "GeneralOrthoMclSettingsVisualPanel.percentMatchText.text")); // NOI18N

        eValueText.setText(org.openide.util.NbBundle.getMessage(GeneralOrthoMclSettingsVisualPanel.class, "GeneralOrthoMclSettingsVisualPanel.eValueText.text")); // NOI18N

        jTextArea1.setBackground(new java.awt.Color(240, 240, 240));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText(org.openide.util.NbBundle.getMessage(GeneralOrthoMclSettingsVisualPanel.class, "GeneralOrthoMclSettingsVisualPanel.jTextArea1.text")); // NOI18N
        jTextArea1.setWrapStyleWord(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(filterLengthText)
                            .addComponent(filterStopCodons)
                            .addComponent(percentMatchText)
                            .addComponent(eValueText)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jTextArea1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(filterLengthText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(filterStopCodons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(percentMatchText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(eValueText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextArea1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(53, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField eValueText;
    private javax.swing.JTextField filterLengthText;
    private javax.swing.JTextField filterStopCodons;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField percentMatchText;
    // End of variables declaration//GEN-END:variables
}
