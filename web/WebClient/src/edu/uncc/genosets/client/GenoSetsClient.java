/*
 * Client to test Restful Web Services
 */
package edu.uncc.genosets.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import edu.uncc.genosets.datamanager.entity.Organism;

/**
 *
 * @author aacain
 */
public class GenoSetsClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Client client = Client.create();
        WebResource webResource = client.resource("http://localhost:8084/GenoSetsServer/webresources/load/1");
        ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }

        Organism output = response.getEntity(Organism.class);

        System.out.println("Output from Server .... \n");
        System.out.println(output);
    }
}
