/*
 * 
 * 
 */

package edu.uncc.genosets.graphnodeview.view;

import edu.uncc.genosets.treemap.DAGGraph;
import java.util.Iterator;
import prefuse.Constants;
import prefuse.Visualization;
import prefuse.action.GroupAction;
import prefuse.data.Graph;
import prefuse.data.expression.Predicate;
import prefuse.data.tuple.TupleSet;
import prefuse.data.util.BreadthFirstIterator;
import prefuse.data.util.FilterIterator;
import prefuse.util.PrefuseLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

/**
 *
 * @author aacain
 */
public class GraphNodeDistanceFilter extends GroupAction {

    protected int m_distance;
    protected String m_sources;
    protected Predicate m_groupP;
    protected BreadthFirstIterator m_bfs;
    protected NodeItem m_root;

    /**
     * Create a new GraphDistanceFilter that processes the given data group
     * and uses a graph distance of 1. By default, the
     * {@link prefuse.Visualization#FOCUS_ITEMS} group will be used as the
     * source nodes from which to measure the distance.
     * @param group the group to process. This group should resolve to a
     * Graph instance, otherwise exceptions will be thrown when this
     * Action is run.
     */
    public GraphNodeDistanceFilter(String group) {
        this(group, 1);
    }

    /**
     * Create a new GraphDistanceFilter that processes the given data group
     * and uses the given graph distance. By default, the
     * {@link prefuse.Visualization#FOCUS_ITEMS} group will be used as the
     * source nodes from which to measure the distance.
     * @param group the group to process. This group should resolve to a
     * Graph instance, otherwise exceptions will be thrown when this
     * Action is run.
     * @param distance the graph distance within which items will be
     * visible.
     */
    public GraphNodeDistanceFilter(String group, int distance) {
        this(group, Visualization.FOCUS_ITEMS, distance);
    }

    /**
     * Create a new GraphDistanceFilter that processes the given data group
     * and uses the given graph distance.
     * @param group the group to process. This group should resolve to a
     * Graph instance, otherwise exceptions will be thrown when this
     * Action is run.
     * @param sources the group to use as source nodes for measuring
     * graph distance.
     * @param distance the graph distance within which items will be
     * visible.
     */
    public GraphNodeDistanceFilter(String group, String sources, int distance)
    {
        super(group);
        m_sources = sources;
        m_distance = distance;
        m_groupP = new InGroupPredicate(
            PrefuseLib.getGroupName(group, Graph.NODES));
        m_bfs = new BreadthFirstIterator();
    }

    /**
     * Return the graph distance threshold used by this filter.
     * @return the graph distance threshold
     */
    public int getDistance() {
        return m_distance;
    }

    /**
     * Set the graph distance threshold used by this filter.
     * @param distance the graph distance threshold to use
     */
    public void setDistance(int distance) {
        m_distance = distance;
    }

    /**
     * Get the name of the group to use as source nodes for measuring
     * graph distance. These form the roots from which the graph distance
     * is measured.
     * @return the source data group
     */
    public String getSources() {
        return m_sources;
    }

    /**
     * Set the name of the group to use as source nodes for measuring
     * graph distance. These form the roots from which the graph distance
     * is measured.
     * @param sources the source data group
     */
    public void setSources(String sources) {
        m_sources = sources;
    }

    /**
     * @see prefuse.action.GroupAction#run(double)
     */
    @Override
    public void run(double frac) {
        // mark the items
        Iterator items = m_vis.visibleItems(m_group);
        while ( items.hasNext() ) {
            VisualItem item = (VisualItem)items.next();
            item.setDOI(Constants.MINIMUM_DOI);
        }

        // set up the graph traversal
        TupleSet src = m_vis.getFocusGroup(m_sources);
        Iterator srcs = new FilterIterator(src.tuples(), m_groupP);
        m_bfs.init(srcs, m_distance, Constants.NODE_AND_EDGE_TRAVERSAL);

        // traverse the graph
        while ( m_bfs.hasNext() ) {
            VisualItem item = (VisualItem)m_bfs.next();
            int d = m_bfs.getDepth(item);
            PrefuseLib.updateVisible(item, true);
            item.setDOI(-d);
            item.setExpanded(d < m_distance);
            if(item.isExpanded()){
                //get Ancestors

            }
        }

        // mark unreached items
        items = m_vis.visibleItems(m_group);
        while ( items.hasNext() ) {
            VisualItem item = (VisualItem)items.next();
            if ( item.getDOI() == Constants.MINIMUM_DOI ) {
                PrefuseLib.updateVisible(item, false);
                item.setExpanded(false);
            }
        }
    }

    private void visitAncestors(NodeItem n, int distance){
        NodeItem parent = (NodeItem) n.getParent();
        if(parent != null){
            visitFocus(parent, n, distance);
        }
    }

    private void visitFocus(NodeItem n, NodeItem c, int distance){
        visitAncestors(n,distance);
        PrefuseLib.updateVisible(n, true);
        n.setDOI(-distance);
        if(c != null){
            EdgeItem e = (EdgeItem) c.getParentEdge();
            e.setDOI(c.getDOI());
            PrefuseLib.updateVisible(e, true);
        }
    }
}
