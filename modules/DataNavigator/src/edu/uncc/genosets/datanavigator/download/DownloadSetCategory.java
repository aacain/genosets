/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uncc.genosets.datanavigator.download;

/**
 *
 * @author aacain
 */
public class DownloadSetCategory {
    private String name;
    private String iconBase;

    public DownloadSetCategory(String name, String iconBase) {
        this.name = name;
        this.iconBase = iconBase;
    }
    
    public String getName(){
        return this.name;
    }

    public String getIconBase() {
        return iconBase;
    }
    
    
}
