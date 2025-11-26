package ch.unil.bookit.bookitwebapp.ui;

import ch.unil.bookit.bookitwebapp.BookItService;
import ch.unil.bookit.domain.HotelManager;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.core.Response;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.UUID;

@SessionScoped
@Named
public class ManagerBean extends HotelManager implements Serializable {
    private static final long serialVersionUID = 1L;

    private HotelManager manager;
    private String currentPassword;
    private String newPassword;
    private boolean changed;
    private String dialogMessage;

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
}
