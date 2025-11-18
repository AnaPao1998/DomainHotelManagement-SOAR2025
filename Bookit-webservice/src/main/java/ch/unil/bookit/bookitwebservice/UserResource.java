package ch.unil.bookit.bookitwebservice;

import ch.unil.bookit.domain.Hotel;
import ch.unil.bookit.domain.HotelManager;
import ch.unil.bookit.domain.booking.Booking;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private ApplicationResource applicationResource;

    @GET
    @Path("/authenticate/{email}/{password}/{role}")
    public UUID authenticate(@PathParam("email") String email, @PathParam("password") String password, @PathParam("role") String role) {
        if (role.equals("manager")) {
            return applicationResource.authenticateManager(email, password);
        } else if (role.equals("guest")) {
            return applicationResource.authenticateGuest(email, password);
        }
        return null;
    }
}
