package ch.unil.bookit.bookitwebapp.ui;

import ch.unil.bookit.bookitwebapp.BookItService;
import ch.unil.bookit.domain.Guest;
import ch.unil.bookit.domain.HotelManager;
import ch.unil.bookit.domain.User;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import jakarta.ws.rs.core.Response;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Logger;

@SessionScoped
@Named
public class RegisterBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(RegisterBean.class.getName());
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
    private String role;
    private String managerCode;
    private static final String REQUIRED_MANAGER_CODE = "BOOKIT2025";

    @Inject
    BookItService service;

    public RegisterBean() {
        reset();
    }

    public void reset() {
        firstName = null;
        lastName = null;
        email = null;
        password = null;
        confirmPassword = null;
        role = null;
    }

    public String register() {
        LoginBean.invalidateSession();
        String errorMessage = null;

        if (isInvalid(password) || isInvalid(confirmPassword)) { // checks if passwords are properly filled
            FacesContext.getCurrentInstance().
                    addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed.", "Please enter a valid password."));
            return null;
        }

        if (!password.equals(confirmPassword)) { // checks if passwords match
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed.", "Passwords do not match."));
            return null;
        }

        String hashedPassword = password;

        if (isInvalid(firstName) || isInvalid(lastName) ||  isInvalid(email) || isInvalid(password) || isInvalid(role)) { // checks if all required fields are properly filled
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "All fields are required.", null));
            return null;
        }

        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) { // checks email format
            FacesContext.getCurrentInstance()
                    .addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed.", "Please enter a valid email address."));
            return null;
        }

        if ("manager".equals(role)) {
            if (managerCode == null || !managerCode.equals(REQUIRED_MANAGER_CODE)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Registration failed.",
                                "Invalid Manager Access Code. You are not authorized."));
                return null;
            }
        }

        switch (role) {
            case "guest":
                Guest guest = new Guest();
                guest.setFirstName(firstName);
                guest.setLastName(lastName);
                guest.setEmail(email);
                guest.setPassword(hashedPassword);

                try {
                Response response = service.createGuest(guest);
                log.info("Guest successfully created.");
                return "Login?faces-redirect=true";
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    log.warning("Error creating guest: " + errorMessage);
                }
                break;
            case "manager":
                HotelManager manager = new HotelManager();
                manager.setFirstName(firstName);
                manager.setLastName(lastName);
                manager.setEmail(email);
                manager.setPassword(hashedPassword);

                try {
                Response response = service.createManager(manager);
                log.info("Manager successfully created.");
                return "Login?faces-redirect=true";
                } catch (Exception e)  {
                    errorMessage = e.getMessage();
                    log.warning("Error creating manager: " + errorMessage);
                }
                break;
            default:
                throw new IllegalStateException("Invalid role: " + role);
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed." + errorMessage, null));
        return "register?faces-redirect=true";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isInvalid(String value) {
        return value == null || value.isBlank();
    }

    public String getManagerCode() { return managerCode; }
    public void setManagerCode(String managerCode) { this.managerCode = managerCode; }
}
