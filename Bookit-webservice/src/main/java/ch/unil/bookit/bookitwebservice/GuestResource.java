package ch.unil.bookit.bookitwebservice;

import ch.unil.bookit.domain.Guest;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/guests")

public class GuestResource {
    @Inject
    private ApplicationResource applicationResource;

    //create
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGuest(Guest guest) {
        if (guest == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Guest newGuest = applicationResource.createGuest(guest);
        return Response.status(Response.Status.CREATED).entity(newGuest).build();
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Guest> getAllGuests() {
        return new ArrayList<>(applicationResource.getAllGuests().values());
    }

    //read
    @GET
    @Path("/{guestId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGuests(@PathParam("guestId")UUID guestId) {
        Guest guest = applicationResource.getGuest(guestId);
        if (guest != null) {
            return Response.ok(guest).build(); //200 ok
        } else {
            return Response.status(Response.Status.NOT_FOUND).build(); //404
        }

    }

    //update
    @PUT
    @Path("/{guestId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateGuest(@PathParam("guestId")UUID guestId, Guest guest) {
        Guest result = applicationResource.updateGuest(guestId, guest);
        if (result != null) {
            return Response.ok(guest).build();
        } else  {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    //delete
    @DELETE
    @Path("/{guestId}")
    public Response deleteGuest(@PathParam("guestId")UUID guestId) {
        if (applicationResource.deleteGuest(guestId)) {
            return Response.noContent().build(); //204
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
