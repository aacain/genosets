/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer.actions;

import edu.uncc.genosets.ontologizer.GoEnrichment;
import edu.uncc.genosets.ontologizer.view.EnrichmentDetailsTopComponent2;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

@ActionID(id = "edu.uncc.genosets.ontologizer.actions.EnrichmentDetailsAction", category = "Tools")
@ActionRegistration(iconInMenu = true, displayName = "#CTL_EnrichmentDetailsAction", iconBase="edu/uncc/genosets/ontologizer/resources/enrichment_details.png")
@ActionReferences(value = {
    @ActionReference(path = "Menu/Window", position = 240),
    @ActionReference(path = "Toolbars/Explore", position = 200),
    //@ActionReference(path = "StudySet/Nodes/Actions", position = 100),
    @ActionReference(path = "StudySet/Nodes/Feature", position = 1200),
    @ActionReference(path = "GoEnrichment/Nodes/Actions", position = 100)
})
@NbBundle.Messages("CTL_EnrichmentDetailsAction=Enrichment Details")
public final class EnrichmentDetailsAction extends AbstractAction implements LookupListener, ContextAwareAction, Presenter.Popup {

    private static final String ICON = "edu/uncc/genosets/ontologizer/resources/enrichment_details.png";
    private Lookup context;
    Lookup.Result<GoEnrichment> lkpInfo;

    public EnrichmentDetailsAction() {
        this(Utilities.actionsGlobalContext());
    }

    public EnrichmentDetailsAction(Lookup context) {
        super(Bundle.CTL_EnrichmentDetailsAction());
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon(ICON, false));
        this.context = context;
    }

    private void init() {
        assert SwingUtilities.isEventDispatchThread() :
                "this shall be called just from the AWT thread";
        if (lkpInfo != null) {
            return;
        }
        lkpInfo = context.lookupResult(GoEnrichment.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public boolean isEnabled() {
        init();
        return super.isEnabled();
    }

    @SuppressWarnings("unchecked")
    public void actionPerformed(ActionEvent ev) {
        for (GoEnrichment goEnrichment : lkpInfo.allInstances()) {
            EnrichmentDetailsTopComponent2 instance = EnrichmentDetailsTopComponent2.findInstance(goEnrichment);
            instance.open();
            instance.requestActive();
        }
        
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Collection<? extends GoEnrichment> allInstances = lkpInfo.allInstances();
                if(allInstances.isEmpty()){
                    setEnabled(false);
                    return;
                }
                setEnabled(true);
            }
        });
        
    }

    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new EnrichmentDetailsAction(context);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return new JMenuItem(this);
    }
}
