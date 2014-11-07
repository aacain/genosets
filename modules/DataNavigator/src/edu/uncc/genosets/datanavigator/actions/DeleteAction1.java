/*
 * Copyright (C) 2014 Aurora Cain
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.uncc.genosets.datanavigator.actions;

import edu.uncc.genosets.datamanager.api.DeleteException;
import edu.uncc.genosets.datamanager.api.Deleter;
import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;

@ActionID(
        category = "Actions",
        id = "edu.uncc.genosets.datanavigator.actions.DeleteAction1")
@ActionRegistration(
        displayName = "#CTL_DeleteAction1")
@ActionReference(path="Toolbars/Edit", position=1)
@Messages("CTL_DeleteAction1=Delete1")
public final class DeleteAction1 extends org.openide.actions.DeleteAction {

    private final Lookup context;
    private final RequestProcessor.Task bodyTask;

    public DeleteAction1() {
        this(Utilities.actionsGlobalContext());
    }

    public DeleteAction1(Lookup context) {
        super();
        this.context = context;
        bodyTask = new RequestProcessor("DeleteBody").create(new Runnable() { // NOI18N
            @Override
            public void run() {
                doPerform();
            }
        });
    }

    private void doPerform() {
        Collection<? extends Deleter> lkpInfo = context.lookupAll(Deleter.class);
        for (Deleter deleter : lkpInfo) {
            try {
                deleter.delete();
            } catch (DeleteException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        bodyTask.schedule(0);
        if ("waitFinished".equals(ev.getActionCommand())) {
            bodyTask.waitFinished();
        }
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return super.createContextAwareInstance(actionContext);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public void setEnabled(boolean value) {
        super.setEnabled(value);
    }

}
