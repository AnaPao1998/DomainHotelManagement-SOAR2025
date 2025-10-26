package ch.unil.bookit.domain;

public class Booking {
=======
import java.util.UUID;

public class Booking {

    private UUID bookingId;
    private UUID hotelId;
    private UUID userId;
    private UUID roomTypeId;

    //constructor
    public Booking(UUID bookingId, UUID hotelId, UUID userId, UUID roomTypeId) {
        this.bookingId = bookingId;
        this.hotelId = hotelId;
        this.userId = userId;
        this.roomTypeId = roomTypeId;
    }

    //getters
    public UUID getBookingId() {return bookingId;}
}
