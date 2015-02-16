/*
 * 
 * 
 */
package edu.uncc.genosets.embl.goa;

import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.wizards.MethodWizardPanel;
import edu.uncc.genosets.geneontology.api.GeneOntology;
import edu.uncc.genosets.geneontology.api.GoTermPersister;
import edu.uncc.genosets.geneontology.obo.OboDataObject;
import edu.uncc.genosets.geneontology.obo.OboWizardPanel;
import edu.uncc.genosets.taskmanager.TaskManagerFactory;
import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressRunnable;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.InstantiatingIterator;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author aacain
 */
public class LoadAnnotationsInstantiatingIterator implements InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wizard;
    
    /**
     * Initialize panels representing individual wizard's steps and sets various
     * properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            //set AnnotationMethod constants
            AnnotationMethod method = new AnnotationMethod();
            method.setMethodCategory(NbBundle.getMessage(this.getClass(), "GoaMethodCategory"));
            method.setMethodType(NbBundle.getMessage(this.getClass(), "GoaMethodType"));
            method.setMethodSourceType(NbBundle.getMessage(this.getClass(), "GoaMethodSource"));
            Date date = new Date();
            method.setLoadDate(date);
            method.setRunDate(date);
            panels = new WizardDescriptor.Panel[]{
                //new GoaSelectWizardPanel(),
                new GoaOrganismWizardPanel(),
                new OboWizardPanel(),
                new MethodWizardPanel(method)
                //new OboSelectWizardPanel(Collections.singleton("FULL"), Boolean.TRUE)
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

    @SuppressWarnings("unchecked")
    public Set instantiate() throws IOException {
        //Set<AssembledUnit> assSet = (Set<AssembledUnit>) this.wizard.getProperty(GoaSelectWizardPanel.PROP_AssSet);
        Set<Organism> orgSet = (Set<Organism>)this.wizard.getProperty(GoaOrganismWizardPanel.PROP_ORGS);
        AnnotationMethod method = (AnnotationMethod) this.wizard.getProperty(MethodWizardPanel.PROP_AnnotationMethod);
        //final OboDataObject obodao = (OboDataObject) this.wizard.getProperty(OboSelectWizardPanel.PROP_OBO_DATAOBJECT);
        final OboDataObject obodao = (OboDataObject)this.wizard.getProperty(OboWizardPanel.PROP_OBO);
        
        
//        //sort assunits by organism
//        HashMap<Organism, Set<AssembledUnit>> orgMap = new HashMap<Organism, Set<AssembledUnit>>();
//        for (AssembledUnit assembledUnit : assSet) {
//            Set<AssembledUnit> asses = orgMap.get(assembledUnit.getOrganism());
//            if (asses == null) {
//                asses = new HashSet<AssembledUnit>();
//                orgMap.put(assembledUnit.getOrganism(), asses);
//            }
//            asses.add(assembledUnit);
//        }
        
        
        //load the obo
        ProgressRunnable<GoTermPersister> runnable = new ProgressRunnable<GoTermPersister>(){

            @Override
            public GoTermPersister run(ProgressHandle handle) {
                handle.switchToDeterminate(100);
                GoTermPersister termPersister = null;
                try {
                    // Connect
                     InputStream is = obodao.getFileObject(handle).getInputStream();
                    termPersister = edu.uncc.genosets.geneontology.api.GoTermPersister.instantiate(is, handle);
                    //termPersister = GoTermPersister.instantiate(obodao.getPrimaryInputStream(), handle);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }finally{
                    handle.finish();
                }
                return termPersister;
            }

        };
        GoTermPersister termPersister = ProgressUtils.showProgressDialogAndRun(runnable, "Loading obo file", true);
        if (termPersister != null) {
            for (Organism org : orgSet) {
                try {
                    AnnotationMethod myMethod = method.clone();
                    GeneOntologyGoaImpl go = new GeneOntologyGoaImpl(termPersister, myMethod, org);
                    //GeneOntology go = GeneOntology.instantiate(termPersister, myMethod, org);
                    TaskManagerFactory.getDefault().addPendingTask(go);
                } catch (CloneNotSupportedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
//            for (Entry<Organism, Set<AssembledUnit>> entry : orgMap.entrySet()) {
//                try {
//                    AnnotationMethod myMethod = method.clone();
//                    GeneOntology go = GeneOntology.instantiate(termPersister, myMethod, entry.getValue());
//                    TaskManagerFactory.getDefault().addPendingTask(go);
//                } catch (CloneNotSupportedException ex) {
//                    Exceptions.printStackTrace(ex);
//                }
//            }
        }
        return Collections.EMPTY_SET;
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
