package ch.unil.bookit.bookitwebapp.ui;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class UserData {

    public String getManagerRole() {
        return "manager";
    }

    public String getGuestRole() {
        return "guest";
    }
}
