/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datanavigator.actions.DownloadAction;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Utilities;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author lucy
 */
public class AnnotationMethodNode extends AbstractNode {

    private static List<? extends Action> registeredActions;
    private static HashMap<String, List<? extends Action>> factTypeActionMap = new HashMap<String, List<? extends Action>>();
    private final AnnotationMethod method;
    private final Fact fact;

    public AnnotationMethodNode(Fact fact, AnnotationMethod method) {
        super(Children.LEAF, Lookups.fixed(fact, method));
        this.method = method;
        this.fact = fact;
        this.setDisplayName(method.getMethodName());
        this.setIconBaseWithExtension("edu/uncc/genosets/datanavigator/resources/method.png");
    }

    protected static List<? extends Action> getRegisterActions() {
        if (registeredActions == null) {
            registeredActions = Utilities.actionsForPath("AnnotationMethod/Nodes/Actions");
        }
        return registeredActions;
    }

    protected static List<? extends Action> getFactTypeActions(Fact fact) {
        List<? extends Action> actions = factTypeActionMap.get(fact.getFactType().getName());
        if (actions == null) {
            actions = Utilities.actionsForPath("AnnotationMethod/Nodes/" + fact.getFactType().getName() + "/Actions");
            factTypeActionMap.put(fact.getFactType().getName(), actions);
        }
        return actions;
    }

    @Override
    public Action[] getActions(boolean context) {
        ArrayList<Action> actions = new ArrayList<Action>();
        actions.addAll(getRegisterActions());
        actions.addAll(getFactTypeActions(fact));
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public boolean canCopy() {
        return true;
    }

    @Override
    public Transferable drag() throws IOException {
        Transferable deflt = clipboardCopy();
        ExTransferable added = ExTransferable.create(deflt);
        added.put(new ExTransferable.Single(fact.getFlavor()) {

            @Override
            protected AnnotationMethod getData() {
                return getLookup().lookup(AnnotationMethod.class);
            }
        });
        return added;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set setProps = Sheet.createPropertiesSet();
        setProps.setDisplayName("Properties");
        sheet.put(setProps);

        Property<String> methodId = new PropertySupport.ReadOnly<String>("id", String.class, "ID", "db id") {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                AnnotationMethod method = getLookup().lookup(AnnotationMethod.class);
                return method.getAnnotationMethodId().toString();
            }
        };
        setProps.put(methodId);
        methodId.setValue("suppressCustomEditor", Boolean.TRUE);

        Property methodCategory = new PropertySupport.ReadOnly<String>("methodCategory", String.class, "Category", "Category") {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return getLookup().lookup(AnnotationMethod.class).getMethodCategory();
            }
        };
        setProps.put(methodCategory);
        methodCategory.setValue("suppressCustomEditor", Boolean.TRUE);

        Property methodType = new PropertySupport.ReadOnly<String>("methodType", String.class, "Type", "Type") {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return getLookup().lookup(AnnotationMethod.class).getMethodType();
            }
        };
        setProps.put(methodType);
        methodType.setValue("suppressCustomEditor", Boolean.TRUE);

        Property methodSource = new PropertySupport.ReadOnly<String>("methodSource", String.class, "Source", "Source") {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return getLookup().lookup(AnnotationMethod.class).getMethodSourceType();
            }
        };
        setProps.put(methodSource);
        methodSource.setValue("suppressCustomEditor", Boolean.TRUE);


        Property methodDate = new PropertySupport.ReadOnly<Date>("loadDate", Date.class, "Date", "Date") {

            @Override
            public Date getValue() throws IllegalAccessException, InvocationTargetException {
                return getLookup().lookup(AnnotationMethod.class).getLoadDate();
            }
        };
        setProps.put(methodDate);
        methodDate.setValue("suppressCustomEditor", Boolean.TRUE);

        Property methodName = new PropertySupport.ReadWrite<String>("methodName", String.class, "Name", "Name") {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                AnnotationMethod method = getLookup().lookup(AnnotationMethod.class);
                return method.getMethodName();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                AnnotationMethod method = getLookup().lookup(AnnotationMethod.class);
                if (method.getMethodName().equals(val)) {
                    return;
                }
                method.setMethodName(val);
                DataManager.getDefault().save(method);
            }
        };
        setProps.put(methodName);

        Property methodDesc = new PropertySupport.ReadWrite<String>("methodDesc", String.class, "Description", "Description") {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                AnnotationMethod method = getLookup().lookup(AnnotationMethod.class);
                return method.getMethodDescription();
            }

            @Override
            public void setValue(String val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                AnnotationMethod method = getLookup().lookup(AnnotationMethod.class);
                if (method.getMethodDescription().equals(val)) {
                    return;
                }
                method.setMethodDescription(val);
                DataManager.getDefault().save(method);
            }
        };
        setProps.put(methodDesc);

        return sheet;
    }
    ////////////////////////////Actions
}
