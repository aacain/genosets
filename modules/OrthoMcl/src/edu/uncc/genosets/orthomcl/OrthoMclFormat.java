/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.orthomcl;

import edu.uncc.genosets.datamanager.api.DownloadException;
import edu.uncc.genosets.datamanager.dimension.FocusEntity;
import edu.uncc.genosets.fasta.download.DownloadProteinFasta;
import edu.uncc.genosets.studyset.StudySet;
import edu.uncc.genosets.util.RunBash;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;

/**
 *
 * @author aacain
 */
public class OrthoMclFormat {

    final static String PROP_MIN_PROT_LENGTH = "PROP_MIN_PROT_LENGTH";
    final static String PROP_MAX_PERCENT_STOPS = "PROP_MAX_PERCENT_STOPS";
    final static String PROP_PERCENT_MATCH = "PROP_PERCENT_MATCH";
    final static String PROP_EVALUE_CUTOFF = "PROP_EVALUE_CUTOFF";
    static final String PROP_DO_BLASTP = "PROP_DO_BLASTP";
    static final String PROP_DO_MAKEBLAST = "PROP_DO_MAKEBLAST";
    static final String PROP_BLASTP_PARAMETERS = "PROP_BLASTP_PARAMETERS";
    static final String PROP_MAKEBLASTDB_PARAMETERS = "PROP_MAKEBLASTDB_PARAMETERS";
    private String perlDir;
    private String orthoDir;
    private String mclDir;
    private Boolean isWindows;
    private boolean runBlastp = true;
    private boolean runMakeBlastDb = true;
    private String blastpParameters = "";
    private String makeblastdbParameters = "";
    private String blastBin;
    private String databaseName;
    private String db_userName;
    private String db_password;
    private Boolean isMysql = true;
    private Boolean dropCreateDatabase = true;
    private String mysqlBin;
    private String minProteinLength = "";
    private String maxPercentStops = "";
    private String percentMatchCutoff = "";
    private String eValueCutoff = "";
    private boolean prefixOrganism;
    private boolean filePerOrganism;
    private boolean filePerMethod;
    private boolean execute;
    private String directory;
    private final Collection<? extends StudySet> studySets;
    private FileObject script;
    private int startingPrefix;

    public OrthoMclFormat(Collection<? extends StudySet> studySets) {
        this.studySets = studySets;
    }

    public OrthoMclFormat(OrthoMclFormat toClone, Collection<? extends StudySet> studySets) {
        this(studySets);
        perlDir = toClone.getPerlDir();
        orthoDir = toClone.getOrthoDir();
        mclDir = toClone.getMclDir();
        isWindows = toClone.isIsWindows();
        runBlastp = toClone.isRunBlastp();
        runMakeBlastDb = toClone.isRunMakeBlastDb();
        blastpParameters = toClone.getBlastpParameters();
        makeblastdbParameters = toClone.getMakeblastdbParameters();
        blastBin = toClone.getBlastBin();
        databaseName = toClone.getDatabaseName();
        db_userName = toClone.getDb_userName();
        db_password = toClone.getDb_password();
        isMysql = toClone.getIsMysql();
        dropCreateDatabase = toClone.getDropCreateDatabase();
        mysqlBin = toClone.getMysqlBin();
        minProteinLength = toClone.getMinProteinLength();
        maxPercentStops = toClone.getMaxPercentStops();
        percentMatchCutoff = toClone.getPercentMatchCutoff();
        eValueCutoff = toClone.geteValueCutoff();
        prefixOrganism = toClone.isPrefixOrganism();
        filePerOrganism = toClone.isFilePerOrganism();
        filePerMethod = toClone.isFilePerMethod();
        execute = toClone.isExecute();
        directory = toClone.getDirectory();
        startingPrefix = toClone.getStartingPrefix();
    }

    public Collection<? extends StudySet> getStudySets() {
        return studySets;
    }
    
    

    public Boolean isIsWindows() {
        return isWindows;
    }

    public void setIsWindows(Boolean isWindows) {
        this.isWindows = isWindows;
    }

    public String getMclDir() {
        return mclDir;
    }

    public void setMclDir(String mclDir) {
        this.mclDir = mclDir;
    }

    public String getOrthoDir() {
        return orthoDir;
    }

    public void setOrthoDir(String orthoDir) {
        this.orthoDir = orthoDir;
    }

    public String getPerlDir() {
        return perlDir;
    }

    public void setPerlDir(String perlDir) {
        this.perlDir = perlDir;
    }

    public String getBlastpParameters() {
        return blastpParameters;
    }

    public void setBlastpParameters(String blastpParameters) {
        this.blastpParameters = blastpParameters;
    }

    public String getMakeblastdbParameters() {
        return makeblastdbParameters;
    }

    public void setMakeblastdbParameters(String makeblastdbParameters) {
        this.makeblastdbParameters = makeblastdbParameters;
    }

    public String getBlastBin() {
        return blastBin;
    }

    public void setBlastBin(String blastBin) {
        this.blastBin = blastBin;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDb_password() {
        return db_password;
    }

    public void setDb_password(String db_password) {
        this.db_password = db_password;
    }

    public String getDb_userName() {
        return db_userName;
    }

    public void setDb_userName(String db_userName) {
        this.db_userName = db_userName;
    }

    public String geteValueCutoff() {
        return eValueCutoff;
    }

    public void seteValueCutoff(String eValueCutoff) {
        this.eValueCutoff = eValueCutoff;
    }

    public String getMaxPercentStops() {
        return maxPercentStops;
    }

    public void setMaxPercentStops(String maxPercentStops) {
        this.maxPercentStops = maxPercentStops;
    }

    public String getMinProteinLength() {
        return minProteinLength;
    }

    public void setMinProteinLength(String minProteinLength) {
        this.minProteinLength = minProteinLength;
    }

    public String getPercentMatchCutoff() {
        return percentMatchCutoff;
    }

    public void setPercentMatchCutoff(String percentMatchCutoff) {
        this.percentMatchCutoff = percentMatchCutoff;
    }

    public boolean isRunBlastp() {
        return runBlastp;
    }

    public void setRunBlastp(boolean runBlastp) {
        this.runBlastp = runBlastp;
    }

    public boolean isRunMakeBlastDb() {
        return runMakeBlastDb;
    }

    public void setRunMakeBlastDb(boolean runMakeBlastDb) {
        this.runMakeBlastDb = runMakeBlastDb;
    }

    public Boolean getDropCreateDatabase() {
        return dropCreateDatabase;
    }

    public void setDropCreateDatabase(Boolean dropCreateDatabase) {
        this.dropCreateDatabase = dropCreateDatabase;
    }

    public Boolean getIsMysql() {
        return isMysql;
    }

    public void setIsMysql(Boolean isMysql) {
        this.isMysql = isMysql;
    }

    public String getFormatFolderName() {
        return "OrthoMCL";
    }

    public boolean isFilePerMethod() {
        return filePerMethod;
    }

    public void setFilePerMethod(boolean filePerMethod) {
        this.filePerMethod = filePerMethod;
    }

    public boolean isFilePerOrganism() {
        return filePerOrganism;
    }

    public void setFilePerOrganism(boolean filePerOrganism) {
        this.filePerOrganism = filePerOrganism;
    }

    public boolean isPrefixOrganism() {
        return prefixOrganism;
    }

    public void setPrefixOrganism(boolean prefixOrganism) {
        this.prefixOrganism = prefixOrganism;
    }

    public String getMysqlBin() {
        return mysqlBin;
    }

    public void setMysqlBin(String mysqlBin) {
        this.mysqlBin = mysqlBin;
    }

    public boolean isExecute() {
        return execute;
    }

    public void setExecute(boolean execute) {
        this.execute = execute;
    }

    public RequestProcessor.Task download() throws DownloadException {
        final ProgressHandle handle = ProgressHandleFactory.createHandle("Downloading OrthoMCL files");
        RequestProcessor.Task bodyTask = new RequestProcessor("LoadDataBody").create(new Runnable() { // NOI18N
            @Override
            public void run() {
                try {
                    handle.start();
                    doPerform();
                    if (execute) {
                        Logger.getLogger(OrthoMclFormat.class.getName()).log(Level.INFO, "Starting to RunBash from OrthoMclFormat.");
                        RunBash bash = new RunBash();
                        bash.run(FileUtil.toFile(script));
                        Logger.getLogger(OrthoMclFormat.class.getName()).log(Level.INFO, "Completed RunBash from OrthoMclFormat");
                    }
                } catch (DownloadException ex) {
                    throw new RuntimeException(ex);
                } finally {
                    handle.finish();
                }
            }
        });
        bodyTask.schedule(0);
        return bodyTask;
    }

    private void doPerform() throws DownloadException {
        List<Integer> ids = new LinkedList<Integer>();
        FocusEntity focus = null;
        for (StudySet studySet : studySets) {
            ids.addAll(studySet.getIdSet());
            focus = studySet.getFocusEntity();
        }
        try {
            FileObject myDir = FileUtil.createFolder(new File(directory));
            //move the files to fasta folder
            FileObject fastaDir = myDir.createFolder("fasta");
            DownloadProteinFasta.download(true, startingPrefix, fastaDir, "fasta", ids, focus);
            createOrthoMclConfigFile(myDir);
            //now download the script
            if (isWindows) {
                createWindowsScript(myDir);
            } else {
                createLinuxScript(myDir);
            }
        } catch (Exception ex) {
            throw new DownloadException(ex);
        }
    }

    public FileObject getScriptFile() {
        return this.script;
    }

    private FileObject createWindowsScript(FileObject myDir) throws IOException {
        BufferedWriter br = null;
        try {
            String perl = updatePath(this.getPerlDir(), "perl", true) + " ";
            //String orthomcl = updatePath(this.getOrthoDir(), "orthomcl", false);
            String mcl = updatePath(this.getMclDir(), "mcl", true) + " ";
            String mysql = updatePath(this.getMysqlBin(), "mysql", true) + " ";


            //create the script file
            FileObject script = myDir.createData("runOrthomcl", "bat");
            //set the permissions
            File asFile = FileUtil.toFile(script);
            asFile.setReadable(true, true);
            asFile.setWritable(true, true);
            br = new BufferedWriter(new OutputStreamWriter(script.getOutputStream()));
            if (this.getDropCreateDatabase()) {
                br.append(mysql).append("-u").append(this.getDb_userName());
                if (this.getDb_password() != null && !this.getDb_password().isEmpty()) {
                    br.append(" -p").append(this.getDb_password());
                }
                br.append(" -e \"DROP DATABASE IF EXISTS ").append(this.getDatabaseName()).append("\"").append("\r\n");
                br.append(mysql).append("-u").append(this.getDb_userName());
                if (this.getDb_password() != null && !this.getDb_password().isEmpty()) {
                    br.append(" -p").append(this.getDb_password());
                }

                br.append(" -e \"CREATE DATABASE ").append(this.getDatabaseName()).append("\"").append("\r\n");
            }
            br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclInstallSchema", true)).append(" config_file install_schema.log").append("\r\n");
            if (this.isRunMakeBlastDb()) {
                br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclFilterFasta", true)).append(" fasta ").append(this.getMinProteinLength()).append(" ").append(this.getMaxPercentStops()).append("\r\n");
                br.append(updatePath(this.getBlastBin(), "makeblastdb", true)).append("makeblastdb ");
                br.append(makeblastdbParameters);
                br.append("\r\n");
            }
            if (this.isRunBlastp()) {
                br.append(updatePath(this.getBlastBin(), "blastp", true)).append(" ");
                br.append(blastpParameters);
                br.append("\r\n");
            }

            br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclBlastParser", true)).append(" blast.out fasta > simSequences.txt").append("\r\n");
            br.append(perl).append("-pe \"BEGIN { binmode STDIN; binmode STDOUT } s/\\x0D(?=\\x0A)//\" simSequences.txt > similarSequences.txt").append("\r\n");
            br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclLoadBlast", true)).append(" config_file similarSequences.txt").append("\r\n");
            br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclPairs", true)).append(" config_file orthomcl_pairs.log cleanup=no").append("\r\n");
            br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclDumpPairsFiles", true)).append(" config_file").append("\r\n");
            br.append(mcl).append("mclInput --abc -I 1.5 -o mclOutput").append("\r\n");
            br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclMclToGroups", true)).append(" my_prefix 1000 < mclOutput > groups.txt").append("\r\n");
            Logger.getLogger(OrthoMclFormat.class.getName()).log(Level.INFO, "Created Script.");
            this.script = script;
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return this.script;
    }

    private FileObject createLinuxScript(FileObject myDir) throws IOException {
        BufferedWriter br = null;
        try {
            String perl = "";
            if (!this.getPerlDir().isEmpty()) {
                perl = updatePath(this.getPerlDir(), "perl", false) + " ";
            }
            //String orthomcl = updatePath(this.getOrthoDir(), "orthomcl", false);
            String mcl = updatePath(this.getMclDir(), "mcl", false) + " ";
            String mysql = updatePath(this.getMysqlBin(), "mysql", false) + " ";

            //create the script file
            FileObject script = myDir.createData("runOrthomcl", "sh");
            //set the permissions
            File asFile = FileUtil.toFile(script);
            asFile.setReadable(true, true);
            asFile.setWritable(true, true);
            br = new BufferedWriter(new OutputStreamWriter(script.getOutputStream()));
            br.append("#!/bin/bash\n");
            br.append("set -e\n");
            if (this.getDropCreateDatabase()) {
                br.append(mysql).append(" -u").append(this.getDb_userName());
                if (this.getDb_password() != null && !this.getDb_password().isEmpty()) {
                    br.append(" -p").append(this.getDb_password());
                }
                br.append(" -e \"DROP DATABASE IF EXISTS ").append(this.getDatabaseName()).append("\"").append("\n");
                br.append(mysql).append(" -u").append(this.getDb_userName());
                if (this.getDb_password() != null && !this.getDb_password().isEmpty()) {
                    br.append(" -p").append(this.getDb_password());
                }

                br.append(" -e \"CREATE DATABASE ").append(this.getDatabaseName()).append("\"").append("\n");
            }
            br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclInstallSchema", false)).append(" config_file install_schema.log").append("\n");

            if (this.isRunMakeBlastDb()) {
                br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclFilterFasta", false)).append(" fasta ").append(this.getMinProteinLength()).append(" ").append(this.getMaxPercentStops()).append("\n");
                br.append(updatePath(blastBin, "makeblastdb", false));
                br.append(" ").append(makeblastdbParameters);
                br.append("\n");
            }
            if (this.isRunBlastp()) {
                br.append(updatePath(blastBin, "blastp", false)).append(" ");
                br.append(blastpParameters);
                br.append("\n");
            }

            br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclBlastParser", false)).append(" blast.out fasta > similarSequences.txt").append("\n");
            br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclLoadBlast", false)).append(" config_file similarSequences.txt").append("\n");
            br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclPairs", false)).append(" config_file orthomcl_pairs.log cleanup=no").append("\n");
            br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclDumpPairsFiles", false)).append(" config_file").append("\n");
            br.append(mcl).append(" mclInput --abc -I 1.5 -o mclOutput").append("\n");
            br.append(perl).append(updatePath(this.getOrthoDir(), "orthomclMclToGroups", false)).append(" my_prefix 1000 < mclOutput > groups.txt").append("\n");
            this.script = script;
            Logger.getLogger(OrthoMclFormat.class.getName()).log(Level.INFO, "Created Script.");
            RunBash.chmod(FileUtil.toFile(script), "755");
            Logger.getLogger(OrthoMclFormat.class.getName()).log(Level.INFO, "chmod complete.");
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return this.script;
    }

    private static String updatePath(String bin, String program, boolean isWindows) {
        if (bin == null) {
            bin = "";
        }
        StringBuilder bldr = new StringBuilder(bin);
        if (bldr.length() > 0) {
            if (isWindows && bldr.charAt(bldr.length() - 1) != '\\') {
                bldr.append('\\');
            }
            if (!isWindows && bldr.charAt(bldr.length() - 1) != '/') {
                bldr.append("/");
            }
        }
        bldr.append(program);
        if (bldr.toString().split("\\s+").length > 1) {
            if (isWindows) {
                bldr.insert(0, '\"').append('\"');
            } else {
                bldr.insert(0, '\'').append('\'');
            }
        }
        return bldr.toString();
    }

    private void createOrthoMclConfigFile(FileObject myDir) throws IOException {
        FileObject configFo = myDir.createData("config_file");
        //set the permissions
        File asFile = FileUtil.toFile(configFo);
        asFile.setReadable(true, true);
        asFile.setWritable(true, true);
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(new OutputStreamWriter(configFo.getOutputStream()));
            br.append("dbVendor=");
            if (this.isMysql) {
                br.append("mysql");
            } else {
                br.append("oracle");
            }
            br.newLine();
            br.append("dbConnectString=dbi:");
            if (this.isMysql) {
                br.append("mysql");
            } else {
                br.append("oracle");
            }
            br.append(":").append(this.getDatabaseName()).append("\n");
            br.append("dbLogin=").append(this.getDb_userName()).append("\n");
            br.append("dbPassword=").append(this.getDb_password()).append("\n");
            br.append("similarSequencesTable=SimilarSequences").append("\n");
            br.append("orthologTable=Ortholog").append("\n");
            br.append("inParalogTable=InParalog").append("\n");
            br.append("coOrthologTable=CoOrtholog").append("\n");
            br.append("interTaxonMatchView=InterTaxonMatch").append("\n");
            br.append("percentMatchCutoff=").append(this.getPercentMatchCutoff()).append("\n");
            br.append("evalueExponentCutoff=").append(this.geteValueCutoff()).append("\n");
            br.append("oracleIndexTblSpc=NONE").append("\n");
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getDirectory() {
        return directory;
    }

    public int getStartingPrefix() {
        return startingPrefix;
    }

    public void setStartingPrefix(int startingPrefix) {
        this.startingPrefix = startingPrefix;
    }

    public boolean validate() {


        return true;
    }

    public File createLinuxValidatingScript() throws IOException {
        File tempFile = File.createTempFile("tempFile", "sh");
        RunBash.chmod(tempFile, "744");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(tempFile));
            bw.append("#!/bin/bash\n");
            bw.append("which blastp >> scriptTest.txt");
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                }
            }
        }
        return tempFile;
    }

    public File createWindowsValidatingScript() throws IOException {
        File tempFile = File.createTempFile("tempFile", "bat");
        RunBash.chmod(tempFile, "744");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(tempFile));
            bw.append("which blastp >> scriptTest.txt");
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ex) {
                }
            }
        }
        return tempFile;
    }
    
    public static void updatePreferences(OrthoMclFormat format){
        NbPreferences.forModule(OrthoMclFormat.class).put("blast-dir", format.getBlastBin());
        NbPreferences.forModule(OrthoMclFormat.class).put("mysql-user", format.getDb_userName());
        NbPreferences.forModule(OrthoMclFormat.class).put("mysql-dir", format.getMysqlBin());
        NbPreferences.forModule(OrthoMclFormat.class).put("perl-dir", format.getPerlDir());
        NbPreferences.forModule(OrthoMclFormat.class).put("ortho-dir", format.getOrthoDir());
        NbPreferences.forModule(OrthoMclFormat.class).put("mcl-dir", format.getMclDir());
    }
}
