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
    private String role;

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
        role = null;
    }

    public String register() {
        LoginBean.invalidateSession();
        String hashedPassword = password;
        String errorMessage = null;

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
