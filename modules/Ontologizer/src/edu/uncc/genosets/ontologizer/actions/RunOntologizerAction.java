/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer.actions;

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.geneontology.obo.OboWizardPanel;
import edu.uncc.genosets.geneontology.obo.OboDataObject;
import edu.uncc.genosets.ontologizer.GoEnrichment;
import edu.uncc.genosets.ontologizer.view.RunOntologizerWizardWizardIterator;
import edu.uncc.genosets.ontologizer.Ontologizer;
import edu.uncc.genosets.ontologizer.OntologizerParameters;
import edu.uncc.genosets.ontologizer.view.OntologizerExplorerTopComponent;
import edu.uncc.genosets.studyset.StudySet;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
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
import org.openide.windows.WindowManager;

@ActionID(id = "edu.uncc.genosets.ontologizer.actions.RunOntologizerAction", category = "Tools")
@ActionRegistration(iconInMenu = true, displayName = "#CTL_RunOntologizerAction", iconBase = "edu/uncc/genosets/ontologizer/resources/ontologizer_run.png")
@ActionReferences(value = {
    //    @ActionReference(path = "Menu/Analyze"),
    @ActionReference(path = "Toolbars/Analyze", position = 500),
    @ActionReference(path = "StudySet/Nodes/Feature", position = 1100)})
@NbBundle.Messages("CTL_RunOntologizerAction=Run Ontologizer")
public final class RunOntologizerAction extends AbstractAction implements LookupListener, ContextAwareAction, Presenter.Popup {

    private static final String ICON = "edu/uncc/genosets/ontologizer/resources/ontologizer_run.png";
    private Lookup context;
    Lookup.Result<StudySet> lkpInfo;
    private FocusEntity focusEntity;

    public RunOntologizerAction() {
        this(Utilities.actionsGlobalContext());
    }

    public RunOntologizerAction(Lookup context) {
        super(Bundle.CTL_RunOntologizerAction());
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon(ICON, false));
        this.context = context;
    }

    private void init() {
        assert SwingUtilities.isEventDispatchThread() :
                "this shall be called just from the AWT thread";
        if (lkpInfo != null) {
            return;
        }
        lkpInfo = context.lookupResult(StudySet.class);
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
        List<StudySet> sets = new ArrayList(lkpInfo.allInstances());
        WizardDescriptor wiz = new WizardDescriptor(new RunOntologizerWizardWizardIterator(sets, focusEntity));
        wiz.setTitleFormat(new MessageFormat("{0} ({1})"));
        wiz.setTitle("Run Ontologizer");
        wiz.putProperty(RunOntologizerWizardWizardIterator.PROP_ONTOLOGIZER_PARAMS, new OntologizerParameters());
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
            OntologizerParameters params = (OntologizerParameters) wiz.getProperty(RunOntologizerWizardWizardIterator.PROP_ONTOLOGIZER_PARAMS);
            OboDataObject obo = (OboDataObject) wiz.getProperty(OboWizardPanel.PROP_OBO);
            //String obo = (String) wiz.getProperty(OboWizardPanel.PROP_OBO);
            params.setObo(obo);
            Ontologizer ontologizer = new Ontologizer();
            List<GoEnrichment> newResults = ontologizer.runOntologizer(sets, params);
            //set default if not already set, then set it to one of the results
            if (newResults != null) {
                for (GoEnrichment goEnrichment : newResults) {
                    GoEnrichment defaultGo = goEnrichment.getStudySet().getLookup().lookup(GoEnrichment.class);
                    if (defaultGo == null) {
                        goEnrichment.setIsDefault(Boolean.TRUE);
                    }
                }
            }

            OntologizerExplorerTopComponent findTopComponent = (OntologizerExplorerTopComponent) WindowManager.getDefault().findTopComponent("OntologizerExplorerTopComponent");
            findTopComponent.open();
            findTopComponent.requestActive();
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Collection<? extends StudySet> allInstances = lkpInfo.allInstances();
                if (allInstances.isEmpty()) {
                    setEnabled(false);
                    return;
                }
                FocusEntity currentFocus = null;
                for (StudySet studySet : lkpInfo.allInstances()) {
                    if (currentFocus == null) {
                        currentFocus = studySet.getFocusEntity();
                    } else {
                        if (studySet.getFocusEntity() != currentFocus) {
                            setEnabled(false);
                            return;
                        }
                    }
                }
                focusEntity = currentFocus;
                setEnabled(true);
            }
        });
        
    }

    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new RunOntologizerAction(context);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return new JMenuItem(this);
    }
}
