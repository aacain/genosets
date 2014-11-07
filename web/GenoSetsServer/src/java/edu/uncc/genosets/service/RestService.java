/*
 */
package edu.uncc.genosets.service;

import com.sun.jersey.api.spring.Autowire;
import edu.uncc.genosets.datamanager.entity.AnnotationMethod;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author aacain
 */
@Path("testrest")
@Autowire
public class RestService {

    List<AnnotationMethod> annoList;
    public RestService() {
        annoList = new ArrayList(2);
        AnnotationMethod m = new AnnotationMethod();
        m.setAnnotationMethodId(0);
        m.setMethodName("method 0");
        annoList.add(m);
        m = new AnnotationMethod();
        m.setAnnotationMethodId(1);
        m.setMethodName("method 1");
        annoList.add(m);
    }
    
    
    @PUT
    @Path("loadGff3")
    public Response loadGff3(@QueryParam("id") String id){
        Response.ResponseBuilder bldr = Response.status(Response.Status.ACCEPTED);
        bldr.type(MediaType.TEXT_PLAIN);
        bldr.entity(id);
        return bldr.build();
    }
    
    @GET
    @Path("{id}")
    @Produces("text/plain")
    @Transactional
    public String find(@PathParam("id") Integer id) {
        return annoList.get(id).getMethodName();
    }
}
