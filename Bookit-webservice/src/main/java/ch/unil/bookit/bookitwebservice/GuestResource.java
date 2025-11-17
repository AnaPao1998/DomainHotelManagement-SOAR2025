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

    @DELETE
    @Path("/{guestId}")
    public Response deleteGuest(@PathParam("guestId")UUID guestId) {
        if (applicationResource.deleteGuest(guestId)) {
            return Response.noContent().build(); //204
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/{guestId}/wallet/deposit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response depositToWallet(@PathParam("guestId") UUID guestId,
                                    Integer amount) {

        if (amount == null || amount <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Amount must be positive")
                    .build();
        }

        Guest guest = applicationResource.depositToGuestWallet(guestId, amount);
        if (guest == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(guest).build();  // updated guest with new balance
    }


    @PUT
    @Path("/{guestId}/wallet/withdraw")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response withdrawFromWallet(@PathParam("guestId") UUID guestId,
                                       Integer amount) {

        if (amount == null || amount <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Amount must be positive")
                    .build();
        }

        try {
            Guest guest = applicationResource.withdrawFromGuestWallet(guestId, amount);
            if (guest == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(guest).build(); // updated guest with reduced balance
        } catch (IllegalArgumentException | IllegalStateException e) {
            // e.g. not enough balance
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

}
