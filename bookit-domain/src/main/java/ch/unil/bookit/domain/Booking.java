package ch.unil.bookit.domain;

import java.util.Map;
import java.util.UUID;

public class Booking {

    private UUID bookingId;
    private UUID hotelId;
    private UUID userId;
    private UUID roomTypeId;

    public UUID getRoomTypeId() {
        return roomTypeId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getHotelId() {
        return hotelId;
    }

    public void setHotelId(UUID hotelId) {
        this.hotelId = hotelId;
    }
    // Bonjour ceci est mon commentaire

    //booking states
    public enum bookingStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }

    private bookingStatus status;

    //constructor
    public Booking(UUID bookingId, UUID hotelId, UUID userId, UUID roomTypeId) {
        this.bookingId = bookingId;
        this.hotelId = hotelId;
        this.userId = userId;
        this.roomTypeId = roomTypeId;
        this.status = bookingStatus.PENDING;
    }

    //getters
    public UUID getBookingId() {return bookingId;}

    public bookingStatus getStatus() {
        return status;
    }

    //setter
    public void setStatus(bookingStatus status) {
        this.status = status;
    }

    public Guest getGuest(Map<UUID, Guest> guests) {
        if (guests == null) {
            throw new IllegalArgumentException("Guest map cannot be null");
        }
        Guest guest = guests.get(userId);
        if (guest == null) {
            throw new IllegalStateException("No Guest found for userId: " + userId);
        }
        return guest;
    }

}
