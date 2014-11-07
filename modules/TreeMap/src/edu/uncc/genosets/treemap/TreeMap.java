package edu.uncc.genosets.treemap;

import edu.uncc.genosets.ontologizer.GoEnrichment;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.TermCalculation;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;

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
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Tree;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.query.RangeQueryBinding;
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.tuple.TableTuple;
import prefuse.data.tuple.TupleSet;
import prefuse.data.util.Index;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.ColorMap;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTree;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;

/**
 * Demonstration showcasing a TreeMap layout of a hierarchical data
 * set and the use of dynamic query binding for text search. Animation
 * is used to highlight changing search results.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class TreeMap extends Display {

    /*commented this*///	List<SelectedDimListener> listenerList = new LinkedList();
    //variables
    int[] pos = {
        ColorLib.rgb(224, 243, 248),
        ColorLib.rgb(171, 217, 233),
        ColorLib.rgb(116, 173, 209),
        ColorLib.rgb(69, 117, 180),
        ColorLib.rgb(49, 54, 149)
    };
    int[] neg = {
        ColorLib.rgb(254, 224, 144),
        ColorLib.rgb(253, 174, 97),
        ColorLib.rgb(244, 109, 67),
        ColorLib.rgb(215, 48, 39),
        ColorLib.rgb(165, 0, 38)
    };
    private ColorMap positiveColorMap = new ColorMap(pos, -3, 0);
    private ColorMap negativeColorMap = new ColorMap(neg, -3, 0);
    private double pValue = 0.1000;
    int[] green = {
        ColorLib.rgb(0, 90, 50),
        ColorLib.rgb(35, 132, 67),
        ColorLib.rgb(65, 171, 93),
        ColorLib.rgb(120, 198, 121)
    };
    private ColorMap greenCMap = new ColorMap(green, 0, pValue);
    double minLog = 0.0;
    double maxLog = -100;
    private static String AREA_COLUMN = GOSchema.TOTAL_COUNT;
    // create data description of labels, setting colors, fonts ahead of time
    private static final Schema LABEL_SCHEMA = PrefuseLib.getVisualItemSchema();

    static {
        LABEL_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
        LABEL_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(150));
        LABEL_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", 16));
    }
    private static final String treeGroup = "tree";
    private static final String treeNodes = "tree.nodes";
    private static final String treeEdges = "tree.edges";
    private static final String labels = "labels";
    public static final String GRAPH_VISIBLE = "graphVisible";
    private TreeMapLayout areaController;
    private DAGGraph graph;
    private Tree tree;
    private JPanel panel;
    private int queryCount;
    Index idIndex;
    private boolean canSpawn = true;
    private List<GOTreeView> spawnedFrames = new LinkedList<GOTreeView>();
    private NodeItem subRoot;
    private int maxDepth = 0;
    private SearchQueryBinding searchQ;
    private RangeQueryBinding depthQ;
    private int rootGoId = -1;

    public TreeMap(DAGGraph graph) {
        super(new Visualization());
        this.graph = graph;
        idIndex = graph.getNodeTable().index(GOSchema.GO_TERM_ID);
        idIndex.index();
        tree = graph.getTreeRep();
        subRoot = null;
        graph.addColumn(GRAPH_VISIBLE, boolean.class, true);
        tree.addColumn(GRAPH_VISIBLE, boolean.class, true);
    }

    public TreeMap(DAGGraph graph, String areaColumn) {
        this(graph);
        AREA_COLUMN = areaColumn;
    }

    public TreeMap(DAGGraph graph, NodeItem n) {
        this(graph);
        canSpawn = false;
        subRoot = n;
    }

    public TreeMap(DAGGraph graph, String areaColumn, String subroot) {
        this(graph, areaColumn);
        int row = idIndex.get(subroot);
        Node node = graph.getNode(row);
        rootGoId = node.getInt(GOSchema.ID);
    }

    public TreeMap(DAGGraph graph, int referenceTotalCount) {
        this(graph);
        this.queryCount = referenceTotalCount;
    }

    public TreeMap(DAGGraph graph, int referenceTotalCount, String areaColumn) {
        this(graph, areaColumn);
        this.queryCount = referenceTotalCount;
    }

    public DAGGraph getGraph() {
        return this.graph;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }

    public SearchQueryBinding getSearchQuery() {
        return searchQ;
    }

    public RangeQueryBinding getDepthQuery() {
        return depthQ;
    }

    /**
     * @return the panel
     */
    public JPanel getPanel() {
        return panel;
    }

    /**
     * @return the canSpawn
     */
    public boolean isCanSpawn() {
        return canSpawn;
    }

    /**
     * @param canSpawn the canSpawn to set
     */
    public void setCanSpawn(boolean canSpawn) {
        this.canSpawn = canSpawn;
    }

    //-------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public TreeMap createVisualiztion(int width, int height) {

        // add the tree to the visualization
        VisualTree vt = m_vis.addTree(treeGroup, tree);
        m_vis.setVisible(treeEdges, null, false);

        vt.addColumn("depth", "treedepth()");
        vt.addColumn("hasChild", "childcount() = 0");
        depthQ = new RangeQueryBinding(vt, "depth", false);

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

        TreeDepthItemSorter sorter = new TreeDepthItemSorter(true);
        setItemSorter(sorter);


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
        areaController.setFrameWidth(5);
        if (rootGoId >= 0) {
            TupleSet set = m_vis.getVisualGroup(treeNodes);
            set.tuples();
            for (Iterator<NodeItem> mIt = set.tuples(); mIt.hasNext();) {
                NodeItem t = mIt.next();
                int goId = t.getInt(GOSchema.ID);
                if (rootGoId == goId) {
                    subRoot = t;
                }
            }
            areaController.setLayoutRoot(subRoot);
        } else if (subRoot != null) {
            areaController.setLayoutRoot(subRoot);
        }

        // create the single filtering and layout action list
        ActionList layout = new ActionList();
        layout.add(areaController);
        layout.add(new LabelLayout(labels));
        layout.add(color);
        layout.add(interaction);
        layout.add(new RepaintAction());
        m_vis.putAction("layout", layout);

        ActionList clear = new ActionList();
        clear.add(new ClearColorBorder(treeNodes));
        clear.add(new ClearColorFill(treeNodes));
        clear.add(new RepaintAction());
        m_vis.putAction("clear", clear);

        // initialize our display
        setSize(width, height);
        setHighQuality(true);
        addControlListener(new ZoomControl());
        //setItemSorter(new TreeDepthItemSorter());
        addControlListener(new ControlAdapter() {

            @Override
            public void itemEntered(VisualItem item, MouseEvent e) {
                item.setStrokeColor(borderColor.getColor(item));
                item.getVisualization().repaint();
            }

            @Override
            public void itemExited(VisualItem item, MouseEvent e) {
                item.setStrokeColor(item.getEndStrokeColor());
                item.getVisualization().repaint();
            }
        });

        searchQ = new SearchQueryBinding(vt.getNodeTable(), GOSchema.NAME);
        m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, searchQ.getSearchSet());
        searchQ.getPredicate().addExpressionListener(new UpdateListener() {

            public void update(Object src) {
                m_vis.cancel("animatePaint");
                m_vis.run("color");
                m_vis.run("animatePaint");
            }
        });

        this.addControlListener(new WheelZoomControl());
        this.addControlListener(new ZoomToFitControl());
        this.addControlListener(new PanControl());

        // perform layout
        m_vis.run("layout");
        createPanel();
        return this;
    }

    @SuppressWarnings("unchecked")
    public void setSubLayout(VisualItem item) {
        TupleSet ts = m_vis.getVisualGroup(treeNodes);
        Iterator<VisualItem> it = ts.tuples();
        while (it.hasNext()) {
            VisualItem vItem = it.next();
            vItem.setX(0);
            vItem.setY(0);
            vItem.setSize(1);
            vItem.setBounds(0, 0, 0, 0);
        }
        m_vis.removeAction("layout");
        final BorderColorAction borderColor = new BorderColorAction(treeNodes);
        // color settings
        ActionList color = new ActionList();
        color.add(new FillColorAction(treeNodes));
        color.add(borderColor);
        m_vis.putAction("color", color);

        ActionList layout = new ActionList();
        layout.add(areaController);
        layout.add(new LabelLayout(labels));
        layout.add(color);
        layout.add(new RepaintAction());
        m_vis.putAction("layout", layout);
        areaController.setLayoutRoot((NodeItem) item);
        m_vis.run("layout");
    }

    private TreeMap changeSize(int width, int height) {
        // perform layout
        setSize(width, height);
        m_vis.run("layout");
        createPanel();
        return this;
    }

    private void createPanel() {
        final TreeMap me = this;

        final JFastLabel title = new JFastLabel("                 ");
        title.setPreferredSize(new Dimension(450, 20));
        title.setVerticalAlignment(SwingConstants.BOTTOM);
        title.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
        title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));

        this.addControlListener(new ControlAdapter() {

            @Override
            public void itemEntered(VisualItem item, MouseEvent e) {
                NodeItem n = (NodeItem) item;
                //Lookup backingrow
                int sourceNodeRow = n.getInt(DAGTree.SOURCE_NODE_ROW);
                String selectedCount = graph.getNode(sourceNodeRow).getString(GOSchema.STUDY_COUNT);
                title.setText(item.getString(GOSchema.NAME) + " Selected: " + selectedCount + " Depth: " + item.get("depth"));
            }

            public void recursiveInEdges(Node target, Node newTarget, Graph g) {
                for (Iterator it = target.inEdges(); it.hasNext();) {
                    Edge edge = (Edge) it.next();
                    Node parent = edge.getSourceNode();
                    Node newParent = g.addNode();
                    Node graphNode = graph.getNode(parent.getInt(DAGTree.SOURCE_NODE_ROW));
                    newParent.setString("label", graphNode.getString(GOSchema.NAME).
                            concat(" (" + graphNode.getInt(GOSchema.STUDY_COUNT) + ")"));
                    g.addEdge(newParent, newTarget);
                    recursiveInEdges(parent, newParent, g);
                }
                return;
            }

            public void recursiveOutEdges(Node source, Node newSource, Graph g) {
                for (Iterator it = source.outEdges(); it.hasNext();) {
                    Edge edge = (Edge) it.next();
                    Node child = edge.getTargetNode();
                    Node newChild = g.addNode();
                    Node graphNode = graph.getNode(child.getInt(DAGTree.SOURCE_NODE_ROW));
                    newChild.setString("label", graphNode.getString(GOSchema.NAME).
                            concat(" (" + graphNode.getInt(GOSchema.STUDY_COUNT) + ")"));
                    g.addEdge(newSource, newChild);
                    recursiveOutEdges(child, newChild, g);
                }
                return;
            }

            @Override
            public void itemClicked(VisualItem item, MouseEvent e) {
                if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK) {
                    NodeItem n = (NodeItem) item;
                    Node graphNode = graph.getNode(n.getInt(DAGTree.SOURCE_NODE_ROW));
                    int goTermId = graphNode.getInt(GOSchema.GO_TERM_ID);
                    GenoSetsPopup popup = new GenoSetsPopup(goTermId);
                    popup.show(e.getComponent(), e.getX(), e.getY());

                } else if (e.getClickCount() >= 2) { //show in tree view
                    NodeItem n = (NodeItem) item;
                    Node graphNode = graph.getNode(n.getInt(DAGTree.SOURCE_NODE_ROW));
                    int goTermId = graphNode.getInt(GOSchema.GO_TERM_ID);
                    GOTreeView termGraph = new GOTreeView(me, graph, GOSchema.NAME, null, goTermId);
                    //GOTermGraphPanel termGraph = new GOTermGraphPanel(graph, "graph", p);
                    //RadialGoGraph termGraph = new RadialGoGraph(graph, GOSchema.GO_TERM_ID, p);
                    System.out.println("Trying to create termGraph");
                    JFrame frame = new JFrame();
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setContentPane(termGraph);
                    frame.pack();
                    frame.setVisible(true);
                    spawnedFrames.add(termGraph);
                } else {
                    NodeItem n = (NodeItem) item;
                    Node graphNode = graph.getNode(n.getInt(DAGTree.SOURCE_NODE_ROW));
                    int goTermId = graphNode.getInt(GOSchema.GO_TERM_ID);
                    for (GOTreeView tv : spawnedFrames) {
                        tv.updateSelectedItem(goTermId);
                    }
                }
            }
        });

        //Box box = UILib.getBox(new Component[]{depthList,search, referenceBox}, true, 10, 3, 5);

        JPanel settingsPanel = createSettingsPanel();
        panel = new JPanel(new BorderLayout());
        panel.add(this, BorderLayout.CENTER);
        settingsPanel.setBackground(Color.RED);
        panel.add(settingsPanel, BorderLayout.SOUTH);
        panel.add(title, BorderLayout.NORTH);
        UILib.setColor(panel, Color.WHITE, Color.BLACK);
    }

    public JPanel getSettingPanel() {
        return createSettingsPanel();
    }

    public void setDepth(int depth) {
        maxDepth = maxDepth + depth;
        if (maxDepth < 0) {
            maxDepth = 0;
        }
        m_vis.run("interaction");
        m_vis.cancel("animatePaint");
        m_vis.run("color");
        m_vis.run("animatePaint");
    }

    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));

        Box settingsBox = new Box(BoxLayout.Y_AXIS);
        settingsBox.setBorder(BorderFactory.createTitledBorder("Settings"));
        settingsBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel depthPanel = new JPanel();
        depthPanel.setLayout(new BoxLayout(depthPanel, BoxLayout.Y_AXIS));
        JLabel depthLabel = new JLabel("Max Tree Depth:");
        depthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

//        final JSlider depthList = depthQ.createSlider();
//        depthList.setAlignmentX(Component.LEFT_ALIGNMENT);
//        depthList.setSelectedIndex(depthList.getItemCount() - 1);
//        depthList.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                Object obj = depthList.getSelectedItem();
//                if (obj instanceof String) {
//                    maxDepth = 100;
//                } else {
//                    maxDepth = (Integer) depthList.getSelectedItem();
//                }
//                m_vis.run("interaction");
//                m_vis.cancel("animatePaint");
//                m_vis.run("color");
//                m_vis.run("animatePaint");
//            }
//        });
//        depthList.setMaximumSize(new Dimension(400, 100));
//        depthList.setPreferredSize(new Dimension(200, 20));
//        depthList.setAlignmentX(Component.LEFT_ALIGNMENT);
//        depthPanel.add(depthLabel);
//        depthPanel.add(depthList);

        settingsBox.add(depthPanel);


        JSearchPanel search = searchQ.createSearchPanel();
        search.setShowResultCount(true);
        search.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
        search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
        search.setAlignmentX(Component.LEFT_ALIGNMENT);

        settingsPanel.add(search);
        settingsPanel.add(settingsBox);


        return settingsPanel;
    }

    @SuppressWarnings("unchecked")
    private void updateSelectedSet(GoEnrichment enrichment) {
        HashMap<String, TermCalculation> calcMap = enrichment.getTermCalculationMap();
        //Now set percent change
        for (Iterator<TableTuple> it = graph.getNodes().tuples(); it.hasNext();) {
            TableTuple node = it.next();
            TermCalculation calc = calcMap.get(node.getString(GOSchema.GO_TERM_ID));
            if (calc != null) {
                node.setDouble(GOSchema.P_VALUE, calc.getpAdjusted());
            } else {
                //What to do if study set hasn't been analyzed?
                node.setDouble(GOSchema.P_VALUE, 1);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void updateSelectedTerm(String goId) {
        //Now set percent change
        for (Iterator<TableTuple> it = graph.getNodes().tuples(); it.hasNext();) {
            TableTuple node = it.next();
            String goTermId = node.getString(GOSchema.GO_TERM_ID);
            if (goTermId.equals(goId)) {
                //node.set
                node.setBoolean(GOSchema.IS_SELECTED, Boolean.TRUE);
            } else {
                node.setBoolean(GOSchema.IS_SELECTED, Boolean.FALSE);
            }
        }


        m_vis.cancel("animatePaint");
        m_vis.run("color");
        m_vis.run("animatePaint");
    }

    public void setDuplicates(NodeItem node) {
        //setOutnodesInactive(node);
    }

    @SuppressWarnings("unchecked")
    private void setOutnodesInactive(Node node) {
        for (Iterator<Edge> it = node.outEdges(); it.hasNext();) {
            Edge edge = it.next();
            Node target = edge.getTargetNode();
        }
    }

    public void updateVis(GoEnrichment enrichment) {
        this.queryCount = queryCount;
        updateSelectedSet(enrichment);
        m_vis.cancel("animatePaint");
        m_vis.run("color");
        m_vis.run("animatePaint");
    }

    void setGoEnrichment(GoEnrichment enrichment) {
        updateVis(enrichment);
    }

    public void updatePValueCutoff(double pValue) {
        this.pValue = pValue;
        greenCMap.setMaxValue(pValue);
        m_vis.cancel("animatePaint");
        m_vis.run("color");
        m_vis.run("animatePaint");
    }

    // ------------------------------------------------------------------------
    /**
     * Set the stroke color for drawing treemap node outlines. A graded
     * grayscale ramp is used, with higer nodes in the tree drawn in
     * lighter shades of gray.
     */
    public class BorderColorAction2 extends ColorAction {

        public BorderColorAction2(String group) {
            super(group, VisualItem.STROKECOLOR);
        }

        @Override
        public int getColor(VisualItem item) {
            NodeItem nitem = (NodeItem) item;
            if (nitem.isHover()) {
                return ColorLib.rgb(99, 130, 191);
            } else {
                int depth = nitem.getDepth();
                //Get selected change from backing graph
                int sourceRow = item.getInt(DAGTree.SOURCE_NODE_ROW);
                Node node = graph.getNode(sourceRow);
                double selectedChange = node.getDouble(GOSchema.P_VALUE);
                //see if the depth is good
                if (nitem.getDepth() == maxDepth || (nitem.getDepth() < maxDepth && nitem.getChildCount() == 0)) {
                    if (selectedChange <= pValue) {
                        return ColorLib.rgb(221, 28, 119);
                    }
                    if (depth < 1) {
                        return ColorLib.gray(255);
                    } else if (depth < 2) {
                        return ColorLib.gray(200);
                    } else if (depth < 3) {
                        return ColorLib.gray(150);
                    } else if (depth < 4) {
                        return ColorLib.gray(100);
                    } else {
                        return ColorLib.gray(100);
                    }
                } else { //we are not at max depth
                    if (nitem.getDepth() > maxDepth && selectedChange <= pValue) {
                        return ColorLib.rgb(221, 28, 119);
                    }
                    return 0;
                }
            }
        }
    }

    /**
     * Set fill colors for treemap nodes. Search items are colored
     * in pink, while normal nodes are shaded according to their
     * depth in the tree.
     */
    public class FillColorAction2 extends ColorAction {

        private ColorMap cmap = new ColorMap(
                ColorLib.getInterpolatedPalette(10,
                ColorLib.rgb(255, 255, 255), ColorLib.rgb(100, 100, 100)), 0, 9);

        public FillColorAction2(String group) {
            super(group, VisualItem.FILLCOLOR);
        }

        @Override
        public int getColor(VisualItem item) {
            if (item instanceof NodeItem) {
                NodeItem nitem = (NodeItem) item;
                //get node from backing graph
                int sourceRow = item.getInt(DAGTree.SOURCE_NODE_ROW);
                Node node = graph.getNode(sourceRow);
                if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {
                    return ColorLib.rgba(49, 130, 189, 50);
                } else if (node.getBoolean(GOSchema.IS_SELECTED)) {
                    return ColorLib.rgba(49, 130, 189, 100);
                }
                double selectedChange = node.getDouble(GOSchema.P_VALUE);
                if (nitem.getDepth() == maxDepth
                        || (nitem.getDepth() < maxDepth
                        && nitem.getChildCount() == 0)) {
                    if (selectedChange <= pValue) {
                        return ColorLib.rgba(161, 215, 106, 100);
                        //return greenCMap.getColor(selectedChange);
                    } else { //Not search highlighted or selected
                        //set translucent
                        int color = cmap.getColor(nitem.getDepth());
                        return ColorLib.setAlpha(color, 100);
                        //return cmap.getColor(nitem.getDepth());
                    }
                } else {
                    return 0;
                }

            } else { //not nodeitem
                return cmap.getColor(0);
            }//end if within max depth
        }//end getColor
    } // end of inner class FillColorAction

    public class BorderColorAction extends ColorAction {

        private ColorMap cmap = new ColorMap(
                ColorLib.getInterpolatedPalette(10,
                ColorLib.rgb(255, 255, 255), ColorLib.rgb(100, 100, 100)), 0, 9);

        public BorderColorAction(String group) {
            super(group, VisualItem.STROKECOLOR);
        }

        @Override
        public int getColor(VisualItem item) {
            NodeItem nitem = (NodeItem) item;
            if (nitem.isHover()) {
                return ColorLib.rgb(99, 130, 191);
            } else {
                int depth = nitem.getDepth();
                //Get selected change from backing graph
                int sourceRow = item.getInt(DAGTree.SOURCE_NODE_ROW);
                Node node = graph.getNode(sourceRow);
                double selectedChange = node.getDouble(GOSchema.P_VALUE);
                int color = cmap.getColor(nitem.getDepth());
                return color;
                //return ColorLib.setAlpha(color, 100);
            }
        }
    }

    public class FillColorAction extends ColorAction {

        private ColorMap cmap = new ColorMap(
                ColorLib.getInterpolatedPalette(10,
                ColorLib.rgb(75, 75, 75), ColorLib.rgb(0, 0, 0)), 0, 9);
        private ColorMap pValueMap = new ColorMap(
                ColorLib.getInterpolatedPalette(10,
                ColorLib.rgb(247, 104, 161), ColorLib.rgb(122, 1, 119)), 0, 9);

        public FillColorAction(String group) {
            super(group, VisualItem.FILLCOLOR);
        }

        @Override
        public int getColor(VisualItem item) {
            if (item instanceof NodeItem) {
                NodeItem nitem = (NodeItem) item;
                //get node from backing graph
                int sourceRow = item.getInt(DAGTree.SOURCE_NODE_ROW);
                Node node = graph.getNode(sourceRow);
                if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {
                    return ColorLib.rgb(49, 130, 189);
                    //return ColorLib.rgba(49, 130, 189, 50);
                } else if (node.getBoolean(GOSchema.IS_SELECTED)) {
                    return ColorLib.rgb(49, 130, 189);
                    //return ColorLib.rgba(49, 130, 189, 100);
                }

                double selectedChange = node.getDouble(GOSchema.P_VALUE);
                if (selectedChange <= pValue) {
                    int colorP = pValueMap.getColor(nitem.getDepth());
                    return colorP;
                    //return ColorLib.setAlpha(colorP, 100);
                }
                int color = cmap.getColor(nitem.getDepth());
                return color;
                //return ColorLib.setAlpha(color, 100);
            } else { //not nodeitem
                return cmap.getColor(0);
            }//end if within max depth
        }//end getColor
    } // end of inner class FillColorAction

    public class ClearColorBorder extends ColorAction {

        public ClearColorBorder(String group) {
            super(group, VisualItem.STROKECOLOR);
        }

        @Override
        public int getColor(VisualItem item) {
            return ColorLib.rgb(255, 255, 255);
        }
    }

    public class ClearColorFill extends ColorAction {

        public ClearColorFill(String group) {
            super(group, VisualItem.FILLCOLOR);
        }

        @Override
        public int getColor(VisualItem item) {
            return ColorLib.rgb(255, 255, 255);
        }
    }

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
//                if (nitem.getDepth() == maxDepth || (nitem.getDepth() < maxDepth && nitem.getChildCount() == 0)) //Set interaction for all of these
//                {
//                    item.setInteractive(true);
//                } else {
//                    item.setInteractive(false);
//                }
            }
        }
    }

    /**
     * Set label positions. Labels are assumed to be DecoratorItem instances,
     * decorating their respective nodes. The layout simply gets the bounds
     * of the decorated node and assigns the label coordinates to the center
     * of those bounds.
     */
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

//-------------------------------------------------------------------------
    /**
     * A renderer for treemap nodes. Draws simple rectangles, but defers
     * the bounds management to the layout.
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
    } // end of inner class NodeRenderer

//----------------------------------------------------------------------------
    public class GenoSetsPopup extends JPopupMenu {

        int goTermId;
        JMenuItem featureItem;
        JMenuItem hierItem;

        public GenoSetsPopup(int goTermId) {
            this.goTermId = goTermId;
            init();
        }

        private void init() {
            featureItem = new JMenuItem("View feature details");
            featureItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    featureItemClicked(e);

                }
            });
            this.add(featureItem);
            hierItem = new JMenuItem("View in tree");
            hierItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    hierItemClicked(e);
                }
            });
            //this.add(hierItem);
        }

        private void featureItemClicked(ActionEvent evt) {
            System.out.println("Selected Feature");
            /*Commented this*/ //          createFeatureTable(goTermId);
        }

        private void hierItemClicked(ActionEvent evt) {
        }
    }
//end of inner class GenoSetsPopup
} // end of class TreeMap

