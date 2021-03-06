/*
 * Copyright (C) 2013 Aurora Cain
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

package edu.uncc.genosets.studyset.download;

import edu.uncc.genosets.datamanager.wizards.GenoSetsTemplateWizard;
import edu.uncc.genosets.studyset.StudySet;
import java.util.Collection;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

/**
 *
 * @author aacain
 */
@NbBundle.Messages({"CTL_Title=Select File Type", "CTL_DownloadWizard_Title=Download Files"})
public class DownloadStudyWizard extends GenoSetsTemplateWizard{
    private final Collection<? extends StudySet> studySets;
    private String directory;

    public DownloadStudyWizard(FileObject templateFolder, Collection<? extends StudySet> studySets) {
        super(templateFolder, Bundle.CTL_Title());
        this.studySets = studySets;
        this.setTitle(Bundle.CTL_DownloadWizard_Title());
    }
    
    public Collection<? extends StudySet> getStudySets(){
        return this.studySets;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
