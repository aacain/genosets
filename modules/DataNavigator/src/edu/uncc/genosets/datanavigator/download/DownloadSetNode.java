/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.download;

import edu.uncc.genosets.datamanager.api.AnnotationFactType;
import edu.uncc.genosets.datamanager.api.DownloadSet;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datanavigator.FactFlavor;
import edu.uncc.genosets.datanavigator.actions.DownloadAction;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.openide.explorer.view.CheckableNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class DownloadSetNode extends AbstractNode {

    private static List<? extends Action> registeredActions;
    private final DownloadSet ds;
    private final DownloadSetList model;

    public DownloadSetNode(DownloadSetList model, DownloadSet ds) {
        super(Children.create(new DownloadCategoryFactory(ds), true), Lookups.fixed(ds, new CheckableNode() {

            @Override
            public boolean isCheckable() {
                return true;
            }

            @Override
            public boolean isCheckEnabled() {
                return true;
            }

            @Override
            public Boolean isSelected() {
                return true;
            }

            @Override
            public void setSelected(Boolean arg0) {
            }
        }));
        this.ds = ds;
        this.model = model;
        setDisplayName(ds.getName());
        if (AnnotationFactType.ANNO_FACT_TYPE.equals(ds.getFactType())) {
            setIconBaseWithExtension("edu/uncc/genosets/datanavigator/resources/download_blue.png");
        } else {
            setIconBaseWithExtension("edu/uncc/genosets/datanavigator/resources/download.png");
        }
    }

    @Override
    public boolean canDestroy() {
        return Boolean.TRUE;
    }

    @Override
    public void destroy() throws IOException {
        model.remove(ds);
        super.destroy();
    }

    protected static List<? extends Action> getRegisterActions() {
        if (registeredActions == null) {
            registeredActions = Utilities.actionsForPath("DownloadSet/Nodes/Actions");
        }
        return registeredActions;
    }

    @Override
    public Action[] getActions(boolean context) {
        ArrayList<Action> actions = new ArrayList<Action>();
        actions.addAll(getRegisterActions());
        actions.add(new DownloadAction(ds));
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public PasteType getDropType(final Transferable t, int action, int index) {
        List<? extends FactFlavor> dsFlavors = FactFlavorLookup.getFlavors(ds.getFactType());
        if (dsFlavors != null) {
            for (final FactFlavor factFlavor : dsFlavors) {
                if (t.isDataFlavorSupported(factFlavor)) {
                    return new PasteType() {

                        @Override
                        public Transferable paste() throws IOException {
                            try {
                                ds.addMethod((AnnotationMethod) t.getTransferData(factFlavor));
                            } catch (UnsupportedFlavorException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                            return null;
                        }
                    };
                }
            }
        }
        return null;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set setProps = sheet.createPropertiesSet();
        setProps.setDisplayName("Properties");
        sheet.put(setProps);
        final DownloadSet downSet = getLookup().lookup(DownloadSet.class);

        Property<String> nameProp = new PropertySupport.ReadOnly<String>("name", String.class, "Name", "name of download set") {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {
                return downSet.getName();
            }
        };
        setProps.put(nameProp);

        Property<String> location = new PropertySupport.ReadOnly<String>("location", String.class, "Location", "Download location path") {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {

                return downSet.getRootFileObject().getPath();
            }
        };
        setProps.put(location);

        Property<String> factType = new PropertySupport.ReadOnly<String>("factType", String.class, "Type", "Fact Type") {

            @Override
            public String getValue() throws IllegalAccessException, InvocationTargetException {

                return downSet.getFactType().getName();
            }
        };
        setProps.put(factType);

        return sheet;
    }
}
