/*
 * Copyright (C) 2013 Aurora Cain
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
package edu.uncc.genosets.datamanager.api;

import edu.uncc.genosets.connections.Connection;
import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.datamanager.connections.ConnectionManagerFactory;
import org.netbeans.api.autoupdate.UpdateElement;
import org.netbeans.api.autoupdate.UpdateManager;
import org.netbeans.api.autoupdate.UpdateUnit;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        Connection defaultConnection = ConnectionManagerFactory.getConnectionManager().getDefaultConnection();
        if (defaultConnection != null) {
            try {
                DataManager.testConnection(defaultConnection);
                DataManager.openConnection(defaultConnection);
            } catch (InvalidConnectionException ex) {
                ConnectionManagerFactory.getConnectionManager().setDefaultConnection(null);
                NotifyDescriptor d = new NotifyDescriptor.Message("Could not connect to the default connection.  Please update connection properties and try again.");
                DialogDisplayer.getDefault().notify(d);
            }
        }
    }
}
