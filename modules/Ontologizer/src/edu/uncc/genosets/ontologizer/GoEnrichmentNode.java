/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.ontologizer;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author aacain
 */
public class GoEnrichmentNode extends AbstractNode{

    public GoEnrichmentNode(GoEnrichment obj, Children ch, Lookup lookup) {
        super(ch, lookup);
    }
}
