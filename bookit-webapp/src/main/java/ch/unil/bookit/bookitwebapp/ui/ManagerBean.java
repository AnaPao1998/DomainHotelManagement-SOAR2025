package ch.unil.bookit.bookitwebapp.ui;

import ch.unil.bookit.bookitwebapp.BookItService;
import ch.unil.bookit.domain.Guest;
import ch.unil.bookit.domain.HotelManager;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import org.primefaces.PrimeFaces;
import jakarta.faces.context.FacesContext;
import jakarta.faces.application.FacesMessage;
import ch.unil.bookit.domain.booking.Booking;
import ch.unil.bookit.domain.Hotel;
import jakarta.servlet.http.HttpSession;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.Collections;
import java.util.List;

@SessionScoped
@Named
public class ManagerBean extends HotelManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private HotelManager manager;
    private String currentPassword;
    private String newPassword;
    private boolean changed;
    private String dialogMessage;
    private List<Booking> pendingBookings;

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
        currentPassword = null;
        newPassword = null;
        changed = false;
        dialogMessage = null;
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    // password

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void savePasswordChange() throws Exception {
        if (currentPassword == null || newPassword == null) {
            dialogMessage = "Please enter all the required fields.";
            PrimeFaces.current().executeScript("PF('passwordChangeDialog').show()");
        } else if (!currentPassword.equals(manager.getPassword())) {
            dialogMessage = "Passwords don't match!";
            PrimeFaces.current().executeScript("PF('passwordChangeDialog').show()");
        } else if (currentPassword.equals(newPassword)) {
            dialogMessage = "The new password must be different from the old password.";
            PrimeFaces.current().executeScript("PF('passwordChangeDialog').show()");
        } else {
            this.setPassword(newPassword);
            updateManager();
            dialogMessage = "Password successfully changed";
            PrimeFaces.current().executeScript("PF('passwordChangeDialog').show()");
            resetPasswordChange();
        }
    }

    public void resetPasswordChange() {
        this.currentPassword = null;
        this.newPassword = null;
    }

    // manager

    public void updateManager() {
        try {
            UUID uuid = this.getUUID();
            if (uuid != null) {
                Response updated_manager = service.updateManager(this);
                loadManager();
                changed = false;
            }
        } catch (Exception e) {
            dialogMessage = e.getMessage();
            PrimeFaces.current().executeScript("PF('updateErrorDialog').show();");
        }
    }
    public void loadManager() {
        UUID id = this.getUUID();
        //System.out.println("DEBUG ManagerBean: UUID before service call: " + id);
        if (id != null) {
            Response response = service.getManager(id.toString());
            manager = response.readEntity(HotelManager.class);
            if (manager != null) {
                this.setuuid(manager.getId());
                this.setEmail(manager.getEmail());
                this.setPassword(manager.getPassword());
                this.setFirstName(manager.getFirstName());
                this.setLastName(manager.getLastName());

                List<Booking> fresh = service.getPendingBookingsForManager(id);
                this.pendingBookings = (fresh != null) ? fresh : Collections.emptyList();
            }
        }
    }


    // checks if any of the profile fields have been changed
    public void checkIfChanged() {
        if (manager == null) {
            changed = false;
            return;
        }
        boolean firstNameChanged = !manager.getFirstName().equals(this.getFirstName());
        boolean lastNameChanged = !manager.getLastName().equals(this.getLastName());
        boolean emailChanged = !manager.getEmail().equals(this.getEmail());
        boolean passwordChanged = !manager.getPassword().equals(this.getPassword());
        changed = firstNameChanged || lastNameChanged || emailChanged || passwordChanged;
    }

    public boolean isChanged() {
        return changed;
    }

    public UUID getManagerId() {
        return this.getUUID();
    }

    public List<Booking> getPendingBookings() { return pendingBookings; }
    public void setPendingBookings(List<Booking> pendingBookings) { this.pendingBookings = pendingBookings; }
    public void setManager(HotelManager manager) { this.manager = manager; }

    public void loadPendingBookings() {
        UUID managerId = this.getUUID();
        //UUID managerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        System.out.println("DEBUG LoadPendingBookings: managerId = " + managerId);

        try {
            List<Booking> freshBookings = service.getPendingBookingsForManager(managerId);
            this.pendingBookings = (freshBookings != null) ? freshBookings : Collections.emptyList();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Load Error",
                            "Failed to load bookings: " + e.getMessage()));
            this.pendingBookings = Collections.emptyList();
        }
    }

    public void approveBooking(Booking booking) {
        UUID managerId = this.getUUID();
        if (managerId != null && booking != null) {
            try {
                service.approveBooking(managerId.toString(), booking.getBookingId().toString());
                loadPendingBookings();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Booking approved."));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to approve booking: " + e.getMessage()));
            }
        }
    }

    public void cancelBooking(Booking booking) {
        UUID managerId = this.getUUID();
        if (managerId != null && booking != null) {
            try {
                service.cancelBooking(managerId.toString(), booking.getBookingId().toString());
                loadPendingBookings();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Booking rejected."));
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Failed to reject booking: " + e.getMessage()));
            }
        }
    }

    public String getHotelName(UUID hotelId) {
        if (hotelId == null) return "N/A";
        try {
            Response response = service.getHotel(hotelId.toString());
            if (response.getStatus() == 200) {
                Hotel h = response.readEntity(Hotel.class);
                return h.getName();
            }
        } catch (Exception e) {
            System.err.println("Error fetching hotel name for " + hotelId + ": " + e.getMessage());
        }
        return "Hotel Not Found";
    }
    public String getGuestName(UUID userId) {
        if (userId == null) return "Unknown";
        try {
            Response response = service.getGuests(userId.toString());
            if (response.getStatus() == 200) {
                Guest g = response.readEntity(Guest.class);
                return g.getFirstName() + " " + g.getLastName();
            }
        } catch (Exception e) {
            System.err.println("Error fetching guest: " + e.getMessage());
        }
        return "Unknown Guest";
    }

    public String getHotelImage(UUID hotelId) {
        if (hotelId == null) return "default.jpg"; // fallback image
        try {
            Response response = service.getHotel(hotelId.toString());
            if (response.getStatus() == 200) {
                Hotel h = response.readEntity(Hotel.class);
                return h.getImageUrl();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "default.jpg";
    }
    public String getFormattedDate(java.time.Instant instant) {
        if (instant == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                .withZone(ZoneId.systemDefault());

        return formatter.format(instant);
    }
}
