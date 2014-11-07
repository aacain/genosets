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
 */
package edu.uncc.genosets.fasta.download;

import javax.swing.JCheckBox;

/**
 *
 * @author lucy
 */
public class GroupByVisualPanel extends javax.swing.JPanel {

    /**
     * Creates new form GroupByVisualPanel
     */
    public GroupByVisualPanel(boolean showOrthoMCL) {
        initComponents();
        orthoMclCheckBox.setVisible(showOrthoMCL);
    }

    @Override
    public String getName() {
        return "Grouping";
    }

    public JCheckBox getByOrganismCheckBox() {
        return byOrganismCheckBox;
    }

    public JCheckBox getOrthoMclCheckBox() {
        return orthoMclCheckBox;
    }

    public void setOrthoMclCheckBox(JCheckBox orthoMclCheckBox) {
        this.orthoMclCheckBox = orthoMclCheckBox;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        byOrganismCheckBox = new javax.swing.JCheckBox();
        orthoMclCheckBox = new javax.swing.JCheckBox();

        org.openide.awt.Mnemonics.setLocalizedText(byOrganismCheckBox, org.openide.util.NbBundle.getMessage(GroupByVisualPanel.class, "GroupByVisualPanel.byOrganismCheckBox.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(orthoMclCheckBox, org.openide.util.NbBundle.getMessage(GroupByVisualPanel.class, "GroupByVisualPanel.orthoMclCheckBox.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(byOrganismCheckBox)
                    .addComponent(orthoMclCheckBox))
                .addContainerGap(283, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(byOrganismCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(orthoMclCheckBox)
                .addContainerGap(244, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox byOrganismCheckBox;
    private javax.swing.JCheckBox orthoMclCheckBox;
    // End of variables declaration//GEN-END:variables
}
