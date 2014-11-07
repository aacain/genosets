/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.treemap;

import edu.uncc.genosets.geneontology.obo.OboWizardPanel;
import edu.uncc.genosets.geneontology.obo.OboDataObject;
import edu.uncc.genosets.geneontology.obo.OboManager;
import edu.uncc.genosets.ontologizer.GoEnrichment;
import edu.uncc.genosets.treemap.view.GenoSetsTreeMap;
import edu.uncc.genosets.treemap.view.TreeMapLoader1;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.*;

@ActionID(id = "edu.uncc.genosets.treemap.TreeMapAction", category = "Visualizations")
@ActionRegistration(iconInMenu = true, displayName = "#CTL_TreeMapAction", iconBase = "edu/uncc/genosets/treemap/TreeMap.png")
@ActionReferences(value = {
    @ActionReference(path = "Toolbars/Explore", position = 300),
    @ActionReference(path = "Menu/Window", position = 225),
    @ActionReference(path = "StudySet/Nodes/Actions", position = 400),
    @ActionReference(path = "GoEnrichment/Nodes/Actions", position = 200)})
public final class TreeMapAction extends AbstractAction implements LookupListener, ContextAwareAction {

    private final Lookup context;
    private Lookup.Result<GoEnrichment> result;

    public TreeMapAction() {
        this(Utilities.actionsGlobalContext());
    }

    public TreeMapAction(Lookup context) {
        super("GO TreeMap", new ImageIcon(ImageUtilities.loadImage("edu/uncc/genosets/treemap/TreeMap.png")));
        this.context = context;
        init();
    }

    void init() {
        if (result != null) {
            return;
        }
        result = context.lookupResult(GoEnrichment.class);
        result.addLookupListener(this);
        if (result.allInstances().isEmpty()) {
            setEnabled(Boolean.FALSE);
        } else {
            setEnabled(Boolean.TRUE);
        }
    }

    @Override
    public boolean isEnabled() {
        //init();
        return super.isEnabled();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (result != null && result.allInstances().size() == 1) {
            GoEnrichment enrichment = null;
            for (GoEnrichment go : result.allInstances()) {
                enrichment = go;
            }
            if (enrichment != null) {
                GenoSetsTreeMap treeMap = null;
                try {
                    //see if there is a cached instance of the obo dao and use that one
                    OboDataObject obodao = null;
                    obodao = OboManager.getLastUsed();
                    if (obodao == null) {
                        //prompt the user to select the obo, filtered by full obo
                        List<WizardDescriptor.Panel<WizardDescriptor>> panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
                        panels.add(new OboWizardPanel());
                        String[] steps = new String[panels.size()];
                        for (int i = 0; i < panels.size(); i++) {
                            Component c = panels.get(i).getComponent();
                            // Default step name to component name of panel.
                            steps[i] = c.getName();
                            if (c instanceof JComponent) { // assume Swing components
                                JComponent jc = (JComponent) c;
                                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                                jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                                jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                            }
                        }
                        WizardDescriptor wiz = new WizardDescriptor(new WizardDescriptor.ArrayIterator<WizardDescriptor>(panels));
                        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
                        wiz.setTitleFormat(new MessageFormat("{0}"));
                        wiz.setTitle("Select Obo mapping file");
                        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
                            obodao = (OboDataObject) wiz.getProperty(OboWizardPanel.PROP_OBO);
                        }
                    } 
                    if (obodao != null) {
                        final TreeMapLoader1 loader = new TreeMapLoader1();
                        treeMap = loader.createTreeMap(enrichment, obodao);
                        TreeMapTopComponent window = new TreeMapTopComponent(treeMap, enrichment);
                        window.open();
                        window.requestActive();
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new TreeMapAction(context);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (result.allInstances().isEmpty()) {
            setEnabled(Boolean.FALSE);
        } else {
            setEnabled(Boolean.TRUE);
        }
    }
}
