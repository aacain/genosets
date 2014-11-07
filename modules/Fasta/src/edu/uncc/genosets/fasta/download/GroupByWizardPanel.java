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
package edu.uncc.genosets.fasta.download;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;

/**
 *
 * @author aacain
 */
public class GroupByWizardPanel implements WizardDescriptor.Panel<WizardDescriptor>, ChangeListener {

    private final ChangeSupport cs = new ChangeSupport(this);
    private final boolean showOrthoMcl;

    public GroupByWizardPanel(boolean showOrthoMcl) {
        this.showOrthoMcl = showOrthoMcl;
    }
    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private GroupByVisualPanel component;
    private WizardDescriptor wiz;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @Override
    public GroupByVisualPanel getComponent() {
        if (component == null) {
            component = new GroupByVisualPanel(this.showOrthoMcl);
            component.getOrthoMclCheckBox().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    checkboxChanged();
                }
            });

        }
        return component;
    }

    private void checkboxChanged() {
        if(component.getOrthoMclCheckBox().isSelected()){
            component.getByOrganismCheckBox().setSelected(true);
            component.getByOrganismCheckBox().setEnabled(false);
        }else{
            component.getByOrganismCheckBox().setEnabled(true);
        }
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

        return true;
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
        this.wiz = wiz;
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        this.wiz.putProperty("PER_ORGANISM", component.getByOrganismCheckBox().isSelected());
        this.wiz.putProperty("ORTHOMCL", component.getOrthoMclCheckBox().isSelected());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        this.cs.fireChange();
    }
}
