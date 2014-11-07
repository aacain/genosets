/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.fasta.download;

import edu.uncc.genosets.bioio.Fasta;
import edu.uncc.genosets.datamanager.api.DownloadException;
import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.studyset.download.DownloadStudyWizard;
import edu.uncc.genosets.studyset.download.FolderSelectionWizardPanel;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.progress.ProgressRunnable;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

//@TemplateRegistration(
//        folder = "Downloads/Location",
//        id = "LocationFastaDownloadIterator",
//        displayName = "#LocationFastaDownloadIterator_displayName",
//        iconBase = "edu/uncc/genosets/icons/download.png",
//        description = "FastaLocation.html")
//@Messages("LocationFastaDownloadIterator_displayName=FASTA Protein (faa)")
@NbBundle.Messages("NucleotideFastaDownloadIterator_displayName=FASTA Nucleotide (fna)")
public final class NucleotideFastaDownloadIterator implements WizardDescriptor.InstantiatingIterator<WizardDescriptor> {

    private int index;
    private DownloadStudyWizard wizard;
    private List<WizardDescriptor.Panel<WizardDescriptor>> panels;

    private List<WizardDescriptor.Panel<WizardDescriptor>> getPanels() {
        if (panels == null) {
            panels = new ArrayList<WizardDescriptor.Panel<WizardDescriptor>>();
            panels.add(new GroupByWizardPanel(Boolean.FALSE));
            panels.add(new FolderSelectionWizardPanel());
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
        return doRun();
    }

    private Set<?> doRun() throws IOException {
        final boolean perOrganism = (Boolean) this.wizard.getProperty("PER_ORGANISM");
        final boolean usePrefix = (Boolean) this.wizard.getProperty("ORTHOMCL");
        final String directory = this.wizard.getDirectory();
        final FileObject dirFo = FileUtil.createFolder(new File(directory));
        final Collection<? extends StudySet> studySets = this.wizard.getStudySets();
        final ProgressHandle handle = ProgressHandleFactory.createHandle("Downloading Nucleotide FASTA");
        RequestProcessor.Task bodyTask = new RequestProcessor("LoadDataBody").create(new Runnable() {
            @Override
            public void run() {
                try {
                    handle.start();
                    boolean folderPerStudySet = (perOrganism || usePrefix) && studySets.size() > 1;
                    for (StudySet studySet : studySets) {
                        FocusEntity focus = null;
                        focus = studySet.getFocusEntity();
                        HashMap<Organism, List<Integer>> byOrganism = FastaQuery.groupAssUnitByOrganism(studySet.getIdSet(), focus);
                        FileObject fo = null;
                        FileObject ssDirectory = dirFo;
                        if (folderPerStudySet) {
                            ssDirectory = createFreeFolder(dirFo, studySet.getName(), 0);
                        }
                        int i = 0;
                        for (Map.Entry<Organism, List<Integer>> entry : byOrganism.entrySet()) {
                            String prefix = null;
                            if (usePrefix) {
                                StringBuilder bldr = new StringBuilder();
                                bldr.append(i);
                                int length = bldr.length();
                                while (length < 4) {
                                    bldr.insert(0, '0');
                                    length++;
                                }
                                prefix = bldr.toString();
                            }
                            List<Fasta.FastaItem> fastaItems = FastaQuery.byAssembledUnit(entry.getValue(), prefix, 0, entry.getValue().size());
                            Fasta fasta = new Fasta();
                            fasta.setItems(fastaItems);
                            if (usePrefix) {
                                fo = createFreeFile(ssDirectory, prefix, "fna", 0);
                            } else if (perOrganism) {
                                StringBuilder orgName = new StringBuilder();
                                orgName.append(entry.getKey().getStrain() != null ? createValidFileName(entry.getKey().getStrain()) : entry.getKey().getOrganismId());
                                fo = createFreeFile(ssDirectory, orgName.toString(), "fna", 0);
                            } else if (fo == null) {
                                fo = createFreeFile(ssDirectory, createValidFileName(studySet.getName()), "fna", 0);
                            }
                            OutputStream out = null;
                            try {
                                if (usePrefix || perOrganism) { //don't append file
                                    out = fo.getOutputStream();
                                    Fasta.createFasta(out, fasta, null);
                                } else {//append to current file
                                    File file = FileUtil.toFile(fo);
                                    Fasta.createFasta(file, true, fasta, null);
                                }
                            } catch (IOException ex) {
                                Exceptions.printStackTrace(ex);
                            } finally {
                                if (out != null) {
                                    try {
                                        out.close();
                                    } catch (IOException ex) {
                                        Exceptions.printStackTrace(ex);
                                    }
                                }
                            }
                            i++;
                        }
                    }
                } finally {
                    handle.finish();
                }
            }
        });
        bodyTask.schedule(0);
        return Collections.emptySet();
    }

    private String createValidFileName(String name) {
        return name.replaceAll("[/\\\\?\"<>.\\s*|:\\^\\[\\]]+", "_");
    }

    private FileObject createFreeFile(FileObject dir, String name, String ext, int num) {
        if (num < 1) {
            try {
                return FileUtil.createData(dir, FileUtil.findFreeFileName(dir, name, ext) + "." + ext);
            } catch (IOException ex) {
                return createFreeFile(dir, name, ext, num++);
            }
        }
        return null;
    }

    private FileObject createFreeFolder(FileObject dir, String name, int num) {
        if (num < 100) {
            try {
                return FileUtil.createFolder(dir, FileUtil.findFreeFolderName(dir, name));
            } catch (IOException ex) {
                return createFreeFolder(dir, name, num++);
            }
        }
        return null;
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = (DownloadStudyWizard) wizard;
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
    // If something changes dynamically (besides moving between panels), e.g.
    // the number of panels changes in response to user input, then use
    // ChangeSupport to implement add/removeChangeListener and call fireChange
    // when needed

    // You could safely ignore this method. Is is here to keep steps which were
    // there before this wizard was instantiated. It should be better handled
    // by NetBeans Wizard API itself rather than needed to be implemented by a
    // client code.
    private String[] createSteps() {
        String[] res = new String[panels.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = panels.get(i).getComponent().getName();
        }
        return res;
    }
}
