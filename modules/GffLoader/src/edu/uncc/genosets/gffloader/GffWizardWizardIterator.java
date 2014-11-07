/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.gffloader;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.persister.AssembledUnitMapping;
import edu.uncc.genosets.datamanager.persister.LocationMapping;
import edu.uncc.genosets.datamanager.wizards.MethodWizardPanel;
import edu.uncc.genosets.datamanger.gff.GffLoader2;
import edu.uncc.genosets.taskmanager.SimpleTask;
import edu.uncc.genosets.taskmanager.TaskException;
import edu.uncc.genosets.taskmanager.TaskManagerFactory;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.InstantiatingIterator;
import org.openide.filesystems.FileUtil;

/**
 *
 * @author aacain
 */
public class GffWizardWizardIterator implements InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wizard;

    /**
     * Initialize panels representing individual wizard's steps and sets various
     * properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            AnnotationMethod methodConstants = new AnnotationMethod();
            methodConstants.setMethodCategory("Acquisition");
            methodConstants.setMethodType("Annotation");
            wizard.putProperty(MethodWizardPanel.PROP_AnnotationMethod, methodConstants);
            panels = new WizardDescriptor.Panel[]{
                new GffWizardWizardPanel1(),
                new MethodWizardPanel(methodConstants)
            };
            String[] steps = createSteps();
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                // Default step name to component name of panel.
                if (steps[i] == null) {
                    steps[i] = c.getName();
                }
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty(WizardDescriptor.PROP_AUTO_WIZARD_STYLE, Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DISPLAYED, Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty(WizardDescriptor.PROP_CONTENT_NUMBERED, Boolean.TRUE);
                }
            }
        }
        return panels;
    }

    private String[] createSteps() {
        String[] res = new String[panels.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = panels[i].getComponent().getName();
        }
        return res;
    }

    public Set instantiate() throws IOException {
        final WizardDescriptor wiz = wizard;
        LoadGffTask task = new LoadGffTask(wiz, "Loading GFF" + ((File) wizard.getProperty(WizardConstants.PROP_FILE)).getName());
        TaskManagerFactory.getDefault().addPendingTask(task);
        return Collections.EMPTY_SET;
    }

    private static class LoadGffTask extends SimpleTask {

        private final WizardDescriptor wizard;

        public LoadGffTask(WizardDescriptor wizard, String name) {
            super(name);
            this.wizard = wizard;
        }

        @Override
        public void performTask() throws TaskException {
            Organism org = (Organism) wizard.getProperty(WizardConstants.PROP_ORGANISM);
            File file = (File) wizard.getProperty(WizardConstants.PROP_FILE);
            AnnotationMethod method = (AnnotationMethod) wizard.getProperty(MethodWizardPanel.PROP_AnnotationMethod);
            method.setRunDate(new Date(file.lastModified()));
            method.setLoadDate(new Date());
            List transList = (List) wizard.getProperty(WizardConstants.PROP_TRANSLATE_TYPE_LIST);
            Set transSet = null;
            if (transList != null) {
                transSet = new HashSet<String>(transList);
            }

            AssembledUnitMapping assMapping = null;
            File assMappingFile = (File) wizard.getProperty(WizardConstants.PROP_ASSUNIT_MAPPING_FILE);
            if (assMappingFile != null) {
                this.setName("Reading assembled unit file for loading " + file.getName());
                assMapping = new AssembledUnitMapping();
                try {
                    assMapping.readMappings(FileUtil.toFileObject(assMappingFile).asText());
                } catch (Exception ex) {
                    throw new TaskException(ex);
                }
            }

            LocationMapping locMapping = null;
            File featureMappingFile = (File) wizard.getProperty(WizardConstants.PROP_FEATURE_MAPPING_FILE);
            if (featureMappingFile != null) {
                this.setName("Loading location mapping file for loading " + file.getName());
                locMapping = new LocationMapping();
                try {
                    locMapping.readMappings(FileUtil.toFileObject(featureMappingFile).asText());
                } catch (IOException ex) {
                    throw new TaskException(ex);
                }
            }

            InputStream is;
            try {
                is = new FileInputStream(file);
                GffLoader2.GffTask tsk = new GffLoader2.GffTask(org, method, transSet, 11, is, assMapping, locMapping, "Loading " + file.getName());
                TaskManagerFactory.getDefault().addPendingTask(tsk);
            } catch (FileNotFoundException ex) {
               throw new TaskException(ex);
            }          
        }
    }

    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
    }

    public void uninitialize(WizardDescriptor wizard) {
        index = 0;
        this.wizard = null;
        panels = null;
    }

    public WizardDescriptor.Panel current() {
        return getPanels()[index];
    }

    public String name() {
        return index + 1 + ". from " + getPanels().length;
    }

    public boolean hasNext() {
        return index < getPanels().length - 1;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }
}
