/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.shutdown;


import edu.uncc.genosets.taskmanager.Task;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class TaskChildren extends Children.Array{
    private List<Task> taskList;
    public TaskChildren(List<Task> taskList){
        this.taskList = taskList;
    }

    @Override
    protected Collection<Node> initCollection() {
        Collection<Node> nodeList = new ArrayList(taskList.size());
        for (Task task : taskList) {
            AbstractNode node = new AbstractNode(Children.LEAF);
            node.setName(task.getName());
            nodeList.add(node);
        }
        return nodeList;
    }
}
