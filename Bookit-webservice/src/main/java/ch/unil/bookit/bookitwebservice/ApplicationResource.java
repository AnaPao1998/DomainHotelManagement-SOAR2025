package ch.unil.bookit.bookitwebservice;

import ch.unil.bookit.domain.Booking;
import ch.unil.bookit.domain.Guest;
import ch.unil.bookit.domain.Hotel;
import ch.unil.bookit.domain.HotelManager;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class ApplicationResource {
    private Map<UUID, Guest> guests;
    private Map<UUID, Hotel> hotels;
    private Map<UUID, Booking>  bookings;
    private Map<UUID, HotelManager>  managers;

    @PostConstruct
    public void init() {
        guests = new HashMap<>();
        hotels = new HashMap<>();
        bookings = new HashMap<>();
        managers = new HashMap<>();
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

    public Map<UUID, HotelManager> getAllManagers() {
        return managers;
    }

    private void populateApplicationState(){
        //dummy test
        UUID guestId = UUID.randomUUID();
        UUID guestId2 = UUID.randomUUID();
        Guest guest1 = new Guest(guestId, "guest1@bookit.com", "pass123", "Ana", "Montero");
        guest1.deposit(500);
        guests.put(guestId, guest1);
        Guest guest2 = new Guest(guestId2, "bogdanic.duska@gmail.com", "pass123", "Duska", "Bogdanic");
        guests.put(guestId2, guest2);


        // ==== Hotel ====
        UUID hotelId = UUID.randomUUID();
        Hotel hotel = new Hotel(
                hotelId,
                "Bookit Inn",
                "Cozy place near the lake",
                "Lausanne",
                "Switzerland",
                "Rue de la Paix 10")
        ;
        hotel.publish(); // make it visible
        hotels.put(hotelId, hotel);

        // ==== Hotel Manager ====
        UUID managerId = UUID.randomUUID();
        HotelManager manager = new HotelManager(
                managerId,
                "manager@bookit.com",
                "managerPass",
                "Marta",
                "Keller"
        );


        managers.put(managerId, manager);
        // link hotel to manager
        manager.addHotel(hotel);

        // (optional) define simple room types & prices on the manager
        manager.defineRoomTypes(java.util.Arrays.asList("STANDARD", "DELUXE"));
        manager.setRoomPrices("STANDARD", 120.0);
        manager.setRoomPrices("DELUXE", 170.0);

        // ==== Bookings (linked to hotel + guests) ====
        // If you don’t have a real Room/roomType entity handy, just use a UUID as roomTypeId
        UUID stdRoomTypeId = java.util.UUID.randomUUID();
        UUID dlxRoomTypeId = java.util.UUID.randomUUID();

        // Booking 1: guest1, STANDARD
        UUID bookingId1 = UUID.randomUUID();
        Booking b1 = new Booking(bookingId1, hotelId, guestId, stdRoomTypeId);
        b1.setStatus(Booking.bookingStatus.PENDING);
        bookings.put(bookingId1, b1);

        // Booking 2: guest2, DELUXE
        UUID bookingId2 = UUID.randomUUID();
        Booking b2 = new Booking(bookingId2, hotelId, guestId2, dlxRoomTypeId);
        b2.setStatus(Booking.bookingStatus.PENDING);
        bookings.put(bookingId2, b2);

        // Booking 1: guest1, STANDARD
        UUID bookingId3 = UUID.randomUUID();
        Booking b3 = new Booking(bookingId3, hotelId, guestId, stdRoomTypeId);
        b3 .setStatus(Booking.bookingStatus.PENDING);
        bookings.put(bookingId3, b3);

        // Booking 2: guest2, DELUXE
        UUID bookingId4 = UUID.randomUUID();
        Booking b4 = new Booking(bookingId4, hotelId, guestId2, dlxRoomTypeId);
        b4.setStatus(Booking.bookingStatus.PENDING);
        bookings.put(bookingId4, b4);

        manager.approveBooking(b1,guests);
        manager.cancelBooking(b2,guests);
    }

    public Map<UUID, Booking> getBookings() {
        return bookings;
    }


    public HotelManager getManager(UUID managerId) {
        return managers.get(managerId);
    }

    public Booking getBooking(UUID bookingId) {
        return bookings.get(bookingId);
    }

    public void saveBooking(Booking booking) {
        bookings.put(booking.getBookingId(), booking);
    }
    public Guest depositToGuestWallet(UUID guestId, int amount) {
        Guest guest = getGuest(guestId);
        if (guest == null) {
            return null;
        }
        guest.deposit(amount);   // uses User.deposit()
        return guest;
    }

    public Guest withdrawFromGuestWallet(UUID guestId, int amount) {
        Guest guest = getGuest(guestId);
        if (guest == null) {
            return null;
        }
        guest.withdraw(amount);  // uses User.withdraw()
        return guest;
    }

    // create
    public Hotel createHotel(Hotel hotel) {
        if (hotel.getHotelId() == null) {
            hotel.setHotelId(UUID.randomUUID());
        }
        hotels.put(hotel.getHotelId(), hotel);
        return hotel;
    }

    // read all hotels
    public Map<UUID, Hotel> getAllHotels() {
        return hotels;
    }

    // read one hotel
    public Hotel getHotel(UUID id) {
        return hotels.get(id);
    }

    // update
    public Hotel updateHotel(UUID id, Hotel updatedHotel) {
        if (!hotels.containsKey(id)) {
            return null;
        }
        updatedHotel.setHotelId(id);
        hotels.put(id, updatedHotel);
        return updatedHotel;
    }

    // delete
    public boolean deleteHotel(UUID id) {
        return hotels.remove(id) != null;
    }
}