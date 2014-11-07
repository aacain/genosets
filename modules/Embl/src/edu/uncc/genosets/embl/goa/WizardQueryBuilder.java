/*
 * 
 * 
 */
package edu.uncc.genosets.embl.goa;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.AssembledUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author aacain
 */
public class WizardQueryBuilder implements QueryCreator {

    public static final String PROP_DATE = "PROP_DATE";
    @SuppressWarnings("unchecked")
    public Set<AssembledUnit> getAssUnits() {
        DataManager mgr = DataManager.getDefault();
        //get process assunits first
        String included = "SELECT assUnit, max(annoMethod.loadDate) "
                + " FROM Fact_Feature_GoAnno goAnno, AssembledUnit as assUnit inner join fetch assUnit.organism, AnnotationMethod as annoMethod"
                + " WHERE goAnno.assembledUnitId = assUnit.assembledUnitId AND goAnno.annotationMethodId = annoMethod.annotationMethodId "
                + " GROUP BY assUnit.id ";
        List<Object[]> includedList = mgr.createQuery(included);
        Set<AssembledUnit> allSet = new HashSet<AssembledUnit>(includedList.size());
        for (Object[] object : includedList) {
            AssembledUnit assUnit = (AssembledUnit) object[0];
            Date date = (Date) object[1];
            assUnit.setValueOfCustomField(PROP_DATE, date);
            allSet.add(assUnit);
        }

        //now get all assunits and add to set
        String query = "SELECT assUnit "
                + " FROM AssembledUnitAquisition as aq inner join aq.assembledUnit as assUnit inner join fetch assUnit.organism"
                + " GROUP BY assUnit.id";       
        List<AssembledUnit> allAsses = mgr.createQuery(query);
        //all all
        allSet.addAll(allAsses);

        return allSet;

    }
}
