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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.view.CheckableNode;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;

public class CommandExecutorWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private CommandExecutorVisualPanel1 component;
    private ExplorerManager em;
    private WizardDescriptor wiz;
    private File file;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public CommandExecutorVisualPanel1 getComponent() {
        if (component == null) {
            this.em = new ExplorerManager();
            this.em.setRootContext(new AbstractNode(Children.LEAF));
            component = new CommandExecutorVisualPanel1(this.em);
            component.getBrowseButton().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    browseButtonAction();
                }
            });
        }
        return component;
    }

    private void browseButtonAction() {
        //The default dir to use if no value is stored
        File home = new File(System.getProperty("user.home"));
        //Now build a file chooser and invoke the dialog in one line of code
        //"libraries-dir" is our unique key
        File toAdd = new FileChooserBuilder("edu.uncc.genosets.commandexecutorgui").setTitle("Select Command File").
                setDefaultWorkingDirectory(home).setApproveText("Okay").setFilesOnly(true).showOpenDialog();
        //Result will be null if the user clicked cancel or closed the dialog w/o OK
        if (toAdd != null) {
            //do something
            component.getFileTextField().setText(toAdd.getAbsolutePath());
            NbPreferences.forModule(FileChooserBuilder.class).put("edu.uncc.genosets.commandexecutorgui", toAdd.getParent());
            try {
                ArrayList<String> commands = readFile(toAdd);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private ArrayList<String> readFile(File file) throws IOException {
        ArrayList<String> lines = new ArrayList<String>();
        if (file == this.file) {
            setCommands(lines);
            return lines;
        }
        this.file = file;
        if (this.file == null) {
            component.getFileTextField().setText("");
            setCommands(lines);
            return lines;
        }
        component.getFileTextField().setText(file.getAbsolutePath());
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
            }
        }
        setCommands(lines);
        return lines;
    }

    private void setCommands(ArrayList<String> commands) {
        //this.em.setRootContext(new AbstractNode(Children.create(new CommandChildFactory(commands), false)));
        this.em.setRootContext(Node.EMPTY);
    }

    private ArrayList<String> getSelected() {
        ArrayList<String> selected = new ArrayList<String>();
        Node[] nodes = this.em.getRootContext().getChildren().getNodes();
        for (Node node : nodes) {
            if (node instanceof CommandNode) {
                CommandNode n = (CommandNode) node;
                if (n.isSelected()) {
                    selected.add(n.getDisplayName());
                }
            }
        }
        return selected;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        try {
            this.wiz = wiz;
            readFile((File) this.wiz.getProperty("FILE"));
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        this.wiz.putProperty("FILE", this.file);
        this.wiz.putProperty("COMMANDS", this.getSelected());
    }

    public static class CommandChildFactory extends ChildFactory<String> {

        private final ArrayList<String> children;

        public CommandChildFactory(ArrayList<String> children) {
            this.children = children;
        }

        @Override
        protected boolean createKeys(List<String> toPopulate) {
            toPopulate.addAll(children);
            return true;
        }

        @Override
        protected Node createNodeForKey(String key) {
            return new CommandNode(key);
        }
    }

    public static class CommandNode extends AbstractNode implements CheckableNode {

        private static List<? extends Action> registeredActions;
        boolean selected = true;
        String command;

        public CommandNode(String command) {
            super(Children.LEAF);
            this.setDisplayName(command);
            if (command.isEmpty() || command.startsWith("#")) {
                this.setIconBaseWithExtension("edu/uncc/genosets/icons/method.png");
            } else {
                this.setIconBaseWithExtension("edu/uncc/genosets/icons/no-icon.png");
            }
            this.command = command;
        }

        protected static List<? extends Action> getRegisterActions() {
            if (registeredActions == null) {
                registeredActions = Utilities.actionsForPath("Actions/CommandNode");
            }
            return registeredActions;
        }

        @Override
        public Action[] getActions(boolean context) {
            List<? extends Action> actions = getRegisterActions();
            return actions.toArray(new Action[actions.size()]);
        }

        @Override
        public boolean isCheckable() {
            return (command.length() > 0 && !command.startsWith("#"));
        }

        @Override
        public boolean isCheckEnabled() {
            return (command.length() > 0 && !command.startsWith("#"));
        }

        @Override
        public Boolean isSelected() {
            return selected && isCheckable();
        }

        @Override
        public void setSelected(Boolean selected) {
            this.selected = selected;
        }
    }

    @ActionID(
            category = "Actions",
            id = "edu.uncc.genosets.commandexecutorgui.CheckAction")
    @ActionRegistration(
            displayName = "#CTL_CheckAction")
    @ActionReferences(value = {
        @ActionReference(path = "Actions/CommandNode", position = 10)})
    @NbBundle.Messages("CTL_CheckAction=Select")
    public static class CheckAction extends AbstractAction implements LookupListener, ContextAwareAction {

        private Lookup context;
        Lookup.Result<CommandNode> lkpInfo;

        public CheckAction() {
            this(Bundle.CTL_CheckAction());
        }

        public CheckAction(String name) {
            this(name, Utilities.actionsGlobalContext());
        }

        public CheckAction(String name, Lookup context) {
            super(name);
            this.context = context;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for (CommandNode commandNode : lkpInfo.allInstances()) {
                commandNode.setSelected(true);
            }
        }

        public boolean isEnabled() {
            init();
            return super.isEnabled();
        }

        private void init() {
            assert SwingUtilities.isEventDispatchThread() : "this shall be called just from AWT thread";

            if (lkpInfo != null) {
                return;
            }

            //The thing we want to listen for the presence or absence of
            //on the global selection
            lkpInfo = context.lookupResult(CommandNode.class);
            lkpInfo.addLookupListener(this);
            resultChanged(null);
        }

        @Override
        public void resultChanged(LookupEvent ev) {
            setEnabled(!lkpInfo.allInstances().isEmpty());
        }

        @Override
        public Action createContextAwareInstance(Lookup actionContext) {
            return new CheckAction(Bundle.CTL_CheckAction(), actionContext);
        }
    }

    @ActionID(
            category = "Actions",
            id = "edu.uncc.genosets.commandexecutorgui.UncheckAction")
    @ActionRegistration(
            displayName = "#CTL_Uncheck")
    @ActionReferences(value = {
        @ActionReference(path = "Actions/CommandNode", position = 10)})
    @NbBundle.Messages("CTL_Uncheck=Deselect")
    public static final class UncheckAction extends CheckAction {

        private UncheckAction() {
            super(Bundle.CTL_Uncheck());
        }

        private UncheckAction(Lookup context) {
            super(Bundle.CTL_Uncheck(), context);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for (CommandNode commandNode : lkpInfo.allInstances()) {
                commandNode.setSelected(false);
            }
        }

        @Override
        public Action createContextAwareInstance(Lookup actionContext) {
            return new UncheckAction(actionContext);
        }
    }
}
