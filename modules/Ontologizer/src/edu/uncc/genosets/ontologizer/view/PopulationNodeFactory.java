/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer.view;

import edu.uncc.genosets.ontologizer.GoEnrichment;
import edu.uncc.genosets.studyset.StudySet;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
@Deprecated
public class PopulationNodeFactory extends ChildFactory<StudySet> {

    private final GoEnrichment enrichment;

    public PopulationNodeFactory(GoEnrichment enrichment) {
        this.enrichment = enrichment;
    }

    @Override
    protected boolean createKeys(List<StudySet> toPopulate) {
        if (enrichment != null) {

        }
        return true;
    }

    @Override
    protected Node createNodeForKey(StudySet key) {
        return super.createNodeForKey(key);
    }

    public static class PopulationNode extends AbstractNode {

        public PopulationNode(GoEnrichment enrichment, StudySet popSet) {
            super(Children.LEAF, Lookups.fixed(enrichment));
            this.setName(popSet.getUniqueName());
            this.setDisplayName(popSet.getName());
            this.setIconBaseWithExtension("edu/uncc/genosets/ontologizer/resources/project.png");
        }
    }
}
