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
package edu.uncc.genosets.rast;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

public class RastLoadWizardPanel1 implements WizardDescriptor.Panel<WizardDescriptor>, ChangeListener{

    public static final String PROP_RASTFILE = "PROP_RASTFILE";
    public static final String PROP_FASTAFILE = "PROP_FASTAFILE";
    public static final String PROP_ID_TYPE = "PROP_ID_TYPE";
    public static final String PROP_ORGANISM = "PROP_ORGANISM";
    
    private ChangeSupport cs = new ChangeSupport(this);

    @Override
    public void stateChanged(ChangeEvent e) {
        this.cs.fireChange();
    }

    public enum ID_TYPE {

        GENOSETS_ID, LOAD_FASTA, ID_LOOKUP
    }
    
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private RastLoadVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public RastLoadVisualPanel1 getComponent() {
        if (component == null) {
            component = new RastLoadVisualPanel1();
            component.addChangeListener(this);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...) and
        // this condition changes (last form field filled in...) then
        // use ChangeSupport to implement add/removeChangeListener below.
        // WizardDescriptor.ERROR/WARNING/INFORMATION_MESSAGE will also be useful.
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        this.cs.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        this.cs.removeChangeListener(l);
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        // use wiz.getProperty to retrieve previous panel state
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        wiz.putProperty(PROP_FASTAFILE, getComponent().getFastaFile());
        wiz.putProperty(PROP_RASTFILE, getComponent().getRastFile());
        wiz.putProperty(PROP_ID_TYPE, getComponent().getIdType());
        wiz.putProperty(PROP_ORGANISM, getComponent().getOrganism());
    }
}
