package ch.unil.bookit.bookitwebservice;

import ch.unil.bookit.domain.HotelManager;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class ManagerRegistry {

    private final HotelManager defaultManager;

    public ManagerRegistry() {
        this.defaultManager = new HotelManager(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "manager@bookit.com",
                "secret",
                "Default",
                "Manager"
        );
    }

    public HotelManager getDefaultManager() {
        return defaultManager;
    }
}

