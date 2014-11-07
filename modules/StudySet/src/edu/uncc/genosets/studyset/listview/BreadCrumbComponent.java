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

import edu.uncc.genosets.studyset.actions.FocusChangedAction;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.openide.awt.HtmlRenderer;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;

/**
 *
 * @author aacain
 */
public class BreadCrumbComponent<T extends JLabel & HtmlRenderer.Renderer> extends JComponent implements PropertyChangeListener {

    public BreadCrumbComponent() {
        setPreferredSize(new Dimension(0, COMPONENT_HEIGHT));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                expand(e);
            }
        });
    }
    private final Image SEPARATOR = ImageUtilities.loadImage("edu/uncc/genosets/icons/separator.png");
    public static final Image NO_ICON = ImageUtilities.loadImage("edu/uncc/genosets/icons/no-icon.png");
    private static final int USABLE_HEIGHT = 19;
    private static final int LEFT_SEPARATOR_INSET = 2;
    private static final int RIGHT_SEPARATOR_INSET = 10;
    private static final int ICON_TEXT_SEPARATOR = 5;
    private static final int START_INSET = 8;
    private static final int MAX_ROWS_IN_POP_UP = 20;
    public static final int COMPONENT_HEIGHT = USABLE_HEIGHT;
    private Node[] nodes;
    private double[] sizes;
    private double height;
    private ExplorerManager seenManager;
    private final T renderer = (T) HtmlRenderer.createLabel();

    private ExplorerManager findManager() {
        ExplorerManager manager = ExplorerManager.find(this);

        if (seenManager != manager) {
            if (seenManager != null) {
                seenManager.removePropertyChangeListener(this);
            }
            if (manager != null) {
                manager.addPropertyChangeListener(this);
            }
            seenManager = manager;
        }

        assert manager != null;

        return manager;
    }

    private void expand(MouseEvent e) {
        int clickX = e.getPoint().x;
        int elemX = START_INSET;

        for (int i = 0; i < sizes.length; i++) {
            int startX = elemX;
            elemX += sizes[i];

            elemX += LEFT_SEPARATOR_INSET;

            if (clickX <= elemX) {
                //found:
                List<Node> path = computeNodePath();
                Node selected = path.get(i);
                if (e.getButton() == MouseEvent.BUTTON1) {
                    open(selected);
                } else {
                    //expand(startX, selected);
                }
                return;
            }

            startX = elemX;
            elemX += SEPARATOR.getWidth(null);

            if (clickX <= elemX) {
                //found:
                List<Node> path = computeNodePath();
                expand(startX, path.get(i));
                return;
            }

            elemX += RIGHT_SEPARATOR_INSET;
        }
    }

    private void open(Node node) {
        //        Openable openable = node.getLookup().lookup(Openable.class);
        //
        //        if (openable != null) {
        //            openable.open();
        //        }
        try {
            findManager().setSelectedNodes(new Node[]{node});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void expand(int startX, final Node what) {
        GroupHierarchy groupHierarchy = what.getLookup().lookup(GroupHierarchy.class);
        JPopupMenu actionsToPopup = Utilities.actionsToPopup(new Action[]{}, what.getLookup());
        
        Point p = new Point(startX, 0);
        actionsToPopup.show(this, p.x, p.y - actionsToPopup.getPreferredSize().height);
        
//        if (what.getChildren().getNodesCount() == 0) {
//            return;
//        }
//
//        final ExplorerManager expandManager = new ExplorerManager();
//        class Expanded extends JPanel implements ExplorerManager.Provider {
//
//            public Expanded(LayoutManager layout) {
//                super(layout);
//            }
//
//            @Override
//            public ExplorerManager getExplorerManager() {
//                return expandManager;
//            }
//        }
//        final JPanel expanded = new Expanded(new BorderLayout());
//        expanded.setBorder(new LineBorder(Color.BLACK, 1));
//        expanded.add(new ListView() {
//            {
//                int nodesCount = what.getChildren().getNodesCount();
//
//                if (nodesCount >= MAX_ROWS_IN_POP_UP) {
//                    list.setVisibleRowCount(MAX_ROWS_IN_POP_UP);
//                } else {
//                    list.setVisibleRowCount(nodesCount);
//
//                    NodeRenderer nr = new NodeRenderer();
//                    int i = 0;
//                    int width = getPreferredSize().width;
//
//                    for (Node n : what.getChildren().getNodes()) {
//                        if (nr.getListCellRendererComponent(list, n, i, false, false).getPreferredSize().width > width) {
//                            Dimension pref = getPreferredSize();
//                            pref.height += getHorizontalScrollBar().getPreferredSize().height;
//                            setPreferredSize(pref);
//                            break;
//                        }
//                    }
//                }
//            }
//        }, BorderLayout.CENTER);
//        expandManager.setRootContext(what);
//
//        Point place = new Point(startX, 0);
//
//        SwingUtilities.convertPointToScreen(place, this);
//
//        expanded.validate();
//
//        final Popup popup = PopupFactory.getSharedInstance().getPopup(this, expanded, place.x, place.y - expanded.getPreferredSize().height);
//        final AWTEventListener multicastListener = new AWTEventListener() {
//            @Override
//            public void eventDispatched(AWTEvent event) {
//                if (event instanceof MouseEvent && ((MouseEvent) event).getClickCount() > 0) {
//                    Object source = event.getSource();
//
//                    while (source instanceof Component) {
//                        if (source == expanded) {
//                            return; //accept
//                        }
//                        source = ((Component) source).getParent();
//                    }
//
//                    popup.hide();
//                    Toolkit.getDefaultToolkit().removeAWTEventListener(this);
//                }
//            }
//        };
//
//        Toolkit.getDefaultToolkit().addAWTEventListener(multicastListener, AWTEvent.MOUSE_EVENT_MASK);
//
//        expandManager.addPropertyChangeListener(new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
//                    Node[] selected = expandManager.getSelectedNodes();
//                    if (selected.length == 1) {
//                        open(selected[0]);
//                        popup.hide();
//                        Toolkit.getDefaultToolkit().removeAWTEventListener(multicastListener);
//                    }
//                }
//            }
//        });
//
//        popup.show();
    }

    private List<Node> computeNodePath() {
        ExplorerManager manager = findManager();
        List<Node> path = new ArrayList<Node>();
//        Node sel = manager.getExploredContext();

        Node[] selectedNodes = manager.getSelectedNodes();
        Node sel = manager.getRootContext();
        if (selectedNodes.length == 1) {
            sel = selectedNodes[0];
        }


        // see #223480; root context need not be the root of the node structure.
        Node stopAt = manager.getRootContext().getParentNode();
        while (sel != null && sel != stopAt) {
            path.add(sel);
            sel = sel.getParentNode();
        }

        path.remove(path.size() - 1); //XXX

        Collections.reverse(path);

        return path;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (nodes == null) {
            measurePrepaint();
        }

        assert nodes != null;

        if ("Aqua".equals(UIManager.getLookAndFeel().getID())) //NOI18N
        {
            setBackground(UIManager.getColor("NbExplorerView.background")); //NOI18N
        }
        int height = getHeight();

        if (nodes.length == 0) {
            g.drawImage(SEPARATOR, START_INSET, (height - SEPARATOR.getHeight(null)) / 2, null);
            return;
        }

        int x = START_INSET;

        for (int i = 0; i < nodes.length; i++) {
            configureForNode(nodes[i]);
            Dimension preferred = renderer.getPreferredSize();
            int labelY = (height - preferred.height) / 2;
            g.translate(x, labelY);
            renderer.setSize(preferred);
            renderer.paint(g);
            g.translate(-x, -labelY);

            x += sizes[i];

            g.drawImage(SEPARATOR, x + LEFT_SEPARATOR_INSET, (height - SEPARATOR.getHeight(null)) / 2, null);

            x += LEFT_SEPARATOR_INSET + SEPARATOR.getWidth(null) + RIGHT_SEPARATOR_INSET;
        }
    }

    private void measurePrepaint() {
        List<Node> path = computeNodePath();

        int i = 0;

        nodes = path.toArray(new Node[path.size()]);
        sizes = new double[path.size()];

        int xTotal = 0;

        height = /*XXX*/ 0;

        for (Node n : nodes) {
            configureForNode(n);
            Dimension preferedSize = renderer.getPreferredSize();
            xTotal += sizes[i] = preferedSize.width;

            height = Math.max(height, preferedSize.height);

            i++;
        }

        setPreferredSize(new Dimension((int) (xTotal + (nodes.length - 1) * (LEFT_SEPARATOR_INSET + SEPARATOR.getWidth(null) + RIGHT_SEPARATOR_INSET) + START_INSET), USABLE_HEIGHT/*(int) (height + 2 * INSET_HEIGHT)*/));
    }

    private void configureForNode(Node node) {
        renderer.reset();

        Image nodeIcon = node.getIcon(BeanInfo.ICON_COLOR_16x16);
        Icon icon = nodeIcon != null && nodeIcon != NO_ICON ? ImageUtilities.image2Icon(nodeIcon) : null;
        int width = icon != null ? icon.getIconWidth() : 0;
        if (width > 0) {
            renderer.setIcon(icon);
            renderer.setIconTextGap(ICON_TEXT_SEPARATOR);
        } else {
            renderer.setIcon(null);
            renderer.setIconTextGap(0);
        }
        String html = node.getHtmlDisplayName();
        if (html != null) {
            renderer.setHtml(true);
            renderer.setText(html);
        } else {
            renderer.setHtml(false);
            renderer.setText(node.getDisplayName());
        }
        renderer.setFont(getFont());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                measurePrepaint();
                repaint();
            }
        });

    }
}
