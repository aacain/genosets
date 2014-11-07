/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.orthomcl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.FileObject;

/**
 *
 * @author aacain
 */
public class ConfigFileReader {
    public static final String dbVendor = "dbVendor";
    public static final String dbConnectString = "dbConnectString";
    public static final String dbLogin = "dbLogin";
    public static final String dbPassword = "dbPassword";
    public static final String similarSequencesTable = "similarSequencesTable";
    public static final String orthologTable = "orthologTable";
    public static final String inParalogTable = "inParalogTable";
    public static final String coOrthologTable = "coOrthologTable";
    public static final String interTaxonMatchView = "interTaxonMatchView";
    public static final String percentMatchCutoff = "percentMatchCutoff";
    public static final String evalueExponentCutoff = "evalueExponentCutoff";
    public static final String oracleIndexTblSpc = "oracleIndexTblSpc";
    
    private final HashMap<String, String> propValues = new HashMap<String, String>();
    private final FileObject fo;
    
    public ConfigFileReader(FileObject fo){
        this.fo = fo;
    }
    
    
    public void readConfigFile(){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(fo.openInputStream()));
            String line = null;
            while((line = reader.readLine()) != null){
                if(!line.startsWith("#")){
                    String ss[] = line.split("=");
                    propValues.put(ss[0].trim(), ss[1].trim());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConfigFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException ex) {
                    Logger.getLogger(ConfigFileReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
    }
    
    public void installSchema(){
        //create similiarSequencesTable
        
    }
    
}
