/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.studyset.navigator;

import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.actions.StudySetDownloadWizardAction;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.ContextAwareAction;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 *
 * @author lucy
 */
//@ActionID(id = "edu.uncc.genosets.studyset.navigator.DownloadAction", category = "StudySet")
//@ActionRegistration(displayName = "Download")
//@ActionReference(path = "StudySet/Nodes/Actions", position=2200)
public class DownloadAction extends AbstractAction implements LookupListener, ContextAwareAction, Presenter.Popup {

    Lookup context;
    Lookup.Result<StudySet> lkpInfo;
    
    public DownloadAction() {
        this(Utilities.actionsGlobalContext());
    }

    public DownloadAction(Lookup lookup) {
        super("Download");
        this.context = lookup;
    }
    
    void init(){
        assert SwingUtilities.isEventDispatchThread() 
               : "this shall be called just from AWT thread";
 
        if (lkpInfo != null) {
            return;
        }
 
        //The thing we want to listen for the presence or absence of
        //on the global selection
        lkpInfo = context.lookupResult(StudySet.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);       
    }

    @Override
    public boolean isEnabled() {
        init();
        return super.isEnabled();
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
//        StudySetDownloadWizardAction action = new StudySetDownloadWizardAction(lkpInfo.allInstances());
//        action.actionPerformed(e);
    }

    @Override
    public Action createContextAwareInstance(Lookup lkp) {
        return new DownloadAction(lkp);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return new JMenuItem(this);
        
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        setEnabled(!lkpInfo.allInstances().isEmpty());
    }
    
}
