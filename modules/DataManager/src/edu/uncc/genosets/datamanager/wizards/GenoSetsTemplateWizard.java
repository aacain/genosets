package edu.uncc.genosets.datamanager.wizards;

import javax.swing.JComponent;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.TemplateWizard;

/**
 *
 * @author aacain
 */
public class GenoSetsTemplateWizard extends TemplateWizard {

    private FileObject templatesFO;
    private String title;

    public GenoSetsTemplateWizard(FileObject fo) {
        this(fo, null);
    }

    public GenoSetsTemplateWizard(FileObject fo, String title) {
        super();
        this.templatesFO = fo;
        putProperty(LoadDataWizardPanel.TEMPLATES_FOLDER, templatesFO);
        if (title != null) {
            this.title = title;
        }else{
            this.title = Bundle.LBL_LoadDataPanel_Name();
        }
    }

    @Override
    protected WizardDescriptor.Panel<WizardDescriptor> createTemplateChooser() {
        WizardDescriptor.Panel<WizardDescriptor> panel = new LoadDataWizardPanel(this.title);
        JComponent jc = (JComponent) panel.getComponent();
        jc.setPreferredSize(new java.awt.Dimension(500, 340));
        jc.putClientProperty(WizardDescriptor.PROP_CONTENT_SELECTED_INDEX, new Integer(0));
        jc.putClientProperty(WizardDescriptor.PROP_CONTENT_DATA, new String[]{
            title,
            Bundle.LBL_LoadDataPanel_Dots()
        });

        return panel;
    }
}
