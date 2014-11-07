/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.prefuse;

import prefuse.util.ColorLib;

/**
 *
 * @author aacain
 */
public class OrganismColor {

    private static int[] palette = new int[] {
            ColorLib.rgba((float)102,(float)194,(float)165, (float)0.5),
            ColorLib.rgba((float)252,(float)141,(float)98, (float)0.5),
            ColorLib.rgba((float)141,(float)160,(float)203, (float)0.5),
            ColorLib.rgba((float)231,(float)138,(float)195, (float)0.5),
            ColorLib.rgba((float)166,(float)216,(float)84, (float)0.5),
            ColorLib.rgba((float)255,(float)217,(float)47, (float)0.5),
            ColorLib.rgba((float)229,(float)169,(float)148, (float)0.5),
    };
    public static int[] getPalette(){
        return palette;
    }
}
