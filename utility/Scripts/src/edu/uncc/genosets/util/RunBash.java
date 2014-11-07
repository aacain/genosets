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
package edu.uncc.genosets.util;

import edu.uncc.genosets.util.CommandExecutor.Job;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author aacain
 */
public class RunBash {

    public void run(File file) {
        Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Running command line");
        List<String> commands = new LinkedList<String>();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") >= 0) {
            commands.add("cmd.exe");
            commands.add("/C");
            commands.add("START");
            commands.add(file.getName());
            Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Running Windows");
        } else if (os.indexOf("mac") >= 0) {
            commands.addAll(Arrays.asList("osascript",
                    "-e", "tell app \"Terminal\" to do script \"cd " + file.getParentFile().getAbsolutePath() + "; "
                    + "./" + file.getName() + "\"",
                    "-e",
                    "tell app \"Terminal\" to activate"));
            Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Running mac");
        } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0) {
            Logger.getLogger(RunBash.class.getName()).log(Level.SEVERE, "Finished process.", new UnsupportedOperationException("Operating system name is not supported: " + os));
        }
        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(file.getParentFile());
            pb.redirectErrorStream(true);
            Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Starting process.");
            Process process = pb.start();
            System.out.println("Reading streams");
            Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Handling output.");
            handleProcessOutput(process.getInputStream(), "STDOUT");
            //handleProcessOutput(process.getInputStream(), "ERR");
            try {
                int waitFor = process.waitFor();
                Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Process completed with code: " + waitFor);
                System.out.println("Finished process.");
            } catch (InterruptedException ex) {
                Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Interrupted Exception running process");
                process.destroy();
            }
        } catch (IOException ex) {
            Logger.getLogger(RunBash.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void runHidden(File file, List<String> params) {
        Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Running command line");
        List<String> commands = new LinkedList<String>();
        String os = System.getProperty("os.name").toLowerCase();
        if (os.indexOf("win") >= 0) {
            commands.add("cmd.exe");
            commands.add("/C");
            commands.add(file.getName());
            if (params != null) {
                commands.addAll(params);
            }
            Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Running Windows");
        } else if (os.indexOf("mac") >= 0) {
            StringBuilder bldr = new StringBuilder();
            if(params != null){
                for (String param : params) {
                    bldr.append(" ").append(param);
                }
            }
            commands.addAll(Arrays.asList("osascript",
                    "-e", "tell app \"Terminal\" to do script \"cd " + file.getParentFile().getAbsolutePath() + "; "
                    + "./" + file.getName() + " " + bldr.toString() + "\"",
                    "-e",
                    "tell app \"Terminal\" to activate"));
            Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Running mac");
        } else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0) {
            Logger.getLogger(RunBash.class.getName()).log(Level.SEVERE, "Finished process.", new UnsupportedOperationException("Operating system name is not supported: " + os));
        }
        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(file.getParentFile());
            pb.redirectErrorStream(true);
            Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Starting process.");
            Process process = pb.start();
            System.out.println("Reading streams");
            Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Handling output.");
            handleProcessOutput(process.getInputStream(), "STDOUT");
            //handleProcessOutput(process.getInputStream(), "ERR");
            try {
                int waitFor = process.waitFor();
                Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Process completed with code: " + waitFor);
                System.out.println("Finished process.");
            } catch (InterruptedException ex) {
                Logger.getLogger(RunBash.class.getName()).log(Level.INFO, "Interrupted Exception running process");
                process.destroy();
            }
        } catch (IOException ex) {
            Logger.getLogger(RunBash.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleProcessOutput(final InputStream is, final String property) throws IOException {
        Runnable c = new Runnable() {
            @Override
            public void run() {
                BufferedReader in = null;
                PrintStream ps = System.out;
                if (property.equals("ERR")) {
                    ps = System.err;
                }

                try {
                    in = new BufferedReader(new InputStreamReader(is));

                    int c;
                    while ((c = in.read()) != -1) {
                        ps.print((char) c);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(RunBash.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        Logger.getLogger(RunBash.class
                                .getName()).log(Level.WARNING, null, ex);
                    }
                }
            }
        };
        new Thread(c).start();
    }

    public static void chmod(File file, String command) {
        CommandExecutor exec = new CommandExecutor(1);
        String os = System.getProperty("os.name").toLowerCase();
        Job job = null;
        if (os.indexOf("win") >= 0) {
            job = exec.createJob(Arrays.asList("cmd.exe", "/C", "attrib", "+X", file.getAbsolutePath()), "addExecute " + file.getAbsolutePath());
        } else if (os.indexOf("mac") >= 0 || os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0) {
            job = exec.createJob(Arrays.asList("chmod", command, file.getAbsolutePath()), "chmod " + command + " " + file.getAbsolutePath());
        }
        exec.submitAndRun(job);
        exec.shutdown();
        while (exec.isRunning()) { //wait
        }
    }

    public static void main(String[] args) {
        Option help = new Option("h", "help", false, "print this message");
        Options options1 = new Options();
        options1.addOption(help);

        Options options2 = new Options();
        options2.addOption(OptionBuilder.withArgName("file")
                .hasArg()
                .isRequired()
                .withDescription("file containing commands to process.  Blank lines and lines beginning with pound sign (#) will be ignored.")
                .withLongOpt("in")
                .create("i"));


        CommandLineParser parser = new GnuParser();
        try {
            CommandLine c1 = parser.parse(options1, args);
            if (c1.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("runBash", "", options2, "Use '-h' or '--help' to print this message", true);
            } else {
                doMain(args, options2);
            }
        } catch (ParseException ex) {
            doMain(args, options2);
        }
    }

    public static void doMain(String[] args, Options options) {
        CommandLineParser parser = new GnuParser();
        try {
            CommandLine c1 = parser.parse(options, args);
            String fileName = c1.getOptionValue("i");
//            File file = new File(fileName);
//            file = file.getAbsoluteFile();
//            RunBash bash = new RunBash();
//            bash.run(file);
            try {
                File script = createLinuxValidatingScript();
                chmod(script, "744");
                System.out.println(script.getAbsolutePath());
                RunBash bash = new RunBash();
                File tempFile = File.createTempFile("pathScriptOut", ".txt");
                bash.runHidden(script, Collections.singletonList(tempFile.getName()));
                readResults(tempFile);
            } catch (IOException ex) {
                Logger.getLogger(RunBash.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("runBash", "", options, "Use '-h' or '--help' to print this message", true);
        }
    }

    public static File createWindowsValidatingScript() throws IOException {
        File tempFile = File.createTempFile("pathScript", ".bat");
        //RunBash.chmod(tempFile, "744");
        InputStream is = ClassLoader.class.getResourceAsStream("/edu/uncc/genosets/util/resources/pathScript.bat");
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
    
    public static File createLinuxValidatingScript() throws IOException {
        File tempFile = File.createTempFile("pathScript", ".sh");
        System.out.println("File: " + tempFile.getAbsolutePath());
        //RunBash.chmod(tempFile, "744");
        InputStream is = ClassLoader.class.getResourceAsStream("/edu/uncc/genosets/util/resources/pathScript.sh");
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

    private static void readResults(File file) throws IOException{
        HashMap<String, String> results = new HashMap<String, String>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            NodeList nodes = doc.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                System.out.println(node.getNodeName());
                NodeList tests = node.getChildNodes();
                for (int j = 0; j < tests.getLength(); j++) {
                    Node item = tests.item(j);
                    if(item.getNodeType() == Node.ELEMENT_NODE){
                        Element el = (Element)item;
                        if(el.getAttribute("result").equals("0")){
                            results.put(el.getAttribute("command"), el.getElementsByTagName("value").item(0).getTextContent());
                            System.out.println(el.getAttribute("command") + el.getElementsByTagName("value").item(0).getTextContent());
                        }
                    }
                }
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(RunBash.class.getName()).log(Level.SEVERE, null, ex);
        }catch (SAXException ex) {
            Logger.getLogger(RunBash.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
