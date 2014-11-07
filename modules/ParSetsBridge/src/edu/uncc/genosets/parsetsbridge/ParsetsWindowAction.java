/*
 * 
 * 
 */
package edu.uncc.genosets.parsetsbridge;

import edu.uncc.genosets.datamanager.api.OrthologTableCreator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Actions",
id = "edu.uncc.genosets.parsetsbridge.ParsetsWindowAction")
@ActionRegistration(iconBase = "edu/uncc/genosets/parsetsbridge/resources/PS_icon_24.png", displayName = "#CTL_ParsetsWindowAction1")
@ActionReferences(value = {
    @ActionReference(path = "Menu/Window", position = 210),
    @ActionReference(path = "Toolbars/Explore", position = 100)
})
@NbBundle.Messages({
    "CTL_ParsetsWindowAction1=Parallel Sets"
})
public final class ParsetsWindowAction implements ActionListener {

    public void actionPerformed(ActionEvent e) {

//        //see if ortholog clustering has been performed
//        if (OrthologTableCreator.isClusterPerformed()) {
            ParSetsTopComponent window = ParSetsTopComponent.findInstance();
            window.open();
            window.requestActive();
//        } else { //ortholog clustering hasn't been performed yet
//            NotifyDescriptor d = new NotifyDescriptor("Ortholog clustering has not been performed yet.  Cannot open window", "Error", NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.ERROR_MESSAGE, null, null);
//            DialogDisplayer.getDefault().notify(d);
//        }
    }
}
