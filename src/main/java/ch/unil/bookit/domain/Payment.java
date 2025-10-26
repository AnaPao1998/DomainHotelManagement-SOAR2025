package ch.unil.bookit.domain;
import com.bookit.domain.shared.PaymentStatus;
import com.bookit.domain.booking.Booking;
import com.bookit.domain.user.Guest;
import jakarta.persistence.*;
import java.util.UUID;

public class Payment {

    private UUID id;
    private Booking booking;
    private Guest guest;
    private int amountCents;
    private String currency;
    private PaymentStatus status = PaymentStatus.AUTHORIZED;
    private String method = "WALLET";
}