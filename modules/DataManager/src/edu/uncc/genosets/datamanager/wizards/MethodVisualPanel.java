/*
 * 
 * 
 */

/*
 * AccessionSelectVisualPanel.java
 *
 * Created on Oct 16, 2010, 4:38:22 PM
 */
package edu.uncc.genosets.datamanager.wizards;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.awt.Toolkit;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import org.openide.util.ChangeSupport;

/**
 *
 * @author aacain
 */
public class MethodVisualPanel extends javax.swing.JPanel {

    private ChangeSupport cs = new ChangeSupport(this);
    private static final int MAX = 255;

    /**
     * Creates new form AccessionSelectVisualPanel
     */
    public MethodVisualPanel(AnnotationMethod methodConstants) {
        initComponents();
        initMethod(methodConstants);
        this.nameField.getDocument().addDocumentListener(new DocumentListener() {

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
        return "Enter Method Details";
    }

    public JTextField getCategoryField() {
        return categoryField;
    }

    public void setCategoryField(JTextField categoryField) {
        this.categoryField = categoryField;
    }

    public JTextArea getDescriptionArea() {
        return descriptionArea;
    }

    public void setDescriptionArea(JTextArea descriptionArea) {
        this.descriptionArea = descriptionArea;
    }

    public JTextField getSourceField() {
        return sourceField;
    }

    public void setSourceField(JTextField sourceField) {
        this.sourceField = sourceField;
    }

    public JTextField getTypeField() {
        return typeField;
    }

    public void setTypeField(JTextField typeField) {
        this.typeField = typeField;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public void addChangeListener(ChangeListener l) {
        synchronized (cs) {
            this.cs.addChangeListener(l);
        }
    }

    public void removeChangeListener(ChangeListener l) {
        synchronized (cs) {
            this.cs.removeChangeListener(l);
        }
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
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        descriptionArea = new javax.swing.JTextArea(new ShortDocument());
        sourceField = new javax.swing.JTextField();
        typeField = new javax.swing.JTextField();
        categoryField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();

        jLabel1.setText(org.openide.util.NbBundle.getMessage(MethodVisualPanel.class, "MethodVisualPanel.jLabel1.text")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(MethodVisualPanel.class, "MethodVisualPanel.jLabel2.text")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(MethodVisualPanel.class, "MethodVisualPanel.jLabel3.text")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(MethodVisualPanel.class, "MethodVisualPanel.jLabel4.text")); // NOI18N

        descriptionArea.setColumns(20);
        descriptionArea.setRows(5);
        jScrollPane1.setViewportView(descriptionArea);

        sourceField.setText(org.openide.util.NbBundle.getMessage(MethodVisualPanel.class, "MethodVisualPanel.sourceField.text")); // NOI18N

        typeField.setText(org.openide.util.NbBundle.getMessage(MethodVisualPanel.class, "MethodVisualPanel.typeField.text")); // NOI18N

        categoryField.setText(org.openide.util.NbBundle.getMessage(MethodVisualPanel.class, "MethodVisualPanel.categoryField.text")); // NOI18N

        jLabel5.setText(org.openide.util.NbBundle.getMessage(MethodVisualPanel.class, "MethodVisualPanel.jLabel5.text")); // NOI18N

        nameField.setText(org.openide.util.NbBundle.getMessage(MethodVisualPanel.class, "MethodVisualPanel.nameField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(categoryField, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(typeField, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                            .addComponent(sourceField, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(36, 36, 36)
                        .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE))
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(categoryField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(typeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(sourceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                .addGap(11, 11, 11))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField categoryField;
    private javax.swing.JTextArea descriptionArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField nameField;
    private javax.swing.JTextField sourceField;
    private javax.swing.JTextField typeField;
    // End of variables declaration//GEN-END:variables

    protected void initMethod(AnnotationMethod methodConstants) {
        categoryField.setEnabled(methodConstants.getMethodCategory() == null);
        categoryField.setText(methodConstants.getMethodCategory());
        typeField.setEnabled(methodConstants.getMethodType() == null);
        typeField.setText(methodConstants.getMethodType());
        sourceField.setEnabled(methodConstants.getMethodSourceType() == null);
        sourceField.setText(methodConstants.getMethodSourceType());
        nameField.setEnabled(methodConstants.getMethodName() == null);
        nameField.setText(methodConstants.getMethodName());
        descriptionArea.setEnabled(methodConstants.getMethodDescription() == null);
        descriptionArea.setText(methodConstants.getMethodDescription());
    }

    private static final class ShortDocument extends PlainDocument {

        @Override
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            if (this.getLength() == MAX) {
                Toolkit.getDefaultToolkit().beep();
                return;
            }
            if (this.getLength() + str.length() > MAX) {
                Toolkit.getDefaultToolkit().beep();
                int index = MAX - this.getLength();
                if (index > str.length()) {
                    index = str.length();
                }
                str = str.substring(0, index);
            }
            super.insertString(offs, str, a);
        }
    }
}
