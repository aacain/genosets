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
package edu.uncc.genosets.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.biojava3.core.sequence.DNASequence;
import org.biojava3.core.sequence.ProteinSequence;
import org.biojava3.core.sequence.RNASequence;
import org.biojava3.core.sequence.compound.NucleotideCompound;
import org.biojava3.core.sequence.template.SequenceView;
import org.biojava3.core.sequence.transcription.Frame;
import org.biojava3.core.sequence.transcription.TranscriptionEngine;

/**
 *
 * @author aacain
 */
public class Translate {

    HashMap<String, DNASequence> seqMap = new HashMap();
    TranscriptionEngine engine;

    public Translate() {
        TranscriptionEngine.Builder b = new TranscriptionEngine.Builder();
        b.table(11);
        b.translateNCodons(true);
        engine = b.build();
    }

    private void run(String[] args) {
        readNuc(new File(args[0]));
        readWritePos(new File(args[1]));
    }

    public static void main(String[] args) {
        Translate trans = new Translate();
        trans.run(args);
    }

    private void readNuc(File file) {
        FileInputStream fr = null;
        try {
            fr = new FileInputStream(file);
            try {
                Fasta parse = Fasta.parse(fr);
                for (Fasta.FastaItem fastaItem : parse.getItems()) {
                    seqMap.put(fastaItem.getId(), new DNASequence(fastaItem.getSequence()));
                }
            } catch (IOException ex) {
                Logger.getLogger(Translate.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fr.close();
                } catch (IOException ex) {
                    Logger.getLogger(Translate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Translate.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                Logger.getLogger(Translate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void readWritePos(File file) {
        File out = new File("protSequence");
        try {
            out.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(Translate.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(out));
        } catch (IOException ex) {
            Logger.getLogger(Translate.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(";");
                String locationId = split[1];
                String assUnitId = split[2];
                Integer start = Integer.parseInt(split[4]);
                Integer end = Integer.parseInt(split[5]);
                int min = start;
                int max = end;
                Frame frame = Frame.ONE;
                if (start > end) {
                    min = end;
                    max = start;
                    frame = Frame.REVERSED_ONE;
                }
                DNASequence seq = seqMap.get(assUnitId);
                SequenceView<NucleotideCompound> subSequence = seq.getSubSequence(min, max);
                seq = new DNASequence(subSequence.getSequenceAsString());
                RNASequence rna = seq.getRNASequence(engine, frame);
                ProteinSequence proteinSequence = rna.getProteinSequence(engine);
                bw.write(locationId);
                bw.write("\t");
                bw.write(proteinSequence.getSequenceAsString());
                bw.newLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Translate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Translate.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                br.close();
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Translate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
