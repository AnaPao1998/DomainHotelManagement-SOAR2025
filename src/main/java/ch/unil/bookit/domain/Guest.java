package ch.unil.bookit.domain;

import java.util.*;

public class Guest extends User{
    //it maps a booking's ID to the booking object
    private Map<UUID, Booking> bookings;
    //Default Constructor
    public Guest(){
        super();
        this.bookings = new TreeMap<>();
    }
    //Main Constructor
    public Guest(UUID uuid, String email, String password, String firstName, String lastName) {
        super(uuid, email, password, firstName, lastName); //calls the parent User constructor
        this.bookings = new TreeMap<>();
    }


    //Guest specific methods
    //single booking
    public Booking getBooking(UUID bookingId){
        return this.bookings.get(bookingId);
    }
    //list of bookings
    public List<Booking> getBooking(){
        return new ArrayList<>(this.bookings.values());
    }
    //cancel booking
    public boolean cancelBooking(Booking bookingId){
        if(bookingId == null){
            return false;
        }
        return false;
    }
    //add booking
    public void addBooking(Booking booking){
        if (booking == null){
            throw new IllegalArgumentException("Booking cannot be null");
        }

        if (bookings.put(booking.getBookingId(), booking) != null) {
            throw new IllegalArgumentException("Booking already exists");
        }
    }

}

