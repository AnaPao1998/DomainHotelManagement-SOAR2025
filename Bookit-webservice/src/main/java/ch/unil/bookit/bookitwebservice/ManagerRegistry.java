package ch.unil.bookit.bookitwebservice;

import ch.unil.bookit.domain.HotelManager;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class ManagerRegistry {

    private static final UUID DEFAULT_MANAGER_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final HotelManager defaultManager;

    public ManagerRegistry() {
        this.defaultManager = new HotelManager(
                DEFAULT_MANAGER_ID,
                "manager@bookit.com",
                "managerPass",   // <— same password you use for login
                "Marta",
                "Keller"
        );
    }

    public HotelManager getDefaultManager() {
        return defaultManager;
    }

    public UUID getDefaultManagerId() {
        return DEFAULT_MANAGER_ID;
    }
}

