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
package edu.uncc.genosets.util.commandline;

import edu.uncc.genosets.util.CommandExecutor;
import edu.uncc.genosets.util.CommandExecutor.Job;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author lucy
 */
public class CommandLinePanel extends javax.swing.JPanel {

    CommandExecutor process;
    Process runningProcess = null;
    SwingWorker myWorker;
    PropertyChangeListener outputListener;
    StyledDocument doc;

    /**
     * Creates new form CommandLinePanel
     */
    public CommandLinePanel() {
        initMore();
    }

    public CommandLinePanel(List<String> commands) {
        initMore();
        process = new CommandExecutor(1);
        process.addPropertyChangeListener(outputListener);
        if (commands == null) {
            return;
        }
        List<Job> jobs = new ArrayList<Job>();
        for (String string : commands) {
            Job job = process.convertLineToJob(string);
            if (job != null) {
                jobs.add(job);
            }
        }
        process.submit(jobs);
        process.executeAll();
    }

    public CommandLinePanel(File file) {
        initMore();
        process = new CommandExecutor(1);
        process.addPropertyChangeListener(outputListener);
        if (file == null) {
            return;
        }
        List<Job> jobs = null;
        try {
            jobs = process.convertFileToJobs(file);
        } catch (IOException ex) {
            Logger.getLogger(CommandLinePanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (Job job : jobs) {
            process.submitAndRun(job);
        }
        process.shutdown();
    }

    private void initMore() {
        initComponents();
        //cutomize the textPane document
        doc = textPane.getStyledDocument();
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);
        doc.addStyle("regular", def);
        Style red = doc.addStyle("red", def);
        StyleConstants.setForeground(red, Color.red);
        textPane.setEditable(false);

        //add actions
        InputMap inputMap = textPane.getInputMap();
        ActionMap actionMap = textPane.getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "MyEnterPressed");
        outputListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                updateOutput(evt);
            }
        };

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "CTRL_C");
        actionMap.put("CTRL_C", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancelled");
                if (process == null || !process.isRunning()) {
                    return;
                }
                System.out.println("Really cancelled");
                process.cancel();
            }
        });
    }

    public void cancel() {
        this.process.cancel();
    }

    public void updateOutput(final PropertyChangeEvent evt) {
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();

        jScrollPane2.setViewportView(textPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane textPane;
    // End of variables declaration//GEN-END:variables

    public static void run(final String[] args, final Options options) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                CommandLineParser parser = new GnuParser();
                CommandLinePanel panel = null;
                try {
                    CommandLine c1 = parser.parse(options, args);
                    String command = c1.getOptionValue("c");
                    if (command != null) {
                        panel = new CommandLinePanel(Collections.singletonList(command));
                    } else {
                        String infilename = c1.getOptionValue("i");
                        if (infilename != null) {
                            panel = new CommandLinePanel(new File(infilename));
                        }
                    }
                    final CommandLinePanel myPanel = panel;
                    JFrame frame = new JFrame("Native Executor");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.getContentPane().setLayout(new BorderLayout());
                    frame.add(panel, BorderLayout.CENTER);
                    frame.pack();
                    frame.setVisible(true);
                    Runtime.getRuntime().addShutdownHook(new Thread() {
                        public void run() {
                            myPanel.cancel();

                        }
                    });
                } catch (ParseException ex) {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp("commandExecutor", "", options, "Use '-h' or '--help' to print this message", true);
                }
            }
        });
    }

    public static void main(String[] args) {
        Option help = new Option("h", "help", false, "print this message");
        Options options1 = new Options();
        options1.addOption(help);

        OptionGroup group = new OptionGroup();
        Options options2 = new Options();
        group.isRequired();
        group.addOption(OptionBuilder.withArgName("command")
                .hasArg()
                .withDescription("single command to process (put into double-quotes if the command contains spaces)")
                .withLongOpt("command")
                .create("c"));
        group.addOption(OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("file containing commands to process.  Blank lines and lines beginning with pound sign (#) will be ignored.")
                .withLongOpt("in")
                .create("i"));
        options2.addOptionGroup(group);


        CommandLineParser parser = new GnuParser();
        try {
            CommandLine c1 = parser.parse(options1, args);
            if (c1.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("runCommandView", "", options2, "Use '-h' or '--help' to print this message", true);
            } else {
                run(args, options2);
            }
        } catch (ParseException ex) {
            run(args, options2);
        }
    }
}
