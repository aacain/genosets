/*
 * 
 * 
 */

package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;

/**
 *
 * @author aacain
 */
@Deprecated
public class GeneAnnotationConstants {
    private static final String EntityName_ClusterFact = "Annotation_Classification";
    private static final String EntityName_ClusterDetailFact = "AnnoClassificationDetail";
    private static final String EntityName_Cluster = FeatureCluster.DEFAULT_NAME;
    private static final String EntityName_Method = AnnotationMethod.DEFAULT_NAME;
    private static final String Cluster_Category = "Feature Annotation";
    private static final String Method_Category = "Feature Annotation";
    private static final String FeatureType_CDS = "CDS";

    public static String getCluster_Category() {
        return Cluster_Category;
    }

    public static String getEntityName_Cluster() {
        return EntityName_Cluster;
    }

    public static String getEntityName_ClusterDetailFact() {
        return EntityName_ClusterDetailFact;
    }

    public static String getEntityName_ClusterFact() {
        return EntityName_ClusterFact;
    }

    public static String getMethod_Category() {
        return Method_Category;
    }

    public static String getFeatureType_CDS() {
        return FeatureType_CDS;
    }

    public static String getEntityName_Method() {
        return EntityName_Method;
    }

}
