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
package edu.uncc.genosets.datamanager.hibernate;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.io.Console;
import java.util.Properties;

/**
 *
 * @author aacain
 */
public class ComboPooledDataSourceConfig implements DataSourceConfigInterface {

    private static final String LOCALHOST = "jdbc:mysql://localhost:3306";
    private ComboPooledDataSource ds;
    private String dbName = "MpkInfo";

    @Override
    public void configureDataSource() {
        Properties props = this.propertiesPrompt();
        this.setProperties(props);
    }

    private void setProperties(Properties props) {
        ds.setUser(props.getProperty("username"));
        ds.setPassword(props.getProperty("password"));
        ds.setJdbcUrl(props.getProperty("url") + "/" + dbName);
    }

    private Properties propertiesPrompt() {
        Properties props = new Properties();
        Console c = System.console();
        if (c != null) {
            String location = c.readLine("Database Location: ");
            if (location.equalsIgnoreCase("localhost")) {
                location = LOCALHOST;
            }

            String login = c.readLine("Login: ");

            char[] pass = c.readPassword("Password: ");
            String passString = new String(pass);

            props = new Properties();
            props.setProperty("username", login);
            props.setProperty("url", location);
            props.setProperty("password", passString);
        }

        return props;
    }

    public void setDs(ComboPooledDataSource ds) {
        this.ds = ds;
    }
}
