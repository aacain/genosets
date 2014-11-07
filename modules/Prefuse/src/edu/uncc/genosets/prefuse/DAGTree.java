/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.prefuse;

import java.util.Iterator;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Tree;
import prefuse.data.Tuple;

import prefuse.data.tuple.TupleManager;
import prefuse.visual.tuple.TableEdgeItem;
import prefuse.visual.tuple.TableNodeItem;

/**
 *
 * @author aacain
 */
public class DAGTree extends Tree{
    static int count;

    /** Extra edge table data field recording the id of the source edge
     * a tree edge represents. */
    public static final String SOURCE_EDGE = "source";
    //Ref column for source node row
    public static final String SOURCE_NODE_ROW = "source_node";

    //Search column - used for search
    public static String searchColumn;
    public static String idColumn;

    /**Schema for the node table*/
    protected static Schema NODE_SCHEMA = new Schema();
    static{
        NODE_SCHEMA.addColumn(SOURCE_NODE_ROW, int.class, new Integer(-1));
    }

    /** Edge table schema used by the tree. */
    protected static Schema EDGE_SCHEMA = new Schema();
    static{
        EDGE_SCHEMA.addColumn(DEFAULT_SOURCE_KEY, int.class, new Integer(-1));
        EDGE_SCHEMA.addColumn(DEFAULT_TARGET_KEY, int.class, new Integer(-1));
    }


    /** A reference to the backing graph that this tree spans. */
    private DAGGraph m_backing;

    /**
     * Create a new SpanningTree.
     * @param g the backing Graph to span
     * @param root the Node to use as the root of the spanning tree
     */
    public DAGTree(DAGGraph g, Node root, String searchColumn, String idColumn) {
        super(NODE_SCHEMA.instantiate(), EDGE_SCHEMA.instantiate());
        m_backing = g;
        DAGTree.searchColumn = searchColumn;
        DAGTree.idColumn = idColumn;
        if(NODE_SCHEMA.getColumnIndex(searchColumn) < 0){ //TODO: this was a quick fix
        	NODE_SCHEMA.addColumn(searchColumn, String.class);
        	NODE_SCHEMA.addColumn(idColumn, int.class);
        	this.getNodeTable().addColumn(DAGTree.searchColumn, String.class);
        	this.getNodeTable().addColumn(DAGTree.idColumn, int.class);
        }
        buildDAGTree(root);

        TupleManager etm = new TupleManager(getEdgeTable(), null, TableEdgeItem.class){
            @Override
            public Tuple getTuple(int row){
                return super.getTuple(row);//m_backing.getEdge(m_table.getInt(row, SOURCE_EDGE));
            }
        };
        getEdgeTable().setTupleManager(etm);

        TupleManager ntm = new TupleManager(getNodeTable(), null, TableNodeItem.class){
            @Override
            public Tuple getTuple(int row){
                return super.getTuple(row);
            }
        };
        getNodeTable().setTupleManager(ntm);
        //super.setTupleManagers(m_backing.m_nodeTuples, etm);
        super.setTupleManagers(ntm, etm);

        /*super(g.getNodeTable(), EDGE_SCHEMA.instantiate());
        m_backing = g;
        TupleManager etm = new TupleManager(getEdgeTable(), null,
        TableEdgeItem.class) {
        @Override
        public Tuple getTuple(int row) {
        return m_backing.getEdge(m_table.getInt(row, SOURCE_EDGE));
        }
        };
        getEdgeTable().setTupleManager(etm);
        super.setTupleManagers(g.m_nodeTuples, etm);
        buildDAGTree(root);*/
    }

    /**
     * Build the DAG tree, starting at the given root. Duplicates
     * each 
     * @param root the root node of the spanning tree
     */
    public void buildDAGTree(final Node root) {

        //re-use previous tree
        super.clear();
        Node newRoot = this.addRoot();
        newRoot.set(SOURCE_NODE_ROW, root.getRow());
        String s = (String) root.get(searchColumn);
        int id = root.getInt(idColumn);
        newRoot.set(searchColumn, s);
        newRoot.set(idColumn, id);
        System.out.println("Added column " + idColumn);
        addChildNodes(root, newRoot);
    }
    protected void addChildNodes(Node backingNode, Node newNode){
        Iterator<Edge> it = getBackingGraph().outEdges(backingNode);
        while(it.hasNext()){
            Edge e = it.next();
            Node backingChild = e.getTargetNode();
            Node child = this.addChild(newNode);
            child.set(SOURCE_NODE_ROW, backingChild.getRow());
            child.set(searchColumn, backingChild.get(searchColumn));
            child.set(idColumn, backingChild.getInt(idColumn));
            addChildNodes(backingChild, child);
        }
    }

    /**
     * @return the m_backing
     */
    public Graph getBackingGraph() {
        return m_backing;
    }
}
