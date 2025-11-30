package ch.unil.bookit.bookitwebservice;

import ch.unil.bookit.domain.Guest;
import ch.unil.bookit.domain.Hotel;
import ch.unil.bookit.domain.HotelManager;
import ch.unil.bookit.domain.booking.Booking;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ApplicationScoped
public class ApplicationResource {
    private Map<UUID, Guest> guests;
    private Map<UUID, Hotel> hotels;
    private Map<UUID, Booking>  bookings;
    private Map<UUID, HotelManager>  managers;

    /*@Inject
    private ManagerRegistry managerRegistry;
    */
    @PostConstruct
    public void init() {
        guests = new HashMap<>();
        hotels = new HashMap<>();
        bookings = new HashMap<>();
        managers = new HashMap<>();
        populateApplicationState();
    }

    public Guest createGuest(Guest guest){
        guest.setuuid(UUID.randomUUID());
        guests.put(guest.getUUID(), guest);
        return guest;
    }

    public Map<UUID, Guest> getAllGuests() {
        return guests;
    }

    public Guest getGuest(UUID id){
        return guests.get(id);
    }

    public Guest updateGuest(UUID id, Guest updatedGuest) {
        if(guests.containsKey(id)){
            updatedGuest.setuuid(id);
            guests.put(id, updatedGuest);
            return updatedGuest;
        }
        return null;
    }

    public boolean deleteGuest(UUID id){
        return guests.remove(id) != null;
    }

    public Guest depositToGuestWallet(UUID guestId, int amount) {
        Guest guest = getGuest(guestId);
        if (guest == null) {
            return null;
        }
        guest.deposit(amount);
        return guest;
    }

    public Guest withdrawFromGuestWallet(UUID guestId, int amount) {
        Guest guest = getGuest(guestId);
        if (guest == null) {
            return null;
        }
        guest.withdraw(amount);
        return guest;
    }

    public HotelManager createManager(HotelManager manager){
        manager.setuuid(UUID.randomUUID());
        managers.put(manager.getUUID(), manager);
        return manager;
    }

    public Map<UUID, HotelManager> getAllManagers() {
        return managers;
    }

    public HotelManager getManager(UUID managerId) {
        return managers.get(managerId);
    }

    public HotelManager updateManager(UUID id, HotelManager updatedManager) {
        if (managers.containsKey(id)) {
            updatedManager.setuuid(id);
            managers.put(id, updatedManager);
            return updatedManager;
        }
        return null;
    }

    public Hotel createHotel(Hotel hotel) {

        if (hotel.getManagerId() == null) {
            throw new IllegalArgumentException("Manager ID is required");
        }


        if (!managers.containsKey(hotel.getManagerId())) {
            throw new IllegalArgumentException("Manager not found");
        }


        HotelManager manager = managers.get(hotel.getManagerId());

        if (hotel.getHotelId() == null) {
            hotel.setHotelId(UUID.randomUUID());
        }

        hotel.publish();

        manager.addHotel(hotel);

        hotels.put(hotel.getHotelId(), hotel);
        return hotel;
    }

    public Map<UUID, Hotel> getAllHotels() {
        return hotels;
    }

    public Hotel getHotel(UUID id) {
        return hotels.get(id);
    }

    public Hotel updateHotel(UUID id, Hotel updatedHotel) {
        if (!hotels.containsKey(id)) {
            return null;
        }
        updatedHotel.setHotelId(id);
        hotels.put(id, updatedHotel);
        return updatedHotel;
    }

    public boolean deleteHotel(UUID id) {
        return hotels.remove(id) != null;
    }

    public Map<UUID, Booking> getBookings() {
        return bookings;
    }

    public Booking getBooking(UUID bookingId) {
        return bookings.get(bookingId);
    }

    public void saveBooking(Booking booking) {
        bookings.put(booking.getBookingId(), booking);
    }

    public Booking createBooking(UUID hotelId, UUID guestId, UUID roomTypeId) {
        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking(bookingId, hotelId, guestId, roomTypeId);
        bookings.put(bookingId, booking);
        booking.setCreatedAt(Instant.now());
        booking.setUpdatedAt(Instant.now());

        Guest guest = guests.get(guestId);
        if (guest != null) {
            guest.addBooking(booking);   // keep guest view in sync
        }
        return booking;
    }

    public java.util.List<Booking> getBookingsForGuest(UUID guestId) {
        if (guestId == null) {
            return java.util.Collections.emptyList();
        }

        return bookings.values().stream()
                .filter(b -> guestId.equals(b.getUserId()))
                .toList();
    }

    private void populateApplicationState() {
        
        UUID guestId1 = UUID.randomUUID();
        UUID guestId2 = UUID.randomUUID();

        Guest guest1 = new Guest(guestId1, "guest1@bookit.com", "pass123", "Ana", "Montero");
        guest1.deposit(500);
        guests.put(guestId1, guest1);

        Guest guest2 = new Guest(guestId2, "bogdanic.duska@gmail.com", "pass123", "Duska", "Bogdanic");
        guests.put(guestId2, guest2);

        UUID managerId = UUID.randomUUID();
        HotelManager manager = new HotelManager(managerId, "manager@bookit.com", "pass123", "Marta", "Keller");
        managers.put(managerId, manager);

        String[][] seed = {
                {"Bookit Inn", "Cozy place near the lake", "Lausanne", "Switzerland", "Rue de la Paix 10", "120.00", "hotel1.jpg"},
                {"Alpine Retreat", "Mountain view hotel with spa", "Zermatt", "Switzerland", "Matterhornstrasse 5", "230.00", "hotel2.jpg"},
                {"Leman Palace", "Elegant lakeside hotel", "Geneva", "Switzerland", "Quai du Mont-Blanc 20", "320.00", "hotel3.jpg"},
                {"City Central Hotel", "Right in the old town", "Zurich", "Switzerland", "Bahnhofstrasse 50", "210.00", "hotel4.jpg"},
                {"Sunrise Beach Resort", "Sea view and cocktails", "Nice", "France", "Promenade des Anglais 7", "190.00", "hotel5.jpg"},
                {"Canal View Lodge", "Quiet spot near canals", "Bruges", "Belgium", "Spiegelrei 3", "160.00", "hotel6.jpg"},
                {"Royal Arden Hotel", "Business & conference center", "Brussels", "Belgium", "Avenue Louise 120", "220.00", "hotel7.jpg"},
                {"Mountain Cabin Hotel", "Rustic vibes, hiking paths", "Chamonix", "France", "Route du Tour 15", "180.00", "hotel8.jpg"},
                {"Harbour Lights Inn", "Harbour and seafood nearby", "Hamburg", "Germany", "Fischmarkt 12", "170.00", "hotel9.jpg"},
                {"Old Town Boutique", "Boutique rooms in city center", "Prague", "Czech Republic", "Karlova 9", "150.00", "hotel10.jpg"}
        };

        Hotel firstHotel = null;
        Hotel secondHotel = null;

        for (String[] h : seed) {
            // Pass the local 'manager' object
            Hotel created = createHotelForManager(manager, h[0], h[1], h[2], h[3], h[4], h[5], h[6]);
            if (firstHotel == null) firstHotel = created;
            else if (secondHotel == null) secondHotel = created;
        }

        // Room Types & prices
        manager.defineRoomTypes(java.util.Arrays.asList("STANDARD", "DELUXE"));
        manager.setRoomPrices("STANDARD", 120.0);
        manager.setRoomPrices("DELUXE", 170.0);

        // roomType IDs for demo bookings
        UUID stdTypeId = UUID.randomUUID();
        UUID dlxTypeId = UUID.randomUUID();

        // Demo bookings for first hotel
        if (firstHotel != null && secondHotel != null) {
            //Booking 1: Ana
            UUID b1_Id = UUID.randomUUID();
            Booking b1 = new Booking(b1_Id, firstHotel.getHotelId(), guestId1, stdTypeId);
            b1.setStatus(ch.unil.bookit.domain.booking.BookingStatus.PENDING);
            bookings.put(b1_Id, b1);
            guest1.addBooking(b1);

            //Booking 2: Ana
            UUID b2_Id = UUID.randomUUID();
            Booking b2 = new Booking(b2_Id, secondHotel.getHotelId(), guestId1, dlxTypeId);
            b2.setStatus(ch.unil.bookit.domain.booking.BookingStatus.PENDING);
            bookings.put(b2_Id, b2);
            guest1.addBooking(b2);

            //Booking 3: Duska
            UUID b3_Id = UUID.randomUUID();
            Booking b3 = new Booking(b3_Id, firstHotel.getHotelId(), guestId2, stdTypeId);
            b3.setStatus(ch.unil.bookit.domain.booking.BookingStatus.PENDING);
            bookings.put(b3_Id, b3);
            guest2.addBooking(b3);

            //Booking 4: Duska
            UUID b4_Id = UUID.randomUUID();
            Booking b4 = new Booking(b4_Id, secondHotel.getHotelId(), guestId2, dlxTypeId);
            b4.setStatus(ch.unil.bookit.domain.booking.BookingStatus.PENDING);
            bookings.put(b4_Id, b4);
            guest2.addBooking(b4);

            manager.approveBooking(b1, guests);
            //manager.cancelBooking(b2, guests);
        }
    }

    private Hotel createHotelForManager(
            HotelManager manager,
            String name,
            String description,
            String city,
            String country,
            String address,
            String price,
            String imageUrl
    ) {
        UUID hotelId = UUID.randomUUID();

        Hotel hotel = new Hotel(
                hotelId,
                manager.getId(),
                name,
                description,
                city,
                country,
                address,
                new java.math.BigDecimal(price)
        );

        hotel.publish();
        hotel.setImageUrl(imageUrl);

        hotels.put(hotelId, hotel);
        manager.addHotel(hotel);

        return hotel;
    }

    public UUID authenticateGuest(String email, String password) {
        for (Guest guest : guests.values()) {
            if (guest.getEmail().equals(email) && guest.getPassword().equals(password)) {
                return guest.getId();
            }
        }
        return null;
    }

    public UUID authenticateManager(String email, String password) {
        for (HotelManager manager : managers.values()) {
            if (manager.getEmail().equals(email) && manager.getPassword().equals(password)) {
                return manager.getId();
            }
        }
        return null;
    }

    public boolean deleteManager(UUID managerId) {
        return getAllManagers().remove(managerId) != null;
    }

    public Booking createBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }

        if (booking.getBookingId() == null) {
            booking.setBookingId(UUID.randomUUID());
        }

        if (booking.getCreatedAt() == null) {
            booking.setCreatedAt(java.time.Instant.now());
        }
        booking.setUpdatedAt(java.time.Instant.now());

        bookings.put(booking.getBookingId(), booking);

        Guest g = guests.get(booking.getUserId());
        if (g != null) {
            g.addBooking(booking);
        }

        return booking;
    }
}
