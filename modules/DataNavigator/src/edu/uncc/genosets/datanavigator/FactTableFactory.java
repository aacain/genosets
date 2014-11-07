/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator;

import edu.uncc.genosets.datamanager.api.AnnotationFactType;
import edu.uncc.genosets.datamanager.api.AssUnitFactType;
import edu.uncc.genosets.datamanager.api.GoAnnotationFactType;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author lucy
 */
public class FactTableFactory extends ChildFactory.Detachable<Fact> {
    //private List<AnnotationMethod> assUnitFact;

    private Fact annoFact;
    private Fact assUnitFact;
    private Fact goFact;
    private final Organism organism;

    public FactTableFactory(Organism organism) {
        this.organism = organism;
        assUnitFact = new Fact(AssUnitFactType.ASS_UNIT_FACT_TYPE, AssAcqFlavor.ASS_ACQ_FLAVOR, "Assembled Unit Acquisition");
        annoFact = new Fact(AnnotationFactType.ANNO_FACT_TYPE, AnnoFactFlavor.ANNO_FACT_FLAVOR, "Annotations");
        goFact = new Fact(GoAnnotationFactType.GO_ANNO_FACT_TYPE, GoFactFlavor.GO_FACT_FLAVOR, "Go Annotations");
    }

    @Override
    protected boolean createKeys(List<Fact> toPopulate) {
        List<AnnotationMethod> assUnitList = FactsQueryCreator.getAssUnitFacts(organism);
        assUnitFact.setMethods(assUnitList);
        List<AnnotationMethod> annoList = FactsQueryCreator.getAnnotationFacts(organism);
        annoFact.setMethods(annoList);
        List<AnnotationMethod> goList = FactsQueryCreator.getGoFacts(organism);
        goFact.setMethods(goList);
        toPopulate.add(assUnitFact);
        toPopulate.add(annoFact);
        toPopulate.add(goFact);
        return true;
    }

    @Override
    protected Node createNodeForKey(Fact key) {
        if (key.getFlavor().getClass().equals(AssAcqFlavor.ASS_ACQ_FLAVOR.getClass())) {
            Node n = new FactTableNode(assUnitFact);
            return n;
        } else if (key.getFlavor().getClass().equals(AnnoFactFlavor.ANNO_FACT_FLAVOR.getClass())) {
            Node n = new FactTableNode(annoFact);
            return n;
        } else if (key.getFlavor().getClass().equals(GoFactFlavor.GO_FACT_FLAVOR.getClass())) {
            Node n = new FactTableNode(goFact);
            return n;
        }
        return null;
    }
}
