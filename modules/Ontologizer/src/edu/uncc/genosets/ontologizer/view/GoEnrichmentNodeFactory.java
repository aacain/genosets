/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer.view;

import edu.uncc.genosets.ontologizer.EnrichmentServiceProvider;
import edu.uncc.genosets.ontologizer.GoEnrichment;
import edu.uncc.genosets.studyset.StudySet;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 * Creates GoEnrichment nodes for the study set that is in the global lookup
 * Listenes for changes to the GoEnrichmentsService provider to see if new
 * enrichements are added.
 *
 * @author aacain
 */
public class GoEnrichmentNodeFactory extends Children.Keys<GoEnrichment> implements PropertyChangeListener {

    private final StudySet set;

    public GoEnrichmentNodeFactory(StudySet set) {
        this.set = set;
    }

    @Override
    protected Node[] createNodes(GoEnrichment key) {
        GoEnrichmentNode node = new GoEnrichmentNode(key, Children.LEAF, Lookups.fixed(key, key.getStudySet()));
        return new Node[]{node};
    }

    @SuppressWarnings("unchecked")
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (EnrichmentServiceProvider.PROP_ENRICHMENTS_CHANGED.equals(evt.getPropertyName())) {
            GoEnrichment e = (GoEnrichment) evt.getNewValue();
            if (e.getStudySet().equals(this.set)) {
                EnrichmentServiceProvider provider = Lookup.getDefault().lookup(EnrichmentServiceProvider.class);
                List<GoEnrichment> enrichments = provider.getEnrichments(set);
                if (enrichments != null) {
                    this.setKeys(enrichments);
                } else {
                    this.setKeys(Collections.EMPTY_LIST);
                }
            }
        } else if (EnrichmentServiceProvider.PROP_STUDYSET_DELETED.equals(evt.getPropertyName())) {
            StudySet ss = (StudySet) evt.getNewValue();
            if (ss == this.set) {
                EnrichmentServiceProvider provider = Lookup.getDefault().lookup(EnrichmentServiceProvider.class);
                List<GoEnrichment> enrichments = provider.getEnrichments(ss);
                if (enrichments != null) {
                    this.setKeys(enrichments);
                } else {
                    this.setKeys(Collections.EMPTY_LIST);
                }
            }
        }
    }

    @Override
    protected void addNotify() {
        EnrichmentServiceProvider provider = Lookup.getDefault().lookup(EnrichmentServiceProvider.class);
        List<GoEnrichment> enrichments = provider.getEnrichments(set);
        if (enrichments != null) {
            this.setKeys(enrichments);
        }
        //listen for enrichment changes
        provider.addPropertyChangeListener(WeakListeners.create(PropertyChangeListener.class, this, set));
    }

    static class GoEnrichmentNode extends AbstractNode implements PropertyChangeListener {

        private static List<? extends Action> registeredActions;
        private final GoEnrichment enrichment;

        public GoEnrichmentNode(GoEnrichment enrichment, Children children, Lookup lookup) {
            super(new StudySetNodeFactory(enrichment), lookup);
            this.enrichment = enrichment;
            this.setName(enrichment.getUniqueName());
            this.setDisplayName(enrichment.getUniqueName());
            if (this.enrichment.getIsUpdateNeeded()) {
                this.setIconBaseWithExtension("edu/uncc/genosets/ontologizer/resources/alert_16.png");
                this.setShortDescription("Studyset or population set has changed since run.");
            } else {
                this.setIconBaseWithExtension("edu/uncc/genosets/ontologizer/resources/ontologizer_run.png");
            }
            enrichment.addPropertyChangeListener(WeakListeners.create(PropertyChangeListener.class, this, this.enrichment));
            propertyChange(null);
        }

        protected static List<? extends Action> getRegisterActions() {
            if (registeredActions == null) {
                registeredActions = Utilities.actionsForPath("GoEnrichment/Nodes/Actions");
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
        public String getHtmlDisplayName() {
            if (this.enrichment.getIsDefault()) {
                return "<b>" + this.getDisplayName() + "</b>";
            }
            return this.getDisplayName();
        }

        @Override
        public boolean canDestroy() {
            return Boolean.TRUE;
        }

        @Override
        public void destroy() throws IOException {
            EnrichmentServiceProvider provider = Lookup.getDefault().lookup(EnrichmentServiceProvider.class);
            provider.deleteEnrichment(enrichment);
            super.destroy();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt == null) {
                this.fireDisplayNameChange(null, getDisplayName());
            } else if (GoEnrichment.PROP_IS_DEFAULT.equals(evt.getPropertyName())) {
                this.fireDisplayNameChange(null, getDisplayName());
            } else if (GoEnrichment.PROP_NEEDS_UPDATE.equals(evt.getPropertyName())) {
                this.setIconBaseWithExtension("edu/uncc/genosets/ontologizer/resources/alert_16.png");
                this.setShortDescription("Studyset or population set has changed since run.");
            }
        }

        @Override
        public HelpCtx getHelpCtx() {
            return new HelpCtx("edu.uncc.genosets.ontologizer.about");
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set setProps = sheet.createPropertiesSet();
            setProps.setDisplayName("Properties");
            sheet.put(setProps);

            Property<String> idProp = new PropertySupport.ReadOnly<String>("id", String.class, "ID", "unique id") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    this.setValue("htmlDisplayValue", "<font color='000000'>" + enrichment.getUniqueName() + "</font>");
                    return (null != enrichment.getUniqueName()) ? enrichment.getUniqueName() : "";
                }
            };
            setProps.put(idProp);

            PropertySupport.ReadOnly<String> oboProp = new PropertySupport.ReadOnly<String>("Obo", String.class, "Obo", "Obo file") {

                @Override
                public String getValue() throws IllegalAccessException, InvocationTargetException {
                    if (enrichment.getOntologizerParameters() != null) {
                        if (enrichment.getOntologizerParameters().getObo()!= null) {
                            this.setValue("htmlDisplayValue", "<font color='000000'>" + enrichment.getOntologizerParameters().getObo() + "</font>");
                            return enrichment.getOntologizerParameters().getObo().getUrl();
                        }
                    }
                    return "";
                }
            };
            setProps.put(oboProp);
            idProp.setValue("supressCustomEditor", Boolean.TRUE);
            oboProp.setValue("supressCustomEditor", Boolean.TRUE);

            return sheet;
        }
    }
}
