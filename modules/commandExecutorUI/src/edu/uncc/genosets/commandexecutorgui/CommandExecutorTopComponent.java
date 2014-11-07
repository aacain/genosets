/*
 * Copyright (C) 2014 Aurora Cain
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
package edu.uncc.genosets.commandexecutorgui;

import edu.uncc.genosets.util.CommandExecutor;
import edu.uncc.genosets.util.commandline.CommandLinePanel;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays the output of CommandExecutor.
 */
//@ConvertAsProperties(
//        dtd = "-//edu.uncc.genosets.commandexecutorgui//CommandExecutor//EN",
//        autostore = false)
//@TopComponent.Description(preferredID = "CommandExecutorTopComponent",
//        persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED)
//@TopComponent.Registration(mode = "editor", openAtStartup = false)
//@ActionID(category = "Window", id = "edu.uncc.genosets.commandexecutorgui.CommandExecutorTopComponent")
//@TopComponent.OpenActionRegistration(displayName = "#CTL_CommandExecutorAction")
@Messages({
    "CTL_CommandExecutorAction=CommandExectutorOutput",
    "CTL_CommandExecutorTopComponent=Command Executor Window",
    "HINT_CommandExecutorTopComponent=This is a Command Executor window"
})
public final class CommandExecutorTopComponent extends TopComponent {

    private final StyledDocument doc;
    private final CommandExecutor process;

    public CommandExecutorTopComponent() {
        this(null);
    }

    public CommandExecutorTopComponent(CommandExecutor commandExecutor) {
        this.process = commandExecutor;
        initComponents();
        this.doc = this.textPane.getStyledDocument();

        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);
        doc.addStyle("regular", def);
        Style red = doc.addStyle("red", def);
        StyleConstants.setForeground(red, Color.red);
        textPane.setEditable(false);

        setName(Bundle.CTL_CommandExecutorTopComponent());
        setToolTipText(Bundle.HINT_CommandExecutorTopComponent());
        if (this.process != null) {
            this.process.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    handleProcessOutput(evt);
                }
            });
        }

        //add actions
        InputMap inputMap = this.getInputMap();
        ActionMap actionMap = this.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "CTRL_C");
        actionMap.put("CTRL_C", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (process != null) {
                    process.cancel();
                }
            }
        });
    }

    private void handleProcessOutput(final PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Style style = doc.getStyle("regular");
                if ("ERR".equals(evt.getPropertyName())) {
                    style = doc.getStyle("red");
                }
                try {
                    doc.insertString(doc.getLength(), (String) evt.getNewValue(), style);
                } catch (BadLocationException ex) {
                    Logger.getLogger(CommandLinePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void shutdown() {
        if (this.process != null) {
            this.process.cancel();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();

        jScrollPane1.setViewportView(textPane);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane textPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        if (this.process != null) {
            this.process.executeAll();
            this.process.shutdown();
        }
    }

    @Override
    public void componentClosed() {
        shutdown();
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        HTMLEditorKit kit = new HTMLEditorKit();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            kit.write(out, doc, doc.getStartPosition().getOffset(), doc.getLength());
            String contents = new String(out.toByteArray());
            p.setProperty("contents", contents);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        String contents = p.getProperty("contents");
        HTMLEditorKit kit = new HTMLEditorKit();
        ByteArrayInputStream in = new ByteArrayInputStream(contents.getBytes());
        try {
            kit.read(in, doc, 0);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
