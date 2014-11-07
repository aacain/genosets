/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.persister;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.ClusterGraph;
import org.hibernate.StatelessSession;

/**
 * To use this persister, you should set all the values that
 * you know using the setter methods.
 * The fact can be just an empty fact and the id's will be set later.
 *
 * @author aacain
 */
public class ClusterGraphPersister implements Persister{

    private String annotationMethodEntityName;
    private String clusterGraphEntityName;
    private AnnotationMethod method;
    private ClusterGraph graph;

    public static ClusterGraphPersister instantiate(AnnotationMethod method, String annotationMethodEntityName, ClusterGraph graph, String clusterGraphEntityName){
        ClusterGraphPersister per =  new ClusterGraphPersister();
        return per;
    }

    @Override
    public void persist(StatelessSession session) {
        if(method.getAnnotationMethodId() == null){
            session.insert(annotationMethodEntityName, method);
        }
        graph.setAnnotationMethodId(method.getAnnotationMethodId());
        if(graph.getChildId() == null){
            graph.setChildId(graph.getChild().getFeatureClusterId());
        }
        if(graph.getParentId() == null){
            graph.setParentId(graph.getParent().getFeatureClusterId());
        }

        session.insert(clusterGraphEntityName, graph);
    }

}
