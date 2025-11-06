package ch.unil.bookit.domain;

import java.util.UUID;
import java.util.*;

public class HotelManager extends User {
    private List<Hotel> hotels;
    private List<String> roomTypes = new ArrayList<>();
    private Map<String, Double> roomPrices = new HashMap<>();

    // main constructor
    public HotelManager(UUID uuid, String email, String password, String firstName, String lastName) {
        super(uuid, email, password, firstName, lastName);
        hotels = new ArrayList<>();
    }
    // define a list of room types
    public void defineRoomTypes(List<String> types) {
        this.roomTypes = types;
    }

    // define prices for each room type
    public void setRoomPrices(String roomType, double price) {
        if (!roomTypes.contains(roomType)) {
            throw new IllegalArgumentException("Room type " + roomType + " does not exist.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price must be non-negative.");
        }
        roomPrices.put(roomType, price);
    }

    // approve booking
    public void approveBooking(Booking booking) {
        if (booking.getStatus() == null) {
            throw new IllegalStateException("Booking status cannot be null.");
        }
        if (booking.getStatus() != Booking.bookingStatus.PENDING) {
            throw new IllegalStateException("Only PENDING bookings can be approved.");
        }
        booking.setStatus(Booking.bookingStatus.CONFIRMED);
    }
    // cancel booking
    public void cancelBooking(Booking booking) {
        if (booking.getStatus() == null) {
            throw new IllegalStateException("Booking status cannot be null.");
        }
        if (booking.getStatus() != Booking.bookingStatus.PENDING) {
            throw new IllegalStateException("Only PENDING bookings can be cancelled.");
        }
        booking.setStatus(Booking.bookingStatus.CANCELLED);
    }









}
