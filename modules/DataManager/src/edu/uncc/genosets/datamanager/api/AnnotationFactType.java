/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.datamanager.api;

import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aacain
 */
@ServiceProvider(service=FactType.class)
public class AnnotationFactType implements FactType{
    public static final AnnotationFactType ANNO_FACT_TYPE = new AnnotationFactType();
    public static final String name = NbBundle.getMessage(AnnotationFactType.class, "CTL_AnnotationsDisplayName");
    public static final String desc = NbBundle.getMessage(AnnotationFactType.class, "CTL_AnnotationsDescription");
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return desc;
    }
}
