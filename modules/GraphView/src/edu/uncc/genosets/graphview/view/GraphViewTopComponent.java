/*
 * 
 * 
 */
package edu.uncc.genosets.graphview.view;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.studyset.TermCalculation;
import edu.uncc.genosets.treemap.GenoSetsTreeMapSource;
import edu.uncc.genosets.treemap.view.GenoSetsTreeMap;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.*;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.TupleSet;
//import org.openide.util.ImageUtilities;

/**
 * Top component which displays something.
 */
@NbBundle.Messages({
    "CTL_GraphViewTopComponent=GO Tree Navigator Window",
    "HINT_GraphViewTopComponent=This is a GO Tree Navigator window"
})
public final class GraphViewTopComponent extends TopComponent {

    private ComponentAdapter resizeListener;
    private static GraphViewTopComponent instance;
    /**
     * path to the icon used by the component and its open action
     */
//    static final String ICON_PATH = "SET/PATH/TO/ICON/HERE";
    private static final String PREFERRED_ID = "GraphViewTopComponent";
    private GraphVisualization graphVis;
    private TupleSetListener focusTsListener;
    private TupleSetListener hoverTsListener;
    private GenoSetsTreeMap treemap;
    //listens for changes to termCalculation
    private TermCalculation selectedTermCalculation;
    private Lookup.Result<TermCalculation> termResult;
    private LookupListener termLookupListener;
    private PropertyChangeListener dbChangeListener;

    public GraphViewTopComponent() {
        initComponents();
        setName(Bundle.CTL_GraphViewTopComponent());
        setToolTipText(Bundle.HINT_GraphViewTopComponent());
//        setIcon(ImageUtilities.loadImage(ICON_PATH, true));
        dbChangeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                dbChanged(evt);
            }
        };
        DataManager.getDefault().addPropertyChangeListener(WeakListeners.create(PropertyChangeListener.class, dbChangeListener, DataManager.getDefault()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 603, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 424, Short.MAX_VALUE)
        );

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    /**
     * Gets default instance. Do not use directly: reserved for *.settings files
     * only, i.e. deserialization routines; otherwise you could get a
     * non-deserialized instance. To obtain the singleton instance, use {@link #findInstance}.
     */
    public static synchronized GraphViewTopComponent getDefault() {
        if (instance == null) {
            instance = new GraphViewTopComponent();
        }
        return instance;
    }

    /**
     * Obtain the GraphViewTopComponent instance. Never call {@link #getDefault}
     * directly!
     */
    public static synchronized GraphViewTopComponent findInstance() {
        TopComponent win = WindowManager.getDefault().findTopComponent(PREFERRED_ID);
        if (win == null) {
            Logger.getLogger(GraphViewTopComponent.class.getName()).warning(
                    "Cannot find " + PREFERRED_ID + " component. It will not be located properly in the window system.");
            return getDefault();
        }
        if (win instanceof GraphViewTopComponent) {
            return (GraphViewTopComponent) win;
        }
        Logger.getLogger(GraphViewTopComponent.class.getName()).warning(
                "There seem to be multiple components with the '" + PREFERRED_ID
                + "' ID. That is a potential source of errors and unexpected behavior.");
        return getDefault();
    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    @Override
    public void componentOpened() {
        //look for treemap
        this.treemap = Lookup.getDefault().lookup(GenoSetsTreeMapSource.class).getLookup().lookup(GenoSetsTreeMap.class);
        if (treemap == null) {
            NotifyDescriptor d = new NotifyDescriptor.Message("You must open the TreeMap before this window",
                    NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notifyLater(d);
            final TopComponent tc = this;
            WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

                @Override
                public void run() {
                    tc.close();
                }
            });
        } else {
            focusTsListener = new TupleSetListener() {

                @Override
                public void tupleSetChanged(TupleSet ts, Tuple[] added, Tuple[] removed) {
                    graphVis.setSelectedGoTerms(ts, added, removed);
                }
            };
            this.treemap.addTupleSetListener(GenoSetsTreeMap.FOCUS, focusTsListener);

            hoverTsListener = new TupleSetListener() {

                @Override
                public void tupleSetChanged(TupleSet ts, Tuple[] added, Tuple[] removed) {
                    graphVis.setHoveredGoTerms(added, removed);
                }
            };
            this.treemap.addTupleSetListener(GenoSetsTreeMap.HOVER, hoverTsListener);

            //initialize term lookup
            this.termResult = Utilities.actionsGlobalContext().lookupResult(TermCalculation.class);
            this.termLookupListener = new LookupListener() {

                @Override
                public void resultChanged(LookupEvent ev) {
                    termChanged();
                }
            };
            this.termResult.addLookupListener(termLookupListener);

            graphVis = new GraphVisualization(this.treemap.getGraph().getTreeRep());
            jPanel1.setLayout(new BorderLayout());
            jPanel1.add(graphVis.getPanel(), BorderLayout.CENTER);
            resizeListener = new java.awt.event.ComponentAdapter() {

                public void componentResized(java.awt.event.ComponentEvent evt) {
                    resizePanel();
                }
            };
            jPanel1.addComponentListener(resizeListener);
        }
    }

    @Override
    public void componentClosed() {
        if (this.treemap != null) {
            this.treemap.removeTupleSetListener(GenoSetsTreeMap.FOCUS, focusTsListener);
            focusTsListener = null;
            this.treemap.removeTupleSetListener(GenoSetsTreeMap.HOVER, hoverTsListener);
            hoverTsListener = null;
            this.treemap = null;
        }
        if (this.termResult != null) {
            this.termResult.removeLookupListener(this.termLookupListener);
        }
        this.termLookupListener = null;
        this.termResult = null;
        jPanel1.removeComponentListener(resizeListener);
        resizeListener = null;
        jPanel1.remove(graphVis.getPanel());
        graphVis = null;
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    Object readProperties(java.util.Properties p) {
        if (instance == null) {
            instance = this;
        }
        instance.readPropertiesImpl(p);
        return instance;
    }

    private void readPropertiesImpl(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    private void termChanged() {
        TermCalculation oldTerm = this.selectedTermCalculation;
        for (TermCalculation goTerm : this.termResult.allInstances()) {
            this.selectedTermCalculation = goTerm;
        }
        boolean changed = false;
        if (oldTerm != null) {
            if (this.selectedTermCalculation != null) {
                if (!oldTerm.getTermId().equals(this.selectedTermCalculation.getTermId())) {
                    changed = true;
                }
            }
        } else {
            changed = true;
        }
        if (selectedTermCalculation != null) {
            if (changed) {
                this.graphVis.updateSelectedTerm(this.selectedTermCalculation);
            }
        }
    }

    private void dbChanged(PropertyChangeEvent evt) {
        final TopComponent tc = this;
        if (DataManager.PROP_DB_CHANGED.equals(evt.getPropertyName())) {
            WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

                @Override
                public void run() {
                    tc.close();
                }
            });
        }
    }

    private void resizePanel() {
        System.out.println("Resized" + jPanel1.getSize());
        graphVis.resizeMe(jPanel1.getSize());
    }
}
