package ch.unil.bookit.bookitwebservice;
import ch.unil.bookit.domain.Guest;
import ch.unil.bookit.domain.Hotel;
import ch.unil.bookit.domain.User;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class ApplicationResource {
    private Map<UUID, Guest> guests;
    private Map<UUID, Hotel> hotels;

    @PostConstruct
    public void init() {
        guests = new HashMap<>();
        hotels = new HashMap<>();
        populateApplicationState();
    }

    //create
    public Guest createGuest(Guest guest){
        guest.setuuid(UUID.randomUUID());
        guests.put(guest.getUUID(), guest);
        return guest;
    }

    public Map<UUID, Guest> getAllGuests() {
        return guests;
    }
    //read
    public Guest getGuest(UUID id){
        return guests.get(id);
    }

    //update
    public Guest updateGuest(UUID id, Guest updatedGuest) {
        if(guests.containsKey(id)){
            updatedGuest.setuuid(id);
            guests.put(id, updatedGuest);
            return updatedGuest;
        }
        return null; //if guest not found return null
    }
    //delete
    public boolean deleteGuest(UUID id){
        return guests.remove(id) != null;
    }

    private void populateApplicationState(){
        //dummy test
        UUID guestId = UUID.randomUUID();
        UUID guestId2 = UUID.randomUUID();
        Guest guest1 = new Guest(guestId, "guest1@bookit.com", "pass123", "Ana", "Montero");
        guest1.deposit(500);
        guests.put(guestId, guest1);
        Guest guest2 = new Guest(guestId2, "guest2@bookit.com", "pass123", "Duska", "Bogdanic");
        guests.put(guestId2, guest2);
    }
}