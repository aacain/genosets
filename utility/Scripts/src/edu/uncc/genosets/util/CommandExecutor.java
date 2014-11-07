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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author aacain
 */
public class CommandExecutor {

    ExecutorService exec;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private File log;
    private final File workingDirectory;
    private final LinkedList<Job> awaitingJobQueue = new LinkedList<Job>();

    /**
     * Creates a new command executor. Uses the user.dir system variable as the
     * working directory.
     *
     * @param concurrentJobs - the number of jobs to run concurrently. The jobs
     * will run sequentially with concurrentJobs set to 1 (sequentially by the
     * order in which the jobs were submitted)
     *
     */
    public CommandExecutor(int concurrentJobs) {
        this(new File(System.getProperty("user.dir")), null, concurrentJobs);
    }

    /**
     * Create a new command executor with the given log file to execute in the
     * user.dir.
     *
     * @param concurrentJobs - the number of jobs to run concurrently. The jobs
     * will run sequentially with concurrentJobs set to 1 (sequentially by the
     * order in which the jobs were submitted)
     * @param log - file to write log information
     *
     */
    public CommandExecutor(int concurrentJobs, File log) {
        this(new File(System.getProperty("user.dir")), log, concurrentJobs);
    }

    /**
     * Creates a new command executor with the given log file to execute in the
     * given workingdirectory.
     *
     * @param workingDirectory - the directory in which commands will be run.
     * @param log - file to write log information
     * @param concurrentJobs - the number of jobs to run concurrently. The jobs
     * will run sequentially with concurrentJobs set to 1 (sequentially by the
     * order in which the jobs were submitted)
     */
    public CommandExecutor(File workingDirectory, File log, int concurrentJobs) {
        this(workingDirectory, log, concurrentJobs, null);
    }
    
    /**
     * Creates a new command executor with the given log file to execute in the
     * given workingdirectory.
     *
     * @param workingDirectory - the directory in which commands will be run.
     * @param log - file to write log information
     * @param concurrentJobs - the number of jobs to run concurrently. The jobs
     * will run sequentially with concurrentJobs set to 1 (sequentially by the
     * order in which the jobs were submitted)
     * @param path = the path extension to add to the environment.
     */
    public CommandExecutor(File workingDirectory, File log, int concurrentJobs, String path) {
        exec = concurrentJobs == 1 ? Executors.newSingleThreadExecutor() : Executors.newFixedThreadPool(concurrentJobs);
        this.workingDirectory = workingDirectory;
        this.log = log;
        Runnable listener = new Runnable() {
            @Override
            public void run() {
                while (isRunning()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        firePropertyChange(new PropertyChangeEvent(this, "SHUTDOWN", null, "Executor interrupted, will shutdown now.\n"));
                    }
                }
                firePropertyChange(new PropertyChangeEvent(this, "SHUTDOWN", null, "Executor shutdown.\n"));
            }
        };
        (new Thread(listener)).start();
    }

    /**
     * Tests to see if this command executor is currently running.
     *
     * @return - true is executor is running and is not terminated.
     */
    public boolean isRunning() {
        return !exec.isTerminated();
    }

    /**
     * Attempts to stop all running commands. See {@link ExecutorService#shutdownNow()
     * } for more information
     *
     * @return list of jobs that never commenced execution.
     */
    public List<Job> cancel() {
        List<Runnable> shutdownNow;
        synchronized (this) {
            shutdownNow = exec.shutdownNow();
        }
        List<Job> cancelled = new ArrayList(shutdownNow.size());
        for (Runnable r : shutdownNow) {
            if (r instanceof Job) {
                cancelled.add((Job) r);
            }
        }
        return cancelled;
    }

    /**
     * Initiates an orderly shutdown of all jobs submitted to this executor. See {@link ExecutorService#shutdown()
     * } for more information.
     */
    public void shutdown() {
        this.exec.shutdown();
    }

    /**
     * Submit job and begin execution. To submit jobs for later execution see {@link CommandExecutor#submit(edu.uncc.genosets.util.CommandExecutor.Job) .
     *
     * @param job
     */
    public void submitAndRun(Job job) {
        exec.submit(job);
    }

    /**
     * Submit jobs and begin execution. To submit jobs for later execution see {@link CommandExecutor#submit(java.util.List) .
     *
     * @param jobs
     */
    public void submitAndRun(List<Job> jobs) {
        for (Job job : jobs) {
            exec.submit(job);
        }
    }

    public void submit(Job job) {
        this.awaitingJobQueue.add(job);
    }

    public void submit(List<Job> jobs) {
        for (Job job : jobs) {
            exec.submit(job);
        }
    }

    /**
     * Executes all submitted and pending tasks and shutsdown this executor.
     */
    public void executeAll() {
        Job job = null;
        while ((job = this.awaitingJobQueue.poll()) != null) {
            exec.submit(job);
        }
        exec.shutdown();
    }

    /**
     * Converts a line to a job. (ignoring blank lines and lines beginning with
     * pound (#) sign)
     *
     * @param line to convert
     * @return new job with given commands and input and output redirections.
     * Returns null if the line is blank or begins with pound sign.
     *
     */
    public Job convertLineToJob(String line) {
        if (line.isEmpty() || line.startsWith("#")) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\\"\\s*([^\\\"]*)\\s*\\\"");
        ArrayList<int[]> matches = new ArrayList<int[]>();
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            matches.add(new int[]{matcher.start(), matcher.end()});
        }
        ArrayList<String> words = new ArrayList<String>();
        int currentStart = 0;
        for (int[] match : matches) {
            if (match[0] == currentStart) {
            } else {
                String[] prefix = line.substring(currentStart, match[0]).split("\\s+");
                words.addAll(Arrays.asList(prefix));
            }
            words.add(line.substring(match[0], match[1]));
            currentStart = match[1];
        }
        if (currentStart < line.length()) {
            words.addAll(Arrays.asList(line.substring(currentStart).trim().split("\\s+")));
        }
        ArrayList<String> commands = new ArrayList();
        ArrayList<String> reout = new ArrayList();
        ArrayList<String> rein = new ArrayList();
        boolean append = false;
        List<String> currentList = commands;
        for (int i = 0; i < words.size(); i++) {
            if (words.get(i).matches("<")) {
                currentList = rein;
            } else if (words.get(i).matches(">")) {
                currentList = reout;
            } else if (words.get(i).matches(">>")) {
                currentList = reout;
                append = true;
            } else {
                currentList.add(words.get(i));
            }
        }
        String out = null;
        String in = null;
        if (!reout.isEmpty()) {
            out = reout.get(0);
        }
        if (!rein.isEmpty()) {
            in = rein.get(0);
        }
        Job job = new Job(commands, line, out, append, in);
        return job;
    }

    /**
     * Reads all lines in a file (ignoring blank lines and lines beginning with
     * pound (#) sign) and converts each line to a job. See {@link #convertLineToJob(java.lang.String)
     * } for more information.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public List<Job> convertFileToJobs(File file) throws IOException {
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(file));
        String line = null;
        ArrayList<Job> jobs = new ArrayList<Job>();
        while ((line = br.readLine()) != null) {
            Job job = convertLineToJob(line);
            if (job != null) {
                jobs.add(job);
            }
        }
        return jobs;
    }

    /**
     * Creates a job with the given commands. The job is not executed until {@link #submitAndRun(edu.uncc.genosets.util.CommandExecutor.Job)
     * } is called.
     *
     * @param commands - commands to execute
     * @return the new job that is ready for execution.
     */
    public Job createJob(List<String> commands, String fullCommand) {
        return createJob(fullCommand, commands, null, false, null);
    }

    /**
     * Creates a job with the given commands, and output path. The job is not
     * executed until {@link #submitAndRun(edu.uncc.genosets.util.CommandExecutor.Job)
     * } is called. If appendOutput is set to true, then the output file will be
     * appended and not overwritten.
     *
     * @param fullCommand - the full string of the command to execute.
     * @param commands - commands to execute
     * @param redirectOutput - path of file to redirect output to
     * @param appendOutput - whether the output file should be appended or
     * overwritten.
     * @return the new job that is ready for execution.
     */
    public Job createJob(List<String> commands, String fullCommand, String redirectOutput, boolean appendOutput) {
        return createJob(fullCommand, commands, redirectOutput, appendOutput, null);
    }

    /**
     * Creates a job with the given commands and input path. The job is not
     * executed until {@link #submitAndRun(edu.uncc.genosets.util.CommandExecutor.Job)
     * } is called.
     *
     * @param fullCommand - the full string of the command to execute.
     * @param commands - commands to execute
     * @param redirectInput - the input file to stream into the program.
     * @return the new job that is ready for execution.
     */
    public Job createJob(List<String> commands, String fullCommand, String redirectInput) {
        return createJob(fullCommand, commands, null, false, redirectInput);
    }

    /**
     * Creates a job with the given commands, output path, and input path. The
     * job is not executed until {@link #submitAndRun(edu.uncc.genosets.util.CommandExecutor.Job)
     * } is called.
     *
     * @param fullCommand - the full string of the command to execute.
     * @param commands - commands to execute
     * @param redirectOutput - path of file to redirect output to
     * @param appendOutput - whether the output file should be appended or
     * overwritten.
     * @param redirectInput - the input file to stream into the program.
     * @return the new job that is ready for execution.
     */
    public Job createJob(String fullCommand, List<String> commands, String redirectOutput, boolean appendOutput, String redirectInput) {
        Job job = new Job(commands, fullCommand, redirectOutput, appendOutput, redirectInput);
        return job;
    }

    /**
     * Fires property change to all listeners.
     *
     * @param evt
     */
    private synchronized void firePropertyChange(PropertyChangeEvent evt) {
        this.pcs.firePropertyChange(evt);
    }

    /**
     * Add property change listener to listen for output (if not redirected) and
     * error output.
     *
     * @param listener
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public synchronized void removePropertyChangeLisenter(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    public static void main(String[] args) {
        Option help = new Option("h", "help", false, "print this message");
        Options options1 = new Options();
        options1.addOption(help);

        OptionGroup group = new OptionGroup();
        Options options2 = new Options();
        group.isRequired();
        group.addOption(OptionBuilder.withArgName("command")
                .hasArg()
                .withDescription("single command to process (put into double-quotes if the command contains spaces)")
                .withLongOpt("command")
                .create("c"));
        group.addOption(OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("file containing commands to process.  Blank lines and lines beginning with pound sign (#) will be ignored.")
                .withLongOpt("in")
                .create("i"));
        options2.addOptionGroup(group);


        CommandLineParser parser = new GnuParser();
        try {
            CommandLine c1 = parser.parse(options1, args);
            if (c1.hasOption("help")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("commandExecutor", "", options2, "Use '-h' or '--help' to print this message", true);
            } else {
                run(args, options2);
            }
        } catch (ParseException ex) {
            run(args, options2);
        }
    }

    private static void run(String[] args, Options options) {
        CommandLineParser parser = new GnuParser();
        try {
            CommandLine c1 = parser.parse(options, args);
            final CommandExecutor run = new CommandExecutor(1);
            run.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("ERR")) {
                        System.err.println(evt.getNewValue());
                    } else {
                        System.out.println(evt.getNewValue());
                    }
                }
            });
            List<Job> jobs = new ArrayList<Job>();
            String command = c1.getOptionValue("c");
            if (command != null) {
                Job job = run.convertLineToJob(command);
                jobs.add(job);
            } else {
                String infilename = c1.getOptionValue("i");
                if (infilename != null) {
                    try {
                        System.out.println("Shutting down.");
                        jobs = run.convertFileToJobs(new File(infilename));
                    } catch (IOException ex) {
                        Logger.getLogger(CommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    System.out.println("Shutdown hook");
                    if (run.isRunning()) {
                        run.cancel();
                    }
                }
            });
            for (Job job : jobs) {
                run.submitAndRun(job);
            }
            run.shutdown();
        } catch (ParseException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("commandExecutor", "", options, "Use '-h' or '--help' to print this message", true);
        }
    }

    public class Job implements Runnable {

        final private List<String> commands;
        final private String redirectOutput;
        final private String redirectInput;
        final private boolean appendOutput;
        final private String fullCommand;
//        boolean running;
//        String stdOutput;
//        String errorOutput;
//        int exitValue;

        public Job(List<String> commands, String fullCommand) {
            this(commands, fullCommand, null, false, null);
        }

        public Job(List<String> commands, String fullCommand, String redirectOutput, boolean append) {
            this(commands, fullCommand, redirectOutput, append, null);
        }

        public Job(List<String> commands, String fullCommand, String redirectInput) {
            this(commands, fullCommand, null, false, redirectInput);
        }

        public Job(List<String> commands, String fullCommand, String redirectOutput, boolean append, String redirectInput) {
            this.commands = commands;
            this.redirectOutput = redirectOutput;
            this.appendOutput = append;
            this.redirectInput = redirectInput;
            this.fullCommand = fullCommand;
        }

        @Override
        public void run() {
            try {
                firePropertyChange(new PropertyChangeEvent(this, "RUN", null, "Running: " + fullCommand + "\n"));
                ProcessBuilder pb = new ProcessBuilder(commands);
                pb.directory(workingDirectory);
                File output = null;
                File input = null;
                if (redirectOutput != null) {
                    output = new File(FilenameUtils.concat(workingDirectory.getAbsolutePath(), redirectOutput));
                }
                if (redirectInput != null) {
                    input = new File(FilenameUtils.concat(workingDirectory.getAbsolutePath(), redirectInput));
                }

                final Process proc = pb.start();

                //handle output
                Future stdOutThread = null;
                if (output == null) {
                    stdOutThread = handleProcessOutput(proc.getInputStream(), "STDOUT", output, this.appendOutput, true);
                } else {
                    stdOutThread = handleProcessOutput(proc.getInputStream(), "STDOUT", output, this.appendOutput, false);
                }
                Future errOutThread = handleProcessOutput(proc.getErrorStream(), "ERR", log, true, true);

                //handle process input
                if (input != null) {
                    handleProcessInput(proc.getOutputStream(), input);
                }

                int exitValue = -1;
                try {
                    try {
                        if (stdOutThread != null) {
                            Object get = stdOutThread.get();
                        }
                        if(errOutThread != null){
                            Object get = errOutThread.get();
                        }
                    } catch (InterruptedException ex) {
                        firePropertyChange(new PropertyChangeEvent(this, "ERR", null, "Error. Canceling Job: " + fullCommand + "\n"));
                        exec.shutdownNow();
                    } catch (ExecutionException ex) {
                        firePropertyChange(new PropertyChangeEvent(this, "ERR", null, "Error. Canceling Job: " + fullCommand + "\n"));
                        exec.shutdownNow();
                    }
                    exitValue = proc.waitFor();
                    if (exitValue != 0) {
                        exec.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    proc.destroy();
                    firePropertyChange(new PropertyChangeEvent(this, "ERR", null, "Cancelled: " + fullCommand + "\n"));
                } finally {
                    if (exitValue == 0) {
                        firePropertyChange(new PropertyChangeEvent(this, "Complete", null, "Complete: " + fullCommand + "\n"));
                    } else {
                        firePropertyChange(new PropertyChangeEvent(this, "ERR", null, "Failed: " + fullCommand + "\n"));
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(CommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
                firePropertyChange(new PropertyChangeEvent(this, "ERR", null, "Failed: " + fullCommand + "\n"));
                firePropertyChange(new PropertyChangeEvent(this, "ERR", null, ex.getMessage() + "\n"));
            } catch (Exception ex) {
                Logger.getLogger(CommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally {
            }
        }

        private Future handleProcessOutput(final InputStream is, final String property, final File file, final boolean append, final boolean notify) throws IOException {
            Callable c = new Callable() {
                @Override
                public Object call() throws Exception {
                    BufferedReader in = null;
                    BufferedWriter writer = null;
                    PrintStream ps = System.out;
                    if (property.equals("ERR")) {
                        ps = System.err;
                    }

                    try {
                        in = new BufferedReader(new InputStreamReader(is));
                        if (file != null) {
                            writer = new BufferedWriter(new FileWriter(file, append));
                        }
                        String output = null;
                        while ((output = in.readLine()) != null) {
                            output = output + System.getProperty("line.separator");
                            if (writer != null) {
                                try {
                                    writer.write(output);
                                } catch (IOException ex) {
                                    writer.close();
                                    writer = null;
                                }
                            } else {
                                ps.print(output);
                            }
                            if (notify) {
                                firePropertyChange(new PropertyChangeEvent(this, property, null, output));

                            }
                        }
                    } catch (IOException ex) {
                        System.out.println("");
                        throw ex;
                    } finally {
                        try {
                            in.close();
                            if (writer != null) {
                                writer.close();
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(CommandExecutor.class
                                    .getName()).log(Level.WARNING, null, ex);
                        }
                    }
                    return null;
                }
            };
//            Runnable r = new Runnable() {
//                @Override
//                public void run() {
//                    BufferedReader in = null;
//                    BufferedWriter writer = null;
//                    PrintStream ps = System.out;
//                    if (property.equals("ERR")) {
//                        ps = System.err;
//                    }
//
//                    try {
//                        in = new BufferedReader(new InputStreamReader(is));
//                        if (file != null) {
//                            writer = new BufferedWriter(new FileWriter(file, append));
//                        }
//                        String output = null;
//                        while ((output = in.readLine()) != null) {
//                            output = output + System.getProperty("line.separator");
//                            if (writer != null) {
//                                try {
//                                    writer.write(output);
//                                } catch (IOException ex) {
//                                    writer.close();
//                                    writer = null;
//                                }
//                            } else {
//                                ps.print(output);
//                            }
//                            if (notify) {
//                                firePropertyChange(new PropertyChangeEvent(this, property, null, output));
//
//                            }
//                        }
//                    } catch (IOException ex) {
//                        firePropertyChange(new PropertyChangeEvent(this, "ERR", null, "Error writing output.\n"));
//                    } finally {
//                        try {
//                            in.close();
//                            if (writer != null) {
//                                writer.close();
//                            }
//                        } catch (IOException ex) {
//                            Logger.getLogger(CommandExecutor.class
//                                    .getName()).log(Level.WARNING, null, ex);
//                        }
//                    }
//                }
//            };
//
//            (new Thread(r)).start();
            ExecutorService exec = Executors.newSingleThreadExecutor();
            Future submit = exec.submit(c);
            exec.shutdown();
            return submit;
        }

        private void handleProcessInput(final OutputStream os, final File file) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    BufferedReader in = null;
                    BufferedWriter writer = null;
                    try {

                        in = new BufferedReader(new FileReader(file));
                        writer = new BufferedWriter(new OutputStreamWriter(os));
                        int c = 0;
                        while ((c = in.read()) != -1) {
                            writer.write(c);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(CommandExecutor.class
                                .getName()).log(Level.SEVERE, null, ex);

                    } finally {
                        try {
                            in.close();
                            writer.close();
                        } catch (IOException ex) {
                            Logger.getLogger(CommandExecutor.class
                                    .getName()).log(Level.WARNING, null, ex);
                        }
                    }
                }
            };
            (new Thread(r)).start();
        }
    }
}
