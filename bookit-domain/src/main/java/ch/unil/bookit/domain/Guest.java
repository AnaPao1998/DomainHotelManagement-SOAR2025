package ch.unil.bookit.domain;

import ch.unil.bookit.domain.booking.Booking;
import ch.unil.bookit.domain.booking.BookingStatus;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name= "Guests")
@DiscriminatorValue("GUEST")
@PrimaryKeyJoinColumn(name = "user_id")
public class Guest extends User {

    @JsonbTransient
    @Transient
    private final Map<UUID, Booking> bookings = new TreeMap<>();

    public Guest() {
        super();
    }

    public Guest(UUID uuid, String email, String password, String firstName, String lastName) {
        super(uuid, email, password, firstName, lastName); // calls the parent User constructor
    }


    public Booking getBooking(UUID bookingId) {
        return this.bookings.get(bookingId);
    }

    public List<Booking> getBookings() {
        return new ArrayList<>(this.bookings.values());
    }

    public void addBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }

        if (bookings.containsKey(booking.getBookingId())) {
            throw new IllegalArgumentException("Booking already exists");
        }

        booking.setGuest(this);          // link Booking -> Guest (optional but nice)
        bookings.put(booking.getBookingId(), booking);
    }

    public boolean cancelBooking(UUID bookingId) {
        if (bookingId == null) {
            return false;
        }

        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            return false; // booking not found for this guest
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            return false;
        }

        booking.markCancelled();
        return true;
    }
    public void setBookings(List<Booking> bookingList) {
        this.bookings.clear();
        if (bookingList != null) {
            for (Booking b : bookingList) {
                this.bookings.put(b.getBookingId(), b);
            }
        }
    }
}
