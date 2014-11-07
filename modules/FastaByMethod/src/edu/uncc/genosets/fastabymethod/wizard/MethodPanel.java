/*
 * 
 * 
 */

/*
 * MethodPanel.java
 *
 * Created on Jul 15, 2011, 4:48:52 PM
 */

package edu.uncc.genosets.fastabymethod.wizard;

import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.OutlineView;

/**
 *
 * @author aacain
 */
public class MethodPanel extends javax.swing.JPanel implements ExplorerManager.Provider{

    private ExplorerManager em;
    /** Creates new form MethodPanel */
    public MethodPanel(ExplorerManager em) {
        this.em = em;
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainScrollPane = new OutlineView();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public ExplorerManager getExplorerManager() {
        return this.em;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane mainScrollPane;
    // End of variables declaration//GEN-END:variables

}
