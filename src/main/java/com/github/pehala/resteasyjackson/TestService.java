package com.github.pehala.resteasyjackson;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api")
@Produces(MediaType.TEXT_PLAIN)
@ApplicationScoped
@RegisterRestClient(configKey="core-service")
@RegisterClientHeaders
public interface TestService {

    @GET
    @Path("/test")
    String test();

    @GET
    @Path("/test")
    String testHeader(@HeaderParam("Authorization") String auth);

    @GET
    @Path("/testNonAuth")
    String testNoAuth();
}
