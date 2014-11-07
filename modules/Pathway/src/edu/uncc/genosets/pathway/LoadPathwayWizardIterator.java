/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.pathway;

import edu.uncc.genosets.bioio.FileSelectorWizardPanel1;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.wizards.MethodWizardPanel;
import edu.uncc.genosets.taskmanager.TaskManagerFactory;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

// TODO define position attribute
@Messages("LoadPathwayWizardIterator_displayName=Load Pathway")
public final class LoadPathwayWizardIterator implements WizardDescriptor.InstantiatingIterator<WizardDescriptor> {

    private int index;
    private WizardDescriptor wizard;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;

    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
        if (panels == null) {
            AnnotationMethod method = new AnnotationMethod();
            method.setMethodCategory(NbBundle.getMessage(LoadPathwayWizardIterator.class, "Pathway.MethodCategory"));
            method.setMethodType(NbBundle.getMessage(LoadPathwayWizardIterator.class, "Pathway.MethodType"));
            method.setMethodName("autoset");
            method.setLoadDate(new Date());
            method.setRunDate(new Date());
            
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new FileSelectorWizardPanel1(null));
            panels.add(new LoadPathwayWizardPanel1());
            panels.add(new MethodWizardPanel(method));
            String[] steps = createSteps();
            for (int i = 0; i < panels.size(); i++) {
                Component c = panels.get(i).getComponent();
                if (steps[i] == null) {
                    // Default step name to component name of panel. Mainly
                    // useful for getting the name of the target chooser to
                    // appear in the list of steps.
                    steps[i] = c.getName();
                }
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
    public Set<?> instantiate() throws IOException {
        final AnnotationMethod methodConstants = (AnnotationMethod) wizard.getProperty(MethodWizardPanel.PROP_AnnotationMethod);
        Set<File> files = (Set<File>) wizard.getProperty(FileSelectorWizardPanel1.PROP_FILES);
        Integer locColumn = (Integer)wizard.getProperty(LoadPathwayWizardPanel1.PROP_LOCATION_ID);
        Integer pathIdColumn = (Integer)wizard.getProperty(LoadPathwayWizardPanel1.PROP_PATH_ID_COLUMN);
        Integer pathNameColumn = (Integer)wizard.getProperty(LoadPathwayWizardPanel1.PROP_PATH_NAME_COLUMN);
        
        if(files != null){
            for (File file : files) {
                try {
                    AnnotationMethod method = methodConstants.clone();
                    method.setMethodName(file.getName());
                    PathwayPersist pathwayPersist = new PathwayPersist(method, file, pathIdColumn, pathNameColumn, locColumn);
                   TaskManagerFactory.getDefault().addPendingTask(pathwayPersist);
                } catch (CloneNotSupportedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return Collections.emptySet();
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        panels = null;
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
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
    
    private String[] createSteps() {
        String[] res = new String[panels.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = panels.get(i).getComponent().getName();
        }
        return res;
    }
}
