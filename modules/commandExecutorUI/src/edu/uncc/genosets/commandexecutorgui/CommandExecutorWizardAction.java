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

import edu.uncc.genosets.util.RunBash;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;

@ActionID(category = "AddData", id = "edu.uncc.genosets.commandexecutorgui.CommandExecutorWizardAction")
@ActionRegistration(displayName = "Run Commands")
@ActionReference(path = "Menu/Tools", position = 1)
public final class CommandExecutorWizardAction implements ActionListener {

    private File myFile;

    public CommandExecutorWizardAction() {
        this(null);
    }

    public CommandExecutorWizardAction(File myFile) {
        this.myFile = myFile;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
        panels.add(new CommandExecutorWizardPanel1());
        String[] steps = new String[panels.size()];
        for (int i = 0; i < panels.size(); i++) {
            Component c = panels.get(i).getComponent();
            // Default step name to component name of panel.
            steps[i] = c.getName();
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
            }
        }
        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wiz.setTitleFormat(new MessageFormat("{0}"));
        wiz.setTitle("Run Commands");
        wiz.putProperty("FILE", myFile);
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            File file = (File) wiz.getProperty("FILE");
            File parentFile = file.getParentFile();
            ArrayList<String> commands = (ArrayList<String>) wiz.getProperty("COMMANDS");
            if (file != null && commands != null) {
                file = file.getAbsoluteFile();
                RunBash bash = new RunBash();
                bash.run(file);
//                final CommandExecutor process = new CommandExecutor(parentFile, null, 1);
//                for (String string : commands) {
//                    CommandExecutor.Job job = process.convertLineToJob(string);
//                    if (job != null) {
//                        process.submit(job);
//                    }
//                }
//
//                Action[] actions = new Action[]{new StopAction(process)};
//                final InputOutput io = IOProvider.getDefault().getIO(file.getName(), actions);
//                process.addPropertyChangeListener(new PropertyChangeListener() {
//                    @Override
//                    public void propertyChange(PropertyChangeEvent evt) {
//                        if (evt.getPropertyName().equals("ERR")) {
//                            io.getErr().print(evt.getNewValue());
//                        } else {
//                            io.getOut().print(evt.getNewValue());
//                        }
//                        //close streams is shutdown command.
//                        if (evt.getPropertyName().equals("SHUTDOWN")) {
//                            io.getOut().close();
//                            io.getErr().close();
//                        }
//                    }
//                });
//
//                process.executeAll();
//                process.shutdown();
            }
        }
    }
}
