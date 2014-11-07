
package edu.uncc.genosets.util;

import edu.uncc.genosets.util.gff.ConvertPatricGff;
import java.io.File;

/**
 *
 * @author aacain
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ConvertPatricGff gffConvert = new ConvertPatricGff(new File(args[0]));
        gffConvert.convert(new File(args[1]));
    }
}
