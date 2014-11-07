/*
 * 
 * 
 */
package edu.uncc.genosets.graphview.view;

import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.TermCalculation;
import edu.uncc.genosets.treemap.DAGGraph;
import edu.uncc.genosets.treemap.DAGTree;
import edu.uncc.genosets.treemap.GOSchema;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JPanel;
import org.openide.util.Exceptions;
import org.openide.util.WeakListeners;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.assignment.StrokeAction;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.GraphicsLib;
import prefuse.util.StrokeLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.ui.UILib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;

/**
 *
 * @author aacain
 */
public class GraphVisualization extends Display implements PropertyChangeListener {

    private static final String tree = "tree";
    private static final String treeNodes = "tree.nodes";
    private static final String treeEdges = "tree.edges";
    private static final String LABEL_COLUMN = GOSchema.NAME;
    private static final String SELECTED = "graphview_selected";
    private static final String HOVER = "graphview_hover";
    private LabelRenderer m_nodeRenderer;
    private EdgeRenderer m_edgeRenderer;
    private int m_orientation = Constants.ORIENT_LEFT_RIGHT;
    private final HashMap<String, LookupNode> idLookupMap = new HashMap<String, LookupNode>();
    private DAGGraph graph;
    private FishEyeTreeFilter fishEye;
    private NodeColorAction nodeColor;

    public GraphVisualization(DAGTree t) {
        super(new Visualization());
        graph = (DAGGraph) t.getBackingGraph();
        m_vis.add(tree, t);
        initializeLookup();

        graph.addPropChangeListener(WeakListeners.propertyChange(this, this.graph));

        //add selected focus group
        TupleSet selectedItems = new DefaultTupleSet();
        m_vis.addFocusGroup(SELECTED, selectedItems);
        TupleSet hoverItems = new DefaultTupleSet();
        m_vis.addFocusGroup(HOVER, hoverItems);

        //m_nodeRenderer = new LabelRenderer(LABEL_COLUMN);
        m_nodeRenderer = new GoLabelRenderer(LABEL_COLUMN);
        m_nodeRenderer.setHorizontalTextAlignment(Constants.LEFT);
        m_nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_DRAW_AND_FILL);
        m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
        m_nodeRenderer.setRoundedCorner(8, 8);
        m_edgeRenderer = new EdgeRenderer(Constants.EDGE_TYPE_CURVE);

        DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
        m_vis.setRendererFactory(rf);

        // colors
        nodeColor = new NodeColorAction(treeNodes);
        ItemAction textColor = new ColorAction(treeNodes,
                VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));
        BorderColorAction borderColor = new BorderColorAction(treeNodes);//new ColorAction(treeNodes, VisualItem.STROKECOLOR, ColorLib.rgb(0, 0, 0));
        StrokeAction stroke = new StrokeAction(treeNodes, StrokeLib.getStroke(2));


        ActionList labelPaint = new ActionList();
        labelPaint.add(textColor);
        //labelPaint.add(borderColor);
        //m_vis.putAction("textColor", textColor);
        m_vis.putAction("textColor", labelPaint);

        ItemAction edgeColor = new ColorAction(treeEdges,
                VisualItem.STROKECOLOR, ColorLib.rgb(0, 0, 0));

        // quick repaint
        ActionList repaint = new ActionList();
        repaint.add(nodeColor);
        repaint.add(borderColor);
        repaint.add(stroke);
        repaint.add(new RepaintAction());
        m_vis.putAction("repaint", repaint);

        // full paint
        ActionList fullPaint = new ActionList();
        fullPaint.add(nodeColor);
        fullPaint.add(borderColor);
        fullPaint.add(stroke);
        m_vis.putAction("fullPaint", fullPaint);

        // animate paint change
        ActionList animatePaint = new ActionList(400);
        animatePaint.add(new ColorAnimator(treeNodes));
        animatePaint.add(new RepaintAction());
        m_vis.putAction("animatePaint", animatePaint);

        // create the tree layout action
        NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout(tree,
                m_orientation, 30, 1, 8);
        treeLayout.setLayoutAnchor(new Point2D.Double(25, 300));
        m_vis.putAction("treeLayout", treeLayout);

        CollapsedSubtreeLayout subLayout =
                new CollapsedSubtreeLayout(tree, m_orientation);
        m_vis.putAction("subLayout", subLayout);



        // create the filtering and layout
        ActionList filter = new ActionList();
        fishEye = new FishEyeTreeFilter(tree, SELECTED, SELECTED, 1);
        filter.add(fishEye);
        filter.add(new FontAction(treeNodes, FontLib.getFont("Tahoma", 16)));
        filter.add(treeLayout);
        filter.add(subLayout);
        filter.add(textColor);
        filter.add(nodeColor);
        filter.add(edgeColor);
        filter.add(borderColor);
        filter.add(stroke);
        m_vis.putAction("filter", filter);

        // animated transition
        //MyAutoPanAction autoPan = new MyAutoPanAction();
        ActionList animate = new ActionList(1000);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(new QualityControlAnimator());
        animate.add(new VisibilityAnimator(tree));
        animate.add(new LocationAnimator(treeNodes));
        animate.add(new ColorAnimator(treeNodes));
        animate.add(new RepaintAction());
        m_vis.putAction("animate", animate);


        MyAutoPanAction autoPan = new MyAutoPanAction();
        ActionList panTo = new ActionList(1000);
        panTo.setPacingFunction(new SlowInSlowOutPacer());
        panTo.add(autoPan);
        panTo.add(new RepaintAction());
        m_vis.putAction("pan", panTo);

        ZoomToFitAction zToFit = new ZoomToFitAction();
        m_vis.putAction("zoomToFit", zToFit);

        m_vis.alwaysRunAfter("filter", "animate");
        m_vis.alwaysRunAfter("animate", "pan");
        

//        // create animator for orientation changes
//        ActionList orient = new ActionList(2000);
//        orient.setPacingFunction(new SlowInSlowOutPacer());
//        orient.add(autoPan);
//        orient.add(new QualityControlAnimator());
//        orient.add(new LocationAnimator(treeNodes));
//        orient.add(new RepaintAction());
//        m_vis.putAction("orient", orient);

        // ------------------------------------------------

        // initialize the display
        //setSize(700, 600);
        setItemSorter(new TreeDepthItemSorter());
        addControlListener(new ZoomToFitControl());
        addControlListener(new ZoomControl());
        addControlListener(new WheelZoomControl());
        addControlListener(new PanControl());
        //addControlListener(new FocusControl(1, "filter"));
        addControlListener(new MyClickControl());

        addControlListener(new ControlAdapter() {

            @Override
            public void itemClicked(VisualItem item, MouseEvent e) {
                //System.out.println("Clicked: " + item.getX() + ", " + item.getY());
            }
        });
        //        registerKeyboardAction(
        //                new OrientAction(Constants.ORIENT_LEFT_RIGHT),
        //                "left-to-right", KeyStroke.getKeyStroke("ctrl 1"), WHEN_FOCUSED);
        //        registerKeyboardAction(
        //                new OrientAction(Constants.ORIENT_TOP_BOTTOM),
        //                "top-to-bottom", KeyStroke.getKeyStroke("ctrl 2"), WHEN_FOCUSED);
        //        registerKeyboardAction(
        //                new OrientAction(Constants.ORIENT_RIGHT_LEFT),
        //                "right-to-left", KeyStroke.getKeyStroke("ctrl 3"), WHEN_FOCUSED);
        //        registerKeyboardAction(
        //                new OrientAction(Constants.ORIENT_BOTTOM_TOP),
        //                "bottom-to-top", KeyStroke.getKeyStroke("ctrl 4"), WHEN_FOCUSED);
        // ------------------------------------------------
        // filter graph and perform layout
        setOrientation(m_orientation);
        //run the filter to initialize display

        m_vis.run("filter");
    }

    @SuppressWarnings("unchecked")
    private void initializeLookup() {
        for (Iterator<NodeItem> it = m_vis.getVisualGroup(treeNodes).tuples(); it.hasNext();) {
            NodeItem item = it.next();
            int sourceNodeRow = item.getInt(DAGTree.SOURCE_NODE_ROW);
            String goString = graph.getNode(sourceNodeRow).getString(GOSchema.GO_TERM_ID);
            LookupNode lookupNode = idLookupMap.get(goString);
            if (lookupNode == null) {
                lookupNode = new LookupNode(sourceNodeRow);
                idLookupMap.put(goString, lookupNode);
            }
            lookupNode.addTreeRow(item.getRow());
        }
    }

    public void resizeMe(Dimension dimensions) {
        this.setSize(dimensions);
        m_vis.run("repaint");
    }

    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(this, BorderLayout.CENTER);
        return panel;
    }

    public void setSelectedGoTerms(TupleSet ts, Tuple[] added, Tuple[] removed) {
        TupleSet selectedTs = m_vis.getFocusGroup(SELECTED);
        TupleSet hover = m_vis.getFocusGroup(HOVER);
        hover.clear();
        boolean hasItem = false;

        for (Tuple t : added) {
            VisualItem visualItem = m_vis.getVisualItem(treeNodes, t);
            selectedTs.addTuple(visualItem);
            visualItem.setStrokeColor(ColorLib.rgb(255, 255, 179));
            hasItem = true;
            hover.addTuple(t);
            m_vis.getFocusGroup(Visualization.FOCUS_ITEMS).setTuple(visualItem);
        }
        if (hasItem) {
            m_vis.run("filter");
        }
    }

    public void setHoveredGoTerms(Tuple[] added, Tuple[] removed) {
        TupleSet hover = m_vis.getFocusGroup(HOVER);
        hover.clear();

        for (Tuple t : added) {
            VisualItem visualItem = m_vis.getVisualItem(treeNodes, t);
            if (visualItem.isVisible()) {
                hover.addTuple(visualItem);
                visualItem.setFillColor(nodeColor.getColor(visualItem));
                visualItem.getVisualization().repaint();
            }
        }
        for (Tuple t : removed) {
            VisualItem visualItem = m_vis.getVisualItem(treeNodes, t);
            if (visualItem.isVisible()) {
                visualItem.setFillColor(nodeColor.getColor(visualItem));
                visualItem.getVisualization().repaint();
            }
        }
    }

    public void updateStudySet(StudySet studySet) {
//        HashMap<String, TermCalculation> calcMap = studySet.getTermCalculationMap();
//        //Now set percent change
//        for (Iterator<TableTuple> it = graph.getNodes().tuples(); it.hasNext();) {
//            TableTuple node = it.next();
//            TermCalculation calc = calcMap.get(node.getString(GOSchema.GO_TERM_ID));
//            if (calc != null) {
//                node.setDouble(GOSchema.P_VALUE, calc.getpAdjusted());
//            } else {
//                //What to do if study set hasn't been analyzed?
//                node.setDouble(GOSchema.P_VALUE, 1);
//            }
//        }
//        m_vis.run("repaint");
    }

    public void updateSelectedTerm(TermCalculation term) {
        TupleSet selected = m_vis.getFocusGroup(SELECTED);
        TupleSet hover = m_vis.getFocusGroup(HOVER);
        hover.clear();
        LookupNode lookupNode = idLookupMap.get(term.getTermId());
        DAGTree thisTree = graph.getTreeRep();
        Table table = thisTree.getNodeTable();
        int i = 0;
        VisualItem mainFocus = null;
        for (Integer row : lookupNode.getTreeRowList()) {
            Tuple tuple = table.getTuple(row);
            VisualItem me = m_vis.getVisualItem(treeNodes, tuple);
            selected.addTuple(me);
            hover.addTuple(tuple);
            if (i == 0) {
                mainFocus = me;
            }
            i++;
        }
        if (mainFocus != null) {
            m_vis.getFocusGroup(Visualization.FOCUS_ITEMS).clear();
            m_vis.getFocusGroup(Visualization.FOCUS_ITEMS).addTuple(mainFocus);
        }
        m_vis.run("filter");
    }

    // ------------------------------------------------------------------------
    public void setOrientation(int orientation) {
        NodeLinkTreeLayout rtl = (NodeLinkTreeLayout) m_vis.getAction("treeLayout");
        CollapsedSubtreeLayout stl = (CollapsedSubtreeLayout) m_vis.getAction("subLayout");
        switch (orientation) {
            case Constants.ORIENT_LEFT_RIGHT:
                m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
                m_edgeRenderer.setHorizontalAlignment1(Constants.RIGHT);
                m_edgeRenderer.setHorizontalAlignment2(Constants.LEFT);
                m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
                break;
            case Constants.ORIENT_RIGHT_LEFT:
                m_nodeRenderer.setHorizontalAlignment(Constants.RIGHT);
                m_edgeRenderer.setHorizontalAlignment1(Constants.LEFT);
                m_edgeRenderer.setHorizontalAlignment2(Constants.RIGHT);
                m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
                break;
            case Constants.ORIENT_TOP_BOTTOM:
                m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment1(Constants.BOTTOM);
                m_edgeRenderer.setVerticalAlignment2(Constants.TOP);
                break;
            case Constants.ORIENT_BOTTOM_TOP:
                m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment1(Constants.TOP);
                m_edgeRenderer.setVerticalAlignment2(Constants.BOTTOM);
                break;
            default:
                throw new IllegalArgumentException(
                        "Unrecognized orientation value: " + orientation);
        }
        m_orientation = orientation;
        rtl.setOrientation(orientation);
        stl.setOrientation(orientation);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ((GOSchema.P_VALUE).equals(evt.getPropertyName())) {
            m_vis.run("repaint");
        }
    }

    // ------------------------------------------------------------------------
    public class OrientAction extends AbstractAction {

        private int orientation;

        public OrientAction(int orientation) {
            this.orientation = orientation;
        }

        public void actionPerformed(ActionEvent evt) {
            setOrientation(orientation);
            getVisualization().cancel("orient");
            getVisualization().run("treeLayout");
            getVisualization().run("orient");
        }
    }

    private class MyClickControl extends ControlAdapter {

        VisualItem curFocus = null;

        @Override
        public void itemClicked(VisualItem item, MouseEvent e) {
            if (UILib.isButtonPressed(e, Control.LEFT_MOUSE_BUTTON)
                    && e.getClickCount() == 1) {
                boolean shift = e.isShiftDown();
                if (shift) {
                    //remove focus
                    m_vis.run("filter");
                } else {
                    if (item != curFocus) {
                        curFocus = item;
                        TupleSet focusTS = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
                        focusTS.setTuple(item);
                        TupleSet selectedTS = m_vis.getFocusGroup(SELECTED);
                        selectedTS.addTuple(item);
                        m_vis.run("filter");
                    }
                }
            }
        }
    }

    public class AutoPanAction extends Action {

        private Point2D m_start = new Point2D.Double();
        private Point2D m_end = new Point2D.Double();
        private Point2D m_cur = new Point2D.Double();
        private int m_bias = 150;

        public void run(double frac) {
            TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
            if (ts.getTupleCount() == 0) {
                return;
            }

            if (frac == 0.0) {
                int xbias = 0, ybias = 0;
                switch (m_orientation) {
                    case Constants.ORIENT_LEFT_RIGHT:
                        xbias = m_bias;
                        break;
                    case Constants.ORIENT_RIGHT_LEFT:
                        xbias = -m_bias;
                        break;
                    case Constants.ORIENT_TOP_BOTTOM:
                        ybias = m_bias;
                        break;
                    case Constants.ORIENT_BOTTOM_TOP:
                        ybias = -m_bias;
                        break;
                }

                VisualItem vi = (VisualItem) ts.tuples().next();
                m_cur.setLocation(getWidth() / 2, getHeight() / 2);
                getAbsoluteCoordinate(m_cur, m_start);
                m_end.setLocation(vi.getX() + xbias, vi.getY() + ybias);
            } else {
                m_cur.setLocation(m_start.getX() + frac * (m_end.getX() - m_start.getX()),
                        m_start.getY() + frac * (m_end.getY() - m_start.getY()));
                panToAbs(m_cur);
            }
        }
    }

    public class ZoomToFitAction extends Action {

        @Override
        public void run(double d) {
            int m_margin = 50;
            if (!isTranformInProgress()) {
                Rectangle2D bounds = m_vis.getBounds(Visualization.ALL_ITEMS);
                GraphicsLib.expand(bounds, m_margin + (int) (1 / getScale()));
                DisplayLib.fitViewToBounds(m_vis.getDisplay(0), bounds, 2000);
            }
        }
    }

    public class MyAutoPanAction extends Action {

        private Point2D m_start = new Point2D.Double();
        private Point2D m_end = new Point2D.Double();
        private Point2D m_cur = new Point2D.Double();
        private int m_bias = 150;

        public void run(double frac) {
            TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
            if (ts.getTupleCount() == 0) {
                return;
            }

            if (frac == 0.0) {
                int xbias = 0, ybias = 0;
                switch (m_orientation) {
                    case Constants.ORIENT_LEFT_RIGHT:
                        xbias = m_bias;
                        break;
                    case Constants.ORIENT_RIGHT_LEFT:
                        xbias = -m_bias;
                        break;
                    case Constants.ORIENT_TOP_BOTTOM:
                        ybias = m_bias;
                        break;
                    case Constants.ORIENT_BOTTOM_TOP:
                        ybias = -m_bias;
                        break;
                }

                VisualItem vi = (VisualItem) ts.tuples().next();
                m_cur.setLocation(getWidth() / 2, getHeight() / 2);
                getAbsoluteCoordinate(m_cur, m_start);
                m_end.setLocation(vi.getX() + xbias, vi.getY() + ybias);
            } else {
                m_cur.setLocation(m_start.getX() + frac * (m_end.getX() - m_start.getX()),
                        m_start.getY() + frac * (m_end.getY() - m_start.getY()));
                //System.out.println(frac + " start: " + m_start.getX() + ", " + m_start.getY() + " offset: " + (frac * (m_end.getX() - m_start.getX())) + ", " + (frac * (m_end.getY() - m_start.getY())) + " final: " + m_cur.getX() + ", " + m_cur.getY());
                panToAbs(m_cur);
            }
        }
    }

    public class NodeColorAction extends ColorAction {

        public NodeColorAction(String group) {
            super(group, VisualItem.FILLCOLOR);
        }

        public int getColor(VisualItem item) {
            NodeItem nitem = (NodeItem) item;
            int sourceRow = item.getInt(DAGTree.SOURCE_NODE_ROW);
            Node node = graph.getNode(sourceRow);
            double selectedChange = node.getDouble(GOSchema.P_VALUE);
            if (selectedChange <= 0.01) {
                double ratio = node.getDouble(GOSchema.RATIO);
                if (ratio >= 1) {
                    return ColorLib.rgb(250, 159, 181);
                } else {
                    return ColorLib.rgb(65, 174, 118);
                }
            }
            if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {
                return ColorLib.rgb(255, 190, 190);
            } else if (m_vis.isInGroup(item, HOVER)) {
                return ColorLib.rgb(252, 141, 98);
            } else {
                return ColorLib.rgba(255, 255, 255, 0);
            }
        }
    }

    public class BorderColorAction extends ColorAction {

        public BorderColorAction(String group) {
            super(group, VisualItem.STROKECOLOR);
        }

        @Override
        public int getColor(VisualItem item) {
            if (item.isInGroup(Visualization.FOCUS_ITEMS)) {
                return ColorLib.rgb(255, 255, 179);
            }
            return ColorLib.rgb(255, 255, 255);
        }
    }

    public class GoLabelRenderer extends LabelRenderer {

        public GoLabelRenderer(String textField) {
            super(textField);
        }

        @Override
        protected String getText(VisualItem item) {
            try{
            if (item instanceof NodeItem) {
                String s = super.getText(item);
                if(s == null){
                    int x = 5;
                }
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
            }catch(Exception ex){
                Exceptions.printStackTrace(ex);
            }
            return super.getText(item);
        }
    }
    // end of inner class TreeMapColorAction

    private static class LookupNode {

        private int graphRow;
        private List<Integer> treeRowList = new LinkedList<Integer>();

        public LookupNode(int graphRow) {
            this.graphRow = graphRow;
        }

        public int getGraphRow() {
            return graphRow;
        }

        public List<Integer> getTreeRowList() {
            return treeRowList;
        }

        public void addTreeRow(int row) {
            treeRowList.add(row);
        }
    }
}
