/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.prefuse;

import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;


/**
 *
 * @author aacain
 */
public class DAGGraph extends Graph{
    DAGTree treeRep;
    private Node root;
    Schema schema;
    String searchColumn;
    private int totalCount = 0;

    public DAGGraph(){
        super();
    }
    public DAGGraph(Schema schema, String searchColumn){
        super(schema.instantiate(), true);
        this.searchColumn = searchColumn;
        this.schema = schema;
    }
    public DAGTree getTreeRep(){
        if(treeRep == null){
            treeRep = new DAGTree(this,getRoot(), searchColumn, GOSchema.ID);
        }

        return treeRep;
    }
    public void setRoot(Node root){
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
}
