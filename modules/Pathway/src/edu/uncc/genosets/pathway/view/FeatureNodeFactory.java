/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.pathway.view;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.QueryCreator;
import edu.uncc.genosets.datamanager.entity.Feature;
import edu.uncc.genosets.datamanager.entity.FeatureCluster;
import edu.uncc.genosets.studyset.StudySet;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.*;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author lucy
 */
public class FeatureNodeFactory extends ChildFactory.Detachable<Feature> {

    private final StudySet studySet;
    private final FeatureCluster pathwayCluster;

    public FeatureNodeFactory(StudySet studySet, FeatureCluster pathwayCluster) {
        this.studySet = studySet;
        this.pathwayCluster = pathwayCluster;
    }

    @Override
    protected boolean createKeys(List<Feature> toPopulate) {
        if (studySet.getIdSet().size() > 0) {
            toPopulate.addAll(FeatureQueryCreator.createQuery(studySet, pathwayCluster));
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Feature key) {
        return new FeatureNode(key);
    }

    static class FeatureQueryCreator implements QueryCreator {

        static List<? extends Feature> createQuery(StudySet studySet, FeatureCluster pathwayCluster) {
            StringBuilder bldr = new StringBuilder("SELECT f FROM Feature as f inner join fetch f.organism inner join fetch f.assembledUnit, PathwayFact as fact where f.featureId = fact.featureId AND fact.featureClusterId = ");
            bldr.append(pathwayCluster.getFeatureClusterId().toString());
            bldr.append(" AND f.featureId IN (");
            int i = 0;
            for (Integer id : studySet.getIdSet()) {
                if (i > 0) {
                    bldr.append(", ");
                }
                bldr.append(id);
                i++;
            }
            bldr.append(")");
            return DataManager.getDefault().createQuery(bldr.toString(), Feature.class);
        }
    }

    public static class FeatureNode extends AbstractNode {

        private static List<? extends Action> registeredActions;

        public FeatureNode(Feature feature) {
            super(Children.create(new FeatureDetailChildFactory(feature), true), Lookups.singleton(feature));
            this.setName(feature.getPrimaryName() + " (" + feature.getOrganism().getSpecies() + ") " + (feature.getProduct() == null ? "" : feature.getProduct()));
        }

        protected static List<? extends Action> getRegisterActions() {
            if (registeredActions == null) {
                registeredActions = Utilities.actionsForPath("Feature/Nodes/Actions");
            }
            return registeredActions;
        }

        @Override
        public Action[] getActions(boolean context) {
            Feature feature = getLookup().lookup(Feature.class);
            if (feature == null) {
                int x = 5;
            }
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

    public static class FeatureDetailChildFactory extends ChildFactory.Detachable<String[]> {

        private final Feature feature;

        public FeatureDetailChildFactory(Feature feature) {
            this.feature = feature;
        }

        @Override
        protected boolean createKeys(List<String[]> toPopulate) {
            List<Object[]> createQuery = DataManager.getDefault().createQuery("SELECT d.detailType, d.detailValue FROM AnnoFactDetail as d, AnnoFact as a WHERE d.featureId = a.featureId AND d.featureId = " + feature.getFeatureId());
            for (Object[] strings : createQuery) {
                String[] ss = new String[]{(String) strings[0], (String) strings[1]};
                toPopulate.add(ss);
            }
            List<String> pathways = DataManager.getDefault().createQuery("SELECT c.customProperties.pathwayName FROM PathwayFact as f, ClusterPathway c WHERE f.featureCluster = c.featureClusterId AND f.featureId = " + feature.getFeatureId() + " GROUP BY f.featureCluster, f.featureId ");
            for (String s : pathways) {
                String[] ss = new String[]{(String) s};
                toPopulate.add(ss);
            }
            return true;
        }

        @Override
        protected Node createNodeForKey(String[] key) {
            if (key.length == 2) {
                return new DetailNode(key[0], key[1]);
            } else {
                return new PathwayNode(key[0]);
            }
        }

        public static class DetailNode extends AbstractNode {

            public DetailNode(String detailType, String detailValue) {
                super(Children.LEAF);
                this.setName(detailType + " : " + detailValue);
            }
        }

        public static class PathwayNode extends AbstractNode {

            public PathwayNode(String value) {
                super(Children.LEAF);
                this.setName("Pathway: " + value);
                this.setIconBaseWithExtension("edu/uncc/genosets/studyset/resources/call_graph.png");
            }
        }
    }
}
