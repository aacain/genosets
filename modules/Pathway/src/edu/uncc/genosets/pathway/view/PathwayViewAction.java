/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.pathway.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author lucy
 */
@ActionID(category = "Window",
id = "edu.uncc.genosets.pathway.view.PathwayViewAction")
@ActionRegistration(iconBase = "edu/uncc/genosets/pathway/resources/call_graph.png", displayName = "#CTL_PathwayViewAction")
@ActionReferences(value = {
    @ActionReference(path = "StudySet/Nodes/Actions", position=300)
})
@NbBundle.Messages({
    "CTL_PathwayViewAction=Pathway List"
})
public class PathwayViewAction implements ActionListener{

    @Override
    public void actionPerformed(ActionEvent e) {
        TopComponent tc = WindowManager.getDefault().findTopComponent("PathwayTopComponent");
        tc.open();
        tc.requestActive();
    }
    
}
