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
package edu.uncc.genosets.fasta.download;

import edu.uncc.genosets.bioio.Fasta;
import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.datamanager.entity.Organism;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author aacain
 */
public class DownloadProteinFasta {

    public static void download(boolean perOrganism, boolean usePrefix, FileObject dirFo, String ext, List<Integer> ids, FocusEntity focus) throws IOException {
        if (usePrefix) {
            download(perOrganism, 0, dirFo, ext, ids, focus);
        } else {
            download(perOrganism, -1, dirFo, ext, ids, focus);
        }
//        HashMap<Organism, List<Integer>> byOrganism = FastaQuery.groupLocationByOrganism(ids, focus);
//        int i = 0;
//        FileObject fo = null;
//        for (Map.Entry<Organism, List<Integer>> entry : byOrganism.entrySet()) {
//            String prefix = null;
//            if (usePrefix) {
//                StringBuilder bldr = new StringBuilder();
//                bldr.append(i);
//                int length = bldr.length();
//                while (length < 4) {
//                    bldr.insert(0, '0');
//                    length++;
//                }
//                prefix = bldr.toString();
//            }
//            List<Fasta.FastaItem> fastaItems = FastaQuery.byLocation(entry.getValue(), prefix, 0, entry.getValue().size());
//            Fasta fasta = new Fasta();
//            fasta.setItems(fastaItems);
//            if ((!perOrganism && fo == null) || perOrganism) {
//                if (usePrefix) {
//                    fo = createFreeFile(dirFo, prefix, ext, 0);
//                } else {
//                    StringBuilder orgName = new StringBuilder();
//                    orgName.append(entry.getKey().getStrain() != null ? entry.getKey().getStrain().replaceAll("[\\s{1,}.\\/]", "_") : entry.getKey().getOrganismId());
//                    fo = createFreeFile(dirFo, orgName.toString(), ext, 0);
//                }
//            }
//            OutputStream out = null;
//            try {
//                if (!perOrganism) {
//                    File file = FileUtil.toFile(fo);
//                    Fasta.createFasta(file, true, fasta, null);
//                } else {
//                    out = fo.getOutputStream();
//                    Fasta.createFasta(out, fasta, null);
//                }
//            } catch (IOException ex) {
//                Exceptions.printStackTrace(ex);
//            } finally {
//                if (out != null) {
//                    out.close();
//                }
//            }
//            i++;
//        }
    }

    public static void download(boolean perOrganism, int startPrefix, FileObject dirFo, String ext, List<Integer> ids, FocusEntity focus) throws IOException {
        HashMap<Organism, List<Integer>> byOrganism = FastaQuery.groupLocationByOrganism(ids, focus);
        int i = 0;
        if (startPrefix > -1) {
            i = startPrefix;
        }

        FileObject fo = null;
        for (Map.Entry<Organism, List<Integer>> entry : byOrganism.entrySet()) {
            String prefix = null;
            StringBuilder bldr = new StringBuilder();
            bldr.append(i);
            int length = bldr.length();
            while (length < 4) {
                bldr.insert(0, '0');
                length++;
            }
            prefix = bldr.toString();
            List<Fasta.FastaItem> fastaItems = FastaQuery.byLocation(entry.getValue(), prefix, 0, entry.getValue().size());
            Fasta fasta = new Fasta();
            fasta.setItems(fastaItems);
            if ((!perOrganism && fo == null) || perOrganism) {
                if (startPrefix > -1) {
                    fo = createFreeFile(dirFo, prefix, ext, 0);
                } else {
                    StringBuilder orgName = new StringBuilder();
                    orgName.append(entry.getKey().getStrain() != null ? entry.getKey().getStrain().replaceAll("[\\s{1,}.\\/]", "_") : entry.getKey().getOrganismId());
                    fo = createFreeFile(dirFo, orgName.toString(), ext, 0);
                }
            }
            OutputStream out = null;
            try {
                if (!perOrganism) {
                    File file = FileUtil.toFile(fo);
                    Fasta.createFasta(file, true, fasta, null);
                } else {
                    out = fo.getOutputStream();
                    Fasta.createFasta(out, fasta, null);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            i++;
        }
    }

    private static FileObject createFreeFile(FileObject dir, String name, String ext, int num) {
        if (num < 1) {
            try {
                return FileUtil.createData(dir, FileUtil.findFreeFileName(dir, name, ext) + "." + ext);
            } catch (IOException ex) {
                return createFreeFile(dir, name, ext, num++);
            }
        }
        return null;
    }
}
