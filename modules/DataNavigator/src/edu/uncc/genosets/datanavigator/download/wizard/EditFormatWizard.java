/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.datanavigator.download.wizard;

import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.TemplateWizard;

/**
 *
 * @author aacain
 */
public class EditFormatWizard extends TemplateWizard {

    public void run() {
//        try {
//            //reload (DataObject.find (Templates.getTemplate (NewFileWizard.this)));
//            // bugfix #44481, check if the template is null
//            if (Templates.getTemplate(NewFileWizard.this) != null) {
//                DataObject obj = DataObject.find(Templates.getTemplate(NewFileWizard.this));
//
//                // read the attributes declared in module's layer
//                Object unknownIterator = obj.getPrimaryFile().getAttribute("instantiatingIterator"); //NOI18N
//                if (unknownIterator == null) {
//                    unknownIterator = obj.getPrimaryFile().getAttribute("templateWizardIterator"); //NOI18N
//                }
//                // set default NewFileIterator if no attribute is set
//                if (unknownIterator == null) {
//                    try {
//                        obj.getPrimaryFile().setAttribute("instantiatingIterator", NewFileIterator.genericFileIterator()); //NOI18N
//                    } catch (java.io.IOException e) {
//                        // can ignore it because a iterator will created though
//                    }
//                }
//                Hacks.reloadPanelsInWizard(NewFileWizard.this, obj);
//            }
//        } catch (DataObjectNotFoundException ex) {
//            ex.printStackTrace();
//        }
    }
    
    /**
     * Find the template with which a custom template wizard iterator is associated.
     * <p class="nonnormative">
     * If the user selects File | New File, this will be the template chosen in the first panel.
     * If the user selects New from {@link org.netbeans.spi.project.ui.support.CommonProjectActions#newFileAction}, this will
     * be the template selected from the context submenu.
     * </p>
     * @param wizardDescriptor the wizard as passed to {@link WizardDescriptor.InstantiatingIterator#initialize}
     *                         or {@link TemplateWizard.Iterator#initialize}
     * @return the corresponding template marker file (or null if not set)
     */
    public static FileObject getTemplate( WizardDescriptor wizardDescriptor ) {
        if (wizardDescriptor == null) {
            throw new IllegalArgumentException("Cannot pass a null wizardDescriptor"); // NOI18N
        }
        if ( wizardDescriptor instanceof TemplateWizard ) {
            DataObject template = ((TemplateWizard)wizardDescriptor).getTemplate();
            if (template != null) {
                return template.getPrimaryFile();            
            }
        }
        return (FileObject) wizardDescriptor.getProperty("Test");
    }
}
