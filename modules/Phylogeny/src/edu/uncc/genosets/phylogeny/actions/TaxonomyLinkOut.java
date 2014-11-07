/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.phylogeny.actions;

import edu.uncc.genosets.datamanager.entity.Organism;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.openide.awt.*;
import org.openide.awt.HtmlBrowser.URLDisplayer;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

@ActionID(category = "Actions",
        id = "edu.uncc.genosets.phylogeny.actions.TaxonomyLinkOut")
@ActionRegistration(displayName = "#CTL_TaxonomyLinkOut")
@ActionReferences({
    @ActionReference(path = "Actions/Nodes/Organism", position = 500)
})
@Messages("CTL_TaxonomyLinkOut=Link to NCBI Taxonomy")
public final class TaxonomyLinkOut extends AbstractAction implements LookupListener, ContextAwareAction, Presenter.Popup {

    private Lookup context;
    Lookup.Result<Organism> lkpInfo;

    public TaxonomyLinkOut() {
        this(Utilities.actionsGlobalContext());
    }

    public TaxonomyLinkOut(Lookup context) {
        super(Bundle.CTL_TaxonomyLinkOut());
        this.context = context;
    }

    private void init() {
        assert SwingUtilities.isEventDispatchThread() :
                "this shall be called just from the AWT thread";
        if (lkpInfo != null) {
            return;
        }
        lkpInfo = context.lookupResult(Organism.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public boolean isEnabled() {
        init();
        return super.isEnabled();
    }

    public void actionPerformed(ActionEvent ev) {
        Collection<? extends Organism> allInstances = this.lkpInfo.allInstances();
        for (Organism organism : allInstances) {
            try {
                Integer taxonomyIdentifier = organism.getTaxonomyIdentifier();
                if (taxonomyIdentifier != null) {
                    URLDisplayer.getDefault().showURL(new URL("http://www.ncbi.nlm.nih.gov/Taxonomy/Browser/wwwtax.cgi?mode=Info&id=" + Integer.toString(taxonomyIdentifier)));
                }
            } catch (MalformedURLException ex) {
            }
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Collection<? extends Organism> allInstances = lkpInfo.allInstances();
        if (allInstances.isEmpty() || allInstances.size() > 1) {
            setEnabled(false);
            return;
        } else {
            for (Organism organism : allInstances) {
                if (organism.getTaxonomyIdentifier() == null) {
                    setEnabled(false);
                }else{
                    setEnabled(true);
                }
            }
        }
    }

    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new TaxonomyLinkOut(context);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return new JMenuItem(this);
    }
}
