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
package edu.uncc.genosets.datamanager.api;

import com.mchange.v2.c3p0.DataSources;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author aacain
 */
public class DatabaseValidator {

    public static boolean validateUser(String host, String port, String username, String password) {
        boolean valid = false;
        try {
            LogFactory.getLog(DatabaseValidator.class).info("Validating mysql user " + username + " using password " + password == null || password.isEmpty() ? "FALSE" : "TRUE");
            String fullUrl = "jdbc:mysql://" + host + ":" + port;
            //set the driver class
            String driverClass = "com.mysql.jdbc.Driver";
            //Create the connection
            Class.forName(driverClass);
            DataSource ds;
            Connection sqlConnection = null;
            try {
                ds = DataSources.unpooledDataSource(fullUrl, username, password == null ? "" : password);
                sqlConnection = ds.getConnection();
                valid = true;
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseValidator.class.getName()).log(Level.INFO, "Database user or password is incorrect for " + username + " host: " + host + " port: " + port);
            } finally {
                try {
                    if (sqlConnection != null) {
                        sqlConnection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseValidator.class.getName()).log(Level.INFO, null, ex);
                }
                return valid;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseValidator.class.getName()).log(Level.SEVERE, "Database driver class not found.", ex);
        }
        return valid;
    }

    public static boolean databaseExists(String host, String port, String username, String password, String dbName) {
        boolean dbExists = false;
        try {
            LogFactory.getLog(DatabaseValidator.class).info("Validating mysql user " + username + " using password " + password == null || password.isEmpty() ? "FALSE" : "TRUE");
            String fullUrl = "jdbc:mysql://" + host + ":" + port;
            //set the driver class
            String driverClass = "com.mysql.jdbc.Driver";
            //Create the connection
            Class.forName(driverClass);
            DataSource ds;
            Connection sqlConnection = null;
            try {
                ds = DataSources.unpooledDataSource(fullUrl, username, password == null ? "" : password);
                sqlConnection = ds.getConnection();
                Statement stmt = sqlConnection.createStatement();
                ResultSet result = stmt.executeQuery("SHOW DATABASES LIKE '" + dbName + "'");
                if (result.next()) {
                    dbExists = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseValidator.class.getName()).log(Level.INFO, "Database user or password is incorrect for " + username + " host: " + host + " port: " + port);
            } finally {
                try {
                    if (sqlConnection != null) {
                        sqlConnection.close();
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseValidator.class.getName()).log(Level.INFO, null, ex);
                }
                return dbExists;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseValidator.class.getName()).log(Level.SEVERE, "Database driver class not found.", ex);
        }
        return dbExists;
    }
}
