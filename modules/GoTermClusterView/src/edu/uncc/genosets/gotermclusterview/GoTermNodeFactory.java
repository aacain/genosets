/*
 * 
 * 
 */
package edu.uncc.genosets.gotermclusterview;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.studyset.GoTerm;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import javax.swing.Action;
import org.openide.nodes.*;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author aacain
 */
public class GoTermNodeFactory extends ChildFactory<Feature> {

    private Set<Integer> featureIds;
    private GoTerm term;

    public GoTermNodeFactory(GoTerm term, Set<Integer> featureIds) {
        this.term = term;
        this.featureIds = featureIds;
    }

    @Override
    protected boolean createKeys(List<Feature> toPopulate) {
        //create query

        if (!featureIds.isEmpty()) {
            Query query = new Query();
            List<? extends Feature> features = query.createQuery(featureIds);
            if (features != null) {
                toPopulate.addAll(features);
            }
        }

        return true;
    }

    @Override
    protected Node[] createNodesForKey(Feature f) {
        AbstractNode node = new FeatureNode(Children.LEAF, new ProxyLookup(Lookups.singleton(f), Lookups.singleton(f.getOrganism())));
        return new Node[]{node};
    }

    public static class FeatureNode extends AbstractNode {

        private static List<? extends Action> registeredActions;

        public FeatureNode(Children children, Lookup lookup) {
            super(children, lookup);
            Feature f = lookup.lookup(Feature.class);
            Organism o = lookup.lookup(Organism.class);
            this.setDisplayName(f.getPrimaryName());
            this.setIconBaseWithExtension("edu/uncc/genosets/gotermclusterview/resources/dna-icon.png");
        }

        protected static List<? extends Action> getRegisterActions() {
            if (registeredActions == null) {
                registeredActions = Utilities.actionsForPath("Feature/Nodes/Actions");
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
            final Feature f = getLookup().lookup(Feature.class);
            Sheet sheet = Sheet.createDefault();
            Sheet.Set setProps = Sheet.createPropertiesSet();
            setProps.setDisplayName("general");
            sheet.put(setProps);

            Node.Property<Integer> idProp = new PropertySupport.ReadOnly<Integer>(
                    "id", Integer.class, "ID", "identification") {

                @Override
                public Integer getValue() throws IllegalAccessException, InvocationTargetException {
                    return (null != f) ? f.getFeatureId() : null;
                }
            };

            Node.Property<String> orgProp = new PropertySupport.ReadOnly<String>(
                    "organism", String.class, "Organism", "organism") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return (null != f) ? f.getOrganism().getStrain() : "";
                }
            };
            Node.Property<String> locusProp = new PropertySupport.ReadOnly<String>(
                    "locus", String.class, "Locus", "Locus") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return (null != f.getPrimaryName()) ? f.getPrimaryName() : "";
                }
            };
            Node.Property<String> productProp = new PropertySupport.ReadOnly<String>(
                    "product", String.class, "Product", "Product") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return (null != f.getProduct()) ? f.getProduct() : "";
                }
            };
            Node.Property<String> assProp = new PropertySupport.ReadOnly<String>(
                    "assembledUnit", String.class, "Assembled Unit", "assembled unit") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return (null != f) ? f.getAssembledUnit().getAssembledUnitName() : "";
                }
            };

            Node.Property<String> repNameProp = new PropertySupport.ReadOnly<String>(
                    "repUnitName", String.class, "Rep Unit Name", "The name of the replicating name") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    return (null != f) ? f.getAssembledUnit().getRepUnitName() : "";
                }
            };

            setProps.put(idProp);
            setProps.put(locusProp);
            setProps.put(productProp);
            setProps.put(orgProp);
            setProps.put(assProp);
            setProps.put(repNameProp);

            idProp.setValue("suppressCustomEditor", Boolean.TRUE);
            locusProp.setValue("suppressCustomEditor", Boolean.TRUE);
            productProp.setValue("suppressCustomEditor", Boolean.TRUE);
            orgProp.setValue("suppressCustomEditor", Boolean.TRUE);
            assProp.setValue("suppressCustomEditor", Boolean.TRUE);
            repNameProp.setValue("suppressCustomEditor", Boolean.TRUE);

            idProp.setValue("htmlDisplayValue", "<font color='000000'>" + (f.getFeatureId().toString()) + "</font>");
            locusProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f.getPrimaryName()) ? f.getPrimaryName() : "") + "</font>");
            productProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f.getProduct()) ? f.getProduct() : "") + "</font>");
            orgProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f) ? f.getOrganism().getStrain() : "") + "</font>");
            assProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f) ? f.getAssembledUnit().getAssembledUnitName() : "") + "</font>");
            repNameProp.setValue("htmlDisplayValue", "<font color='000000'>" + ((null != f) ? f.getAssembledUnit().getRepUnitName() : "") + "</font>");

            return sheet;
        }

        public static String[] getProperties() {
            return new String[]{
                        "product", "Product",
                        "organism", "Organism",
                        "assembledUnit", "Assembled Unit",
                        "repUnitName", "Rep Unit Name"};
        }
    }

    private static class Query implements QueryCreator {

        List<? extends Feature> createQuery(Collection<Integer> featureIds) {
            StringBuilder bldr = new StringBuilder("SELECT f FROM Feature as f inner join fetch f.organism inner join fetch f.assembledUnit WHERE f.featureId in ( ");
            for (Integer featureId : featureIds) {
                bldr.append(featureId).append(",");
            }
            //remove last comma
            bldr.deleteCharAt(bldr.length() - 1);
            bldr.append(")");
            return DataManager.getDefault().createQuery(bldr.toString(), Feature.class);
        }
    }
}
