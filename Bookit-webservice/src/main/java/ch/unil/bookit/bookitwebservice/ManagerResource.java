package ch.unil.bookit.bookitwebservice;

import ch.unil.bookit.domain.Hotel;
import ch.unil.bookit.domain.HotelManager;
import ch.unil.bookit.domain.booking.Booking;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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

    @POST
    public Response createHotel(Hotel hotel) {
        if (hotel == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Hotel created = applicationResource.createHotel(hotel);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @GET
    public List<Hotel> getAllHotels() {
        return new ArrayList<>(applicationResource.getAllHotels().values());
    }

    @GET
    @Path("/{hotelId}")
    public Response getHotel(@PathParam("hotelId") UUID hotelId) {
        Hotel hotel = applicationResource.getHotel(hotelId);
        if (hotel != null) {
            return Response.ok(hotel).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{hotelId}")
    public Response updateHotel(@PathParam("hotelId") UUID hotelId, Hotel updatedHotel) {
        Hotel result = applicationResource.updateHotel(hotelId, updatedHotel);
        if (result == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(result).build();
    }

    @DELETE
    @Path("/{hotelId}")
    public Response deleteHotel(@PathParam("hotelId") UUID hotelId) {
        if (applicationResource.deleteHotel(hotelId)) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Path("/manager")
    public Response createManager(HotelManager manager) {
        if (manager == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        HotelManager newManager = applicationResource.createManager(manager);
        return Response.status(Response.Status.CREATED).entity(newManager).build();
    }

    @GET
    @Path("/manager/{managerId}")
    public Response getManager(@PathParam("managerId") UUID managerId) {
        HotelManager manager = applicationResource.getManager(managerId);
        if (manager != null) {
            return Response.ok(manager).build();
        } else  {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("/manager/{managerId}")
    public Response updateManager(@PathParam("managerId") UUID managerId, HotelManager manager) {
        HotelManager updatedManager = applicationResource.updateManager(managerId, manager);
        if (updatedManager != null) {
            return Response.ok(updatedManager).build();
        } else  {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/all")
    public Response getAllManagers() {
        List<HotelManager> managers =
                new ArrayList<>(applicationResource.getAllManagers().values());
        return Response.ok(managers).build();
    }

    @DELETE
    @Path("/hotelmanager/{managerId}")
    public Response deleteManager(@PathParam("managerId") UUID managerId) {
        boolean deleted = applicationResource.deleteManager(managerId);
        if (deleted) {
            return Response.noContent().build(); // 204
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Path("/{managerId}/bookings/{bookingId}/approve")
    @Transactional
    public Response approveBooking(
            @PathParam("managerId") UUID managerId,
            @PathParam("bookingId") UUID bookingId) {

        HotelManager manager = applicationResource.getManager(managerId);
        if (manager == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Manager not found: " + managerId)
                    .build();
        }

        Booking booking = applicationResource.getBooking(bookingId);
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Booking not found: " + bookingId)
                    .build();
        }

        try {
            manager.approveBooking(booking, applicationResource.getAllGuests());
            applicationResource.saveBooking(booking);   // keep storage in sync
            return Response.ok(booking).build();
        } catch (IllegalStateException ex) {
            // e.g. booking not PENDING
            return Response.status(Response.Status.CONFLICT)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{managerId}/bookings/{bookingId}/cancel")
    @Transactional
    public Response cancelBooking(
            @PathParam("managerId") UUID managerId,
            @PathParam("bookingId") UUID bookingId) {

        HotelManager manager = applicationResource.getManager(managerId);
        if (manager == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Manager not found: " + managerId)
                    .build();
        }

        Booking booking = applicationResource.getBooking(bookingId);
        if (booking == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Booking not found: " + bookingId)
                    .build();
        }

        try {
            manager.cancelBooking(booking, applicationResource.getAllGuests());
            applicationResource.saveBooking(booking);
            return Response.ok(booking).build();
        } catch (IllegalStateException ex) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(ex.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{managerId}/bookings/pending")
    public Response getPendingBookings(@PathParam("managerId") UUID managerId) {
        List<Booking> pendingBookings =
                applicationResource.getPendingBookingsForManager(managerId);
        return Response.ok(pendingBookings).build();
    }

}
