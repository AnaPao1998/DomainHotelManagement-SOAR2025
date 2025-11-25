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

    @Inject
    private ManagerRegistry managerRegistry;

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

        HotelManager manager = managerRegistry.getDefaultManager();

        if (!managers.containsKey(manager.getId())) {
            managers.put(manager.getId(), manager);
        }

        if (hotel.getHotelId() == null) {
            hotel.setHotelId(UUID.randomUUID());
        }
        hotel.setManagerId(manager.getId());
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

        HotelManager defaultManager = managerRegistry.getDefaultManager();
        managers.put(defaultManager.getId(), defaultManager);

        UUID hotelId = UUID.randomUUID();
        Hotel hotel = new Hotel(
                hotelId,
                defaultManager.getId(),
                "Bookit Inn",
                "Cozy place near the lake",
                "Lausanne",
                "Switzerland",
                "Rue de la Paix 10",
                new java.math.BigDecimal("120.00")
        );
        hotel.publish();
        hotels.put(hotelId, hotel);
        defaultManager.addHotel(hotel);

        defaultManager.defineRoomTypes(java.util.Arrays.asList("STANDARD", "DELUXE"));
        defaultManager.setRoomPrices("STANDARD", 120.0);
        defaultManager.setRoomPrices("DELUXE", 170.0);

        UUID stdRoomTypeId = UUID.randomUUID();
        UUID dlxRoomTypeId = UUID.randomUUID();

        UUID bookingId1 = UUID.randomUUID();
        Booking b1 = new Booking(bookingId1, hotelId, guestId1, stdRoomTypeId);
        bookings.put(bookingId1, b1);
        guest1.addBooking(b1);

        UUID pendingBookingId = UUID.randomUUID();
        Booking pendingBooking = new Booking(pendingBookingId, hotelId, guestId1, dlxRoomTypeId);
        pendingBooking.setStatus(ch.unil.bookit.domain.booking.BookingStatus.PENDING);
        bookings.put(pendingBookingId, pendingBooking);
        guest1.addBooking(pendingBooking);

        UUID bookingId2 = UUID.randomUUID();
        Booking b2 = new Booking(bookingId2, hotelId, guestId2, dlxRoomTypeId);
        bookings.put(bookingId2, b2);
        guest2.addBooking(b2);

        defaultManager.approveBooking(b1, guests);
        defaultManager.cancelBooking(b2, guests);
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
        // Assuming managers are stored in a Map<UUID, HotelManager>
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