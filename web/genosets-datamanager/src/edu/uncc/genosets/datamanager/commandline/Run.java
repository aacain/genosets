package edu.uncc.genosets.datamanager.commandline;

import edu.uncc.genosets.connections.Connection;
import edu.uncc.genosets.connections.InvalidConnectionException;
import edu.uncc.genosets.datamanager.api.DataManager;
import edu.uncc.genosets.datamanager.api.DatabaseMigrationException;
import edu.uncc.genosets.datamanager.hibernate.HibernateUtil;
import java.util.HashMap;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aacain
 */
public class Run {

    private String command;
    private HashMap<String, String> params = new HashMap<String, String>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InvalidConnectionException, DatabaseMigrationException {
        Run run = new Run();
        try {
            run.parseArgs(args);
            if (run.paramsValid()) {
                run.run();
            }
        } catch (CommandLineParsingException ex) {
            System.err.println(ex.getMessage());
            run.printHelp();
        }
    }

    private void parseArgs(String[] args) throws CommandLineParsingException {
        for (String a : args) {
            if (a.startsWith("--")) {
                int indexOf = a.indexOf("=");
                try {
                    params.put(a.substring(0, indexOf), a.substring(indexOf + 1));
                } catch (StringIndexOutOfBoundsException ex) {
                    throw new CommandLineParsingException("Could not read argument: " + a);
                }
            } else {
                if (isCommand(a)) {
                    this.command = a;
                } else {
                    throw new CommandLineParsingException("Unknown command: " + a);
                }
            }
        }
        if (this.command == null) {
            throw new CommandLineParsingException("No command specified");
        }
    }

    private boolean isCommand(String command) {
        return ("createDb".equals(command)
                || "migrateDb".equals(command)
                || "checkDb".equals(command) 
                || "test".equals(command));
    }

    private boolean paramsValid() {
        return true;
    }

    private void run() throws InvalidConnectionException, DatabaseMigrationException, CommandLineParsingException {
        if ("createDb".equals(command)) {
            LoggerFactory.getLogger(Run.class).debug("Running create database");
            HibernateUtil.createDb(params.get("--host"), params.get("--port"), params.get("--dbName"), params.get("--username"), params.get("--password") == null ? "" : params.get("--password"));
        } else if ("migrateDb".equals(command)) {
            LoggerFactory.getLogger(Run.class).debug("Running migrate database");
            HibernateUtil.migrateDatabase(params.get("--host"), params.get("--port"), params.get("--dbName"), params.get("--username"), params.get("--password") == null ? "" : params.get("--password"));
        } else if ("checkDb".equals(command)) {
            LoggerFactory.getLogger(Run.class).debug("Checking database updated");
            boolean checkDatabaseCurrent = HibernateUtil.checkDatabaseCurrent(params.get("--host"), params.get("--port"), params.get("--dbName"), params.get("--username"), params.get("--password") == null ? "" : params.get("--password"));
            if (checkDatabaseCurrent) {
                LoggerFactory.getLogger(Run.class).debug("Database is upto date");
            }else{
                LoggerFactory.getLogger(Run.class).warn("Database is not up to date.");
            }
        }
        else if ("test".equals(command)) {
            LoggerFactory.getLogger(Run.class).debug("Running testDbFile");
            DataManager.openConnection(new Connection("myConnection", "myConnection", "localhost:3306/brucella_big_test","uncc", "uncc", false, Connection.TYPE_DIRECT_DB, false));
        }
    }

    private void printHelp() {
        System.out.println("Usage:");
        System.out.println("\tcreateDb --host=host --port=port --dbName=dbName --username=username --password=password");
        System.out.println("\tmigrateDb --host=host --port=port --dbName=dbName --username=username --password=password");
        System.out.println("\tcheckDb --host=host --port=port --dbName=dbName --username=username --password=password");
    }
}
