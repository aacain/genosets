/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer.view;

import edu.uncc.genosets.ontologizer.GoEnrichment;
import edu.uncc.genosets.studyset.StudySet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.openide.explorer.view.CheckableNode;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ChangeSupport;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author aacain
 */
public class StudySetNodeFactory extends Children.Keys<StudySet> {

    private GoEnrichment enrichment;

    @SuppressWarnings("unchecked")
    public StudySetNodeFactory(List<? extends StudySet> studySets) {
        this.setKeys(studySets == null ? (List<? extends StudySet>)Collections.EMPTY_LIST : studySets);
    }

    @SuppressWarnings("unchecked")
    public StudySetNodeFactory(GoEnrichment enrichment) {
        this.enrichment = enrichment;
        this.setKeys(enrichment.getOntologizerParameters().getPopulationSets() == null ? (List<? extends StudySet>)Collections.EMPTY_LIST : enrichment.getOntologizerParameters().getPopulationSets());
    }

    @Override
    protected Node[] createNodes(StudySet key) {
        if (enrichment == null) {
            AbstractNode node = new StudySetNode(key, Children.LEAF);
            return new Node[]{node};
        } else {
            AbstractNode node = new StudySetNode(key, Children.LEAF, Lookups.singleton(enrichment));
            return new Node[]{node};
        }
    }

    static class StudySetNode extends AbstractNode implements CheckableNode {

        private StudySet studySet;
        private boolean selected = false;
        private ChangeSupport cs = new ChangeSupport(this);

        public StudySetNode(StudySet studySet, Children children) {
            super(children);
            this.studySet = studySet;
            this.setDisplayName(studySet.getName());
            this.setIconBaseWithExtension("edu/uncc/genosets/ontologizer/resources/project.png");
        }

        public StudySetNode(StudySet studySet, Children children, Lookup lookup) {
            super(children, lookup);
            this.studySet = studySet;
            this.setDisplayName(studySet.getName());
            this.setIconBaseWithExtension("edu/uncc/genosets/ontologizer/resources/project.png");
        }

        @Override
        public boolean isCheckable() {
            return true;
        }

        @Override
        public boolean isCheckEnabled() {
            return true;
        }

        @Override
        public Boolean isSelected() {
            return selected;
        }

        @Override
        public void setSelected(Boolean selected) {
            boolean old = this.selected;
            this.selected = selected;
            if (old != this.selected) {
                this.cs.fireChange();
            }
        }

        public StudySet getStudySet() {
            return this.studySet;
        }

        public void addChangeListener(ChangeListener l) {
            this.cs.addChangeListener(l);
        }

        public void removeChangeListener(ChangeListener l) {
            this.cs.removeChangeListener(l);
        }
    }
}
