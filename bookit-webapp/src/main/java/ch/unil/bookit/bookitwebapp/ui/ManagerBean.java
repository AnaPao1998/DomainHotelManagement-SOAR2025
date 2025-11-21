package ch.unil.bookit.bookitwebapp.ui;

import ch.unil.bookit.bookitwebapp.BookItService;
import ch.unil.bookit.domain.*;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

@SessionScoped
@Named
public class ManagerBean extends HotelManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private HotelManager manager;

    @Inject
    BookItService service;

    public ManagerBean() {
        this(null, null, null, null, null);
    }

    public ManagerBean(UUID uuid, String email, String password, String firstName, String lastName) {
        super(uuid, email, password, firstName, lastName);
        init();
        manager = new HotelManager(uuid, email, password, firstName, lastName);
    }

    public void init() {
        manager = null;
    }

    // load manager

    public void loadManager() {
        var id = this.getUUID();
        if (id != null) {
            Response response = service.getManager(id.toString());
            manager = response.readEntity(HotelManager.class);
            if (manager != null) {
                this.setuuid(manager.getUUID());
                this.setEmail(manager.getEmail());
                this.setPassword(manager.getPassword());
                this.setFirstName(manager.getFirstName());
                this.setLastName(manager.getLastName());
            }
        }
    }

}
