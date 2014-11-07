/*
 * 
 * 
 */
package edu.uncc.genosets.gotermclusterview;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.studyset.GoTerm;
import edu.uncc.genosets.studyset.TermCalculation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author aacain
 */
public class ClusterNodeFactory extends ChildFactory<FeatureCluster> {
    private GoTerm term;
    private HashMap<FeatureCluster, Set<Integer>> clusterToFeatureMap = new HashMap<FeatureCluster, Set<Integer>>();
    private final TermCalculation termCalc;

    public ClusterNodeFactory(GoTerm term, TermCalculation calc) {
        this.term = term;
        this.termCalc = calc;
    }

    @Override
    protected boolean createKeys(List<FeatureCluster> toPopulate) {
        if (termCalc != null && termCalc.getFeatureIds() != null && !termCalc.getFeatureIds().isEmpty()) {
            QueryBuilder bldr = new QueryBuilder();
            this.clusterToFeatureMap = bldr.query(new HashSet<Integer>(termCalc.getFeatureIds()));
            toPopulate.addAll(this.clusterToFeatureMap.keySet());
        }

        return true;
    }

    @Override
    protected Node[] createNodesForKey(FeatureCluster key) {
        Set<Integer> fIds = this.clusterToFeatureMap.get(key);
        Children children = null;
        if (fIds == null) {
            children = Children.LEAF;
        } else {
            children = Children.create(new GoTermNodeFactory(this.term, fIds), true);
        }
        AbstractNode node = new AbstractNode(children);
        node.setDisplayName("Ortholog Cluster " + key.getFeatureClusterId());
        return new Node[]{node};
    }

    private static class QueryBuilder implements QueryCreator {

        public HashMap<FeatureCluster, Set<Integer>> query(Set<Integer> featureIds) {
            HashMap<FeatureCluster, Set<Integer>> clusterToFeatureMap = new HashMap<FeatureCluster, Set<Integer>>();
            StringBuilder bldr = new StringBuilder("SELECT oFact.featureCluster, oFact.featureId FROM OrthoFact as oFact where oFact.featureId in ( ");
            for (Integer featureId : featureIds) {
                bldr.append(featureId).append(",");
            }
            //remove last comma
            bldr.deleteCharAt(bldr.length() - 1);
            bldr.append(")");
            List<? extends Object[]> features = DataManager.getDefault().createQuery(bldr.toString(), Object[].class);
            for (Object[] objects : features) {
                FeatureCluster clust = (FeatureCluster) objects[0];
                Set<Integer> fIds = clusterToFeatureMap.get(clust);
                if (fIds == null) {
                    fIds = new HashSet<Integer>();
                    clusterToFeatureMap.put(clust, fIds);
                }
                fIds.add((Integer) objects[1]);
            }
            return clusterToFeatureMap;
        }
    }
}
