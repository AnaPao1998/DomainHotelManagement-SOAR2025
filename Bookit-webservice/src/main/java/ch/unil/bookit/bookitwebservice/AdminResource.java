package ch.unil.bookit.bookitwebservice;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.TEXT_PLAIN)
public class AdminResource {

    @Inject
    private ApplicationResource applicationResource;

    @POST
    @Path("/seed-demo")
    public Response seedDemoData() {
        applicationResource.populateDbWithDemoDataOnce();
        return Response.ok("Seeding triggered").build();
    }
}

