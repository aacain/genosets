/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer.actions;

import edu.uncc.genosets.datamanager.api.OrthologTableCreator;
import edu.uncc.genosets.ontologizer.view.EnrichmentDetailsTopComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author aacain
 */
//@ActionID(category = "Actions",
//id = "edu.uncc.genosets.ontologizer.view.EnrichmentDetailsWindowAction")
//@ActionRegistration(displayName = "#CTL_EnrichmentDetailsWindowAction", iconBase="edu/uncc/genosets/ontologizer/resources/enrichment_details.png")
////@ActionReferences(value = {
////    @ActionReference(path = "Menu/Window", position = 240),
////    @ActionReference(path = "Toolbars/Explore", position = 200),
////    //@ActionReference(path = "StudySet/Nodes/Actions", position = 100),
////    @ActionReference(path = "StudySet/Nodes/Feature", position = 1100),
////    @ActionReference(path = "GoEnrichment/Nodes/Actions", position = 100)
////})
//@NbBundle.Messages({
//    "CTL_EnrichmentDetailsWindowAction=GO Enrichment Details"
//})
public class EnrichmentDetailsWindowAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        EnrichmentDetailsTopComponent window = (EnrichmentDetailsTopComponent)WindowManager.getDefault().findTopComponent("EnrichmentDetailsTopComponent");
        window.open();
        window.requestActive();
    }
}
