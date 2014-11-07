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
package edu.uncc.genosets.commandexecutorgui;

import edu.uncc.genosets.util.CommandExecutor;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Actions",
        id = "edu.uncc.genosets.commandexecutorgui.StopAction")
@ActionRegistration(
        iconBase = "edu/uncc/genosets/icons/stop.png",
        displayName = "#CTL_StopAction")
@Messages("CTL_StopAction=Stop")
public final class StopAction extends AbstractAction {

    private final CommandExecutor context;
    
    public StopAction(CommandExecutor context) {
        super(NAME, ImageUtilities.loadImageIcon("edu/uncc/genosets/icons/stop.png", false));
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        context.cancel();
        setEnabled(false);
    }
}
