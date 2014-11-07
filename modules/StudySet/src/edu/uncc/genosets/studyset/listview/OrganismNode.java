/*
 * Copyright (C) 2013 Aurora Cain
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
package edu.uncc.genosets.studyset.listview;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.studyset.actions.FocusChangedAction;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.propertieseditor.PropertiesEditable;
import edu.uncc.genosets.propertieseditor.ProxyProperty;
import edu.uncc.genosets.studyset.StudySet;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.actions.Savable;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class OrganismNode extends AbstractNode implements PropertiesEditable, ListViewProperties, GroupHierarchy.HierarchyGroupChangeListener, Savable {

    private Sheet.Set editableProperties;
    private static List<? extends Action> registeredActions;
    private final static String[] props = new String[]{"id", "ID",
        "shortName", "Short Name",
        "strain", "Strain",
        "species", "Species",
        "genus", "Genus",
        "family", "Family",
        "order", "Order",
        "class", "Class",
        "phylum", "Phylum",
        "kingdom", "Kingdom",
        "projectId", "projectId"
    };
    private final GroupHierarchy groupHierarchy;
    private final int level;

    public OrganismNode(Organism organism, StudySet studySet, GroupHierarchy hierarchy, int level) {
        super(hierarchy.getChildren(level, studySet, null, organism, null, null, null), Lookups.fixed(organism, studySet, hierarchy));
        this.setIconBaseWithExtension("edu/uncc/genosets/studyset/resources/bacteria-icon.png");
        this.setName(organism.getStrain());
        this.setDisplayName(organism.getStrain());
        this.groupHierarchy = hierarchy;
        this.level = level;
        this.groupHierarchy.addHierarchyGroupChangeListener(WeakListeners.create(GroupHierarchy.HierarchyGroupChangeListener.class, this, this.groupHierarchy));
    }

    private OrganismNode(Organism organism, Lookup lookup, Children children, GroupHierarchy hierarchy, int level) {
        super(children, lookup);
        this.setIconBaseWithExtension("edu/uncc/genosets/studyset/resources/bacteria-icon.png");
        this.setName(organism.getStrain());
        this.setDisplayName(organism.getStrain());
        this.groupHierarchy = hierarchy;
        this.level = level;
        this.groupHierarchy.addHierarchyGroupChangeListener(WeakListeners.create(GroupHierarchy.HierarchyGroupChangeListener.class, this, this.groupHierarchy));
    }

    protected static List<? extends Action> getRegisterActions() {
        if (registeredActions == null) {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/Nodes/Organism"));
            actions.addAll(Utilities.actionsForPath("Actions/Nodes/All"));
            registeredActions = actions;
        }
        return registeredActions;
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<Action>();
        actions.addAll(getRegisterActions());
        actions.addAll(Arrays.asList(super.getActions(context)));
        FocusChangeListener tc = Utilities.actionsGlobalContext().lookup(FocusChangeListener.class);
        if (tc != null) {
            actions.add(new FocusChangedAction(tc, this));
        }
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set setProps = Sheet.createPropertiesSet();
        setProps.setDisplayName("Properties");
        sheet.put(setProps);
        if (editableProperties == null) {
            editableProperties = Sheet.createPropertiesSet();
        }
        Property<String> orgId = new PropertySupport.ReadOnly<String>("id", String.class, "ID", "db id") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                return org.getOrganismId().toString();
            }
        };
        setProps.put(orgId);
        editableProperties.put(orgId);

        Property shortName = new PropertySupport.ReadWrite<String>("shortName", String.class, "Short Name", "Short Name") {
            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                if (org.getShortName() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getShortName() == null ? "" : org.getShortName()) + "</font>");
                }
                return org.getShortName() == null ? "" : org.getShortName();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getShortName();
                if (val.equals(current)) {
                    return;
                }
                org.setShortName(val);
                this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getShortName() == null ? "" : org.getShortName()) + "</font>");
                firePropertyChange("shortName", null, val);
            }
        };
        setProps.put(shortName);
        editableProperties.put(new ProxyProperty<String>(this, shortName, true));

        Property strain = new PropertySupport.ReadWrite<String>("strain", String.class, "Strain", "Strain") {
            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getStrain() == null ? "null" : org.getStrain()) + "</font>");
                return org.getStrain();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getStrain();
                if (val.equals(current)) {
                    return;
                }
                org.setStrain(val);
                this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getStrain() == null ? "null" : org.getStrain()) + "</font>");
                firePropertyChange("strain", null, val);
            }
        };
        setProps.put(strain);
        editableProperties.put(new ProxyProperty<String>(this, strain, true));

        Property species = new PropertySupport.ReadWrite<String>("species", String.class, "Species", "Species") {
            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                if (org.getSpecies() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getSpecies() == null ? "null" : org.getSpecies()) + "</font>");
                }
                return org.getSpecies();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getSpecies(); 
                if (val.equals(current)) {
                    return;
                }
                org.setSpecies(val);
                this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getSpecies() == null ? "null" : org.getSpecies()) + "</font>");
                firePropertyChange("species", null, val);
            }
        };
        setProps.put(species);
        editableProperties.put(new ProxyProperty<String>(this, species, true));

        Property genus = new PropertySupport.ReadWrite<String>("genus", String.class, "Genus", "Genus") {
            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                if (org.getGenus() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getGenus() == null ? "null" : org.getGenus()) + "</font>");
                }
                return org.getGenus();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getGenus();
                if (val.equals(current)) {
                    return;
                }
                org.setGenus(val);
                this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getGenus() == null ? "null" : org.getGenus()) + "</font>");
                firePropertyChange("genus", null, val);
            }
        };
        setProps.put(genus);
        editableProperties.put(new ProxyProperty<String>(this, genus, true));

        Property family = new PropertySupport.ReadWrite<String>("family", String.class, "Family", "Family") {
            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                if (org.getFamily() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getFamily() == null ? "null" : org.getFamily()) + "</font>");
                }
                return org.getFamily();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getFamily(); 
                if (val.equals(current)) {
                    return;
                }
                org.setFamily(val);
                this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getFamily() == null ? "null" : org.getFamily()) + "</font>");
                firePropertyChange("family", null, val);
            }
        };
        setProps.put(family);
        editableProperties.put(new ProxyProperty<String>(this, family, true));

        Property taxOrder = new PropertySupport.ReadWrite<String>("order", String.class, "Order", "Order") {
            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                if (org.getTaxOrder() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getTaxOrder() == null ? "null" : org.getTaxOrder()) + "</font>");
                }
                return org.getTaxOrder();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getTaxOrder();
                if (val.equals(current)) {
                    return;
                }
                org.setTaxOrder(val);
                this.setValue("htmlDisplayValue", "<font color='000000'>" + (val == null ? "null" : val) + "</font>");
                firePropertyChange("order", null, val);
            }
        };
        setProps.put(taxOrder);
        editableProperties.put(new ProxyProperty<String>(this, taxOrder, true));

        Property taxClass = new PropertySupport.ReadWrite<String>("class", String.class, "Class", "Class") {
            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                if (org.getTaxClass() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getTaxClass() == null ? "null" : org.getTaxClass()) + "</font>");
                }
                return org.getTaxClass();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getTaxClass();
                if (val.equals(current)) {
                    return;
                }
                org.setTaxClass(val);
                this.setValue("htmlDisplayValue", "<font color='000000'>" + (val == null ? "null" : val) + "</font>");
                firePropertyChange("class", null, val);
            }
        };
        setProps.put(taxClass);
        editableProperties.put(new ProxyProperty<String>(this, taxClass, true));

        Property phylum = new PropertySupport.ReadWrite<String>("phylum", String.class, "Phylum", "Phylum") {
            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                if (org.getPhylum() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getPhylum() == null ? "null" : org.getPhylum()) + "</font>");
                }
                return org.getPhylum();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getPhylum();
                if (val.equals(current)) {
                    return;
                }
                org.setPhylum(val);
                this.setValue("htmlDisplayValue", "<font color='000000'>" + (val == null ? "null" : val) + "</font>");
                firePropertyChange("phylum", null, val);
            }
        };
        setProps.put(phylum);
        editableProperties.put(new ProxyProperty<String>(this, phylum, true));

        Property kingdom = new PropertySupport.ReadWrite<String>("kingdom", String.class, "Kingdom", "Kingdom") {
            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                if (org.getKingdom() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getKingdom() == null ? "null" : org.getKingdom()) + "</font>");
                }
                return org.getKingdom();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getKingdom();
                if (val.equals(current)) {
                    return;
                }
                org.setKingdom(val);
                this.setValue("htmlDisplayValue", "<font color='000000'>" + (val == null ? "null" : val) + "</font>");
                firePropertyChange("kingdom", null, val);
            }
        };
        setProps.put(kingdom);
        editableProperties.put(new ProxyProperty<String>(this, kingdom, true));

        Property taxId = new PropertySupport.ReadWrite<Integer>("taxId", Integer.class, "Taxonomy ID", "Taxonomy ID") {
            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                if (org.getTaxonomyIdentifier() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getTaxonomyIdentifier() == null ? "" : org.getTaxonomyIdentifier().toString()) + "</font>");
                }
                return org.getTaxonomyIdentifier();
            }

            @Override
            public void setValue(Integer val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                Integer current = org.getTaxonomyIdentifier();
                if (val.equals(current)) {
                    return;
                }
                org.setTaxonomyIdentifier(val);
                this.setValue("htmlDisplayValue", "<font color='000000'>" + (val == null ? "null" : val) + "</font>");
                firePropertyChange("taxId", null, val);
            }
        };
        setProps.put(taxId);
        editableProperties.put(new ProxyProperty<Integer>(this, taxId, true));

        Property projectId = new PropertySupport.ReadWrite<String>("projectId", String.class, "Project ID", "Project ID") {
            @Override
            public boolean canWrite() {
                return false;
            }

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                if (org.getProjectId() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + (org.getProjectId() == null ? "null" : org.getProjectId()) + "</font>");
                }
                return org.getProjectId();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                Organism org = getLookup().lookup(Organism.class);
                String current = org.getProjectId();
                if (val.equals(current)) {
                    return;
                }
                org.setProjectId(val);
                this.setValue("htmlDisplayValue", "<font color='000000'>" + (val == null ? "null" : val) + "</font>");
                firePropertyChange("projectId", null, val);
            }
        };
        setProps.put(projectId);
        editableProperties.put(new ProxyProperty<String>(this, projectId, true));

        for (Property<?> property : setProps.getProperties()) {
            property.setValue("suppressCustomEditor", Boolean.TRUE);
        }
        return sheet;
    }

    public static String[] getNodeProperties() {
        return props;
    }

    @Override
    public PropertySet[] getEditablePropertySets() {
        if (editableProperties == null) {
            createSheet();
        }
        return new PropertySet[]{editableProperties};
    }

    @Override
    public String[] getProperties() {
        return getNodeProperties();
    }

    @Override
    public void groupAdded(GroupHierarchy.HierarchyGroupChangeEvent evt) {
        if (evt.getLevel() == this.level) {
            this.setChildren(groupHierarchy.getChildren(this.level, getLookup().lookup(StudySet.class), null, getLookup().lookup(Organism.class), null, null, null));
        }
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public GroupHierarchy getHierarchyGroup() {
        return this.groupHierarchy;
    }

    @Override
    public void save() throws IOException {
        DataManager.getDefault().save(getLookup().lookup(Organism.class));
    }
}
