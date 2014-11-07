/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.taskmanager.view;

import edu.uncc.genosets.taskmanager.TaskLog;
import edu.uncc.genosets.taskmanager.TaskLog.Message;
import edu.uncc.genosets.taskmanager.TaskLogFactory;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Date;
import javax.swing.Action;
import org.openide.actions.DeleteAction;
import org.openide.nodes.*;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;
import org.openide.windows.WindowManager;

/**
 *
 * @author aacain
 */
public class MessageChildren extends Children.Keys<Message> implements PropertyChangeListener {

    public MessageChildren() {
        TaskLogFactory.getDefault().addPropertyChangeListener(WeakListeners.create(PropertyChangeListener.class, this, TaskLogFactory.getDefault()));
    }

    @Override
    protected Node[] createNodes(Message key) {
        return new Node[]{new MessageNode(Children.LEAF, Lookups.singleton(key))};
    }

    @Override
    protected void addNotify() {
        setKeys(TaskLogFactory.getDefault().getSnapShot());
    }

    @Override
    protected void removeNotify() {
        setKeys(Collections.EMPTY_SET);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            @Override
            public void run() {
                setKeys(TaskLogFactory.getDefault().getSnapShot());
            }
        });
    }

    public static class MessageNode extends AbstractNode {

        public MessageNode(Children children, Lookup lookup) {
            super(children, lookup);
            Message message = lookup.lookup(Message.class);
            setDisplayName(message.getDesc());
            if (message.getLevel().equals(TaskLog.INFO)) {
                setIconBaseWithExtension("edu/uncc/genosets/taskmanager/resources/blue_dot.png");
            } else if (message.getLevel().equals(TaskLog.WARNING)) {
                setIconBaseWithExtension("edu/uncc/genosets/taskmanager/resources/yellow_dot.png");
            } else if (message.getLevel().equals(TaskLog.ERROR)) {
                setIconBaseWithExtension("edu/uncc/genosets/taskmanager/resources/red_dot.png");
            }
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[]{DeleteAction.get(DeleteAction.class)};
        }

        @Override
        public boolean canDestroy() {
            return Boolean.TRUE;
        }

        @Override
        public void destroy() throws IOException {
            Message m = getLookup().lookup(Message.class);
            TaskLogFactory.getDefault().remove(m);
        }

        @Override
        protected Sheet createSheet() {
            final Message m = getLookup().lookup(Message.class);
            Sheet sheet = Sheet.createDefault();
            Sheet.Set setProps = Sheet.createPropertiesSet();
            setProps.setDisplayName("general");
            sheet.put(setProps);



            Node.Property<String> typeProp = new PropertySupport.ReadOnly<String>(
                    "Type", String.class, "Type", "Type") {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return (null != m) ? m.getLevel() : "";
                }
            };
            setProps.put(typeProp);
            typeProp.setValue("suppressCustomEditor", Boolean.TRUE);
            typeProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != m) ? m.getLevel(): "") + "</font>");

//            Node.Property<String> descProp = new PropertySupport.ReadOnly<String>(
//                    "Message", String.class, "Message", "Message") {
//                @Override
//                public String getValue() throws IllegalAccessException, InvocationTargetException {
//                    return (null != m) ? m.getDesc(): "";
//                }
//            };
//            setProps.put(descProp);
//            descProp.setValue("suppressCustomEditor", Boolean.TRUE);
//            descProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != m) ? m.getDesc(): "") + "</font>");

            Node.Property<String> detailsProp = new PropertySupport.ReadOnly<String>(
                    "Details", String.class, "Details", "Details") {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return (null != m) ? m.getDetails() : "";
                }
            };
            setProps.put(detailsProp);
            detailsProp.setValue("suppressCustomEditor", Boolean.TRUE);
            detailsProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != m) ? m.getDetails() : "") + "</font>");

            Node.Property<String> sourceProp = new PropertySupport.ReadOnly<String>(
                    "Source", String.class, "Source", "Source") {
                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return (null != m) ? m.getSource() : "";
                }
            };
            setProps.put(sourceProp);
            sourceProp.setValue("suppressCustomEditor", Boolean.TRUE);
            sourceProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != m) ? m.getSource() : "") + "</font>");

            Node.Property<Date> dateProp = new PropertySupport.ReadOnly<Date>(
                    "Date", Date.class, "Date", "Date") {
                @Override
                public Date getValue() throws IllegalAccessException, InvocationTargetException {
                    return m.getDate();
                }
            };
            setProps.put(dateProp);
            dateProp.setValue("suppressCustomEditor", Boolean.TRUE);

            return sheet;
        }

        public static String[] getProperties() {
            return new String[]{
                "Type", "Type",
                "Details", "Details",
                "Date", "Date",
                "Source", "Source"};
        }
    }
}
