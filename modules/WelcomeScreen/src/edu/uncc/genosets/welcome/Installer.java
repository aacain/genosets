/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.welcome;

import java.util.Set;
import org.openide.modules.ModuleInstall;
import org.openide.windows.*;

public class Installer extends ModuleInstall implements Runnable {

    @Override public void restored() {
        WindowManager.getDefault().invokeWhenUIReady(this);
        WindowManager.getDefault().addWindowSystemListener( new WindowSystemListener() {

            @Override
            public void beforeLoad( WindowSystemEvent event ) {
            }

            @Override
            public void afterLoad( WindowSystemEvent event ) {
            }

            @Override
            public void beforeSave( WindowSystemEvent event ) {
                WindowManager.getDefault().removeWindowSystemListener( this);
                WelcomeTopComponent topComp = null;
                boolean isEditorShowing = false;
                Set<TopComponent> tcs = TopComponent.getRegistry().getOpened();
                for (Mode mode : WindowManager.getDefault().getModes()) {
                    TopComponent tc = mode.getSelectedTopComponent();
                    if (tc instanceof WelcomeTopComponent) {                
                        topComp = (WelcomeTopComponent) tc;               
                    }
                    if( null != tc && WindowManager.getDefault().isEditorTopComponent( tc ) )
                        isEditorShowing = true;
                }
                if( WelcomeOptions.getDefault().isShowOnStartup() && isEditorShowing ) {
                    if(topComp == null){            
                        topComp = WelcomeTopComponent.findComp();
                    }
                    //activate welcome screen at shutdown to avoid editor initialization
                    //before the welcome screen is activated again at startup
                    topComp.open();
                    topComp.requestActive();
                } else if( topComp != null ) {
                    topComp.close();
                }
            }

            @Override
            public void afterSave( WindowSystemEvent event ) {
            }
        });
    }

    @Override
    public void run() {
        FeedbackSurvey.start();
    }
}
