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
package edu.uncc.genosets.ontologizer.actions;

import edu.uncc.genosets.datamanager.wizards.GenoSetsTemplateWizard;
import edu.uncc.genosets.ontologizer.DownloadEnrichmentWizardWizardIterator;
import edu.uncc.genosets.ontologizer.GoEnrichment;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Set;
import javax.swing.AbstractAction;
import org.openide.ErrorManager;
import org.openide.WizardDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.TemplateWizard;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;


public final class DownloadEnrichment extends AbstractAction {

    private final GoEnrichment enrichment;
    private FileObject templatesFolder;

    public DownloadEnrichment(GoEnrichment context) {
        this.enrichment = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (templatesFolder == null) {
            templatesFolder = FileUtil.getConfigRoot().getFileObject("Templates/GoEnrichment");
        }
        final TemplateWizard wizard = new GenoSetsTemplateWizard(templatesFolder);
        wizard.putProperty("PROP_ENRICHMENT", enrichment);

        final Set newObjects;
        try {
            newObjects = wizard.instantiate();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
