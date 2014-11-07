/*
 * 
 * 
 */

package edu.uncc.genosets.embl.wizard;

import edu.uncc.genosets.datamanager.embl.EmblProject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author aacain
 */
public class EmblProjectNode extends AbstractNode implements Comparable<EmblProjectNode>{

    private boolean visible = true;

    public EmblProjectNode(){
        super(Children.LEAF);
        this.setIconBaseWithExtension("edu/uncc/genosets/emblloader/TextArea_C16.png");
    }
    public EmblProjectNode(EmblProjectNode node){
        this();
        this.setName(node.getName());
        this.setDisplayName(node.getDisplayName());
    }
    public EmblProjectNode(EmblProject proj){
        this();
        this.setName(proj.getAccession());
        this.setDisplayName(proj.getDescription());  
    }

    @Override
    public int compareTo(EmblProjectNode o) {
        return (this.getDisplayName()+""+this.getName()).compareTo(o.getDisplayName() + "" + o.getName());
    }

    public boolean isVisible(){
        return visible;
    }

    public void setVisible(boolean visible){
        this.visible = visible;
    }
}
