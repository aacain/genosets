/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uncc.genosets.taskmanager;

import edu.uncc.genosets.datamanager.api.DataManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.logging.LogFactory;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aacain
 */
public class TaskLog {

    protected static TaskLog instance;
    protected List<Message> messages;
    protected File logFile;
    protected PropertyChangeListener dbListener;
    protected PropertyChangeSupport cs;
    public final static String INFO = "INFO";
    public final static String WARNING = "WARNING";
    public final static String ERROR = "ERROR";
    public final static String PROP_LOG_CHANGED = "PROP_LOG_CHANGED";
    public final static String PROP_ADD = "PROP_ADD";
    public final static String PROP_REMOVE = "PROP_REMOVE";

    /**
     * Constructor
     */
    public TaskLog() {
        messages = new ArrayList<Message>();
        dbListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                dbChanged(evt);
            }
        };
    }

    /**
     * Get a snapshot of the messages.
     *
     * @return - a list of the messages - never returns null
     */
    public synchronized List<Message> getSnapShot() {
        if (messages != null) {
            return new ArrayList<Message>(messages);
        }
        return new ArrayList<Message>();
    }

    /**
     * The database has changed.
     *
     * @param evt
     */
    protected synchronized void dbChanged(PropertyChangeEvent evt) {
        if (evt == null || DataManager.PROP_DB_CHANGED.equals(evt.getPropertyName())) {
            if (DataManager.getDefault().isDatabaseSet()) {
                createFiles();
                this.messages = readFile(this.logFile);
            } else {
                this.messages.clear();
                this.logFile = null;
            }
            this.cs.firePropertyChange(PROP_LOG_CHANGED, null, this.messages);
        }
    }

    /**
     * Adds listener for changes to the message list
     *
     * @param listener
     */
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        this.cs.addPropertyChangeListener(listener);
    }

    /**
     * Removes listener for changes to the message list
     *
     * @param listener
     */
    public synchronized void removeChangeListener(PropertyChangeListener listener) {
        this.cs.removePropertyChangeListener(listener);
    }

    /**
     * Log information for the user.
     *
     * @param desc the description
     * @param source the source of the message
     * @param details details of the message
     * @param level the level
     */
    public synchronized void log(String desc, String source, String details, String level, Date date) {
        Message message = new Message(desc, source, details, level, date);
        messages.add(message);
        writeMessage(logFile, message, Boolean.TRUE);
        cs.firePropertyChange(PROP_ADD, null, message);
    }

    /**
     * Remove the message from the list and the file.
     *
     * @param message
     */
    public synchronized void remove(Message message) {
        if (this.logFile != null) {
            messages.remove(message);
            if (messages.isEmpty()) {
                writeMessage(logFile, null, Boolean.FALSE);
            }
            for (Message m : messages) {
                writeMessage(logFile, m, Boolean.FALSE);
            }
            this.cs.firePropertyChange(PROP_REMOVE, null, message);
        }
    }

    /**
     * Create all necessary files.
     */
    protected void createFiles() {
        this.logFile = null;
        String db = DataManager.getDefault().getConnectionId();
        if (db != null) {
            try {
                this.logFile = new File(db + ".tasklog");
                if(!this.logFile.exists()){
                    this.logFile.createNewFile();
                }
            } catch (IOException ex) {
                LogFactory.getLog(TaskLog.class).warn("Could not create log file.");
            }
        }
    }

    /**
     * Actual method the reads the message log file
     *
     * @param fo - the message log file
     * @return list of the messages in the file.
     */
    private List<Message> readFile(File fo) {
        List<Message> mList = new LinkedList<Message>();
        List<String> lines = null;
        if (fo != null) {
            lines = new LinkedList<String>();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(fo));
                String line = null;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException ex) {
                LoggerFactory.getLogger(TaskLog.class).warn("Could not read log file.");
            }
        }
        if (lines != null) {
            for (String line : lines) {
                String[] ss = line.split("\t");
                if (ss.length == 5) {
                    String level =ss[3];
                    Date date;
                    try {
                        date = Message.FORMAT.parse(ss[4]);
                    } catch (ParseException ex) {
                        date = new Date();
                    }
                    Message m = new Message(ss[0], ss[1], ss[2], level, date);
                    mList.add(m);
                }
            }
        }

        return mList;
    }

    /**
     * Appends the message to the file
     *
     * @param fo - the message file
     * @param message - the message to write
     */
    private synchronized void writeMessage(File fo, Message message, boolean append) {
        if (fo != null) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(new FileWriter(fo, append));
                if (message == null) {
                    writer.println();
                } else {
                    writer.print(message.getDesc() == null ? "" : message.getDesc());
                    writer.print("\t");
                    writer.print(message.getSource() == null ? "" : message.getSource());
                    writer.print("\t");
                    writer.print(message.getDetails() == null ? "" : message.getDetails());
                    writer.print("\t");
                    writer.print(message.getLevel());
                    writer.print("\t");
                    writer.print(Message.FORMAT.format(message.getDate()));
                    writer.println();
                }
            } catch (IOException ex) {
                LogFactory.getLog(TaskLog.class).error(ex);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }

    public static class Message {

        private String desc;
        private String source;
        private String details;
        private String level;
        private Date date;
        public static final DateFormat FORMAT = new SimpleDateFormat("MM/dd/yy HH:mm:ss");

        public Message(String desc, String source, String details, String level, Date date) {
            this.desc = desc;
            this.source = source;
            this.details = details;
            this.level = level;
            this.date = date;
        }

        public String getDesc() {
            return desc;
        }

        public String getDetails() {
            return details;
        }

        public String getLevel() {
            return level;
        }

        public String getSource() {
            return source;
        }

        public Date getDate() {
            return date;
        }
    }
}
