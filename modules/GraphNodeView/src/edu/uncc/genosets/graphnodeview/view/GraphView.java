/*
 * 
 * 
 */
package edu.uncc.genosets.graphnodeview.view;

import edu.uncc.genosets.treemap.DAGGraph;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

/**
 *
 * @author aacain
 */
public class GraphView extends Display {

    private static final String graph = "graph";
    private static final String nodes = "graph.nodes";
    private static final String edges = "graph.edges";
    private static final String SELECTED = "GraphView_Selected";
    private static final String LOCKED = "GraphView_Locked";
    private DAGGraph dagGraph;

    public GraphView(DAGGraph graph, String label) {
        super(new Visualization());
        this.dagGraph = graph;
        createVisualization(label);
    }

    private void createVisualization(String label) {
        VisualGraph vg = m_vis.addGraph(graph, dagGraph);
        m_vis.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.FALSE);
        VisualItem f = (VisualItem) vg.getNode(0);
        m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
        f.setFixed(false);
        // --------------------------------------------------------------------
        // set up the renderers

        //LabelRenderer tr = new LabelRenderer(label);
        GoLabelRenderer tr = new GoLabelRenderer(label);
        tr.setRoundedCorner(8, 8);
        m_vis.setRendererFactory(new DefaultRendererFactory(tr));


        //add selected focus group
        TupleSet selectedItems = new DefaultTupleSet();
        m_vis.addFocusGroup(SELECTED, selectedItems);
        selectedItems.addTuple(m_vis.getVisualItem(nodes, dagGraph.getRoot()));
        TupleSet lockedItems = new DefaultTupleSet();
        m_vis.addFocusGroup(LOCKED, lockedItems);
        lockedItems.addTupleSetListener(new TupleSetListener() {

            public void tupleSetChanged(TupleSet ts, Tuple[] added, Tuple[] removed) {
                for (int i = 0; i < removed.length; ++i) {
                    ((VisualItem) removed[i]).setFixed(false);
                }
                for (int i = 0; i < added.length; ++i) {
                    ((VisualItem) added[i]).setFixed(false);
                    ((VisualItem) added[i]).setFixed(true);
                }
                if (ts.getTupleCount() == 0) {
                    ts.addTuple(removed[0]);
                    ((VisualItem) removed[0]).setFixed(false);
                }
                m_vis.run("draw");
            }
        });

        // fix selected focus nodes
        TupleSet focusGroup = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
        focusGroup.addTupleSetListener(new TupleSetListener() {

            public void tupleSetChanged(TupleSet ts, Tuple[] added, Tuple[] removed) {
                TupleSet locked = m_vis.getFocusGroup(LOCKED);
                for (int i = 0; i < removed.length; ++i) {
                    VisualItem item = ((VisualItem) removed[i]);
                    if (!locked.containsTuple(item)) {
                        ((VisualItem) item).setFixed(false);
                    }
                }
                for (int i = 0; i < added.length; ++i) {
                    VisualItem item = ((VisualItem) added[i]);
                    if (!locked.containsTuple(item)) {
                        ((VisualItem) item).setFixed(false);
                        ((VisualItem) item).setFixed(true);
                    }
                }
                if (ts.getTupleCount() == 0) {
                    VisualItem item = ((VisualItem) removed[0]);
                    ts.addTuple(item);
                    if (!locked.containsTuple(item)) {
                        ((VisualItem) item).setFixed(false);
                    }
                }
                m_vis.run("draw");
            }
        });

        addControlListener(new ControlAdapter() {

            public void itemClicked(VisualItem item, MouseEvent e) {
                if (e.getClickCount() == 1) {
                    TupleSet ts = m_vis.getFocusGroup("_focus_");
                    if (ts.containsTuple(item)) { //then remove
                        ts.removeTuple(item);
                    } else {
                        ts.addTuple(item);
                    }
                    m_vis.repaint();
                }
            }
        });

        // --------------------------------------------------------------------
        // create actions to process the visual data

        int hops = 2;
        final GraphNodeDistanceFilter filter = new GraphNodeDistanceFilter(graph, SELECTED, hops);
        //GraphDistanceFilter filter = new GraphDistanceFilter(graph, hops);
        ColorAction fill = new ColorAction(nodes,
                VisualItem.FILLCOLOR, ColorLib.rgb(200, 200, 255));
        fill.add(VisualItem.FIXED, ColorLib.rgb(255, 100, 100));
        fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255, 200, 125));

        ActionList draw = new ActionList();
        draw.add(filter);
        draw.add(fill);
        draw.add(new ColorAction(nodes, VisualItem.STROKECOLOR, 0));
        draw.add(new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0)));
        draw.add(new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction(edges, VisualItem.STROKECOLOR, ColorLib.gray(200)));


        // create the tree layout action
        //RadialTreeLayout treeLayout = new RadialTreeLayout(graph);
        //treeLayout.setAngularBounds(-Math.PI/2, Math.PI);
        //m_vis.putAction("treeLayout", treeLayout);
        ActionList animate = new ActionList(ActionList.INFINITY);
        ForceDirectedLayout fdl = new ForceDirectedLayout(graph);
        ForceSimulator fsim = fdl.getForceSimulator();
        fsim.getForces()[0].setParameter(0, -1.2f);
        animate.add(fdl);
        animate.add(fill);
        animate.add(new RepaintAction());

//        //run once layout
//        ActionList oneAnimate = new ActionList();
//        ForceDirectedLayout fdl2 = new ForceDirectedLayout(graph, true, true);
//        oneAnimate.add(fdl2);
//        animate.add(fill);
//        animate.add(new RepaintAction());

        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        m_vis.putAction("draw", draw);
        m_vis.putAction("layout", animate);

        m_vis.runAfter("draw", "layout");


        // main display controls
        addControlListener(new ControlAdapter() {

            public void itemClicked(VisualItem item, MouseEvent e) {
                if (e.getClickCount() == 1) {
                    TupleSet ts = m_vis.getFocusGroup("_focus_");
                    if (ts.containsTuple(item)) { //then remove
                        ts.removeTuple(item);
                    } else {
                        ts.addTuple(item);
                    }
                    m_vis.repaint();
                }
            }
        });
        addControlListener(new MultipleDragControl());
        addControlListener(new PanControl());
        addControlListener(new ZoomControl());
        addControlListener(new WheelZoomControl());
        addControlListener(new ZoomToFitControl());
        addControlListener(new NeighborHighlightControl());

        m_vis.run("draw");
    }

    public JPanel getJPanel() {
        JPanel panel = new JPanel();
        panel.add(this);
        return panel;
    }

    public class GoLabelRenderer extends LabelRenderer {

        public GoLabelRenderer(String textField) {
            super(textField);
        }

        @Override
        protected String getText(VisualItem item) {
            if (item instanceof NodeItem) {
                String[] split = super.getText(item).split(" ");
                StringBuilder bldr = new StringBuilder();
                int length = 0;
                for (int i = 0; i < split.length; i++) {
                    String string = split[i];
                    length = length + string.length();
                    bldr.append(string);
                    if (length > 15) {
                        length = 2;
                        bldr.append("\n ");
                    }
                    bldr.append(" ");
                }

                return bldr.toString();
            }
            return super.getText(item);
        }
    }

    /**
     * MAD - Extension of the original DragControl which drags all focused items around
     *
     * @author goose
     *
     */
    public class MultipleDragControl extends DragControl {

        public void itemPressed(VisualItem item, MouseEvent e) {
            super.itemPressed(item, e);
            addToFocusGroup(item, e);
        }

        public void itemClicked(VisualItem vi, MouseEvent e) {
            super.itemPressed(vi, e);
            addToFocusGroup(vi, e);
            if (e.getClickCount() > 1) {
                TupleSet ts = m_vis.getFocusGroup(LOCKED);
                if (ts.containsTuple(vi)) {
                    ts.removeTuple(vi);
                } else {
                    ts.addTuple(vi);
                }
            }
        }

        @Override
        public void itemReleased(VisualItem item, MouseEvent e) {
            super.itemReleased(item, e);
        }

        public void itemDragged(VisualItem item, MouseEvent e) {
            if (!SwingUtilities.isLeftMouseButton(e)) {
                return;
            }

            Visualization vis = item.getVisualization();

            TupleSet focusSet = vis.getGroup("_focus_");


            dragged = true;
            Display d = (Display) e.getComponent();

            d.getAbsoluteCoordinate(e.getPoint(), temp);
            double dx = temp.getX() - down.getX();
            double dy = temp.getY() - down.getY();

            if (e.isControlDown()) {
                Iterator iter = focusSet.tuples();
                while (iter.hasNext()) {
                    moveIt((VisualItem) iter.next(), dx, dy);
                }
            } else {
                moveIt(item, dx, dy);
            }
            down.setLocation(temp);
            if (repaint) {
                vis.repaint();
            }


            if (action != null) {
                d.getVisualization().run(action);
            }

        }

        private void addToFocusGroup(VisualItem vi, MouseEvent e) {
            TupleSet ts = vi.getVisualization().getGroup("_focus_");
            if (!e.isControlDown()) {
                ts.clear();
            }
            ts.addTuple(vi);
        }

        private void moveIt(VisualItem vi, double dx, double dy) {

            double x = vi.getX();
            double y = vi.getY();

            vi.setStartX(x);
            vi.setStartY(y);
            vi.setX(x + dx);
            vi.setY(y + dy);
            vi.setEndX(x + dx);
            vi.setEndY(y + dy);
        }
    } // end of class MultipleDragControl
}
