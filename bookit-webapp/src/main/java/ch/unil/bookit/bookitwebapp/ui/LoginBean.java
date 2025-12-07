package ch.unil.bookit.bookitwebapp.ui;

import ch.unil.bookit.bookitwebapp.BookItService;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.UUID;

@SessionScoped
@Named
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;
    private String role;

    @Inject
    BookItService service;

    @Inject
    GuestBean guestBean;

    @Inject
    ManagerBean managerBean;

    public LoginBean() {
        reset();
    }

    public void reset() {
        email = null;
        password = null;
        role = null;
    }

    public String login() {
        var uuid = service.authenticate(email, password, role);
        var session = getSession(true);
        if (uuid != null) {
            session.setAttribute("uuid", uuid);
            session.setAttribute("email", email);
            session.setAttribute("role", role);
            switch (role) {
                case "manager":
                    managerBean.setuuid(uuid);
                    managerBean.loadManager();
                    return "ManagerHome?faces-redirect=true";
                case "guest":
                    guestBean.setuuid(uuid);
                    guestBean.loadGuest();
                    return "GuestHome?faces-redirect=true";
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Login", null));
        reset();
        return "Login";
    }

    public String logout() {
        invalidateSession();
        reset();
        return "Login?faces-redirect=true";
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public UUID getLoggedInManagerId() {
        return managerBean.getManagerId();
    }

    public static HttpSession getSession(boolean create) {
        var facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {
            return null;
        }
        var externalContext = facesContext.getExternalContext();
        if (externalContext == null) {
            return null;
        }
        return (HttpSession) externalContext.getSession(create);
    }

    public static void invalidateSession() {
        var facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null) {
            facesContext.getExternalContext().invalidateSession();
        }
    }
}
