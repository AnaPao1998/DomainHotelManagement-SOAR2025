package ch.unil.bookit.bookitwebapp.ui;

import ch.unil.bookit.bookitwebapp.BookItService;
import ch.unil.bookit.domain.Guest;
import ch.unil.bookit.domain.Hotel;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@SessionScoped
@Named
public class GuestBean extends Guest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Guest guest;
    private String currentPassword;
    private String newPassword;
    private boolean changed;
    private String dialogMessage;

    @Inject
    BookItService service;

    // getting list of hotels
    private List<Hotel> hotels;
    public List<Hotel> getHotels() {
        return hotels;
    }
    public void loadHotels() {
        try {
            // Call the REST endpoint: GET /api/hotelmanager
            hotels = service.getAllHotels();
        } catch (Exception e) {
            // show dialog or log if needed
            hotels = Collections.emptyList();
        }
    }

    public GuestBean() {
        this(null, null, null, null, null);
    }

    public GuestBean(UUID uuid, String email, String password, String firstName, String lastName) {
        super(uuid, email, password, firstName, lastName);
        init();
        guest = new Guest(uuid, email, password, firstName, lastName);
    }

    public void init() {
        guest = null;
        currentPassword = null;
        newPassword = null;
        changed = false;
        dialogMessage = null;
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
        if (currentPassword == null || newPassword == null) { // check if required fields are filled
            dialogMessage = "Please enter all the required fields.";
            PrimeFaces.current().executeScript("PF('passwordChangeDialog').show()");
        } else if (!currentPassword.equals(guest.getPassword())) { // check if the user typed their password correctly
            dialogMessage = "Passwords don't match!";
            PrimeFaces.current().executeScript("PF('passwordChangeDialog').show()");
        } else if (currentPassword.equals(newPassword)) { // check if the old password is not the same as the new one
            dialogMessage = "The new password must be different from the old password.";
            PrimeFaces.current().executeScript("PF('passwordChangeDialog').show()");
        } else { // change password
            this.setPassword(newPassword);
            updateGuest();
            dialogMessage = "Password successfully changed";
            PrimeFaces.current().executeScript("PF('passwordChangeDialog').show()");
            resetPasswordChange();
        }
    }

    public void resetPasswordChange() {
        this.currentPassword = null;
        this.newPassword = null;
    }

    // guest

    public void updateGuest() {
        try {
            UUID uuid = this.getUUID();
            if (uuid != null) {
                Response updated_guest = service.updateGuest(this);
                loadGuest();
                changed = false;
            }
        } catch (Exception e) {
            dialogMessage = e.getMessage();
            PrimeFaces.current().executeScript("PF('updateErrorDialog').show();");

        }
    }

    public void loadGuest() {
        var id = this.getUUID();
        if (id != null) {
            Response response = service.getGuests(id.toString());
            guest = response.readEntity(Guest.class);
            if (guest != null) {
                this.setuuid(guest.getUUID());
                this.setEmail(guest.getEmail());
                this.setPassword(guest.getPassword());
                this.setFirstName(guest.getFirstName());
                this.setLastName(guest.getLastName());
                this.setBalance(guest.getBalance());
                //List<Booking> freshBookings = service.getBookingsForGuest(id);
                //this.setBookings(freshBookings);

            }
        }
    }

    // checks if any of the profile fields have been changed
    public void checkIfChanged() {
        boolean firstNameChanged = !guest.getFirstName().equals(this.getFirstName());
        boolean lastNameChanged = !guest.getLastName().equals(this.getLastName());
        boolean emailChanged = !guest.getEmail().equals(this.getEmail());
        boolean passwordChanged = !guest.getPassword().equals(this.getPassword());
        changed = firstNameChanged || lastNameChanged || emailChanged || passwordChanged;
    }


    public boolean isChanged() {
        return changed;
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }


    public String getFormattedDate(java.time.Instant instant) {
        if (instant == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                .withZone(ZoneId.systemDefault());

        return formatter.format(instant);
    }
}