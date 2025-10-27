package ch.unil.bookit.bookitwebservice;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/hello-world")
public class HelloResource {
    @GET
    @Produces("text/plain")
    public String hello() {
        return "Hello, World!";
    }
}

///every time you change something in the get/post functions etc you need to clean, compile package and install
/// for the changes to show up (in postman for example)

///when you package it will aslo run the test
///the person who made the test shoudl also be the ones to run the test