/*
 * This code can only be used upon consent of the author
 * 
 */
package com.netsec.phaserix.boundaries;

import com.netsec.phaserix.impl.AuthenticationProviderImpl;
import com.netsec.phaserix.interfaces.AuthenticationProvider;
import javax.ejb.Stateless;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author suparn
 */
@Path("auth")
@Stateless
public class AuthService {

    /**
     * Creates a new instance of AuthService
     */
    public AuthService() {
    }
    @GET
    @Path("/ok")
    public Response getSomething(){
        return Response.ok().entity("Yahoo").build();
    }
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("ticket/{userId}")
    public Response getTicket(@PathParam("userId") Integer userId){
        AuthenticationProvider auth = new AuthenticationProviderImpl();
        return Response.ok().entity(auth.createTicket(userId)).build();
    }
}
