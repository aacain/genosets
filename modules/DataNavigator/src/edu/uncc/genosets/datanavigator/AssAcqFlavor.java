/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.datanavigator;

import edu.uncc.genosets.datamanager.api.AssUnitFactType;
import edu.uncc.genosets.datamanager.api.FactType;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.awt.datatransfer.DataFlavor;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aacain
 */
@ServiceProvider(service=FactFlavor.class)
public class AssAcqFlavor extends FactFlavor{
    public static final FactFlavor ASS_ACQ_FLAVOR = new AssAcqFlavor();
    public static final AssUnitFactType factType = AssUnitFactType.ASS_UNIT_FACT_TYPE;

    public AssAcqFlavor() {
        super(AnnotationMethod.class, Fact.TYPE_ASSFACT);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof AssAcqFlavor ? true : false);
    }

    @Override
    public boolean equals(DataFlavor that) {
        return (that instanceof AssAcqFlavor ? true : false);
    }

    @Override
    public int hashCode() {
        return AssAcqFlavor.class.hashCode();
    }

    @Override
    public FactType getFactType() {
        return factType;
    }
}
