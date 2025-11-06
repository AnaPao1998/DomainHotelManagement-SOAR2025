package ch.unil.bookit.domain;

import java.util.UUID;
import java.util.*;

public class Booking {

    private UUID bookingId;
    private UUID hotelId;
    private UUID userId;
    private UUID roomTypeId;
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

}
