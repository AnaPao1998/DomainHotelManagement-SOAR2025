package ch.unil.bookit.bookitwebapp.ui;

import ch.unil.bookit.bookitwebapp.BookItService;
import ch.unil.bookit.domain.Guest;
import ch.unil.bookit.domain.Hotel;
import ch.unil.bookit.domain.booking.Booking;
import ch.unil.bookit.domain.booking.BookingStatus;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
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
    private Hotel selectedHotelForBooking;
    private UUID selectedRoomTypeId;

    //
    // Injections
    //
    @Inject
    BookItService service;

    @Inject
    LoginBean loginBean;

    //
    // Guest Setup
    //
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
    public void loadGuest() {
        var id = this.getUUID();
        if (id != null) {
            Response response = service.getGuests(id.toString());
            guest = response.readEntity(Guest.class);
            if (guest != null) {
                this.setuuid(guest.getId());
                this.setEmail(guest.getEmail());
                this.setPassword(guest.getPassword());
                this.setFirstName(guest.getFirstName());
                this.setLastName(guest.getLastName());
                this.setBalance(guest.getBalance());
                List<Booking> freshBookings = service.getBookingsForGuest(id);
                this.setBookings(freshBookings);
            }
        }
    }
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


    //
    // Password
    //

    // Getters and Setters
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

    // Password change
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


    //
    // Hotels
    //

    // Getters and Setters
    public Hotel getSelectedHotelForBooking() {
        return selectedHotelForBooking;
    }
    public void setSelectedHotelForBooking(Hotel selectedHotelForBooking) {
        this.selectedHotelForBooking = selectedHotelForBooking;
    }
    public Hotel getHotel(UUID hotelId) {
        if (hotelId == null) return null;
        try {
            Response response = service.getHotel(hotelId.toString());
            if (response.getStatus() == 200) {
                return response.readEntity(Hotel.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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


    //
    // Room types
    //

    // Getters and Setters
    public UUID getSelectedRoomTypeId() {
        return selectedRoomTypeId;
    }
    public void setSelectedRoomTypeId(UUID selectedRoomTypeId) {
        this.selectedRoomTypeId = selectedRoomTypeId;
    }
    public java.util.List<String> getRoomTypeOptions() {
        return roomTypeOptions;
    }

    public String getSelectedRoomTypeCode() {
        return selectedRoomTypeCode;
    }

    public void setSelectedRoomTypeCode(String selectedRoomTypeCode) {
        this.selectedRoomTypeCode = selectedRoomTypeCode;
    }

    // set room type options
    private java.util.List<String> roomTypeOptions =
            java.util.Arrays.asList("Standard Room", "Deluxe Room", "Suite");

    private String selectedRoomTypeCode;


    //
    // Booking
    //

    // which is this dialog Message for?
    public String getDialogMessage() {
        return dialogMessage;
    }
    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    // for "Date Booked"
    public String getFormattedDate(java.time.Instant instant) {
        if (instant == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
                .withZone(ZoneId.systemDefault());

        return formatter.format(instant);
    }

    public String startBookingFromSearch(Hotel hotel) {
        this.selectedHotelForBooking = hotel;
        this.selectedRoomTypeId = null;   // keep reset just in case
        this.selectedRoomTypeCode = null; // user must pick one on CreateBooking.xhtml
        return "CreateBooking.xhtml?faces-redirect=true";
    }

    public void confirmBooking() {
        try {
            // 1. Basic checks
            if (selectedHotelForBooking == null) {
                dialogMessage = "Internal error: no hotel selected. Please go back to Search Hotels.";
                PrimeFaces.current().executeScript("PF('messageDialog').show()");
                return;
            }

            if (selectedRoomTypeCode == null || selectedRoomTypeCode.isBlank()) {
                dialogMessage = "Please select a room type.";
                PrimeFaces.current().executeScript("PF('messageDialog').show()");
                return;
            }

            UUID guestId = this.getUUID();

            if (guestId == null && loginBean != null
                    && loginBean.getEmail() != null
                    && loginBean.getPassword() != null) {

                try {
                    guestId = service.authenticate(
                            loginBean.getEmail(),
                            loginBean.getPassword(),
                            "guest"
                    );
                    if (guestId != null) {
                        this.setuuid(guestId); // store it back in the session bean
                    }
                } catch (Exception ignored) {
                }
            }

            if (guestId == null) {
                dialogMessage = "Could not determine the logged-in guest. "
                        + "Please log out and log in again before booking.";
                PrimeFaces.current().executeScript("PF('messageDialog').show()");
                return;
            }

            Booking booking = new Booking();
            booking.setHotelId(selectedHotelForBooking.getHotelId());
            booking.setUserId(guestId);

            UUID roomTypeId = UUID.nameUUIDFromBytes(
                    selectedRoomTypeCode.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            booking.setRoomTypeId(roomTypeId);
            booking.setStatus(BookingStatus.PENDING);

            Response response = service.createBooking(booking);
            int status = response.getStatus();
            String serverMsg = "";
            try {
                serverMsg = response.readEntity(String.class);
            } catch (Exception ignore) {
            }

            if (status >= 200 && status < 300) {
                dialogMessage = "Booking successfully created!";
                // reload guest data + bookings
                loadGuest();
            } else {
                dialogMessage = "Failed to create booking. Server returned: "
                        + status
                        + (serverMsg != null && !serverMsg.isBlank() ? " – " + serverMsg : "");
            }

            PrimeFaces.current().executeScript("PF('messageDialog').show()");

        } catch (Exception e) {
            dialogMessage = "Unexpected error while creating booking: " + e.getMessage();
            PrimeFaces.current().executeScript("PF('messageDialog').show()");
        }
    }

    public void cancelBooking(Booking booking) {
        try {
            booking.markCancelled();
            service.updateBooking(booking);
            loadGuest();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Booking Cancelled", "Your booking has been cancelled."));

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Could not cancel booking."));
        }
    }

}