/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.pathway.view;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.studyset.StudySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author lucy
 */
public class PathwayNodeFactory extends ChildFactory.Detachable<Object[]> {
    private final StudySet studySet;
    
    public PathwayNodeFactory(StudySet studySet) {
        this.studySet = studySet;
    }
    
    @Override
    protected boolean createKeys(List<Object[]> toPopulate) {
        if (studySet.getIdSet().size() > 0) {
            toPopulate.addAll(PathwayQuery.getPathways(studySet));
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Object[] key) {
        return new FeatureClusterNode(studySet, (FeatureCluster)key[0], (Long)key[1]);
    }
    
    private static class PathwayQuery implements QueryCreator{
        static List<Object[]> getPathways(StudySet studySet){
                        StringBuilder bldr = new StringBuilder("SELECT fc, COUNT(fact.featureId) FROM ClusterPathway as fc, PathwayFact as fact where fc.featureClusterId = fact.featureClusterId AND fact.featureId IN (");
            int i = 0;
            for (Integer id : studySet.getIdSet()) {
                if (i > 0) {
                    bldr.append(", ");
                }
                bldr.append(id);
                i++;
            }
            bldr.append(") ");
            bldr.append(" GROUP BY fact.featureClusterId ");
            return DataManager.getDefault().createQuery(bldr.toString());
        }
    }
    
    private static class FeatureClusterNode extends AbstractNode{
        private static List<? extends Action> registeredActions;

        public FeatureClusterNode(StudySet studySet, FeatureCluster featureCluster, Long count) {
            super(Children.create(new FeatureNodeFactory(studySet, featureCluster), true), Lookups.singleton(featureCluster));
            this.setName((String)featureCluster.getCustomProperties().get("pathwayName") + " (" + count.toString() + ")");
            this.setIconBaseWithExtension("edu/uncc/genosets/pathway/resources/call_graph.png");
        }

        protected static List<? extends Action> getRegisterActions() {
            if (registeredActions == null) {
                registeredActions = Utilities.actionsForPath("ClusterPathway/Nodes/Actions");
            }
            return registeredActions;
        }

        @Override
        public Action[] getActions(boolean context) {
            List<Action> actions = new ArrayList<Action>();
            actions.addAll(getRegisterActions());
            actions.addAll(Arrays.asList(super.getActions(context)));
            return actions.toArray(new Action[actions.size()]);
        }
    }
    
    protected static class FeatureClusterCount {
        FeatureCluster fc;
        int featureCount;
        FeatureClusterCount(FeatureCluster featureCluster, int count){
            this.fc = featureCluster;
            this.featureCount = count;
        }
    }
}
