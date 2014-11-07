/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.studyset.navigator;

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.studyset.StudySet;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.*;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

/**
 *
 * @author aacain
 */
public class StudySetNode extends AbstractNode implements PropertyChangeListener {

    private static Map<FocusEntity, List<? extends Action>> registeredActions;
    private static List<? extends Action> modifyingAction;

    public StudySetNode(StudySet set) {
        super(Children.LEAF, set.getLookup());
        setName(set.getUniqueName());
        setDisplayName(set.getName() + " (" + set.getIdSet().size() + ")");
        if (set.getFocusEntity().getEntityName().equals("Feature")) {
            setIconBaseWithExtension("edu/uncc/genosets/studyset/resources/dna-icon.png");
        }else if (set.getFocusEntity().getEntityName().equals("Organism")) {
            setIconBaseWithExtension("edu/uncc/genosets/studyset/resources/bacteria-icon.png");
        }else {
            setIconBaseWithExtension("edu/uncc/genosets/studyset/resources/method.png");
        }
        set.addPropertyChangeListener(WeakListeners.propertyChange(this, set));
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    protected static List<? extends Action> getRegisterActions(FocusEntity focus) {
        if (registeredActions == null) {
            modifyingAction = Collections.singletonList(new ModifyingStudySetAction());
            List<? extends Action> allActions = Utilities.actionsForPath("StudySet/Nodes/Actions");
            registeredActions = new HashMap();
            for (FocusEntity focusEntity : FocusEntity.getEntities()) {
                List<Action> actionsForPath = new ArrayList(Utilities.actionsForPath("StudySet/Nodes/" + focusEntity.getEntityName()));
                registeredActions.put(focusEntity, actionsForPath);
                actionsForPath.addAll(allActions);
            }
        }
        return registeredActions.get(focus);
    }

    @Override
    public Action[] getActions(boolean context) {
        final StudySet set = getLookup().lookup(StudySet.class);
        List<Action> actions = new ArrayList<Action>();
        getRegisterActions(set.getFocusEntity());
        if (set.isBlocked()) {
            actions.addAll(modifyingAction);
        } else {
            actions.addAll(getRegisterActions(set.getFocusEntity()));
            actions.addAll(Arrays.asList(super.getActions(context)));
            actions.addAll(getLookup().lookupAll(Action.class));
        }

        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        StudySet set = getLookup().lookup(StudySet.class);
        setDisplayName(set.getName() + " (" + set.getIdSet().size() + ")");
    }

    @Override
    protected Sheet createSheet() {
        final StudySet set = getLookup().lookup(StudySet.class);
        Sheet sheet = Sheet.createDefault();
        Sheet.Set setProps = sheet.createPropertiesSet();
        setProps.setDisplayName("general");
        sheet.put(setProps);

        Node.Property<String> idProp = new PropertySupport.ReadOnly<String>(
                "id", String.class, "ID", "identification") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return (null != set) ? set.getUniqueName() : "";
            }
        };

        Node.Property<String> nameProp = new PropertySupport.ReadWrite<String>("name", String.class, "name", "study set name") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return (null != set) ? set.getName() : "";
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if ((null != set) && (val instanceof String)) {
                    set.setName(val);
                }
            }
        };

        Node.Property<String> descProp = new PropertySupport.ReadWrite<String>("description", String.class, "description", "description") {
            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return (null != set) ? set.getDescription() : "";
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                if ((null != set) && (val instanceof String)) {
                    set.setDescription(val);
                }
            }
        };

//        Node.Property<String> treeProp = new PropertySupport.ReadOnly<String>(
//                "conditions", String.class, "conditions", "conditions") {
//
//            @Override
//            public String getValue() throws IllegalAccessException, InvocationTargetException {
//
//                return (null != set && set.getConditionTree() != null) ? set.getConditionTree().toString() : "";
//            }
//        };

        setProps.put(idProp);
        setProps.put(nameProp);
        setProps.put(descProp);


        return sheet;
    }
   
    @ActionID(id = "edu.uncc.genosets.studyset.ModifyingStudySetAction", category = "Tools")
    @ActionRegistration(iconInMenu = true, displayName = "#CTL_ModifyingStudySetAction")
    @NbBundle.Messages("CTL_ModifyingStudySetAction=Modifying StudySet...")
    public static final class ModifyingStudySetAction extends AbstractAction implements ContextAwareAction {

        public ModifyingStudySetAction() {
            this(Utilities.actionsGlobalContext());
        }

        public ModifyingStudySetAction(Lookup context) {
            super(Bundle.CTL_ModifyingStudySetAction());

        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
        }

        @Override
        public Action createContextAwareInstance(Lookup actionContext) {
            return new ModifyingStudySetAction(actionContext);
        }
    }
}
