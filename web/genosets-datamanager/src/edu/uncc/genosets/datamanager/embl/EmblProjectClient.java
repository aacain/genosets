/*
 * 
 * 
 */
package edu.uncc.genosets.datamanager.embl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.LoggerFactory;

/**
 *
 * @author aacain
 */
public class EmblProjectClient {

    private static final String PROJECT_URL = "http://www.ebi.ac.uk/genomes/bacteria.details.txt";

    public Collection<EmblProject> getEmblProjects() {
        Collection<EmblProject> emblProjects = new LinkedList();
        {
            BufferedReader reader = null;
            try {
                URL url = new URL(PROJECT_URL);
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                
                String line = null;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    if (i != 0) { //skip first line
                        String[] ss = line.split("\t");
                        EmblProject proj = new EmblProject(ss[0], ss[1], ss[2], ss[3], ss[4]);
                        emblProjects.add(proj);
                    }
                    i++;
                }
            } catch (MalformedURLException ex) {
                LoggerFactory.getLogger(this.getClass()).error("Could not retreive EMBL projects from URL");
            } catch (IOException ex) {
                LoggerFactory.getLogger(this.getClass()).error("Could not retreive EMBL projects from URL");
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    LoggerFactory.getLogger(this.getClass()).error("Could not retreive EMBL projects from URL");
                }
            }
        }
        return emblProjects;
    }

    public Collection<EmblProject> getEmblProjectsX(){
        List<EmblProject> projects = new LinkedList();

        return projects;
    }
}
