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

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.rast.RastLoader;
import edu.uncc.genosets.datamanager.wizards.MethodWizardPanel;
import edu.uncc.genosets.taskmanager.TaskManagerFactory;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.ChangeSupport;

public final class RastLoadWizardIterator implements WizardDescriptor.InstantiatingIterator<WizardDescriptor>, ChangeListener {

    private int index;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;
    private WizardDescriptor wizard;
    private ChangeSupport cs = new ChangeSupport(this);
    

    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
        if (panels == null) {
            MethodWizardPanel rastMethodPanel = new MethodWizardPanel(new AnnotationMethod());
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            RastLoadWizardPanel1 rastWizard1 = new RastLoadWizardPanel1();
            panels.add(rastWizard1);
            panels.add(rastMethodPanel);
            String[] steps = new String[panels.size()];
            for (int i = 0; i < panels.size(); i++) {
                Component c = panels.get(i).getComponent();
                // Default step name to component name of panel.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, i);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, true);
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, true);
                }
            }
        }
        return panels;
    }

    @Override
    public WizardDescriptor.Panel<WizardDescriptor> current() {
        return getPanels().get(index);
    }

    @Override
    public String name() {
        return index + 1 + ". from " + getPanels().size();
    }

    @Override
    public boolean hasNext() {
        return index < getPanels().size() - 1;
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
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public void addChangeListener(ChangeListener l) {
        this.cs.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        this.cs.removeChangeListener(l);
    }
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then use
    // ChangeSupport to implement add/removeChangeListener and call fireChange
    // when needed

    @Override
    public Set instantiate() throws IOException {
        AnnotationMethod method = (AnnotationMethod)this.wizard.getProperty(MethodWizardPanel.PROP_AnnotationMethod);
        File rastFile = (File)this.wizard.getProperty(RastLoadWizardPanel1.PROP_RASTFILE);
        File fastaFile = (File)this.wizard.getProperty(RastLoadWizardPanel1.PROP_FASTAFILE);
        Organism organism = (Organism)this.wizard.getProperty(RastLoadWizardPanel1.PROP_ORGANISM);
        RastLoadWizardPanel1.ID_TYPE idType = (RastLoadWizardPanel1.ID_TYPE)this.wizard.getProperty(RastLoadWizardPanel1.PROP_ID_TYPE);
        InputStream fastaStream = null;
        if(idType == RastLoadWizardPanel1.ID_TYPE.LOAD_FASTA){
            fastaStream = new FileInputStream(fastaFile);
        }
        
        InputStream rastStream = new FileInputStream(rastFile);
        RastLoader loader = new RastLoader(organism, method, rastStream, method, fastaStream, 11, false);
        //RastLoader loader = new RastLoader(organism, (AnnotationMethod)this.wizard.getProperty(MethodWizardPanel.PROP_AnnotationMethod), rastStream, (AnnotationMethod)this.wizard.getProperty(MethodWizardPanel.PROP_AnnotationMethod), fastaStream, 11);
        
        
        TaskManagerFactory.getDefault().addPendingTask(loader);
        return Collections.EMPTY_SET;
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        index = 0;
        this.wizard = null;
        panels = null;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
//        RastLoadWizardPanel1.ID_TYPE idType = rastWizard1.getComponent().getIdType();
//        if (idType != null && idType.equals(RastLoadWizardPanel1.ID_TYPE.LOAD_FASTA)) {
//            MethodWizardPanel fastaMethodPanel = new MethodWizardPanel(new AnnotationMethod());
//            fastaMethodPanel.setMethodPropertyName("PROP_FASTA_METHOD");
//            this.getPanels().add(fastaMethodPanel);
//            this.cs.fireChange();
//        }
    }
}
