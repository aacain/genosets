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
package edu.uncc.genosets.datamanager.connections;

import edu.uncc.genosets.connections.Connection;
import java.awt.Component;
import java.util.NoSuchElementException;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;

/**
 *
 * @author aacain
 */
public class ConnectionWizardIterator implements WizardDescriptor.Iterator {

    private int index;
    private WizardDescriptor wizardDescriptor;
    private WizardDescriptor.Panel[] allPanels;
    private WizardDescriptor.Panel[] currentPanels;
    private WizardDescriptor.Panel[] schemaValidSequence;
    private WizardDescriptor.Panel[] schemaInvalidSequence;
    private WizardDescriptor.Panel[] createDbSequence;
    private String[] schemaValidIndex;
    private String[] schemaInvalidIndex;
    private String[] createDbIndex;

    private void initializePanels() {
        if (allPanels == null) {
            allPanels = new WizardDescriptor.Panel[]{
                new ConnectionWizardPanel1(),
                new ConnectionUpdatePanel(),
                new ConnectionWizardPanel2(),
                new ConnectionCreatePanel()
            };
            String[] steps = new String[allPanels.length];
            for (int i = 0; i < allPanels.length; i++) {
                Component c = allPanels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
            this.schemaInvalidIndex = new String[]{steps[0], steps[1], steps[2]};
            this.schemaInvalidSequence = new WizardDescriptor.Panel[]{allPanels[0], allPanels[1], allPanels[2]};
            this.schemaValidIndex = new String[]{steps[0], steps[2]};
            this.schemaValidSequence = new WizardDescriptor.Panel[]{allPanels[0], allPanels[2]};
            this.createDbIndex = new String[]{steps[0], steps[3], steps[2]};
            this.createDbSequence = new WizardDescriptor.Panel[]{allPanels[0], allPanels[3], allPanels[2]};
            this.currentPanels = this.schemaValidSequence;
            this.index = 0;
        }
    }

    private void setNeedsUpdateOrNew(boolean needsUpdate, boolean needsNew) {
        String[] contentData;
        if(needsNew){
            currentPanels = createDbSequence;
            contentData = createDbIndex;
        }
        else if (needsUpdate) {
            currentPanels = schemaInvalidSequence;
            contentData = schemaInvalidIndex;
        } else {
            currentPanels = schemaValidSequence;
            contentData = schemaValidIndex;
        }
        wizardDescriptor.putProperty(WizardDescriptor.PROP_CONTENT_DATA, contentData);
    }

    @Override
    public WizardDescriptor.Panel current() {
        initializePanels();
        return currentPanels[index];
    }

    @Override
    public String name() {
        if (index == 0) {
            return index + 1 + " of ...";
        }
        return index + 1 + " of " + currentPanels.length;
    }

    @Override
    public boolean hasNext() {
        initializePanels();
        return index < currentPanels.length - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (index == 0) {
            if (Connection.TYPE_DIRECT_DB.equals(wizardDescriptor.getProperty(WizardConstants.PROP_TYPE))) {
                setNeedsUpdateOrNew((Boolean) wizardDescriptor.getProperty(WizardConstants.PROP_NEEDS_UPDATE), (Boolean) wizardDescriptor.getProperty(WizardConstants.PROP_NO_DB_EXISTS));
            }
        }
        index++;
        wizardDescriptor.putProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, index);
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
        wizardDescriptor.putProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, index);
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    void initialize(WizardDescriptor wizardDescriptor) {
        this.wizardDescriptor = wizardDescriptor;
    }
}
