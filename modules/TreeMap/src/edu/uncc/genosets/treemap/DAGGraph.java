/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.treemap;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Table;

/**
 *
 * @author aacain
 */
public class DAGGraph extends Graph {

    DAGTree treeRep;
    private Node root;
    Schema schema;
    String searchColumn;
    private int totalCount = 0;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public DAGGraph() {
        super();
    }

    public DAGGraph(Schema schema, String searchColumn) {
        super(schema.instantiate(), true);
        this.searchColumn = searchColumn;
        this.schema = schema;
    }

    public DAGGraph(Table nodeTable, Table edgeTable, boolean directed, String nodeKey, String sourceKey, String targetKey, String searchColumn) {
        super(nodeTable, edgeTable, directed, nodeKey, sourceKey, targetKey);
        this.searchColumn = searchColumn;
    }

    public DAGTree getTreeRep() {
        if (treeRep == null) {
            treeRep = new DAGTree(this, getRoot(), searchColumn, GOSchema.ID);
        }

        return treeRep;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    /**
     * @return the totalCount
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount the totalCount to set
     */
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the root
     */
    public Node getRoot() {
        return root;
    }

    public void addPropChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public void firePropertyChange(String property, Object oldValue, Object newValue) {
        this.pcs.firePropertyChange(property, oldValue, newValue);
    }
}
