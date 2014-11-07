/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datamanager;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import org.netbeans.api.javahelp.Help;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx.Provider;
import org.openide.util.*;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author aacain
 */
@ActionID(id = "edu.uncc.genosets.datamanager.HelpAction", category = "Help")
@ActionRegistration(iconInMenu = true,
        displayName = "#CTL_HelpAction",
        iconBase = "edu/uncc/genosets/datamanager/resources/help.png")
@ActionReferences(value = {
    @ActionReference(path = "Menu/Help"),
    @ActionReference(path = "Toolbars/Help", position = 600)})
@NbBundle.Messages("CTL_HelpAction=Help")
public class HelpAction extends AbstractAction implements LookupListener, ContextAwareAction {

    private final Lookup context;
    private Lookup.Result<HelpCtx.Provider> result;

    public HelpAction() {
        this(Utilities.actionsGlobalContext());
    }

    public HelpAction(Lookup context) {
        super(Bundle.CTL_HelpAction(), new ImageIcon(ImageUtilities.loadImage("edu/uncc/genosets/datamanager/resources/help.png")));
        this.context = context;
    }

    void init() {
        if (result != null) {
            return;
        }
        result = context.lookupResult(HelpCtx.Provider.class);
        result.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public boolean isEnabled() {
        init();
        return super.isEnabled();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Provider provider : result.allInstances()) {
            if (provider.getHelpCtx() != HelpCtx.DEFAULT_HELP) {
                Help help = Lookup.getDefault().lookup(org.netbeans.api.javahelp.Help.class);
                help.showHelp(provider.getHelpCtx());
                break;
            }
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (Provider provider : result.allInstances()) {
                    if (provider.getHelpCtx() != HelpCtx.DEFAULT_HELP) {
                        setEnabled(Boolean.TRUE);
                        return;
                    }
                }
                setEnabled(Boolean.FALSE);
            }
        });
    }

    @Override
    public Action createContextAwareInstance(Lookup context) {
        return new HelpAction(context);
    }
}
