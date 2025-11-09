package ch.unil.bookit.bookitwebservice;

import ch.unil.bookit.domain.Booking;
import ch.unil.bookit.domain.Hotel;
import ch.unil.bookit.domain.HotelManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Path("/hotelmanager")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public class ManagerResource {
    @Inject
    private ApplicationResource applicationResource;

    // create
    @POST
    public Response createHotel(Hotel hotel) {
        if (hotel == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Hotel newHotel = applicationResource.createHotel(hotel);
        return Response.status(Response.Status.CREATED).entity(newHotel).build();
    }

    // get all
    @GET
    public List<Hotel> getAllHotels() {
        return new ArrayList<>(applicationResource.getAllHotels().values());
    }

    // get one
    @GET
    @Path("/{hotelId}")
    public Response getHotel(@PathParam("hotelId") UUID hotelId) {
        Hotel hotel = applicationResource.getHotel(hotelId);
        if (hotel != null) {
            return Response.ok(hotel).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    // update
    @PUT
    @Path("/{hotelId}")
    public Response updateHotel(@PathParam("hotelId") UUID hotelId, Hotel hotel) {
        Hotel updatedHotel = applicationResource.updateHotel(hotelId, hotel);
        if (updatedHotel != null) {
            return Response.ok(updatedHotel).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

    }

    // delete
    @DELETE
    @Path("/{hotelId}")
    public Response deleteHotel(@PathParam("hotelId") UUID hotelId) {
        if (applicationResource.deleteHotel(hotelId)) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    @GET
    @Path("/all")
    public Response getAllManagers() {
        List<HotelManager> managers = new ArrayList<>(applicationResource.getAllManagers().values());
        return Response.ok(managers).build();
    }

    // approve booking
    @PUT
    @Path("/{managerId}/bookings/{bookingId}/approve")
    public Response approveBooking(
            @PathParam("managerId") UUID managerId,
            @PathParam("bookingId") UUID bookingId) {

        HotelManager manager = applicationResource.getManager(managerId);
        if (manager == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Manager not found: " + managerId).build();
        }

        Booking booking = applicationResource.getBooking(bookingId);
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Booking not found: " + bookingId).build();
        }

        try {
            manager.approveBooking(booking,applicationResource.getAllGuests());
            // if your storage is by reference, this is enough; if not, persist the update:
            applicationResource.saveBooking(booking); // no-op if you don’t need it
            return Response.ok(booking).build();
        } catch (IllegalStateException ex) {
            // e.g. not PENDING
            return Response.status(Response.Status.CONFLICT).entity(ex.getMessage()).build();
        }
    }

    // cancel booking
    @PUT
    @Path("/{managerId}/bookings/{bookingId}/cancel")
    public Response cancelBooking(
            @PathParam("managerId") UUID managerId,
            @PathParam("bookingId") UUID bookingId) {

        HotelManager manager = applicationResource.getManager(managerId);
        if (manager == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Manager not found: " + managerId).build();
        }

        Booking booking = applicationResource.getBooking(bookingId);
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Booking not found: " + bookingId).build();
        }

        try {
            manager.cancelBooking(booking,applicationResource.getAllGuests());
            applicationResource.saveBooking(booking); // no-op if you don’t need it
            return Response.ok(booking).build();
        } catch (IllegalStateException ex) {
            return Response.status(Response.Status.CONFLICT).entity(ex.getMessage()).build();
        }
    }

}
