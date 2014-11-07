/*
 * Copyright (C) 2014 Aurora Cain
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
package edu.uncc.genosets.orthomcl;

import edu.uncc.genosets.datamanager.api.DatabaseValidator;
import edu.uncc.genosets.util.RunBash;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author aacain
 */
public class PathValidation {

    // private HashMap<String, String> results = new HashMap<String, String>();
    private HashMap<String, PathVariable> results2 = new HashMap<String, PathVariable>();
    private boolean dbUserValid;
    private boolean dbValid;
    private boolean pathValid;

    /**
     * Validates the given paths. Will also run native command if all the paths
     * do not validate. This is a longer running process and should not be
     * called from EDT.
     *
     * @return
     * @throws IOException - if there is a problem detecting the system path
     * from native execution.
     */
    public boolean validate(OrthoMclFormat format) throws IOException {

        //validate database settings
        boolean validUser = DatabaseValidator.validateUser("localhost", "3306", format.getDb_userName(), format.getDb_password());
        if (validUser) {
            this.dbUserValid = true;
            boolean dbExists = DatabaseValidator.databaseExists("localhost", "3306", format.getDb_userName(), format.getDb_password(), format.getDatabaseName());
            if (!dbExists) {
                this.dbValid = true;
            }
        }

        //validate path settings
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = false;
        if (os.indexOf("win") >= 0) {
            isWindows = true;
        }

        results2.put("perl", new PathVariable("perl", format.getPerlDir()));
        results2.put("blastp", new PathVariable("blastp", format.getBlastBin()));
        results2.put("mcl", new PathVariable("mcl", format.getMclDir()));
        results2.put("mysql", new PathVariable("mysql", format.getMysqlBin()));
        results2.put("orthomclLoadBlast", new PathVariable("orthomclLoadBlast", format.getOrthoDir()));

        if (isWindows) {
            results2.get("perl").setUserPathValid(findFile("perl.exe", format.getPerlDir()));
            results2.get("blastp").setUserPathValid(findFile("blastp.exe", format.getBlastBin()));
            results2.get("mcl").setUserPathValid(findFile("mcl.exe", format.getMclDir()));
            results2.get("mysql").setUserPathValid(findFile("mysql.exe", format.getMysqlBin()));
            results2.get("orthomclLoadBlast").setUserPathValid(findFile("orthomclLoadBlast", format.getOrthoDir()));
        } else {
            results2.get("perl").setUserPathValid(findFile("perl", format.getPerlDir()));
            results2.get("blastp").setUserPathValid(findFile("blastp", format.getBlastBin()));
            results2.get("mcl").setUserPathValid(findFile("mcl", format.getMclDir()));
            results2.get("mysql").setUserPathValid(findFile("mysql", format.getMysqlBin()));
            results2.get("orthomclLoadBlast").setUserPathValid(findFile("orthomclLoadBlast", format.getOrthoDir()));
        }
        boolean isValid = true;
        for (PathVariable var : results2.values()) {
            if (!var.isUserPathValid()) {
                isValid = false;
                break;
            }
        }
        if (!isValid) {
            //warn user if mac
            boolean canRun = true;
            if (os.indexOf("mac") >= 0) {
                NotifyDescriptor d = new NotifyDescriptor.Confirmation("This will open a Terminal windows to check system path.", NotifyDescriptor.OK_CANCEL_OPTION);
                Object notify = DialogDisplayer.getDefault().notify(d);
                if (notify != NotifyDescriptor.OK_OPTION) {
                    canRun = false;
                }
            }
            if (canRun) {
                File validatingScript = null;
                if (isWindows) {
                    validatingScript = createWindowsValidatingScript();
                } else {
                    validatingScript = createLinuxValidatingScript();
                }
                File resultsFile = File.createTempFile("pathScriptOut", ".txt");
                RunBash runScript = new RunBash();
                runScript.runHidden(validatingScript, Collections.singletonList(resultsFile.getName()));
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
                readResults(resultsFile);
            }
        }


        pathValid = true;
        for (PathVariable var : results2.values()) {
            if(!var.isValid()){
                pathValid = false;
                break;
            }
        }
        
        return pathValid && dbUserValid && dbValid;
    }

    public OrthoMclFormat attemptCorrection(OrthoMclFormat format) {
        OrthoMclFormat newFormat = new OrthoMclFormat(format, format.getStudySets());
        PathVariable var = results2.get("perl");
        if (!var.isValid()) {
            if (var.isSystemPathValid()) {
                newFormat.setPerlDir("");
            }
        }
        var = results2.get("blastp");
        if (!var.isValid()) {
            if (var.isSystemPathValid()) {
                newFormat.setBlastBin("");
            }
        }
        var = results2.get("mcl");
        if (!var.isValid()) {
            if (var.isSystemPathValid()) {
                newFormat.setMclDir("");
            }
        }
        var = results2.get("mysql");
        if (!var.isValid()) {
            if (var.isSystemPathValid()) {
                newFormat.setMysqlBin("");
            }
        }
        var = results2.get("orthomclLoadBlast");
        if (!var.isValid()) {
            if (var.isSystemPathValid()) {
                newFormat.setOrthoDir(var.getSystemPath());
            }
        }
        return newFormat;
    }

    public Collection<PathVariable> getPathVariables() {
        return results2.values();
    }

    /**
     * Test to see if the database username and password are correct.
     *
     * @return true if could connect to database.
     */
    public boolean isDbUserValid() {
        return this.dbUserValid;
    }

    /**
     * Test to see if the db is valid (does not already exist).
     *
     * @return true if database does not exist and the user can connect.
     */
    public boolean isDbValid() {
        return this.dbValid;
    }
    
    public boolean isPathValid(){
        return pathValid;
    }

    private boolean findFile(String fileName, String parentFolderName) {
        if (parentFolderName == null || parentFolderName.isEmpty()) {
            return false;
        }
        File file = new File(parentFolderName, fileName);
        if (!file.exists()) {
            return false;
        }

        return true;
    }

    private File createWindowsValidatingScript() throws IOException {
        File tempFile = File.createTempFile("pathScript", ".bat");
        //RunBash.chmod(tempFile, "744");
        InputStream is = this.getClass().getResourceAsStream("/edu/uncc/genosets/orthomcl/resources/pathScript.bat");
        if (is == null) {
            throw new IOException("Could not create script file.  Resource not found.");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(tempFile));
            String line = null;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.write("\r\n");
            }
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                }
            }
        }
        return tempFile;
    }

    private File createLinuxValidatingScript() throws IOException {
        File tempFile = File.createTempFile("pathScript", ".sh");
        System.out.println("File: " + tempFile.getAbsolutePath());
        //RunBash.chmod(tempFile, "744");
        InputStream is = this.getClass().getResourceAsStream("/edu/uncc/genosets/orthomcl/resources/pathScript.bat");
        if (is == null) {
            throw new IOException("Could not create script file.  Resource not found.");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(tempFile));
            String line = null;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.write("\n");
            }
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                }
            }
        }
        return tempFile;
    }

    private void readResults(File file) throws IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            NodeList nodes = doc.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                NodeList tests = node.getChildNodes();
                for (int j = 0; j < tests.getLength(); j++) {
                    Node item = tests.item(j);
                    if (item.getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element) item;
                        if (el.getAttribute("result").equals("0")) {
                            PathVariable var = results2.get(el.getAttribute("command"));
                            var.setSystemPath(el.getElementsByTagName("value").item(0).getTextContent());
                            var.setSystemPathValid(true);
                        }
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(PathValidation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(PathValidation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class PathVariable {

        String variableName;
        String userPath;
        boolean userPathValid = false;
        String systemPath;
        boolean systemPathValid = false;

        public PathVariable(String variableName, String userPath) {
            this.variableName = variableName;
            this.userPath = userPath;
        }

        public String getVariableName() {
            return variableName;
        }

        public void setVariableName(String variableName) {
            this.variableName = variableName;
        }

        public String getUserPath() {
            return userPath;
        }

        public void setUserPath(String userPath) {
            this.userPath = userPath;
        }

        public boolean isUserPathValid() {
            return userPathValid;
        }

        public void setUserPathValid(boolean userPathValid) {
            this.userPathValid = userPathValid;
        }

        public String getSystemPath() {
            return systemPath;
        }

        public void setSystemPath(String systemPath) {
            this.systemPath = systemPath;
        }

        public boolean isSystemPathValid() {
            return systemPathValid;
        }

        public void setSystemPathValid(boolean systemPathValid) {
            this.systemPathValid = systemPathValid;
        }

        /**
         * Tests to see if the user path is valid if provided. If user path is
         * not provided, test to see if variable exists in path.
         *
         * @return if path is provided by user and the path is valid, else tests
         * if the variable exists on the system path.
         */
        public boolean isValid() {
            if (this.userPath != null && !this.userPath.isEmpty()) {
                return userPathValid;
            } else {
                return systemPathValid;
            }
        }
    }
}
