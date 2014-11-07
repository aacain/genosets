/*
 * 
 * 
 */
package edu.uncc.genosets.geneontology;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.ClusterGraph;
import edu.uncc.genosets.datamanager.persister.Persister;
import org.hibernate.StatelessSession;

/**
 *
 * @author aacain
 */
public class GoGraphUpdaterPersister implements Persister {

    private ClusterGraph graph;
    private AnnotationMethod method;
    public final static String GRAPH_ENTITY_NAME = "Graph_Go";

    public static GoGraphUpdaterPersister instantiate() {
        return new GoGraphUpdaterPersister();
    }

    public void setup(ClusterGraph graph, AnnotationMethod method, String methodEntityName) {
        this.method = method;
        this.method.setEntityName(methodEntityName);
        this.graph = graph;
    }

    @Override
    public void persist(StatelessSession session) {
        if(method.getAnnotationMethodId() == null){
            session.insert(method.getEntityName(), method);
        }
        graph.setAnnotationMethodId(method.getAnnotationMethodId());
        //session.insert(GRAPH_ENTITY_NAME, graph);
        session.update(GRAPH_ENTITY_NAME, graph);
    }
}
