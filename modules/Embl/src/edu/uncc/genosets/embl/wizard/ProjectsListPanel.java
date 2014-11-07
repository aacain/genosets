/*
 * 
 * 
 */

/*
 * ProjectsListPanel.java
 *
 * Created on Oct 8, 2010, 5:15:35 PM
 */
package edu.uncc.genosets.embl.wizard;

import java.awt.Color;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.ListView;

/**
 *
 * @author aacain
 */
public class ProjectsListPanel extends javax.swing.JPanel implements ExplorerManager.Provider {

    private ExplorerManager em;

    /** Creates new form ProjectsListPanel */
    public ProjectsListPanel(ExplorerManager em) {
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

        availableView = new ListView();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(availableView, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(availableView, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane availableView;
    // End of variables declaration//GEN-END:variables

    @Override
    public void addNotify() {
        super.addNotify();
        advancedInit();
    }

    public void advancedInit() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(availableView, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(availableView, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
        );
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return this.em;
    }
}