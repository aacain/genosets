/*
 * 
 * 
 */
package edu.uncc.genosets.embl.goa;

import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;

public class GoaSelectWizardPanel implements WizardDescriptor.Panel {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private GoaSelectVisualPanel component;
    private WizardDescriptor wd;
    private Set<AssembledUnit> available;
    private ExplorerManager emAvailable;
    private ExplorerManager emSelected;
    private boolean valid;
    public static final String PROP_AssSet = "PROP_AssSet";

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            emAvailable = new ExplorerManager();
            emSelected = new ExplorerManager();
            //initialize ems
            WizardQueryBuilder wqb = new WizardQueryBuilder();
            available = new HashSet<AssembledUnit>(wqb.getAssUnits());
            AbstractNode root = new AbstractNode(Children.create(new AssUnitChildNodeFactory(wqb.getAssUnits()), true));
            emAvailable.setRootContext(root);
            component = new GoaSelectVisualPanel(emAvailable, emSelected);

            //add listeners
            addListeners();
        }
        return component;
    }

    public void addListeners() {
        component.getAddButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addButtonAction();
            }
        });
        component.getRemoveButton().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeButtonAction();
            }
        });
    }

    private void addButtonAction() {
        Node[] selected = emAvailable.getSelectedNodes();
        Set<AssembledUnit> asses = new HashSet<AssembledUnit>();
        for (Node node : selected) {
            if (node instanceof AssUnitChildNodeFactory.AssUnitNode) {
                AssUnitChildNodeFactory.AssUnitNode aNode = (AssUnitChildNodeFactory.AssUnitNode) node;
                asses.add(aNode.getAssUnit());
            }
        }
        Children children = emSelected.getRootContext().getChildren();
        for (Node node : children.getNodes()) {
            if (node instanceof AssUnitChildNodeFactory.AssUnitNode) {
                AssUnitChildNodeFactory.AssUnitNode aNode = (AssUnitChildNodeFactory.AssUnitNode) node;
                asses.add(aNode.getAssUnit());
            }
        }

        Set<AssembledUnit> newAvailable = new HashSet<AssembledUnit>(available);
        newAvailable.removeAll(asses);


        Node sRoot = new AbstractNode(Children.create(new AssUnitChildNodeFactory(asses), true));
        emSelected.setRootContext(sRoot);

        Node aRoot = new AbstractNode(Children.create(new AssUnitChildNodeFactory(newAvailable), true));
        emAvailable.setRootContext(aRoot);

        if (asses.size() > 0) {
            valid = true;
            fireChangeEvent();
        } else {
            valid = false;
            fireChangeEvent();
        }
    }

    private void removeButtonAction() {
        Node[] selected = emSelected.getSelectedNodes();
        Set<AssembledUnit> removed = new HashSet<AssembledUnit>();
        for (Node node : selected) {
            if (node instanceof AssUnitChildNodeFactory.AssUnitNode) {
                AssUnitChildNodeFactory.AssUnitNode aNode = (AssUnitChildNodeFactory.AssUnitNode) node;
                removed.add(aNode.getAssUnit());
            }
        }

        Set<AssembledUnit> notSelected = new HashSet<AssembledUnit>();
        Children children = emAvailable.getRootContext().getChildren();
        for (Node node : children.getNodes()) {
            if (node instanceof AssUnitChildNodeFactory.AssUnitNode) {
                AssUnitChildNodeFactory.AssUnitNode aNode = (AssUnitChildNodeFactory.AssUnitNode) node;
                notSelected.add(aNode.getAssUnit());
            }
        }

        Set<AssembledUnit> newSelected = new HashSet<AssembledUnit>(available);
        newSelected.removeAll(removed);
        newSelected.removeAll(notSelected);

        Set<AssembledUnit> newAvailable = new HashSet<AssembledUnit>(available);
        newAvailable.removeAll(newSelected);

        Node sRoot = new AbstractNode(Children.create(new AssUnitChildNodeFactory(newSelected), true));
        emSelected.setRootContext(sRoot);

        Node aRoot = new AbstractNode(Children.create(new AssUnitChildNodeFactory(newAvailable), true));
        emAvailable.setRootContext(aRoot);

        if (newSelected.size() > 0) {
            valid = true;

        } else {
            valid = false;
        }
        fireChangeEvent();
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }

    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        if (valid == true) {
            return true;
        }
        return false;
        // If it depends on some condition (form filled out...), then:
        // return someCondition();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent();
        // and uncomment the complicated stuff below.
    }
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0

    public final void addChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.add(l);
        }
    }

    public final void removeChangeListener(ChangeListener l) {
        synchronized (listeners) {
            listeners.remove(l);
        }
    }

    protected final void fireChangeEvent() {
        Iterator<ChangeListener> it;
        synchronized (listeners) {
            it = new HashSet<ChangeListener>(listeners).iterator();
        }
        ChangeEvent ev = new ChangeEvent(this);
        while (it.hasNext()) {
            it.next().stateChanged(ev);
        }
    }

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {
        this.wd = (WizardDescriptor) settings;
    }

    public void storeSettings(Object settings) {
        Set<AssembledUnit> selected = new HashSet<AssembledUnit>();
        Children children = emSelected.getRootContext().getChildren();
        for (Node node : children.getNodes()) {
            if (node instanceof AssUnitChildNodeFactory.AssUnitNode) {
                AssUnitChildNodeFactory.AssUnitNode aNode = (AssUnitChildNodeFactory.AssUnitNode) node;
                selected.add(aNode.getAssUnit());
            }
        }
        this.wd.putProperty(PROP_AssSet, selected);
    }
}
