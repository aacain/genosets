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
public class AnnoFactFlavor extends FactFlavor{
    public static final FactFlavor ANNO_FACT_FLAVOR = new AnnoFactFlavor();
    public static final AnnotationFactType factType = AnnotationFactType.ANNO_FACT_TYPE;

    public AnnoFactFlavor() {
        super(AnnotationMethod.class, Fact.TYPE_ANNOFACT);
    }
    
    @Override
    public boolean equals(Object o) {
        return (o instanceof AnnoFactFlavor ? true : false);
    }

    @Override
    public boolean equals(DataFlavor that) {
        return (that instanceof AnnoFactFlavor ? true : false);
    }

    @Override
    public int hashCode() {
        return AnnoFactFlavor.class.hashCode();
    }

    @Override
    public FactType getFactType() {
        return factType;
    }
}
