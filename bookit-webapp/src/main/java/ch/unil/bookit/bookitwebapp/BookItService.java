package ch.unil.bookit.bookitwebapp;
import ch.unil.bookit.domain.*;

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

import java.time.LocalDateTime;
import java.util.*;

@ApplicationScoped
public class BookItService {
    private static final String BASE_URL = "http://localhost:8080/Bookit-webservice-1.0-SNAPSHOT/api";
    private WebTarget bookingTarget;
    private WebTarget guestTarget;
    private WebTarget managerTarget;

    @PostConstruct
    public void init() {
        System.out.print("BookItService created: " + this.hashCode());
        Client client = ClientBuilder.newClient();
        bookingTarget = client.target(BASE_URL).path("bookings");
        guestTarget = client.target(BASE_URL).path("guests");
        managerTarget = client.target(BASE_URL).path("hotelmanager");
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
        Response response = guestTarget.path(id.toString()).request(MediaType.APPLICATION_JSON).get();
        return response;
    }

    //update guest
    public Response updateGuest(Guest guest) {
        Response response = guestTarget.path(guest.getId().toString()).request(MediaType.APPLICATION_JSON).put(Entity.entity(guest, MediaType.APPLICATION_JSON));
        return response;
    }

    //delete guest
    public Response deleteGuest(String id) {
        Response response = guestTarget.path(id.toString()).request(MediaType.APPLICATION_JSON).delete();
        return response;
    }

    //deposit to wallet
    public Response depositToWallet(String id, Integer amount) {
        Response response = guestTarget.path(id.toString()).path("wallet").path("deposit").request(MediaType.APPLICATION_JSON).put(Entity.entity(amount, MediaType.APPLICATION_JSON));
        return response;
    }

    //withdraw from wallet
    public Response withdrawToWallet(String id, Integer amount) {
        Response response = guestTarget.path(id.toString()).path("wallet").path("withdraw").request(MediaType.APPLICATION_JSON).put(Entity.entity(amount, MediaType.APPLICATION_JSON));
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
        Response response = managerTarget.path(id.toString()).request(MediaType.APPLICATION_JSON).get();
        return response;
    }

    //update hotel
    public Response updateHotel(Hotel hotel) {
        Response response = managerTarget.path(hotel.getHotelId().toString()).request(MediaType.APPLICATION_JSON).put(Entity.entity(hotel, MediaType.APPLICATION_JSON));
        return response;
    }

    //delete hotel
    public Response deleteHotel(String id) {
        Response response = managerTarget.path(id.toString()).request(MediaType.APPLICATION_JSON).delete();
        return response;
    }

    //get all managers
    public Response getAllManagers() {
        Response response = managerTarget.path("all").request(MediaType.APPLICATION_JSON).get();
        return response;
    }

    //approve booking
    public Response approveBooking(String manager_id, String booking_id) {
        Response response = managerTarget.path(manager_id.toString()).path("bookings").path(booking_id.toString()).path("approve").request(MediaType.APPLICATION_JSON).put(Entity.json(""));
        return response;
    }

    //cancel booking
    public Response cancelBooking(String manager_id, String booking_id) {
        Response response = managerTarget.path(manager_id.toString()).path("bookings").path(booking_id.toString()).path("cancel").request(MediaType.APPLICATION_JSON).put(Entity.json(""));
        return response;
    }

}


