/*
 * 
 * 
 */
package edu.uncc.genosets.embl.wizard;

import edu.uncc.genosets.datamanager.embl.EmblProject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class EmblProjectChildren extends Children.Array {

    private final Collection<EmblProject> projList;

    public EmblProjectChildren(Collection<EmblProject> projects) {
        this.projList = projects;
    }

    @Override
    protected Collection<Node> initCollection() {
        List nodeCollection = new ArrayList(projList.size());
        for (EmblProject eproj : projList) {
            EmblProjectNode n = new EmblProjectNode(eproj);
            if(n.isVisible())
                nodeCollection.add(n);
        }
        Collections.sort(nodeCollection);
        return (Collection<Node>)nodeCollection;
    }
}
