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
package edu.uncc.genosets.embl.goa;

import java.awt.Component;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

/**
 *
 * @author aacain
 */
public class GoaOrganismWizardPanel implements WizardDescriptor.Panel<WizardDescriptor> {

    private GoaOrganismVisualPanel component;
    private WizardDescriptor wd;
    private final ChangeSupport cs = new ChangeSupport(this);
    
    public static final String PROP_ORGS = "PROP_ORGS";

    @Override
    public GoaOrganismVisualPanel getComponent() {
        if (component == null) {
            this.component = new GoaOrganismVisualPanel();
        }
        return this.component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(WizardDescriptor settings) {
        this.wd = settings;
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        this.wd.putProperty(PROP_ORGS, getComponent().getCheckedNodes());
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        synchronized (cs) {
            cs.addChangeListener(l);
        }
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        synchronized (cs) {
            cs.removeChangeListener(l);
        }
    }
    
}
