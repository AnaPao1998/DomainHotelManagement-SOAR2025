package ch.unil.bookit.domain;
import java.util.UUID;

public class Payment {

    private UUID id;  // unique payment id
    private UUID userId;  // comes from user
    private UUID bookingId; // comes from bookingId
    private int amount;

    public Payment(UUID userId, UUID bookingId, int amount) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.bookingId = bookingId;
        this.amount = amount;
    }
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getBookingId() { return bookingId; }
    public int getAmount() { return amount; }

    public void setId(UUID id) { this.id = id; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setBookingId(UUID bookingId) { this.bookingId = bookingId; }
    public void setAmount(int amount) { this.amount = amount; }
}

