/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.util.List;

/**
 *
 * @author lucy
 */
public class FactsQueryCreator implements QueryCreator {

    public static List<AnnotationMethod> getAssUnitFacts(Organism org) {
        StringBuilder bldr = new StringBuilder("SELECT method FROM AssembledUnitAquisition as f, AnnotationMethod as method ");
        bldr.append(" WHERE f.annotationMethodId = method.annotationMethodId AND f.organismId = ");
        bldr.append(org.getOrganismId().intValue());
        bldr.append(" GROUP BY method.annotationMethodId ");
        List<AnnotationMethod> methods = DataManager.getDefault().createQuery(bldr.toString());
        return methods;
    }

    public static List<AnnotationMethod> getAnnotationFacts(Organism org) {
        StringBuilder bldr = new StringBuilder("SELECT method FROM AnnoFact as f, AnnotationMethod as method ");
        bldr.append(" WHERE f.annotationMethodId = method.annotationMethodId AND f.organismId = ");
        bldr.append(org.getOrganismId().intValue());
        bldr.append(" GROUP BY method.annotationMethodId ");
        List<AnnotationMethod> methods = DataManager.getDefault().createQuery(bldr.toString());
        return methods;
    }

    public static List<AnnotationMethod> getGoFacts(Organism org) {
        StringBuilder bldr = new StringBuilder("SELECT method FROM Fact_Feature_GoAnno as f, AnnotationMethod as method ");
        bldr.append(" WHERE f.annotationMethodId = method.annotationMethodId AND f.organismId = ");
        bldr.append(org.getOrganismId().intValue());
        bldr.append(" GROUP BY method.annotationMethodId ");
        List<AnnotationMethod> methods = DataManager.getDefault().createQuery(bldr.toString());
        return methods;
    }
}
