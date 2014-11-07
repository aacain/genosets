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
package edu.uncc.genosets.propertieseditor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.actions.Savable;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.ProxyLookup;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//edu.uncc.genosets.propertieseditor//PropertiesSheetWindow//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "PropertiesSheetWindowTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "properties", openAtStartup = false)
@ActionID(category = "Window", id = "edu.uncc.genosets.propertieseditor.PropertiesSheetWindowTopComponent")
//@ActionReferences(value = {
//    @ActionReference(path = "Menu/Window", position = 720),
//    @ActionReference(path = "Actions/Nodes/All")
//})
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_PropertiesSheetWindowAction",
        preferredID = "PropertiesSheetWindowTopComponent")
@Messages({
    "CTL_PropertiesSheetWindowAction=Edit",
    "CTL_PropertiesSheetWindowTopComponent=Properties Window",
    "HINT_PropertiesSheetWindowTopComponent=This is a Properties window"
})
public final class PropertiesSheetWindowTopComponent extends TopComponent implements PropertyChangeListener {

//    private final PropertySheet sheet = new PropertySheet();
    private final SettableProxyLookup mp;
    private Node[] current;
    private PropertyChangeListener nodePropertyListener;

    public PropertiesSheetWindowTopComponent() {
        this(new SettableProxyLookup());
    }

    private PropertiesSheetWindowTopComponent(SettableProxyLookup mp) {
        super(mp);
        this.mp = mp;
        initComponents();
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Actions/UndoManager");
        for (Action action : actionsForPath) {
            jToolBar1.add(action);
        }
        setName(Bundle.CTL_PropertiesSheetWindowTopComponent());
        setToolTipText(Bundle.HINT_PropertiesSheetWindowTopComponent());
        nodePropertyListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                nodePropertyChanged(evt);
            }
        };
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sheet = new PropertySheet();
        jToolBar1 = new javax.swing.JToolBar();

        jToolBar1.setRollover(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sheet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(sheet, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel sheet;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        TopComponent.getRegistry().addPropertyChangeListener(this);
        propertyChange(null);
    }

    @Override
    public void componentClosed() {
        TopComponent.getRegistry().removePropertyChangeListener(this);
        ((PropertySheet) sheet).setNodes(new Node[0]);

    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt == null || TopComponent.Registry.PROP_ACTIVATED.equals(evt.getPropertyName())) {
            TopComponent tc = TopComponent.getRegistry().getActivated();
            if (tc != this) {
                if (tc == null) {
                    mp.clear();
                } else {
                    mp.set(tc.getLookup());
                }
            }
        }
        if (evt == null || TopComponent.Registry.PROP_ACTIVATED_NODES.equals(evt.getPropertyName())) {
            Node[] nodes = TopComponent.getRegistry().getActivatedNodes();
            ((PropertySheet) sheet).setNodes(proxy(nodes, nodePropertyListener));
        }
    }

    private void nodePropertyChanged(PropertyChangeEvent evt) {
        this.repaint();
    }

    static UndoProxyNode proxy(Node node, PropertyChangeListener listener) {
        Node me = node;
        if (!(node instanceof UndoProxyNode)) {
            me = new UndoProxyNode(node);
        }
        me.addPropertyChangeListener(WeakListeners.propertyChange(listener, me));
        return (UndoProxyNode) me;
    }

    static UndoProxyNode[] proxy(Node[] nodes, PropertyChangeListener listener) {
        UndoProxyNode[] result = new UndoProxyNode[nodes.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = proxy(nodes[i], listener);
        }
        return result;
    }

    UndoProxyNode[] setNodes(Node[] nodes) {
        if (current != null) {
            for (Node node : current) {
                node.removePropertyChangeListener(nodePropertyListener);
            }
        }
        UndoProxyNode[] proxy = proxy(nodes, nodePropertyListener);
        ((PropertySheet) sheet).setNodes(proxy);
        return proxy;
    }

    void cancel() {
        Collection<? extends UndoProxyNode> lookupAll = this.getLookup().lookupAll(UndoProxyNode.class);
        for (UndoProxyNode n : lookupAll) {
            n.undoAllEdits();
        }
    }

    void save() {
        Collection<? extends UndoProxyNode> lookupAll = this.getLookup().lookupAll(UndoProxyNode.class);
        for (UndoProxyNode n : lookupAll) {
            save(n);
        }
    }

    <T extends UndoProxyNode> void save(T n) {
        try {
            if (n instanceof Savable) {
                ((Savable) n).save();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            n.undoAllEdits();
        }
    }

    static class SettableProxyLookup extends ProxyLookup {

        public void clear() {
            setLookups();
        }

        public void set(Lookup lkp) {
            setLookups(lkp);
        }
    }
}
