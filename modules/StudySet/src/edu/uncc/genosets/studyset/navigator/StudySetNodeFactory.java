/*
 * 
 * 
 */
package edu.uncc.genosets.studyset.navigator;

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.StudySetChangeListener;
import edu.uncc.genosets.studyset.StudySetEvent;
import edu.uncc.genosets.studyset.StudySetManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;

/**
 *
 * @author aacain
 */
public class StudySetNodeFactory extends Children.Keys<StudySet> implements StudySetChangeListener {

    List<StudySet> studySets;
    private static final Comparator<StudySet> comparator = new Comparator<StudySet>() {
        @Override
        public int compare(StudySet o1, StudySet o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
    private final FocusEntity focusEntity;

    public StudySetNodeFactory(FocusEntity focusEntity, List<StudySet> studySets) {
        this.focusEntity = focusEntity;
        this.studySets = studySets;
    }

    @Override
    protected void addNotify() {
        super.addNotify();
        StudySetManager mgr = StudySetManager.StudySetManagerFactory.getDefault();
        mgr.addStudySetChangeListener(WeakListeners.create(StudySetChangeListener.class, this, mgr));
        this.setKeys(studySets);
    }

    @Override
    protected Node[] createNodes(StudySet key) {
        ArrayList<Node> nodeList = new ArrayList<Node>();
        nodeList.add(new StudySetNode(key));
        return nodeList.toArray(new Node[nodeList.size()]);
    }

    @Override
    public void studySetAdded(StudySetEvent evt) {
        if (evt.getStudySet().getFocusEntity().equals(this.focusEntity)) {
            studySets.add(evt.getStudySet());
            Collections.sort(studySets, comparator);
            this.setKeys(studySets);
        }
    }

    @Override
    public void studySetRemoved(StudySetEvent evt) {
        if (evt.getStudySet().getFocusEntity().equals(this.focusEntity)) {
            studySets.remove(evt.getStudySet());
            Collections.sort(studySets, comparator);
            this.setKeys(studySets);
        }
    }

    @Override
    public void selectedStudySetsChanged(StudySetEvent evt) {
    }
}
