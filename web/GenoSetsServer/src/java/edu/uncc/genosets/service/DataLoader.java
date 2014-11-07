/*
 * Rest service to add data 
 */
package edu.uncc.genosets.service;

import com.sun.jersey.api.spring.Autowire;
import edu.uncc.genosets.datamanager.entity.Organism;
import edu.uncc.genosets.datamanager.hibernate.HibernateUtil_spring;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author aacain
 */
@Path("load")
@Autowire
public class DataLoader {

    @Autowired
    private HibernateUtil_spring hibUtil;

    @PUT
    @Path("organism")
    @Consumes({"application/xml", "application/json"})
    public void organism(Organism organism) {
        Logger.getLogger("edu.uncc.genosets.service.DataLoader").info("added organism");
    }

    @PUT
    @Path("byTaxId")
    @Consumes({"application/xml", "application/json"})
    public void byTaxId(String taxId) {
        
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public Organism find(@PathParam("id") Integer id) {
        return (Organism) hibUtil.get(Organism.DEFAULT_NAME, id);
    }
}
