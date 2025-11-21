package ch.unil.bookit.bookitwebapp;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;


// this class is necessary because it is what activates JAX-RS (REST)
// in the web application
@ApplicationPath("/api")
public class HelloApplication extends Application {
}
