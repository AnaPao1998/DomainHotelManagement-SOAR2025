package ch.unil.bookit.bookitwebservice;

import ch.unil.bookit.domain.booking.Booking;
import ch.unil.bookit.domain.booking.BookingStatus;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Path("/bookings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BookingResource {

    @Inject
    private ApplicationResource applicationResource; // central app state


    @POST
    public Response createBooking(Booking booking) {
        if (booking == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Booking cannot be null").build();
        }

        UUID bookingId = UUID.randomUUID();
        Booking newBooking = new Booking(
                bookingId,
                booking.getHotelId(),
                booking.getUserId(),
                booking.getRoomTypeId()
        );
        newBooking.setStatus(BookingStatus.PENDING);

        applicationResource.getBookings().put(bookingId, newBooking);
        return Response.status(Response.Status.CREATED).entity(newBooking).build();
    }


    @GET
    public Response getAllBookings() {
        Collection<Booking> all = applicationResource.getBookings().values();
        return Response.ok(new ArrayList<>(all)).build();
    }


    @GET
    @Path("/{bookingId}")
    public Response getBooking(@PathParam("bookingId") UUID bookingId) {
        Booking booking = applicationResource.getBooking(bookingId);
        if (booking != null) {
            return Response.ok(booking).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Booking not found: " + bookingId).build();
        }
    }


    @PUT
    @Path("/{bookingId}")
    public Response updateBooking(@PathParam("bookingId") UUID bookingId, Booking updatedBooking) {
        Booking existing = applicationResource.getBooking(bookingId);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Booking not found: " + bookingId).build();
        }

        // Update fields (only status here, you can expand later)
        if (updatedBooking.getStatus() != null) {
            existing.setStatus(updatedBooking.getStatus());
        }

        applicationResource.saveBooking(existing);
        return Response.ok(existing).build();
    }

    @DELETE
    @Path("/{bookingId}")
    public Response deleteBooking(@PathParam("bookingId") UUID bookingId) {
        Map<UUID, Booking> allBookings = applicationResource.getBookings();
        if (allBookings.remove(bookingId) != null) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Booking not found: " + bookingId).build();
        }
    }

    //GET /api/bookings/guest/{guestId}
    @GET
    @Path("/guest/{guestId}")
    public Response getBookingsByGuest(@PathParam("guestId") UUID guestId) {
        ArrayList<Booking> guestBookings = new ArrayList<>();

        //all bookings in the system
        for (Booking b : applicationResource.getBookings().values()) {
            //if the booking's User ID matches the requested Guest ID, add it
            if (b.getUserId() != null && b.getUserId().equals(guestId)) {
                guestBookings.add(b);
            }
        }

        //return the filtered list
        return Response.ok(guestBookings).build();
    }
}
