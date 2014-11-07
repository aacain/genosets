/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.geneontology.obo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class FilterChildren extends FilterNode.Children {

    private final Set<String> foldersToInclude;

    public FilterChildren(Set<String> foldersToInclude, Node or) {
        super(or);
        this.foldersToInclude = foldersToInclude;
    }

    @Override
    protected Node[] createNodes(Node object) {
        List<Node> result = new ArrayList<Node>();
        for (Node node : super.createNodes(object)) {
            if (accept(node)) {
                result.add(node);
            }
        }
        return result.toArray(new Node[0]);
    }

    private boolean accept(Node node){
        if (foldersToInclude == null){
            return true;
        }
        if(foldersToInclude.contains(node.getDisplayName())){
            return true;
        }
        return false;
    }
}
