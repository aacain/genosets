/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer.view;

import javax.swing.JComboBox;
import javax.swing.JPanel;

public final class RunOntologizerWizardVisualPanel2 extends JPanel {

    /**
     * Creates new form RunOntologizerWizardVisualPanel2
     */
    public RunOntologizerWizardVisualPanel2() {
        initComponents();
    }

    @Override
    public String getName() {
        return "Finalize";
    }
   
    public String getCalculationType(){
        String calc = (String)calculation.getSelectedItem();
        if(calc.equals("Parent-Child-Union"))
            return RunOntologizerWizardWizardPanel2.CALC_PCunion;
        if(calc.equals("Parent-Child-Intersection"))
            return RunOntologizerWizardWizardPanel2.CALC_PCintersection;
        if(calc.equals("Term-For-Term"))
            return RunOntologizerWizardWizardPanel2.CALC_PCintersection;
        return null;
    }
    public String getMTC(){
        String mtc = (String)mtcMethod.getSelectedItem();
        if(mtc.equals("Bonferroni"))
            return RunOntologizerWizardWizardPanel2.MTC_Bonferroni;
        if(mtc.equals("None"))
            return RunOntologizerWizardWizardPanel2.MTC_None;
        if(mtc.equals("Westfall-Young Single Step"))
            return RunOntologizerWizardWizardPanel2.MTC_Westfall;
        return null;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        mtcMethod = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        calculation = new javax.swing.JComboBox();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(RunOntologizerWizardVisualPanel2.class, "RunOntologizerWizardVisualPanel2.jLabel1.text")); // NOI18N

        mtcMethod.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Bonferroni", "None", "Westfall-Young Single Step" }));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(RunOntologizerWizardVisualPanel2.class, "RunOntologizerWizardVisualPanel2.jLabel2.text")); // NOI18N

        calculation.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Parent-Child-Union", "Parent-Child-Intersection", "Term-For-Term" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(mtcMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(calculation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(162, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(mtcMethod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(calculation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(231, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox calculation;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox mtcMethod;
    // End of variables declaration//GEN-END:variables
}