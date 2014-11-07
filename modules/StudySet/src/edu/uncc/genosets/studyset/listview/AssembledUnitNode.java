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
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.propertieseditor.PropertiesEditable;
import edu.uncc.genosets.propertieseditor.ProxyProperty;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.actions.FocusChangedAction;
import static edu.uncc.genosets.studyset.listview.FeatureNode.getRegisterActions;
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
import org.springframework.util.ConcurrentReferenceHashMap;

/**
 *
 * @author aacain
 */
public class AssembledUnitNode extends AbstractNode implements PropertiesEditable, ListViewProperties, GroupHierarchy.HierarchyGroupChangeListener, Savable {

    private static final ConcurrentReferenceHashMap<Integer, Organism> orgs = new ConcurrentReferenceHashMap<Integer, Organism>();
    private final String[] props = new String[]{
        "id", "ID",
        "organism", "Organism",
        "accessionVersion", "Accession Version",
        "sequenceLength", "Sequence Length",
        "assembledUnitType", "Assembled Unit Type",
        "repUnitName", "Replicating Unit Name",
        "repUnitType", "Replicating Unit Type"
    };
    private static List<? extends Action> registeredActions;
    private final GroupHierarchy groupHierarchy;
    private final int level;
    private Sheet.Set editableProperties;

    /**
     * Creates an AssembledUnitNode for the given assembledUnit. The organism
     * can be null. If this is the case, the database will be queried for the
     * organism.
     *
     * @param studySet
     * @param organism - can be null
     * @param assUnit
     * @param hierarchy
     * @param level
     */
    public AssembledUnitNode(StudySet studySet, Organism organism, AssembledUnit assUnit, GroupHierarchy hierarchy, int level) {
        this(studySet, organism, assUnit, hierarchy, level, createLookup(studySet, organism, assUnit));
    }

    /**
     * Creates an AssembledUnitNode for the given AssembledUnit. The lookup must
     * contain the assembledUnit and the studySet. The organism can be null.
     *
     * @param studySet
     * @param organism - can be null
     * @param assUnit
     * @param hierarchy
     * @param level
     * @param lookup - must contain studyset and assembledUnit
     */
    public AssembledUnitNode(StudySet studySet, Organism organism, AssembledUnit assUnit, GroupHierarchy hierarchy, int level, Lookup lookup) {
        super(hierarchy.getChildren(level, studySet, null, null, assUnit, null, null), lookup);
        this.setName(assUnit.getAssembledUnitName());
        this.groupHierarchy = hierarchy;
        this.level = level;
        this.groupHierarchy.addHierarchyGroupChangeListener(WeakListeners.create(GroupHierarchy.HierarchyGroupChangeListener.class, this, this.groupHierarchy));
    }

    private static Lookup createLookup(StudySet studySet, Organism organism, AssembledUnit assUnit) {
        if (organism == null) {
            return Lookups.fixed(studySet, assUnit);
        } else {
            return Lookups.fixed(studySet, assUnit, organism);
        }
    }

    protected static List<? extends Action> getRegisterActions() {
        if (registeredActions == null) {
            List<Action> actions = new ArrayList();
            actions.addAll(Utilities.actionsForPath("Actions/Nodes/AssembledUnit"));
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
    public String[] getProperties() {
        return props;
    }

    @Override
    protected Sheet createSheet() {
        final AssembledUnit a = getLookup().lookup(AssembledUnit.class);
        Organism tempOrg = getLookup().lookup(Organism.class);
        if (tempOrg == null) {
            tempOrg = orgs.get(a.getOrganismId());
            if (tempOrg == null) {
                tempOrg = (Organism) DataManager.getDefault().get("Organism", a.getOrganismId());
            }
        }
        orgs.putIfAbsent(tempOrg.getOrganismId(), tempOrg);
        final Organism org = tempOrg;
        Sheet sheet = Sheet.createDefault();
        
        if(editableProperties == null){
            editableProperties = Sheet.createPropertiesSet();
        }

        Sheet.Set setProps = Sheet.createPropertiesSet();
        setProps.setDisplayName("general");
        sheet.put(setProps);
        Property<Integer> idProp = new PropertySupport.ReadOnly<Integer>("id", Integer.class, "ID", "identification") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }
            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                return a.getId();
            }
        };
        setProps.put(idProp);
        editableProperties.put(idProp);
        
        Property orgProp = new PropertySupport.ReadOnly<String>("organism", String.class, "Organism", "The organism strain") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                if (org.getStrain() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + org.getStrain() + "</font>");
                }
                return org.getStrain();
            }
        };
        setProps.put(orgProp);
        editableProperties.put(orgProp);

        Property accessionVersion = new PropertySupport.ReadWrite<String>("accessionVersion", String.class, "Accession Version", "The accession version.") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }
            
            @Override
            public boolean canWrite() {
                return false;
            }
            
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                if (a.getAccessionVersion() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + a.getAccessionVersion() + "</font>");
                }
                return a.getAccessionVersion();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }
                if (!val.equals(a.getAccessionVersion())) {
                    a.setAccessionVersion(val);
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + a.getAccessionVersion() + "</font>");
                    firePropertyChange("accessionVersion", null, val);
                }
            }
        };
        setProps.put(accessionVersion);
        editableProperties.put(new ProxyProperty<String>(this, accessionVersion, true));
        
        Property sequenceLength = new PropertySupport.ReadOnly<Integer>("sequenceLength", Integer.class, "Sequence Length", "The length of the sequence") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }
            @Override
            public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                if (a.getSequenceLength() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + a.getSequenceLength()+ "</font>");
                }
                return a.getSequenceLength();
            }
        };
        setProps.put(sequenceLength);
        editableProperties.put(sequenceLength);
        
        Property assembledUnitType = new PropertySupport.ReadWrite<String>("assembledUnitType", String.class, "Assembled Unit Type", "The assembled unit type.") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }
            
            @Override
            public boolean canWrite() {
                return false;
            }
            
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                if (a.getAccessionVersion() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + a.getAssembledUnitType() + "</font>");
                }
                return a.getAssembledUnitType();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (!val.equals(a.getAssembledUnitType())) {
                    a.setAssembledUnitType(val);
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + a.getAssembledUnitType() + "</font>");
                    firePropertyChange("assembledUnitType", null, val);
                }
            }
        };
        setProps.put(assembledUnitType);
        editableProperties.put(new ProxyProperty<String>(this, assembledUnitType, true));
        
        Property repUnitName = new PropertySupport.ReadWrite<String>("repUnitName", String.class, "Replicating Unit Name", "The name of the replicating unit (chromosome I, .") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }
            
            @Override
            public boolean canWrite() {
                return false;
            }
            
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                if (a.getAccessionVersion() != null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + a.getRepUnitName()+ "</font>");
                }
                return a.getRepUnitName();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (!val.equals(a.getRepUnitName())) {
                    a.setRepUnitName(val);
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + a.getRepUnitName()+ "</font>");
                    firePropertyChange("repUnitName", null, val);
                }
            }
        };
        setProps.put(repUnitName);
        editableProperties.put(new ProxyProperty<String>(this, repUnitName, true));
        
        Property repUnitType = new PropertySupport.ReadWrite<String>("repUnitType", String.class, "Replicating Unit Type", "This is the replicating unit type (chromosome, plasmid, etc).") {
            {
                setValue("suppressCustomEditor", Boolean.TRUE);
            }
            
            @Override
            public boolean canWrite() {
                return false;
            }
            
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                if (a.getReplicatingUnitType()!= null) {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + a.getReplicatingUnitType()+ "</font>");
                }
                return a.getReplicatingUnitType();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if (!val.equals(a.getReplicatingUnitType())) {
                    a.setReplicatingUnitType(val);
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + a.getReplicatingUnitType()+ "</font>");
                    firePropertyChange("repUnitType", null, val);
                }
            }
        };
        setProps.put(repUnitType);
        editableProperties.put(new ProxyProperty<String>(this, repUnitType, true));
        
        return sheet;
    }

    @Override
    public void groupAdded(GroupHierarchy.HierarchyGroupChangeEvent evt) {
        if (evt.getLevel() == this.level) {
            this.setChildren(Children.LEAF);
            this.setChildren(groupHierarchy.getChildren(this.level, getLookup().lookup(StudySet.class), null, null, null, getLookup().lookup(Feature.class), null));
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
    public PropertySet[] getEditablePropertySets() {
        if (editableProperties == null) {
            createSheet();
        }
        return new PropertySet[]{editableProperties};
    }

    @Override
    public void save() throws IOException {
        AssembledUnit assUnit = getLookup().lookup(AssembledUnit.class);
        DataManager.getDefault().save(assUnit);
    }
}
