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

import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.studyset.StudySet;
import java.io.File;
import org.openide.WizardDescriptor;

/**
 *
 * @author aacain
 */
public interface DownloadService {
    public boolean download(StudySet studySet, File directory);
    public WizardDescriptor.Iterator getWizardIterator();
    public FocusEntity getFocusEntity();
    public String getDisplayName();
    public String getDescription();
}
