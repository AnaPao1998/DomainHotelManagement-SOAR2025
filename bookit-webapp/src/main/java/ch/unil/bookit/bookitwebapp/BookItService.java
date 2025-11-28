package ch.unil.bookit.bookitwebapp;

import ch.unil.bookit.domain.Guest;
import ch.unil.bookit.domain.Hotel;
import ch.unil.bookit.domain.HotelManager;
import ch.unil.bookit.domain.booking.Booking;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collections;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BookItService {
    private static final String BASE_URL = "http://localhost:8080/Bookit-webservice-1.0-SNAPSHOT/api";
    private WebTarget bookingTarget;
    private WebTarget guestTarget;
    private WebTarget managerTarget;
    private WebTarget userTarget;

    @PostConstruct
    public void init() {
        System.out.print("BookItService created: " + this.hashCode());
        Client client = ClientBuilder.newClient();
        bookingTarget = client.target(BASE_URL).path("bookings");
        guestTarget = client.target(BASE_URL).path("guests");
        managerTarget = client.target(BASE_URL).path("hotelmanager");
        userTarget = client.target(BASE_URL).path("user");
    }

    // BOOKING OPERATIONS

    // create booking
    public Response createBooking(Booking booking) {
        var response = bookingTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(booking, MediaType.APPLICATION_JSON));
        return response;
    }

    //get all bookings
    public Response getAllBookings() {
        Response response = bookingTarget.request(MediaType.APPLICATION_JSON).get();
        return response;
    }

    //get booking
    public Response getBooking(String id) {
        Response response = bookingTarget.path(id.toString()).request(MediaType.APPLICATION_JSON).get();
        return response;
    }

    //update booking
    public Response updateBooking(Booking updated_booking) {
        Response response = bookingTarget.path(updated_booking.getBookingId().toString()).request(MediaType.APPLICATION_JSON).put(Entity.entity(updated_booking, MediaType.APPLICATION_JSON));
        return response;
    }

    //delete booking
    public Response deleteBooking(String id) {
        Response response = bookingTarget.path(id.toString()).request(MediaType.APPLICATION_JSON).delete();
        return response;
    }

    // GUEST OPERATIONS

    //create guest
    public Response createGuest(Guest guest) {
        var response = guestTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(guest, MediaType.APPLICATION_JSON));
        return response;
    }

    //get all guests
    public List<Guest> getAllGuests() {
        var response = guestTarget.request(MediaType.APPLICATION_JSON).get(new GenericType<List<Guest>>() {
        });
        return response;
    }

    //get guests
    public Response getGuests(String id) {
        var response = guestTarget.path(id.toString()).request(MediaType.APPLICATION_JSON).get();
        return response;
    }

    //update guest
    public Response updateGuest(Guest guest) {
        var response = guestTarget.path(guest.getId().toString()).request(MediaType.APPLICATION_JSON).put(Entity.entity(guest, MediaType.APPLICATION_JSON));
        return response;
    }

    //delete guest
    public Response deleteGuest(String id) {
        var response = guestTarget.path(id.toString()).request(MediaType.APPLICATION_JSON).delete();
        return response;
    }

    //deposit to wallet
    public Response depositToWallet(String id, Integer amount) {
        var response = guestTarget.path(id.toString()).path("wallet").path("deposit").request(MediaType.APPLICATION_JSON).put(Entity.entity(amount, MediaType.APPLICATION_JSON));
        return response;
    }

    //withdraw from wallet
    public Response withdrawToWallet(String id, Integer amount) {
        var response = guestTarget.path(id.toString()).path("wallet").path("withdraw").request(MediaType.APPLICATION_JSON).put(Entity.entity(amount, MediaType.APPLICATION_JSON));
        return response;
    }

    // MANAGER OPERATIONS

    //create hotel
    public Response createHotel(Hotel hotel) {
        var response = managerTarget.request(MediaType.APPLICATION_JSON).post(Entity.entity(hotel, MediaType.APPLICATION_JSON));
        return response;
    }

    //get all hotels
    public List<Hotel> getAllHotels() {
        var response = managerTarget.request(MediaType.APPLICATION_JSON).get(new GenericType<List<Hotel>>() {});
        return response;
    }

    //get hotel
    public Response getHotel(String id) {
        var response = managerTarget.path(id.toString()).request(MediaType.APPLICATION_JSON).get();
        return response;
    }

    //update hotel
    public Response updateHotel(Hotel hotel) {
        var response = managerTarget.path(hotel.getHotelId().toString()).request(MediaType.APPLICATION_JSON).put(Entity.entity(hotel, MediaType.APPLICATION_JSON));
        return response;
    }

    //delete hotel
    public Response deleteHotel(String id) {
        var response = managerTarget.path(id.toString()).request(MediaType.APPLICATION_JSON).delete();
        return response;
    }

    // create manager
    public Response createManager(HotelManager manager) {
        var response = managerTarget.path("/manager").request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(manager, MediaType.APPLICATION_JSON));
        return response;
    }

    // get manager
    public Response getManager(String id) {
        var response = managerTarget.path("manager").path(id.toString()).request(MediaType.APPLICATION_JSON).get();
        return response;
    }

    //get all managers
    public Response getAllManagers() {
        var response = managerTarget.path("all").request(MediaType.APPLICATION_JSON).get();
        return response;
    }

    //update manager
    public Response updateManager(HotelManager manager) {
        var response = managerTarget.path("manager").path(manager.getId().toString()).request(MediaType.APPLICATION_JSON).put(Entity.entity(manager, MediaType.APPLICATION_JSON));
        return response;
    }

    //approve booking
    public Response approveBooking(String manager_id, String booking_id) {
        var response = managerTarget.path(manager_id.toString()).path("bookings").path(booking_id.toString()).path("approve").request(MediaType.APPLICATION_JSON).put(Entity.json(""));
        return response;
    }

    //cancel booking
    public Response cancelBooking(String manager_id, String booking_id) {
        var response = managerTarget.path(manager_id.toString()).path("bookings").path(booking_id.toString()).path("cancel").request(MediaType.APPLICATION_JSON).put(Entity.json(""));
        return response;
    }

    public List<Hotel> getHotelsForManager(UUID managerId) {
        return getAllHotels();
    }


    public Hotel createHotelAndReturn(Hotel hotel) {
        return managerTarget
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(hotel, MediaType.APPLICATION_JSON), Hotel.class);
    }

    public Hotel updateHotelAndReturn(Hotel hotel) {
        return managerTarget
                .path(hotel.getHotelId().toString())
                .request(MediaType.APPLICATION_JSON)
                .put(Entity.entity(hotel, MediaType.APPLICATION_JSON), Hotel.class);
    }

    //delete hotel by UUID
    public void deleteHotel(UUID hotelId) {
        managerTarget
                .path(hotelId.toString())
                .request(MediaType.APPLICATION_JSON)
                .delete();
    }

    //get bookings per guest
    public List<Booking> getBookingsForGuest(UUID guestId) {
        //GET /api/bookings/guest/{guestId}
        return bookingTarget
                .path("guest")
                .path(guestId.toString())
                .request(MediaType.APPLICATION_JSON)
                .get(new GenericType<List<Booking>>() {});
    }

    //get hotel by manager
    // get all hotels associated with the currently logged-in manager -> for HotelManagement.xhtml
    public List<Hotel> getHotelsByManager(UUID managerId) {
        // has an endpoint that handles the path: BASE_URL/hotelmanager/{managerId}/hotels
        try {
            var response = managerTarget
                    .path(managerId.toString())
                    .path("hotels")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Hotel>>() {});
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // USER OPERATIONS

    //authenticate
    public UUID authenticate(String email, String password, String role) {
        var response = userTarget.path("authenticate").path(email).path(password).
                path(role).request(MediaType.APPLICATION_JSON).get(UUID.class);
        return response;
    }

    public List<Booking> getPendingBookingsForManager(UUID managerId) {
        // par ex: GET /api/hotelmanager/{managerId}/bookings/pending
        try {
            var response = managerTarget
                    .path(managerId.toString())
                    .path("bookings")
                    .path("pending")
                    .request(MediaType.APPLICATION_JSON)
                    .get(new GenericType<List<Booking>>() {});
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

}


