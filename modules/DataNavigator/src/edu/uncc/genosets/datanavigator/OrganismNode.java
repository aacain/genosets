/*
 * 
 * 
 */
package edu.uncc.genosets.datanavigator;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.DeleteException;
import edu.uncc.genosets.datamanager.api.Deleter;
import edu.uncc.genosets.datamanager.api.DeleterFactory;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.actions.DeleteAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author aacain
 */
public class OrganismNode extends AbstractNode implements Deleter, PropertyChangeListener {

    private static List<Action> registeredActions;
    private Deleter organismDeleter;

    public OrganismNode(Organism org) {
        this(org, Children.create(new FactTableFactory(org), true), Lookups.singleton(org));
    }

    public OrganismNode(Organism org, Children children) {
        this(org, children, Lookups.singleton(org));
    }

    public OrganismNode(Organism org, Children children, Lookup lookup) {
        this(org, children, lookup, new InstanceContent());
    }

    private OrganismNode(Organism org, Children children, Lookup lookup, InstanceContent content) {
        super(children, new ProxyLookup(new AbstractLookup(content), Lookups.singleton(lookup)));
        content.add(this);
        content.add(org);
        this.setName(org.getOrganismId().toString());
        this.setDisplayName(org.getStrain() + " (Project: " + org.getProjectId() + ")");
        this.setIconBaseWithExtension("edu/uncc/genosets/datanavigator/resources/bacteria-icon.png");
        DataManager.getDefault().addPropertyChangeListener(WeakListeners.propertyChange(this, DataManager.getDefault()));
    }

    protected static List<? extends Action> getRegisterActions() {
        if (registeredActions == null) {
            registeredActions = new ArrayList<Action>();
            registeredActions.addAll(Utilities.actionsForPath("Nodes/Organism/Actions"));
            //registeredActions.add(new DeleteAction1());
        }
        return registeredActions;
    }

    @Override
    public Action[] getActions(boolean context) {
        Organism organism = getLookup().lookup(Organism.class);
        List<Action> actions = new ArrayList<Action>();
        actions.addAll(getRegisterActions());
        actions.add(DeleteAction.get(DeleteAction.class));
        actions.addAll(Arrays.asList(super.getActions(context)));
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void delete() throws DeleteException {
        if (organismDeleter == null) {
            organismDeleter = DeleterFactory.organismDeleter(getLookup().lookup(Organism.class));
        }
        organismDeleter.delete();
    }

    @Override
    public boolean canDelete() {
        if (organismDeleter == null) {
            organismDeleter = DeleterFactory.organismDeleter(getLookup().lookup(Organism.class));
        }
        return organismDeleter.canDelete();
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public boolean canRename() {
        return true;
    }

    @Override
    public void destroy() throws IOException {
        if (canDelete()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        organismDeleter.delete();
                    } catch (DeleteException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            };
            super.destroy();
            ProgressHandle handle = ProgressHandleFactory.createHandle("Deleting organism...");
            ProgressUtils.showProgressDialogAndRun(runnable, handle, true);
        } else {
            NotifyDescriptor d = new NotifyDescriptor.Message("Unable to delete organism because of dependancy on downstream analysis.", NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(d);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DataManager.PROP_DB_CHANGED) || evt.getPropertyName().equals(DataManager.PROP_ORGANISM_ADD)) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setChildren(Children.create(new FactTableFactory(getLookup().lookup(Organism.class)), true));
                }
            });

        }
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set setProps = Sheet.createPropertiesSet();
        setProps.setDisplayName("Properties");
        sheet.put(setProps);

        Property<String> orgId = new PropertySupport.ReadOnly<String>("id", String.class, "ID", "db id") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                return org.getOrganismId().toString();
            }
        };
        setProps.put(orgId);

        Property shortName = new PropertySupport.ReadWrite<String>("shortName", String.class, "Short Name", "Short Name") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                return org.getShortName() == null ? "" : org.getShortName();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getShortName() == null ? "" : org.getShortName();
                if (current.equals(val)) {
                    return;
                }
                org.setShortName(val);
                DataManager.getDefault().save(org);
            }
        };
        setProps.put(shortName);

        Property strain = new PropertySupport.ReadWrite<String>("strain", String.class, "Strain", "Strain") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                return org.getStrain();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getStrain() == null ? "" : org.getStrain();
                if (current.equals(val)) {
                    return;
                }
                org.setStrain(val);
                DataManager.getDefault().save(org);
            }
        };
        setProps.put(strain);

        Property species = new PropertySupport.ReadWrite<String>("species", String.class, "Species", "Species") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                return org.getSpecies();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getSpecies() == null ? "" : org.getSpecies();
                if (current.equals(val)) {
                    return;
                }
                org.setSpecies(val);
                DataManager.getDefault().save(org);
            }
        };
        setProps.put(species);

        Property genus = new PropertySupport.ReadWrite<String>("genus", String.class, "Genus", "Genus") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                return org.getGenus();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getGenus() == null ? "" : org.getGenus();
                if (current.equals(val)) {
                    return;
                }
                org.setGenus(val);
                DataManager.getDefault().save(org);
            }
        };
        setProps.put(genus);

        Property family = new PropertySupport.ReadWrite<String>("family", String.class, "Family", "Family") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                return org.getFamily();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getFamily() == null ? "" : org.getFamily();
                if (current.equals(val)) {
                    return;
                }
                org.setFamily(val);
                DataManager.getDefault().save(org);
            }
        };
        setProps.put(family);

        Property taxOrder = new PropertySupport.ReadWrite<String>("order", String.class, "Order", "Order") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                return org.getTaxOrder();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getTaxOrder() == null ? "" : org.getTaxOrder();
                if (current.equals(val)) {
                    return;
                }
                org.setTaxOrder(val);
                DataManager.getDefault().save(org);
            }
        };
        setProps.put(taxOrder);

        Property taxClass = new PropertySupport.ReadWrite<String>("class", String.class, "Class", "Class") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                return org.getTaxClass();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getTaxClass() == null ? "" : org.getTaxClass();
                if (current.equals(val)) {
                    return;
                }
                org.setTaxClass(val);
                DataManager.getDefault().save(org);
            }
        };
        setProps.put(taxClass);

        Property phylum = new PropertySupport.ReadWrite<String>("phylum", String.class, "Phylum", "Phylum") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                return org.getPhylum();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getPhylum() == null ? "" : org.getPhylum();
                if (current.equals(val)) {
                    return;
                }
                org.setPhylum(val);
                DataManager.getDefault().save(org);
            }
        };
        setProps.put(phylum);

        Property kingdom = new PropertySupport.ReadWrite<String>("kingdom", String.class, "Kingdom", "Kingdom") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                return org.getKingdom();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getKingdom() == null ? "" : org.getKingdom();
                if (current.equals(val)) {
                    return;
                }
                org.setKingdom(val);
                DataManager.getDefault().save(org);
            }
        };
        setProps.put(kingdom);

        Property taxId = new PropertySupport.ReadWrite<Integer>("taxId", Integer.class, "Taxonomy ID", "Taxonomy ID") {
            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                return org.getTaxonomyIdentifier();
            }

            @Override
            public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                if (org.getTaxonomyIdentifier() != null && org.getTaxonomyIdentifier().equals(val)) {
                    return;
                }
                org.setTaxonomyIdentifier(val);
                DataManager.getDefault().save(org);
            }
        };
        setProps.put(taxId);

        Property projectId = new PropertySupport.ReadWrite<String>("projectId", String.class, "Project ID", "Project ID") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                return org.getProjectId();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                if (org.getProjectId() != null & org.getProjectId().equals(val)) {
                    return;
                }
                org.setProjectId(val);
                DataManager.getDefault().save(org);
            }
        };
        setProps.put(projectId);


        return sheet;
    }
}
