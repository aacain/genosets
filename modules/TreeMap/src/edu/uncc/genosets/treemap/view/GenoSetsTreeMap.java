/*
 * 
 * 
 */
package edu.uncc.genosets.treemap.view;

import edu.uncc.genosets.ontologizer.GoEnrichment;
import edu.uncc.genosets.studyset.TermCalculation;
import edu.uncc.genosets.treemap.DAGGraph;
import edu.uncc.genosets.treemap.DAGTree;
import edu.uncc.genosets.treemap.GOSchema;
import edu.uncc.genosets.treemap.TreeMapLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.Layout;
import prefuse.controls.ControlAdapter;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.event.TupleSetListener;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.data.tuple.TableTuple;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.ColorMap;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;

/**
 *
 * @author aacain
 */
public class GenoSetsTreeMap extends Display {
    //groups

    private static final String treeGroup = "tree";
    private static final String treeNodes = "tree.nodes";
    private static final String treeEdges = "tree.edges";
    private static final String labels = "labels";
    public static final String SELECTED = "x_selected";
    public static final String FOCUS = "x_focused";
    public static final String HOVER = "x_hover";
    private DAGGraph graph;
    private DAGTree tree;
    private NodeItem subRoot;
    private TreeMapLayout areaController;
    private String AREA_COLUMN;
    private double pValueCutoff;
    private final HashMap<String, LookupNode> idLookupMap = new HashMap<String, LookupNode>();
    private List<TupleSetListener> tupleSetListeners;
    // create data description of labels, setting colors, fonts ahead of time
    private static final Schema LABEL_SCHEMA = PrefuseLib.getVisualItemSchema();

    static {
        LABEL_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
        LABEL_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(150));
        LABEL_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", 16));
    }

    public GenoSetsTreeMap(DAGGraph graph, String areaColumn) {
        super(new Visualization());
        //get DAGTree representation
        this.graph = graph;
        this.tree = graph.getTreeRep();
        m_vis.addTree(treeGroup, tree);
        this.AREA_COLUMN = areaColumn;
        initializeLookup();
        tupleSetListeners = new LinkedList<TupleSetListener>();
    }

    public GenoSetsTreeMap(DAGGraph graph, String areaColumn, String subRootGoId) {
        this(graph, areaColumn);
        subRoot = getNodeItem(subRootGoId);
    }

    private NodeItem getNodeItem(String subRootGoId) {
        LookupNode lookupNode = idLookupMap.get(subRootGoId);
        if (lookupNode != null) {
            if (!lookupNode.getTreeRowList().isEmpty()) {
                //get the visual node for this tree node
                Integer treeNodeRow = lookupNode.getTreeRowList().get(0);
                Node node = tree.getNode(treeNodeRow);
                return (NodeItem) m_vis.getVisualItem(treeNodes, node);
            }
        }
        return null;
    }

    public void updatePValueCutoff(double pValue) {
        this.pValueCutoff = pValue;
    }

    @SuppressWarnings("unchecked")
    public void setSubLayout(NodeItem item) {
        NodeItem old = this.subRoot;
        this.subRoot = item;
        //iterate all visual tree visual items and set their position and size to zero
        TupleSet ts = m_vis.getVisualGroup(treeNodes);
        Iterator<VisualItem> it = ts.tuples();
        while (it.hasNext()) {
            VisualItem vItem = it.next();
            vItem.setX(0);
            vItem.setY(0);
            vItem.setSize(1);
            vItem.setBounds(0, 0, 0, 0);
        }
        areaController.setLayoutRoot(subRoot);
//        VisualItem oldVI = m_vis.getVisualItem(treeNodes, old);
//        oldVI.setStrokeColor(ColorLib.rgb(90, 180, 172));
        m_vis.cancel("layout");
        m_vis.run("layout");
    }

    public NodeItem getSubrootNodeItem() {
        return subRoot;
    }

    public DAGGraph getGraph() {
        return graph;
    }

    public JPanel getPanel() {
        JPanel panel = new JPanel();
        panel.add(this, BorderLayout.CENTER);
        return panel;
    }

    public void updateSelectedTerm(String termId) {
        LookupNode lookupNode = idLookupMap.get(termId);
        TupleSet ts = m_vis.getFocusGroup(SELECTED);
        ts.clear();
        if (lookupNode != null) {
            for (Integer treeRow : lookupNode.getTreeRowList()) {
                Node node = tree.getNode(treeRow);
                ts.addTuple(m_vis.getVisualItem(treeNodes, node));
            }
        }
        m_vis.cancel("animatePaint");
        m_vis.run("color");
        m_vis.run("animatePaint");
    }

    public void updateFocusTerm(VisualItem item, boolean clear) {
        TupleSet ts = m_vis.getFocusGroup(FOCUS);
        if (clear) {
            ts.clear();
        }
        ts.addTuple(item);
    }

    @SuppressWarnings("unchecked")
    public void updateByPopulation(boolean byPopulation) {
        m_vis.cancel("layout");
        TupleSet ts = m_vis.getVisualGroup(treeNodes);
        Iterator<VisualItem> it = ts.tuples();
        while (it.hasNext()) {
            VisualItem vItem = it.next();
            vItem.setX(0);
            vItem.setY(0);
            vItem.setSize(1);
            vItem.setBounds(0, 0, 0, 0);
        }
        if (byPopulation) {
            areaController.setAreaColumn(GOSchema.TOTAL_COUNT);
        } else {
            areaController.setAreaColumn(GOSchema.STUDY_COUNT);
        }
        m_vis.run("layout");
    }

    public void updateHoverTerm(VisualItem item) {
        TupleSet ts = m_vis.getFocusGroup(HOVER);
        ts.clear();
        ts.addTuple(item);
    }

    @SuppressWarnings("unchecked")
    public void updateEnrichment(GoEnrichment enrichment) {
        if (enrichment == null) {
            for (Iterator<TableTuple> it = graph.getNodes().tuples(); it.hasNext();) {
                TableTuple node = it.next();
                //What to do if study set hasn't been analyzed?
                node.setDouble(GOSchema.RATIO, 0.0);
                node.setDouble(GOSchema.P_VALUE, 1);
                node.setInt(GOSchema.STUDY_COUNT, 0);
            }
        } else {
            HashMap<String, TermCalculation> calcMap = enrichment.getTermCalculationMap();
            //Now set percent change
            for (Iterator<TableTuple> it = graph.getNodes().tuples(); it.hasNext();) {
                TableTuple node = it.next();
                TermCalculation calc = calcMap.get(node.getString(GOSchema.GO_TERM_ID));
                if (calc != null) {
                    double popRatio = (double) calc.getPopTerm() / (double) calc.getPopTotal();
                    double studyRatio = (double) calc.getStudyTerm() / (double) calc.getStudyTotal();
                    double studyToPopRatio = studyRatio / popRatio;
                    node.setDouble(GOSchema.RATIO, studyToPopRatio);
                    node.setDouble(GOSchema.P_VALUE, calc.getpAdjusted());
                    node.setInt(GOSchema.STUDY_COUNT, calc.getStudyTerm());
                } else {
                    //What to do if study set hasn't been analyzed?
                    node.setDouble(GOSchema.RATIO, 0.0);
                    node.setDouble(GOSchema.P_VALUE, 1);
                    node.setInt(GOSchema.STUDY_COUNT, 0);
                }
            }
        }
        graph.firePropertyChange(GOSchema.P_VALUE, null, null);
        m_vis.cancel("animatePaint");
        m_vis.run("color");
        m_vis.run("animatePaint");
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

    public void resizeMe(Dimension dimension) {
        this.setSize(dimension);
        this.setSubLayout(subRoot);
        //m_vis.cancel("layout");
        //m_vis.run("layout");
        //m_vis.repaint();
    }

    public GenoSetsTreeMap createVisualization(int width, int height) {
        m_vis.setVisible(treeEdges, null, false);

        // add labels to the visualization
        // first create a filter to show labels only at top-level nodes
        Predicate labelP = (Predicate) ExpressionParser.parse("treedepth()=1");
        // now create the labels as decorators of the nodes
        m_vis.addDecorators(labels, treeNodes, labelP, LABEL_SCHEMA);

        // set up the renderers - one for nodes and one for labels
        DefaultRendererFactory rf = new DefaultRendererFactory();
        rf.add(new InGroupPredicate(treeNodes), new NodeRenderer());
        rf.add(new InGroupPredicate(labels), new LabelRenderer(GOSchema.NAME));
        m_vis.setRendererFactory(rf);


        TupleSet selectedItems = new DefaultTupleSet();
        m_vis.addFocusGroup(SELECTED, selectedItems);
        TupleSet focusItems = new DefaultTupleSet();
        m_vis.addFocusGroup(FOCUS, focusItems);
        TupleSet hoverItems = new DefaultTupleSet();
        m_vis.addFocusGroup(HOVER, hoverItems);

        // Actions
        final BorderColorAction borderColor = new BorderColorAction(treeNodes);
        // color settings
        ActionList color = new ActionList();
        color.add(new FillColorAction(treeNodes));
        color.add(borderColor);
        m_vis.putAction("color", color);

        // animate paint change
        ActionList animatePaint = new ActionList(400);
        animatePaint.add(new ColorAnimator(treeNodes));
        animatePaint.add(new RepaintAction());
        m_vis.putAction("animatePaint", animatePaint);

        //update interaction
        ActionList interaction = new ActionList();
        interaction.add(new InteractionAction(treeNodes));
        m_vis.putAction("interaction", interaction);

        //Create new TreeMapLayout
        areaController = new TreeMapLayout(graph,
                treeGroup, AREA_COLUMN);
        areaController.setFrameWidth(10);
        areaController.setLayoutRoot(subRoot);
        ActionList layout = new ActionList();
        layout.add(areaController);
        layout.add(new LabelLayout(labels));
        layout.add(color);
        layout.add(interaction);
        layout.add(new RepaintAction());
        m_vis.putAction("layout", layout);

        //Add controls
        addControlListener(new ControlAdapter() {

            @Override
            public void itemEntered(VisualItem item, MouseEvent e) {
                item.setStrokeColor(borderColor.getColor(item));
                item.getVisualization().repaint();
                updateHoverTerm(item);
            }

            @Override
            public void itemExited(VisualItem item, MouseEvent e) {
                item.setStrokeColor(item.getEndStrokeColor());
                item.getVisualization().repaint();
            }
        });

        this.addControlListener(new WheelZoomControl());
        this.addControlListener(new ZoomToFitControl());
        this.addControlListener(new PanControl());

        setItemSorter(new TreeDepthItemSorter(true));
        m_vis.run("layout");

        // initialize our display
        setSize(width, height);
        setHighQuality(true);

        return this;
    }

    public void addTupleSetListener(String focusGroup, TupleSetListener listener) {
        m_vis.getFocusGroup(focusGroup).addTupleSetListener(listener);
    }

    public void removeTupleSetListener(String focusGroup, TupleSetListener listener) {
        m_vis.getFocusGroup(focusGroup).removeTupleSetListener(listener);
    }

    /**
     * ************************************************************
     * Renderers and Actions
     */
    /**
     * A renderer for treemap nodes. Draws simple rectangles, but defers the
     * bounds management to the layout.
     *
     * @author aacain
     */
    public class NodeRenderer extends AbstractShapeRenderer {

        private Rectangle2D m_bounds = new Rectangle2D.Double();

        public NodeRenderer() {
            m_manageBounds = false;
        }

        protected Shape getRawShape(VisualItem item) {
            m_bounds.setRect(item.getBounds());
            return m_bounds;
        }
    }

    public class LabelLayout extends Layout {

        public LabelLayout(String group) {
            super(group);
        }

        public void run(double frac) {
            Iterator iter = m_vis.items(m_group);
            while (iter.hasNext()) {
                DecoratorItem item = (DecoratorItem) iter.next();
                VisualItem node = item.getDecoratedItem();
                Rectangle2D bounds = node.getBounds();
                setX(item, null, bounds.getCenterX());
                setY(item, null, bounds.getCenterY());
            }
        }
    } // end of inner class LabelLayout

    public class BorderColorAction extends ColorAction {

        private ColorMap grayMap = new ColorMap(
                ColorLib.getInterpolatedPalette(5,
                ColorLib.rgb(255, 255, 255), ColorLib.rgb(100, 100, 100)), 0, 4);

        public BorderColorAction(String group) {
            super(group, VisualItem.STROKECOLOR);
        }

        @Override
        public int getColor(VisualItem item) {
            NodeItem nitem = (NodeItem) item;
            if (nitem.isHover()) {
                return ColorLib.rgb(99, 130, 191);
            } else {
                //Get selected change from backing graph
                int sourceRow = item.getInt(DAGTree.SOURCE_NODE_ROW);
                Node node = graph.getNode(sourceRow);
                double selectedChange = node.getDouble(GOSchema.P_VALUE);
                int depth = nitem.getDepth();
//                if (selectedChange <= pValueCutoff) {
//                    return ColorLib.rgb(221, 28, 119);
//                }
//                    if (depth < 1) {
//                        return ColorLib.gray(255);
//                    } else if (depth < 2) {
//                        return ColorLib.gray(200);
//                    } else if (depth < 3) {
//                        return ColorLib.gray(150);
//                    } else if (depth < 4) {
//                        return ColorLib.gray(100);
//                    } else {
//                        return ColorLib.gray(100);
//                    }
                int color = grayMap.getColor(nitem.getDepth());
                return color;
                //return ColorLib.setAlpha(color, 100);
            }
        }
    }

    public class FillColorAction extends ColorAction {

        private ColorMap cmap = new ColorMap(
                ColorLib.getInterpolatedPalette(5,
                ColorLib.rgb(75, 75, 75), ColorLib.rgb(0, 0, 0)), 0, 4);
//        private ColorMap pValueMap = new ColorMap(
//                ColorLib.getInterpolatedPalette(4,
//                ColorLib.rgb(122, 1, 119), ColorLib.rgb(247, 104, 161)), 0, 9);
//        private ColorMap pValueMap = new ColorMap(new int[]{
//                    ColorLib.rgb(252, 197, 192),
//                    ColorLib.rgb(250, 159, 181),
//                    ColorLib.rgb(247, 104, 161),
//                    ColorLib.rgb(221, 52, 151)},
//                0, 10);
        private ColorMap posPValueMap = new ColorMap(new int[]{
            ColorLib.hex("C2A5CF"),        
            ColorLib.hex("9970AB"),
            ColorLib.hex("762A83"),
            ColorLib.hex("40004B")},
//                    ColorLib.rgb(221, 52, 151),
//                    ColorLib.rgb(247, 104, 161),
//                    ColorLib.rgb(250, 159, 181),
//                    ColorLib.rgb(252, 197, 192)},
                0, 10);
        private ColorMap negPValueMap = new ColorMap(new int[]{
                ColorLib.hex("A6DBA0"),
                ColorLib.hex("5AAE61"),
                ColorLib.hex("1B7837"),
                ColorLib.hex("00441B")},
//                    ColorLib.hex("00441B"),
//                    ColorLib.hex("1B7837"),
//                    ColorLib.hex("5AAE61"),
//                    ColorLib.hex("A6DBA0")},
//                    ColorLib.rgb(0, 88, 36),
//                    ColorLib.rgb(35, 139, 69),
//                    ColorLib.rgb(65, 174, 118),
//                    ColorLib.rgb(102, 194, 164)},
                0, 10);

        public FillColorAction(String group) {
            super(group, VisualItem.FILLCOLOR);
        }

        @Override
        public int getColor(VisualItem item) {

            if (item.isInGroup(SELECTED)) {
                //return ColorLib.rgb(90, 180, 172);
                return ColorLib.rgb(251,217,117);
            }
            if (item instanceof NodeItem) {
                NodeItem nitem = (NodeItem) item;
                //get node from backing graph
                int sourceRow = item.getInt(DAGTree.SOURCE_NODE_ROW);
                Node node = graph.getNode(sourceRow);
                if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {
                    //return ColorLib.rgb(49, 130, 189);
                    return ColorLib.rgba(49, 130, 189, 50);
                } else if (node.getBoolean(GOSchema.IS_SELECTED)) {
                    //return ColorLib.rgb(49, 130, 189);
                    return ColorLib.rgba(255, 255, 179, 100);
                }

                double selectedChange = node.getDouble(GOSchema.P_VALUE);
                if (selectedChange <= pValueCutoff) {
                    double ratio = node.getDouble(GOSchema.RATIO);
                    if (ratio < 1) {
                        return negPValueMap.getColor(nitem.getDepth());
                    } else {
                        int colorP = posPValueMap.getColor(nitem.getDepth());
                        return colorP;
                    }
                    //return ColorLib.setAlpha(colorP, 100);
                }
                int color = cmap.getColor(nitem.getDepth());
                return color;
                //return ColorLib.setAlpha(color, 200);
            } else { //not nodeitem
                return cmap.getColor(0);
            }//end if within max depth
        }//end getColor
    } // end of inner class FillColorAction

    public class InteractionAction extends ItemAction {

        public InteractionAction(String group) {
            super(group);
        }

        @Override
        public void process(VisualItem item, double frac) {
            updateInteraction(item);
        }

        public void updateInteraction(VisualItem item) {
            if (item instanceof NodeItem) {
                NodeItem nitem = (NodeItem) item;
                item.setInteractive(true);
            }
        }
    }

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
