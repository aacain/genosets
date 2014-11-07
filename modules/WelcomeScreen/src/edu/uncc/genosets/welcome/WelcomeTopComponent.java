/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package edu.uncc.genosets.welcome;

import edu.uncc.genosets.welcome.content.Constants;
import edu.uncc.genosets.welcome.ui.StartPageContent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.ref.WeakReference;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.ErrorManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.windows.TopComponent;
import org.openide.windows.TopComponentGroup;
import org.openide.windows.WindowManager;


/**
 * Taken from netbeans The welcome screen.
 * @author  Richard Gregor, S. Aubrecht, Aurora Cain (modifier)
 */
@TopComponent.Description(preferredID = "WelcomeTopComponent",
//iconBase = "edu/uncc/genosets/datanavigator/resources/bacteria-icon.png",
persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED)
@TopComponent.Registration(mode = "editor", openAtStartup = true, position = 1)
@ActionID(category = "Window", id = "edu.uncc.genosets.welcome.WelcomeTopComponent")
@ActionReference(path = "Menu/Window",
position = 820)
@TopComponent.OpenActionRegistration(displayName = "#CTL_WelcomeTopComponent",
preferredID = "WelcomeTopComponent")
@NbBundle.Messages({
    "CTL_WelcomeTopComponent=Welcome",
    "HINT_WelcomeTopComponent=This is the Welcome Window"
})
public final class WelcomeTopComponent extends TopComponent {
    static final long serialVersionUID=6021472310161712674L;
    private static WeakReference<WelcomeTopComponent> component =
                new WeakReference<WelcomeTopComponent>(null); 
    private JComponent content;

    private boolean initialized = false;
    
    public WelcomeTopComponent() {
        setLayout(new BorderLayout());
        setName(NbBundle.getMessage(WelcomeTopComponent.class, "LBL_Tab_Title"));   //NOI18N
        content = null;
        initialized = false;
        putClientProperty( "activateAtStartup", Boolean.TRUE ); //NOI18N
        putClientProperty( "KeepNonPersistentTCInModelWhenClosed", Boolean.TRUE ); //NOI18N

    }
        
    @Override protected String preferredID(){
        return "WelcomeTopComponent";    //NOI18N
    }
    
    /**
     * #38900 - lazy addition of GUI components
     */    
    
    private void doInitialize() {
        initAccessibility();
        
        if( null == content ) {
            WelcomeOptions.getDefault().incrementStartCounter();

            JScrollPane scroll = new JScrollPane(new StartPageContent());
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.getViewport().setOpaque(false);
            scroll.setOpaque(false);
            scroll.getViewport().setPreferredSize(new Dimension(Constants.START_PAGE_MIN_WIDTH,100));
            JScrollBar vertical = scroll.getVerticalScrollBar();
            if( null != vertical ) {
                vertical.setBlockIncrement(30*Constants.FONT_SIZE);
                vertical.setUnitIncrement(Constants.FONT_SIZE);
            }
            content = scroll;
            add( content, BorderLayout.CENTER );
            setFocusable( false );
        }
    }
        
    /* Singleton accessor. As WelcomeTopComponent is persistent singleton this
     * accessor makes sure that WelcomeTopComponent is deserialized by window system.
     * Uses known unique TopComponent ID "Welcome" to get WelcomeTopComponent instance
     * from window system. "Welcome" is name of settings file defined in module layer.
     */
    public static WelcomeTopComponent findComp() {
        WelcomeTopComponent wc = component.get();
        if (wc == null) {
            TopComponent tc = WindowManager.getDefault().findTopComponent("WelcomeTopComponent"); // NOI18N
            if (tc != null) {
                if (tc instanceof WelcomeTopComponent) {
                    wc = (WelcomeTopComponent)tc;
                    component = new WeakReference<WelcomeTopComponent>(wc); 
                } else {
                    //Incorrect settings file?
                    IllegalStateException exc = new IllegalStateException
                    ("Incorrect settings file. Unexpected class returned." // NOI18N
                    + " Expected:" + WelcomeTopComponent.class.getName() // NOI18N
                    + " Returned:" + tc.getClass().getName()); // NOI18N
                    ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, exc);
                    //Fallback to accessor reserved for window system.
                    wc = WelcomeTopComponent.createComp();
                }
            } else {
                //WelcomeTopComponent cannot be deserialized
                //Fallback to accessor reserved for window system.
                wc = WelcomeTopComponent.createComp();
            }
        }       
        return wc;
    }
    
    /* Singleton accessor reserved for window system ONLY. Used by window system to create
     * WelcomeTopComponent instance from settings file when method is given. Use <code>findComp</code>
     * to get correctly deserialized instance of WelcomeTopComponent. */
    public static WelcomeTopComponent createComp() {
        WelcomeTopComponent wc = component.get();
        if(wc == null) {
            wc = new WelcomeTopComponent();
            component = new WeakReference<WelcomeTopComponent>(wc);
        }
        return wc;
    }
    
    /** Overriden to explicitely set persistence type of WelcomeTopComponent
     * to PERSISTENCE_ALWAYS */
    @Override public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ONLY_OPENED;
    }
    
    private void initAccessibility(){
        getAccessibleContext().setAccessibleDescription(NbBundle.getMessage(WelcomeTopComponent.class, "ACS_Welcome_DESC")); // NOI18N
    }

    /**
     * Called when <code>TopComponent</code> is about to be shown.
     * Shown here means the component is selected or resides in it own cell
     * in container in its <code>Mode</code>. The container is visible and not minimized.
     * <p><em>Note:</em> component
     * is considered to be shown, even its container window
     * is overlapped by another window.</p>
     * @since 2.18
     *
     * #38900 - lazy addition of GUI components
     *
     */
    @Override protected void componentShowing() {
        if (!initialized) {
            initialized = true;
            doInitialize();
        }
        if( null != content && getComponentCount() == 0 ) {
            //notify components down the hierarchy tree that they should 
            //refresh their content (e.g. RSS feeds)
            add( content, BorderLayout.CENTER );
        }
        super.componentShowing();
        setActivatedNodes( new Node[] {} );
    }

    private static boolean firstTimeOpen = true;
    @Override 
    protected void componentOpened() {
        super.componentOpened();
        if( firstTimeOpen ) {
            firstTimeOpen = false;
            if( !WelcomeOptions.getDefault().isShowOnStartup() ) {
                close();
            }
        }
    }

    @Override
    protected void componentClosed() {
        super.componentClosed();
        TopComponentGroup group = WindowManager.getDefault().findTopComponentGroup("InitialLayout"); //NOI18N
        if( null != group ) {
            group.open();
            boolean firstTimeClose = NbPreferences.forModule(WelcomeTopComponent.class).getBoolean("firstTimeClose", true); //NOI18N
            NbPreferences.forModule(WelcomeTopComponent.class).putBoolean("firstTimeClose", false); //NOI18N
            if( firstTimeClose ) {
                TopComponent tc = WindowManager.getDefault().findTopComponent("projectTabLogical_tc"); //NOI18N
                if( null != tc && tc.isOpened() )
                    tc.requestActive();
            }
        }
    }
    
    @Override protected void componentHidden() {
        super.componentHidden();
        if( null != content ) {
            //notify components down the hierarchy tree that they no long 
            //need to periodically refresh their content (e.g. RSS feeds)
            remove( content );
        }
    }

    @Override
    public void requestFocus() {
        if( null != content )
            content.requestFocus();
    }

    @Override
    public boolean requestFocusInWindow() {
        if( null != content )
            return content.requestFocusInWindow();
        return super.requestFocusInWindow();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
  
}
