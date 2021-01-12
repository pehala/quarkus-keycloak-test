package com.github.pehala.resteasyjackson;

import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/test")

@ApplicationScoped
@Produces(MediaType.TEXT_PLAIN)
public class TestResource {
    @Inject
    @RestClient
    TestService testService;

    @GET
    @Path("/test")
    @Authenticated
    public String test() {
        return testService.test();
    }

    @GET
    @Path("/testNonAuth")
    public String testNonAuth() {
        return testService.testNoAuth();
    }

    @GET
    @Path("/testHeader")
    @Authenticated
    public String testHeader() {
        // Add random header to the request, this is to check that wiremock works correctly
        return testService.testHeader("test");
    }
}
