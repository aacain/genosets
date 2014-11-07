/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.persister;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.CustomizableEntity;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
/**
 * To use this persister, you should set all the values that
 * you know using the setter methods.
 * The fact can be just an empty fact and the id's will be set later.
 *
 * @author aacain
 */
public abstract class FactPersister<Fact extends CustomizableEntity> implements Persister {
    protected FeatureCluster cluster;
    protected AnnotationMethod method;
    protected Fact fact;
    protected String clusterEntityName;
    protected String methodEntityName;
    protected String factEntityName;

    public FactPersister(){

    }

    public void setup(FeatureCluster cluster, AnnotationMethod method, Fact fact, String clusterEntityName, String methodEntityName, String factEntityName) {
        this.cluster = cluster;
        this.method = method;
        this.fact = fact;
        this.clusterEntityName = clusterEntityName;
        this.methodEntityName = methodEntityName;
        this.factEntityName = factEntityName;
    }

    public FeatureCluster getCluster() {
        return cluster;
    }

    public void setCluster(FeatureCluster cluster) {
        this.cluster = cluster;
    }

    public AnnotationMethod getMethod() {
        return method;
    }

    public void setMethod(AnnotationMethod method) {
        this.method = method;
    }

    public String getClusterEntityName() {
        return clusterEntityName;
    }

    public void setClusterEntityName(String clusterEntityName) {
        this.clusterEntityName = clusterEntityName;
    }

    public Fact getFact() {
        return fact;
    }

    public void setFact(Fact fact) {
        this.fact = fact;
    }

    public String getFactEntityName() {
        return factEntityName;
    }

    public void setFactEntityName(String factEntityName) {
        this.factEntityName = factEntityName;
    }

    public String getMethodEntityName() {
        return methodEntityName;
    }

    public void setMethodEntityName(String methodEntityName) {
        this.methodEntityName = methodEntityName;
    }
    
}
