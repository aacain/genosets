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
public class GoAnnotationFactType implements FactType{
    public static final GoAnnotationFactType GO_ANNO_FACT_TYPE = new GoAnnotationFactType();

    public static final String name = NbBundle.getMessage(GoAnnotationFactType.class, "CTL_GoAnnoFactTypeDisplayName");
    public static final String desc = NbBundle.getMessage(GoAnnotationFactType.class, "CTL_GoAnnoFactTypeDescription");
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return desc;
    }
}
