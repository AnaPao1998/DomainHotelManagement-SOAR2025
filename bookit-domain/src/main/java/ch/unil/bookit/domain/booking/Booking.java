package ch.unil.bookit.domain.booking;

import ch.unil.bookit.domain.Guest;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import jakarta.json.bind.annotation.JsonbTransient;


public class Booking {

    private UUID bookingId;
    private UUID hotelId;
    private UUID userId;
    private UUID roomTypeId;

    private BookingStatus status = BookingStatus.PENDING;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    @JsonbTransient
    private Guest guest;

    public Booking() {
    }

    public Booking(UUID bookingId, UUID hotelId, UUID userId, UUID roomTypeId) {
        this.bookingId = Objects.requireNonNull(bookingId, "bookingId");
        this.hotelId = Objects.requireNonNull(hotelId, "hotelId");
        this.userId = Objects.requireNonNull(userId, "userId");
        this.roomTypeId = Objects.requireNonNull(roomTypeId, "roomTypeId");
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public UUID getBookingId() { return bookingId; }
    public UUID getHotelId()   { return hotelId; }
    public UUID getUserId()    { return userId; }
    public UUID getRoomTypeId(){ return roomTypeId; }

    public BookingStatus getStatus()   { return status; }
    public Instant getCreatedAt()      { return createdAt; }
    public Instant getUpdatedAt()      { return updatedAt; }

    public Guest getGuest()           { return guest; }
    public void setGuest(Guest guest) { this.guest = guest; }

    public void markCancelled() {
        this.status = BookingStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    public void markConfirmed() {
        this.status = BookingStatus.CONFIRMED;
        this.updatedAt = Instant.now();
    }

    public void markCompleted() {
        this.status = BookingStatus.COMPLETED;
        this.updatedAt = Instant.now();
    }
    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public void setHotelId(UUID hotelId) {
        this.hotelId = hotelId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setRoomTypeId(UUID roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setStatus(BookingStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public Guest resolveGuest(Map<UUID, Guest> guests) {
        if (guests == null) {
            throw new IllegalArgumentException("Guest map cannot be null");
        }
        Guest g = guests.get(userId);
        if (g == null) {
            throw new IllegalStateException("No Guest found for userId: " + userId);
        }
        return g;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking that = (Booking) o;
        return bookingId != null && bookingId.equals(that.bookingId);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}

