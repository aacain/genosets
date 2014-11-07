/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.datanavigator;

import edu.uncc.genosets.datamanager.api.AnnotationFactType;
import edu.uncc.genosets.datamanager.api.FactType;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.awt.datatransfer.DataFlavor;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aacain
 */
@ServiceProvider(service=FactFlavor.class)
public class GoFactFlavor extends FactFlavor{
    public static final FactFlavor GO_FACT_FLAVOR = new GoFactFlavor();
    public static final AnnotationFactType factType = AnnotationFactType.ANNO_FACT_TYPE;

    public GoFactFlavor() {
        super(AnnotationMethod.class, Fact.TYPE_GO);
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof GoFactFlavor ? true : false);
    }

    @Override
    public boolean equals(DataFlavor that) {
        return (that instanceof GoFactFlavor ? true : false);
    }

    @Override
    public int hashCode() {
        return GoFactFlavor.class.hashCode();
    }

    @Override
    public FactType getFactType() {
        return factType;
    }
}
