/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.fasta;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

public final class AssUnitNucFastaVisualPanel1 extends JPanel {

    /**
     * Creates new form AssUnitNucFastaVisualPanel1
     */
    public AssUnitNucFastaVisualPanel1() {
        initComponents();
    }

    @Override
    public String getName() {
        return "File Format";
    }

    public JCheckBox getCheckFilePerMethod() {
        return checkFilePerMethod;
    }

    public void setCheckFilePerMethod(JCheckBox checkFilePerMethod) {
        this.checkFilePerMethod = checkFilePerMethod;
    }

    public JCheckBox getCheckFilePerOrganism() {
        return checkFilePerOrganism;
    }

    public void setCheckFilePerOrganism(JCheckBox checkFilePerOrganism) {
        this.checkFilePerOrganism = checkFilePerOrganism;
    }

    public JCheckBox getCheckPrefix4Letters() {
        return checkPrefix4Letters;
    }

    public void setCheckPrefix4Letters(JCheckBox checkPrefix4Letters) {
        this.checkPrefix4Letters = checkPrefix4Letters;
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        checkFilePerOrganism = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        checkFilePerMethod = new javax.swing.JCheckBox();
        checkPrefix4Letters = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(checkFilePerOrganism, org.openide.util.NbBundle.getMessage(AssUnitNucFastaVisualPanel1.class, "AssUnitNucFastaVisualPanel1.checkFilePerOrganism.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(AssUnitNucFastaVisualPanel1.class, "AssUnitNucFastaVisualPanel1.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(checkFilePerMethod, org.openide.util.NbBundle.getMessage(AssUnitNucFastaVisualPanel1.class, "AssUnitNucFastaVisualPanel1.checkFilePerMethod.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(checkPrefix4Letters, org.openide.util.NbBundle.getMessage(AssUnitNucFastaVisualPanel1.class, "AssUnitNucFastaVisualPanel1.checkPrefix4Letters.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(checkPrefix4Letters)
                    .addComponent(checkFilePerMethod)
                    .addComponent(checkFilePerOrganism))
                .addContainerGap(171, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(checkFilePerOrganism)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkFilePerMethod)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(checkPrefix4Letters)
                .addContainerGap(182, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox checkFilePerMethod;
    private javax.swing.JCheckBox checkFilePerOrganism;
    private javax.swing.JCheckBox checkPrefix4Letters;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
