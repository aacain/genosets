/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.studyset.actions;

import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.datamanager.entity.MolecularSequence;
import edu.uncc.genosets.studyset.StudySet;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.progress.ProgressUtils;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

@ActionID(id = "edu.uncc.genosets.studyset.actions.StudySetDownloadWizardAction", category = "StudySet")
@ActionRegistration(displayName = "Download")
@ActionReference(path = "StudySet/Nodes/Actions", position=2200)
@NbBundle.Messages("CTL_DownloadAction=Download")
public final class StudySetDownloadWizardAction extends AbstractAction implements LookupListener, ContextAwareAction, Presenter.Popup {

    private WizardDescriptor.Panel[] panels;
    private final Lookup context;
    private Lookup.Result<StudySet> lkpInfo;

    public StudySetDownloadWizardAction() {
        this(Utilities.actionsGlobalContext());
    }

    public StudySetDownloadWizardAction(Lookup context) {
        super(Bundle.CTL_DownloadAction());
        this.context = context;
    }

    private void init() {
        if (lkpInfo != null) {
            return;
        }
        lkpInfo = context.lookupResult(StudySet.class);
        lkpInfo.addLookupListener(this);
        resultChanged(null);
    }

    @Override
    public boolean isEnabled() {
        init();
        return super.isEnabled();
    }

    private void createFiles(final Collection<? extends StudySet> sets, final WizardDescriptor wd) {
        final ProgressHandle handle = ProgressHandleFactory.createHandle("Starting download...");
        //create a new runnable thread that will execute this long running task
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DataManager dmgr = DataManager.getDefault();
                File file = (File) wd.getProperty(WizardConstants.PROP_DIRECTORY);
                try {
                    FileObject directory = FileUtil.createData(file);
                    if (file != null) {
                        for (StudySet studySet : sets) {
                            StringBuilder bldr = new StringBuilder("SELECT "
                                    + "P, "
                                    + "O.organismId, "
                                    + "O.strain, "
                                    + "L.minPosition, "
                                    + "L.maxPosition, "
                                    + "L.isForward, "
                                    + "L.assembledUnitId,"
                                    + " F.primaryName, "
                                    + "F.product "
                                    + "FROM ProteinSequence as P, AnnoFact as A, Organism as O, Location as L, Feature as F WHERE F.featureId = A.featureId AND L.locationId = A.locationId AND O.organismId = A.organismId AND P.molecularSequenceId = A.locationId AND F.featureId IN ( ");
                            int i = 0;
                            for (Integer integer : studySet.getIdSet()) {
                                if (i > 0) {
                                    bldr.append(", ");
                                }
                                bldr.append(integer.intValue());
                                i++;
                            }
                            bldr.append(")");
                            handle.setDisplayName("Querying database...");
                            List<Object[]> createQuery = dmgr.createQuery(bldr.toString());


                            handle.setDisplayName("Creating files...");
                            HashMap<Integer, String> orgMap = new HashMap<Integer, String>();
                            HashMap<Integer, List<Result>> sequenceMap = new HashMap<Integer, List<Result>>();
                            for (Object[] objects : createQuery) {
                                Result r = new Result();
                                r.p = (MolecularSequence) objects[0];
                                r.organismId = (Integer) objects[1];
                                r.strain = (String) objects[2];
                                r.minPosition = (Integer) objects[3];
                                r.maxPosition = (Integer) objects[4];
                                r.isForward = (Boolean) objects[5];
                                r.assembledUnitId = (Integer) objects[6];
                                r.primaryName = (String) objects[7];
                                r.product = (String) objects[8];
                                orgMap.put(r.organismId, r.strain);
                                List<Result> list = sequenceMap.get(r.organismId);
                                if (list == null) {
                                    list = new LinkedList<Result>();
                                    sequenceMap.put(r.organismId, list);
                                }
                                list.add(r);
                            }
                            boolean gff = (Boolean) wd.getProperty(WizardConstants.PROP_TYPE_GFF);
                            boolean faa = (Boolean) wd.getProperty(WizardConstants.PROP_TYPE_PROTEINFASTA);
                            boolean tab = (Boolean) wd.getProperty(WizardConstants.PROP_TYPE_TAB);
                            handle.setDisplayName("Saving files...");
                            if (wd.getProperty(WizardConstants.PROP_BY_ORGANISM).equals(Boolean.TRUE)) {
                                for (Entry<Integer, String> entry : orgMap.entrySet()) {
                                    String fileName = entry.getValue().replaceAll("[\\/:*?\"<>|]", "");
                                    BufferedWriter gffWriter = null;
                                    BufferedWriter fnaWriter = null;
                                    BufferedWriter faaWriter = null;
                                    BufferedWriter tabWriter = null;
                                    if (gff) {
                                        FileObject fo = directory.createData(fileName, "gff");
                                        gffWriter = new BufferedWriter(new OutputStreamWriter(fo.getOutputStream()));
                                    }
                                    if (faa) {
                                        FileObject fo = directory.createData(fileName, "faa");
                                        faaWriter = new BufferedWriter(new OutputStreamWriter(fo.getOutputStream()));
                                    }
                                    if (tab) {
                                        FileObject fo = directory.createData(fileName, "tsv");
                                        tabWriter = new BufferedWriter(new OutputStreamWriter(fo.getOutputStream()));
                                    }

                                    List<Result> list = sequenceMap.get(entry.getKey());
                                    BufferedWriter fastaWriter = null;
                                    BufferedWriter gfWriter = null;

                                    //create headers
                                    //faa
                                    try {
                                        if (faaWriter != null) {
                                            faaWriter.write("#>locationId|organismId|organismName");
                                            faaWriter.newLine();
                                        }
                                    } catch (IOException ex) {
                                        if (faaWriter != null) {
                                            try {
                                                faaWriter.close();
                                            } catch (IOException ex1) {
                                            }
                                        }
                                        faaWriter = null;
                                    }
                                    //tab
                                    try {
                                        if (tabWriter != null) {
                                            tabWriter.write("#strain\torganismId\tassembledUnitId\tlocationId\tminPosition\tmaxPosition\tisForward\tprimaryName\tproduct");
                                            tabWriter.newLine();
                                        }
                                    } catch (IOException ex) {
                                        if (tabWriter != null) {
                                            try {
                                                tabWriter.close();
                                            } catch (IOException ex1) {
                                            }
                                            tabWriter = null;
                                        }
                                    }

                                    //iterate results
                                    Set<Integer> idSet = new HashSet<Integer>();
                                    try {
                                        for (Result r : list) {
                                            if (!idSet.contains(r.p.getMolecularSequenceId())) {
                                                writeProteinFasta(faaWriter, r);
                                                idSet.add(r.p.getMolecularSequenceId());
                                            }
                                            writeTab(tabWriter, r);
                                        }//end result iterator
                                    } catch (IOException ex) {
                                        Exceptions.printStackTrace(ex);
                                    } finally {
                                        if (tabWriter != null) {
                                            tabWriter.close();
                                        }
                                        if (faaWriter != null) {
                                            faaWriter.close();
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    handle.finish();
                }
            }
        };//end runnable
        ProgressUtils.showProgressDialogAndRun(runnable, handle, true);
    }

    private void writeProteinFasta(BufferedWriter writer, Result r) throws IOException {
        if (writer != null) {
            writer.write(">");
            writer.write(r.p.getMolecularSequenceId().toString());
            writer.write("|");
            writer.write(r.organismId.toString());
            writer.write("|");
            writer.write(r.strain);
            writer.newLine();
            writer.write(r.p.getForwardSequence());
            writer.newLine();
        }
    }

    private void writeTab(BufferedWriter writer, Result r) throws IOException {
        writer.write(r.strain + "\t");
        writer.write(r.organismId.toString() + "\t");
        writer.write(r.assembledUnitId.toString() + "\t");
        writer.write(r.p.getMolecularSequenceId().toString() + "\t");
        writer.write(r.minPosition.toString() + "\t");
        writer.write(r.maxPosition.toString() + "\t");
        writer.write((r.isForward ? '+' : '-') + "\t");
        writer.write(r.primaryName == null ? "\t" : (r.primaryName + "\t"));
        writer.write(r.product == null ? "" : r.product);
        writer.newLine();
    }

    private void writeGff(BufferedWriter writer, Result r) throws IOException {
    }

    public @Override
    void actionPerformed(ActionEvent e) {
        Collection<? extends StudySet> sets = lkpInfo.allInstances();
        WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels());
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
        wizardDescriptor.setTitle("Download Study Sets");
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            createFiles(sets, wizardDescriptor);
        }
    }

    /**
     * Initialize panels representing individual wizard's steps and sets various
     * properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                new StudySetDownloadWizardPanel1()
            };
            String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
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
        }
        return panels;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Collection<? extends StudySet> allInstances = lkpInfo.allInstances();
        if (allInstances.isEmpty()) {
            setEnabled(false);
            return;
        }
        FocusEntity focus = null;
        int i = 0;
        for (StudySet studySet : lkpInfo.allInstances()) {
            if(i == 0){
                focus = studySet.getFocusEntity();
            }
            i++;
            if (studySet.getFocusEntity() != focus) {
                setEnabled(false);
                return;
            }
        }
        setEnabled(true);
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new StudySetDownloadWizardAction(context);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return new JMenuItem(this);
    }

    private static class Result {

        MolecularSequence p;
        Integer organismId;
        String strain;
        Integer minPosition;
        Integer maxPosition;
        Boolean isForward;
        Integer assembledUnitId;
        String primaryName;
        String product;
    }
}
