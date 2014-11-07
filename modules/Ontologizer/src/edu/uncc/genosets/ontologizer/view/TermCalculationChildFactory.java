/*
 * 
 * 
 */
package edu.uncc.genosets.ontologizer.view;

import edu.uncc.genosets.ontologizer.GoEnrichment;
import edu.uncc.genosets.studyset.GoTerm;
import edu.uncc.genosets.studyset.TermCalculation;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author aacain
 */
public class TermCalculationChildFactory extends ChildFactory<TermCalculation> {

    public static final String PROP_GO_ID = "PROP_GO_ID";
    public static final String PROP_GO_NAME = "PROP_GO_NAME";
    public static final String PROP_PVALUE_ADJUSTED = "PROP_PVALUE_ADJUSTED";
    public static final String PROP_POP_TERM = "PROP_POP_TERM";
    public static final String PROP_STUDY_TERM = "PROP_STUDY_TERM";
    public static final String PROP_RATIO = "PROP_RATIO";
    public static final String PROP_STUDY_TOTAL = "PROP_STUDY_TOTAL";
    public static final String PROP_POP_TOTAL = "PROP_POP_TOTAL";
    public static final String numberFormat = "#.##E";
    private final GoEnrichment enrichment;

    public TermCalculationChildFactory(GoEnrichment enrichment) {
        this.enrichment = enrichment;
    }

    @Override
    protected boolean createKeys(List<TermCalculation> toPopulate) {
        if (this.enrichment != null) {
            HashMap<String, TermCalculation> termMap = this.enrichment.getTermCalculationMap();
            if (termMap != null) {
                Collection<TermCalculation> values = termMap.values();
                toPopulate.addAll(values);
            }
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(TermCalculation key) {
        return new TermCalculationNode(key, this.enrichment);
    }

    @Override
    protected Node[] createNodesForKey(TermCalculation key) {
        return new TermCalculationNode[]{new TermCalculationNode(key, this.enrichment)};
    }

    private static class TermCalculationNode extends AbstractNode {

        private static List<? extends Action> registeredActions;

        public TermCalculationNode(TermCalculation termCalc, GoEnrichment enrichment) {
            super(Children.LEAF, new ProxyLookup(Lookups.singleton(enrichment), Lookups.singleton(termCalc), Lookups.singleton(new GoTerm(termCalc.getTermId()))));
            this.setDisplayName(termCalc.getTermId());
            this.setName(termCalc.getTermId());
        }

        protected static List<? extends Action> getRegisterActions() {
            if (registeredActions == null) {
                registeredActions = Utilities.actionsForPath("TermCalculation/Nodes/Actions");
            }
            return registeredActions;
        }

        @Override
        public Action[] getActions(boolean context) {
            List<Action> actions = new ArrayList<Action>();
            actions.addAll(getRegisterActions());
            actions.addAll(Arrays.asList(super.getActions(context)));
            return actions.toArray(new Action[actions.size()]);
        }

        @Override
        protected Sheet createSheet() {
            final TermCalculation termCalc = this.getLookup().lookup(TermCalculation.class);
            Sheet sheet = Sheet.createDefault();
            Sheet.Set setProps = Sheet.createPropertiesSet();
            setProps.setDisplayName("Identification");
            sheet.put(setProps);
            Property<String> idProperty = new PropertySupport.ReadOnly<String>(TermCalculationChildFactory.PROP_GO_ID, String.class, "ID", "GO ID") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return termCalc.getTermId();
                }
            };

            Property<String> goNameProperty = new PropertySupport.ReadOnly<String>(TermCalculationChildFactory.PROP_GO_NAME, String.class, "GO Name", "GO Name") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return termCalc.getGoName();
                }
            };

            Property<Double> pAdjProperty = new PropertySupport.ReadOnly<Double>(TermCalculationChildFactory.PROP_PVALUE_ADJUSTED, Double.class, "p-value adjusted", "p-value adjusted") {

                @Override
                public Double getValue() throws IllegalAccessException, InvocationTargetException {
                    return termCalc.getpAdjusted();
                }
            };

            Property<Integer> popTermProperty = new PropertySupport.ReadOnly<Integer>(TermCalculationChildFactory.PROP_POP_TERM, Integer.class, "population term", "population term") {

                @Override
                public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                    return termCalc.getPopTerm();
                }
            };

            Property<Integer> studyTermProperty = new PropertySupport.ReadOnly<Integer>(TermCalculationChildFactory.PROP_STUDY_TERM, Integer.class, "study term", "study term") {

                @Override
                public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                    return termCalc.getStudyTerm();
                }
            };

            Property<Double> ratioProperty = new PropertySupport.ReadOnly<Double>(TermCalculationChildFactory.PROP_RATIO, Double.class, "Ratio", "(Study Term Total/Study Total)/(Pop. Term Total/Pop. Total)") {

                @Override
                public Double getValue() throws IllegalAccessException, InvocationTargetException {
                    double ratio = ((double) termCalc.getStudyTerm() / (double) termCalc.getStudyTotal()) / ((double) termCalc.getPopTerm() / (double) termCalc.getPopTotal());
                    return ratio;
                }
            };

            Property<Integer> studyTotal = new PropertySupport.ReadOnly<Integer>(TermCalculationChildFactory.PROP_STUDY_TOTAL, Integer.class, "study total", "study total") {

                @Override
                public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                    return termCalc.getStudyTotal();
                }
            };

            Property<Integer> popTotal = new PropertySupport.ReadOnly<Integer>(TermCalculationChildFactory.PROP_POP_TOTAL, Integer.class, "population total", "population total") {

                @Override
                public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                    return termCalc.getPopTotal();
                }
            };



            setProps.put(idProperty);
            setProps.put(goNameProperty);
            setProps.put(pAdjProperty);
            setProps.put(popTermProperty);
            setProps.put(studyTermProperty);
            setProps.put(ratioProperty);
            setProps.put(studyTotal);
            setProps.put(popTotal);

            idProperty.setValue("suppressCustomEditor", Boolean.TRUE);
            idProperty.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != termCalc) ? termCalc.getTermId() : "") + "</font>");
            goNameProperty.setValue("suppressCustomEditor", Boolean.TRUE);
            goNameProperty.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != termCalc) ? termCalc.getGoName() : "") + "</font>");
            pAdjProperty.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != termCalc) ? termCalc.getpAdjusted() : null) + "</font>");
            popTermProperty.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != termCalc) ? termCalc.getPopTerm() : null) + "</font>");
            studyTermProperty.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != termCalc) ? termCalc.getStudyTerm() : null) + "</font>");
            String ratioString;
            if (termCalc != null) {
                double ratio = ((double) termCalc.getStudyTerm() / (double) termCalc.getStudyTotal()) / ((double) termCalc.getPopTerm() / (double) termCalc.getPopTotal());
                DecimalFormat fm = new DecimalFormat();
                ratioString = fm.format(ratio);
            } else {
                ratioString = "";
            }
            ratioProperty.setValue("suppressCustomEditor", Boolean.TRUE);
            ratioProperty.setValue("htmlDisplayValue", "<font color='000000'>" + ratioString + "</font>");
            studyTotal.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != termCalc) ? termCalc.getStudyTotal() : null) + "</font>");
            popTotal.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != termCalc) ? termCalc.getPopTotal() : null) + "</font>");
            return sheet;
        }
    }
}
