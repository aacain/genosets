/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer.actions;

import edu.uncc.genosets.ontologizer.GoEnrichment;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.*;

/**
 *
 * @author aacain
 */
@ActionID(id = "edu.uncc.genosets.ontologizer.actions.SetDefaultEnrichmentAction", category = "Tools")
@ActionRegistration(iconInMenu = true, displayName = "#CTL_SetDefaultEnrichmentAction", iconBase = "edu/uncc/genosets/ontologizer/resources/ontologizer_run.png")
@ActionReferences(value = {
    @ActionReference(path = "GoEnrichment/Nodes/Actions", position = 2100)})
@NbBundle.Messages("CTL_SetDefaultEnrichmentAction=Set as Default")
public final class SetDefaultEnrichmentAction extends AbstractAction implements LookupListener, ContextAwareAction {

    private final Lookup context;
    private Lookup.Result<GoEnrichment> result;

    public SetDefaultEnrichmentAction() {
        this(Utilities.actionsGlobalContext());
    }

    public SetDefaultEnrichmentAction(Lookup context) {
        super(Bundle.CTL_SetDefaultEnrichmentAction());
        this.context = context;
    }

    void init() {
        if (result != null) {
            return;
        }
        result = context.lookupResult(GoEnrichment.class);
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
        for (GoEnrichment go : result.allInstances()) {
            go.setIsDefault(Boolean.TRUE);
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (result.allInstances().size() != 1) {
            setEnabled(Boolean.FALSE);
        } else {
            for (GoEnrichment go : result.allInstances()) {
                if(go.getIsDefault()){
                    setEnabled(Boolean.FALSE);
                    return;
                }
            }
            setEnabled(Boolean.TRUE);
        }
    }
    
        @Override
    public Action createContextAwareInstance(Lookup context) {
        return new SetDefaultEnrichmentAction(context);
    }
}
