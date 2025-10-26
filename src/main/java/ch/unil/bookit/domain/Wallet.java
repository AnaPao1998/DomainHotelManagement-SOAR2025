package ch.unil.bookit.domain;
import com.bookit.domain.user.Guest;
import jakarta.persistence.*;
import java.util.UUID;

public class Wallet {

    private UUID userId;
    private Guest user;

    @Column(nullable=false) private String currency = "USD";
    @Column(nullable=false) private int balanceCents = 0;
}
