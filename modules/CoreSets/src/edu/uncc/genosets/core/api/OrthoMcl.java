/*
 * 
 * 
 */

package edu.uncc.genosets.core.api;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.taskmanager.api.ProgramStep;
import java.io.File;
import java.util.List;
import org.openide.filesystems.FileObject;


/**
 *
 * @author aacain
 */
public interface OrthoMcl {
    public static final String METHOD_ENTITY_NAME = AnnotationMethod.DEFAULT_NAME;
    public static final String CLUSTER_ENTITY_NAME = FeatureCluster.DEFAULT_NAME;
    public static final String FACT_LOCATION_ENTITY_NAME = "OrthoFact";
    public static final String CLUSTER_CATEGORY = "Ortholog";
    public static final String CLUSTER_TYPE = "OrthoMCL";
    public static final String METHOD_SOURCE_TYPE = "OrthoMCL";

    public OrthoMcl getDefault();
    public List<? extends ProgramStep> getSteps();
    public void run(FileObject folder);
    public void load(File file, AnnotationMethod method);
    public void load(File file, String methodName, String methodDescription);
    public String getVersion();
}
