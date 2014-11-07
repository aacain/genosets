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

import edu.uncc.genosets.propertieseditor.GlobalUndoManager.SaveAction;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Collection;
import org.netbeans.api.actions.Savable;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.nodes.NodeAcceptor;
import org.openide.nodes.NodeOperation;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.UserCancelException;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

/**
 *
 * @author aacain
 */
@ServiceProvider(service = NodeOperation.class, position = 1)
public final class NodeOperationCustomImpl extends NodeOperation {

    @Override
    public boolean customize(Node n) {
        Collection<? extends NodeOperation> lookupAll = Lookup.getDefault().lookupAll(NodeOperation.class);
        NodeOperation current = null;
        for (NodeOperation no : lookupAll) {
            if (no != this) {
                current = no;
                break;
            }
        }
        return current.customize(n);
    }

    @Override
    public void explore(Node n) {
        Collection<? extends NodeOperation> lookupAll = Lookup.getDefault().lookupAll(NodeOperation.class);
        NodeOperation current = null;
        for (NodeOperation no : lookupAll) {
            if (no != this) {
                current = no;
                break;
            }
        }
        current.explore(n);
    }

    @Override
    public void showProperties(Node n) {
//        Collection<? extends NodeOperation> lookupAll = Lookup.getDefault().lookupAll(NodeOperation.class);
//        NodeOperation current = null;
//        for (NodeOperation no : lookupAll) {
//            if(no != this){
//                current = no;
//                break;
//            }
//        }
        openProperties(new Node[]{n});
    }

    /**
     * Helper method, opens properties top component in single mode and requests
     * a focus for it
     */
    private static void openProperties(final Node[] nds) {
        // XXX #36492 in NbSheet the name is set asynch from setNodes.
//        Mutex.EVENT.readAccess (new Runnable () { // PENDING

        final String[] options = new String[]{"Save", "Cancel"};
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                boolean modal = true;
                PropertiesSheetWindowTopComponent tc = (PropertiesSheetWindowTopComponent) WindowManager.getDefault().findTopComponent("PropertiesSheetWindowTopComponent");
//                    Dialog dlg = org.openide.DialogDisplayer.getDefault().createDialog(new DialogDescriptor (
//                        tc,
//                        "Properties",
//                        modal,
//                        new Object [] {DialogDescriptor.CLOSED_OPTION},
//                        DialogDescriptor.CLOSED_OPTION,
//                        DialogDescriptor.BOTTOM_ALIGN,
//                        null,
//                        null
//                    ));
//                    tc.setNodes(nds);                    
//                    dlg.setVisible(true);
                DialogDescriptor dd = new DialogDescriptor(
                        tc,
                        "Properties",
                        modal,
                        options,
                        DialogDescriptor.CLOSED_OPTION,
                        DialogDescriptor.BOTTOM_ALIGN,
                        null,
                        null);
                UndoProxyNode[] undoNodes = tc.setNodes(nds);
                Object selectedValue = DialogDisplayer.getDefault().notify(dd);
                if (selectedValue.equals("Save")) {
                    //tc.save();
                    for (Node node : nds) {
                        if (node instanceof Savable) {
                            try {
                                ((Savable) node).save();
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                    }
                    GlobalUndoManager.get().discardAllEdits();
                } else if (selectedValue.equals("Cancel")) {
                    for (UndoProxyNode node : undoNodes) {
                        node.undoAllEdits();
                    }
                } else {
                    boolean hasEdits = false;
                    for (UndoProxyNode node : undoNodes) {
                        if (node.hasEdits()) {
                            hasEdits = true;
                            break;
                        }
                    }
                    if (hasEdits) {
                        NotifyDescriptor d =
                                new NotifyDescriptor.Confirmation("Would you like to save your changes?", "Save",
                                NotifyDescriptor.OK_CANCEL_OPTION);

                        if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
                            for (Node node : undoNodes) {
                                if (node instanceof UndoProxyNode) {
                                    ((UndoProxyNode) node).undoAllEdits();
                                }
                            }
                        } else {
                            for (Node node : undoNodes) {
                                if (node instanceof UndoProxyNode) {
                                    ((UndoProxyNode) node).undoAllEdits();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void showProperties(Node[] n) {
        Collection<? extends NodeOperation> lookupAll = Lookup.getDefault().lookupAll(NodeOperation.class);
        NodeOperation current = null;
        for (NodeOperation no : lookupAll) {
            if (no != this) {
                current = no;
                break;
            }
        }
        current.showProperties(n);
    }

    @Override
    public Node[] select(String title, String rootTitle, Node root, NodeAcceptor acceptor, Component top) throws UserCancelException {
        Collection<? extends NodeOperation> lookupAll = Lookup.getDefault().lookupAll(NodeOperation.class);
        NodeOperation current = null;
        for (NodeOperation no : lookupAll) {
            if (no != this) {
                current = no;
                break;
            }
        }
        return current.select(title, rootTitle, root, acceptor, top);
    }
}
