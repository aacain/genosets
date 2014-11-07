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
package edu.uncc.genosets.util.gff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author aacain
 */
public class MapGff {

    private final ReadGff gff1;
    private final ReadGff gff2;

    public MapGff(ReadGff gff1, ReadGff gff2) {
        this.gff1 = gff1;
        this.gff2 = gff2;
    }

    public boolean map(String outputFileName) throws IOException {
        List<ReadGff.FeatureExt> f1 = this.gff1.parse();
        List<ReadGff.FeatureExt> f2 = this.gff2.parse();
        //create lookup
        Lookup lookup = new Lookup();
        lookup.createLookup(f1);
        List<ReadGff.FeatureExt[]> mapped = new LinkedList<ReadGff.FeatureExt[]>();
        for (ReadGff.FeatureExt f : f2) {
            ReadGff.FeatureExt lookup1 = lookup.lookup(f);
            if (lookup1 != null) {
                ReadGff.FeatureExt[] obj = new ReadGff.FeatureExt[]{lookup1, f};
                mapped.add(obj);
            }
        }
        if (mapped.isEmpty()) {
            return Boolean.FALSE;
        } else {
            File file = new File(outputFileName);
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            try {
                for (ReadGff.FeatureExt[] fs : mapped) {
                    bw.write(fs[0].gffId);
                    bw.write("\t");
                    bw.write(fs[1].gffId);
                    bw.newLine();
                }
                return Boolean.TRUE;
            } finally {
                try {
                    bw.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    private static class Lookup {

        List<ReadGff.FeatureExt> features = new LinkedList<ReadGff.FeatureExt>();
        Comparator<ReadGff.FeatureExt> comparator;

        Lookup() {
            comparator = new Comparator<ReadGff.FeatureExt>() {
                @Override
                public int compare(ReadGff.FeatureExt o1, ReadGff.FeatureExt o2) {

                    int ends = o1.loc.getEndPosition().compareTo(o2.loc.getEndPosition());
                    if (ends == 0) {
                        int type = o1.getFeatureType().compareTo(o2.getFeatureType());
                        if (type == 0) {
                            int strand = o1.loc.getIsForward().compareTo(o2.loc.getIsForward());
                            if (strand == 0) {
                                return o1.seqId.compareTo(o2.seqId);
                            }
                            return strand;
                        }
                        return type;
                    }
                    return ends;
                }
            };
        }

        void createLookup(List<ReadGff.FeatureExt> features) {
            Collections.sort(features, comparator);
            this.features = features;

        }

        ReadGff.FeatureExt lookup(ReadGff.FeatureExt f2) {
            int binarySearch = Collections.binarySearch(this.features, f2, comparator);
            if (binarySearch >= 0) {
                return this.features.get(binarySearch);
            }
            return null;
        }
    }
}
