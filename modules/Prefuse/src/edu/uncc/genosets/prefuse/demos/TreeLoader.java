/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.prefuse.demos;

    import prefuse.data.Node;
    import prefuse.data.Table;
    import prefuse.data.Tree;
/**
 *
 * @author aacain
 */
public class TreeLoader {

    private Table m_nodes = null;
    private Tree m_tree = null;

    private Node m_activeNode = null;
    private boolean m_inSchema = true;

    public TreeLoader(){
        m_tree = new Tree();
        m_nodes = m_tree.getNodeTable();
    }

    public void createTree(){
        m_nodes.addColumn("column1", java.lang.String.class);
        m_nodes.addColumn("column2", int.class);

        Node root = m_tree.addRoot();
        root.set("column1", "0");
        root.set("column2", 10);
        
        Node n1_1 = m_tree.addChild(root);
        n1_1.set("column1", "1.1");
        n1_1.set("column2", 6);

        Node n1_2 = m_tree.addChild(root);
        n1_2.set("column1", "1.2");
        n1_2.set("column2", 4);

        Node child = m_tree.addChild(n1_1);
        child.set("column1", "1.1.1");
        child.set("column2", 3);

        child = m_tree.addChild(n1_1);
        child.set("column1", "1.1.2");
        child.set("column2", 2);

        child = m_tree.addChild(n1_1);
        child.set("column1", "1.1.3");
        child.set("column2", 1);

        child = m_tree.addChild(n1_2);
        child.set("column1", "1.2");
        child.set("column2", 4);

    }
}
