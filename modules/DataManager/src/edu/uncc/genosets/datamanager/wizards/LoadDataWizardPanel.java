/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datamanager.wizards;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.TemplateWizard;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.AsyncGUIJob;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

public class LoadDataWizardPanel implements WizardDescriptor.Panel<WizardDescriptor> {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private LoadDataVisualPanel panel;
    private final ChangeSupport changeSupport = new ChangeSupport(this);
    private WarmupJob warmUp;
    private boolean warmUpActive;
    private WizardDescriptor wizard;
    private boolean needsReselect = false;
    public final static String TEMPLATES_FOLDER = "TEMPLATES_FOLDER";
    private final String title;

    
    public LoadDataWizardPanel(){
        this.title = Bundle.LBL_LoadDataPanel_Name();
    }
    public LoadDataWizardPanel(String title) {
        this.title = title;
    }
    
    

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    @NbBundle.Messages({"LBL_LoadDataPanel_Name=Select Data Type",
        "LBL_LoadDataPanel_Dots=...",
        "CTL_FileType=File Type"})
    @Override
    public LoadDataVisualPanel getComponent() {
        if (panel == null) {
            LoadDataVisualPanel.Builder firer = new Builder();
            panel = new LoadDataVisualPanel(firer);
            panel.setWizardDescriptor(wizard);
            Utilities.attachInitJob(panel, getWarmUp());
            this.warmUpActive = true;
            this.panel.setName(title);
        }
        return panel;
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
        return this.getComponent().getSelectedTemplate() != null;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        changeSupport.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        changeSupport.removeChangeListener(l);
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        this.wizard = wiz;
        panel.setWizardDescriptor(wizard);
        TemplateWizard wd = (TemplateWizard) wiz;
        wd.putProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{
            this.title,
            Bundle.LBL_LoadDataPanel_Dots()});
        FileObject templatesFolder = (FileObject) wd.getProperty(LoadDataWizardPanel.TEMPLATES_FOLDER);
        if (templatesFolder != null && (wd.getTemplate() == null || needsReselect)) {
            if (isWarmUpActive()) {
                WarmupJob wup = getWarmUp();
                wup.setTemplatesFolder(templatesFolder);
            }
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        TemplateWizard wd = (TemplateWizard) wiz;
        FileObject fo = this.getComponent().getSelectedTemplate();
        if (fo != null && fo.isValid()) {
            try {
                wd.setTemplate(DataObject.find(fo));
            } catch (DataObjectNotFoundException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
    }

    private synchronized boolean isWarmUpActive() {
        return warmUpActive;
    }

    private synchronized WarmupJob getWarmUp() {
        if (this.warmUp == null) {
            this.warmUp = new WarmupJob();
        }
        return this.warmUp;
    }

    private class WarmupJob implements AsyncGUIJob {

        private FileObject templatesFolder;
        private String category;
        private String template;

        @Override
        public void construct() {
            panel.warmUp(this.templatesFolder);
        }

        @Override
        public void finished() {
            Cursor cursor = null;
            try {
                cursor = panel.getCursor();
                panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                panel.doFinished(this.templatesFolder, this.category, this.template);
            } finally {
                if (cursor != null) {
                    panel.setCursor(cursor);
                }
                synchronized (LoadDataWizardPanel.this) {
                    warmUpActive = false;
                }
            }
        }

        void setTemplatesFolder(FileObject fo) {
            this.templatesFolder = fo;
        }

        void setSelectedCategory(String s) {
            this.category = s;
        }

        void setSelectedTemplate(String s) {
            this.template = s;
        }
    }

    private static class TemplateChildren extends Children.Keys<DataObject> {

        private DataFolder root;
        private final String filterText;

        public TemplateChildren(DataFolder folder, String filterText) {
            this.root = folder;
            this.filterText = filterText;
        }

        @Override
        protected void addNotify() {
            setKeys(root.getChildren());
        }

        @Override
        protected void removeNotify() {
            this.setKeys(new DataObject[0]);
        }

        @Override
        protected Node[] createNodes(DataObject dobj) {
            if(dobj instanceof DataFolder){
                return new Node[]{
                        new FilterNode(dobj.getNodeDelegate(), new TemplateChildren((DataFolder) dobj, filterText))
                };
            }else{
                return new Node[]{new FilterNode(dobj.getNodeDelegate(), Children.LEAF)};
            }
            //return new Node[0];
//            if (dobj instanceof DataFolder) {
//                DataFolder folder = (DataFolder) dobj;
//                int type = 0;   //Empty folder or File folder
//                for (DataObject child : folder.getChildren()) {
//                    type = 1;
//                    if (child.getPrimaryFile().isFolder()) {
//                        type = 2;   //Folder folder
//                        break;
//                    }
//                }
//                if (type == 1) {
//                    Node categoryNode = new FilterNode(dobj.getNodeDelegate(), Children.LEAF);
//                    boolean hasFilteredChildren = false;
//                    for (DataObject child : folder.getChildren()) {
//                        if (child.isTemplate()) {
//                            if (null == filterText || child.getNodeDelegate().getDisplayName().toLowerCase().contains(filterText.toLowerCase())) {
//                                hasFilteredChildren = true;
//                                break;
//                            }
//                        }
//                    }
//                    if (hasFilteredChildren) {
//                        return new Node[]{categoryNode};
//                    }
//                } else if (type == 2) {
//                    return new Node[]{
//                        new FilterNode(dobj.getNodeDelegate(), new TemplateChildren((DataFolder) dobj, filterText))
//                    };
//                }
//            }
//            return new Node[0];
        }
    }

    private class Builder implements LoadDataVisualPanel.Builder {

        @Override
        public Children createTemplatesChildren(DataFolder folder, String filterText) {
            assert folder != null : "Folder cannot be null.";
            return new TemplateChildren(folder, filterText);
        }

        @Override
        public String getTemplatesName() {
            return Bundle.CTL_FileType();
        }

        @Override
        public void fireChange() {
            changeSupport.fireChange();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (null != wizard) {
                wizard.doNextClick();
            }
        }
    }
}
