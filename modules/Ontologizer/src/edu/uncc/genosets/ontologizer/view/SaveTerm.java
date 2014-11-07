/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.ontologizer.view;

import edu.uncc.genosets.ontologizer.GoEnrichment;
import edu.uncc.genosets.studyset.TermCalculation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author lucy
 */
public class SaveTerm {
    public static void saveTerms(GoEnrichment go, File file){
        BufferedWriter wr = null;
        try {
            FileObject fo = FileUtil.createData(file);
            wr = new BufferedWriter(new OutputStreamWriter(fo.getOutputStream()));
            wr.write("StudySetName\tTermId\tGOName\tStudyTermTotal\tStudyTotal\tPopulationTermTotal\tPopulationTotal\tP-valueAdjusted");
            wr.newLine();
            for (TermCalculation term : go.getTermCalculationMap().values()) {
                wr.write(go.getStudySet().getName() + "\t");
                wr.write(term.getTermId() + "\t");
                wr.write(term.getGoName() + "\t");
                wr.write(term.getStudyTerm() + "\t");
                wr.write(term.getStudyTotal() + "\t");
                wr.write(term.getPopTerm() + "\t");
                wr.write(term.getPopTotal() + "\t");
                wr.write(term.getpAdjusted().toString());
                wr.newLine();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }finally{
            if(wr != null){
                try {
                    wr.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
