package ch.unil.bookit.bookitwebservice;

import ch.unil.bookit.domain.Guest;
import ch.unil.bookit.domain.Hotel;
import ch.unil.bookit.domain.HotelManager;
import ch.unil.bookit.domain.booking.Booking;
import ch.unil.bookit.domain.booking.BookingStatus;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.util.*;

@ApplicationScoped
public class ApplicationResource {
    private Map<UUID, Guest> guests;
    private Map<UUID, Hotel> hotels;
    private Map<UUID, Booking>  bookings;
    private Map<UUID, HotelManager>  managers;

    @PersistenceContext(unitName = "BookItPU")
    private EntityManager em;   // 🔹 JPA handle

    @PostConstruct
    public void init() {
        guests = new HashMap<>();
        hotels = new HashMap<>();
        bookings = new HashMap<>();
        managers = new HashMap<>();
        populateApplicationState();
    }

    /// //////////////////////
    /// //////GUEST /////////
    /// /////////////////////
    @Transactional
    public Guest createGuest(Guest guest){
        guest.setuuid(UUID.randomUUID());

        // in-memory: keep old behaviour
        guests.put(guest.getUUID(), guest);

        // NEW: persist to DB (JOINED hierarchy → Users + Guests)
        em.persist(guest);

        return guest;
    }


    public Map<UUID, Guest> getAllGuests() {
        // 1) start with what we already have in memory
        Map<UUID, Guest> all = new HashMap<>(guests);

        // 2) load all guests from the DB
        List<Guest> dbGuests = em.createQuery("SELECT g FROM Guest g", Guest.class)
                .getResultList();

        // 3) add DB guests that aren't in the map yet
        for (Guest g : dbGuests) {
            all.putIfAbsent(g.getId(), g);
        }

        // 4) refresh internal cache so everything stays consistent
        guests.clear();
        guests.putAll(all);

        // 5) return an unmodifiable view (optional but nice)
        return Collections.unmodifiableMap(guests);
    }


    public Guest getGuest(UUID id) {
        if (id == null) {
            return null;
        }

        // 1) Try in-memory cache
        Guest g = guests.get(id);
        if (g != null) {
            return g;
        }

        // 2) Fallback to DB
        g = em.find(Guest.class, id);
        if (g != null) {
            guests.put(id, g);   // cache for later
        }

        return g;
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


    /// //////////////////////
    /// ///// MANAGER  ///////
    /// /////////////////////
    @Transactional
    public HotelManager createManager(HotelManager manager){
        manager.setuuid(UUID.randomUUID());

        // in-memory
        managers.put(manager.getUUID(), manager);

        // NEW: persist to DB
        em.persist(manager);

        return manager;
    }


    public Map<UUID, HotelManager> getAllManagers() {

        // 1) Load everything from DB
        List<HotelManager> dbManagers =
                em.createQuery("SELECT m FROM HotelManager m", HotelManager.class)
                        .getResultList();

        // 2) Merge DB managers into in-memory cache
        for (HotelManager m : dbManagers) {
            managers.putIfAbsent(m.getId(), m);
        }

        // 3) Return the merged map
        return Collections.unmodifiableMap(managers);
    }


    public HotelManager getManager(UUID managerId) {
        if (managerId == null) {
            return null;
        }

        // 1) Try in-memory map (seed + just-created managers)
        HotelManager manager = managers.get(managerId);
        if (manager != null) {
            return manager;
        }

        // 2) Fallback to DB
        manager = em.find(HotelManager.class, managerId);
        if (manager != null) {
            // optional: put into cache for later
            managers.put(managerId, manager);
        }

        return manager;
    }

    public HotelManager updateManager(UUID id, HotelManager updatedManager) {
        if (managers.containsKey(id)) {
            updatedManager.setuuid(id);
            managers.put(id, updatedManager);
            return updatedManager;
        }
        return null;
    }

    public boolean deleteManager(UUID managerId) {
        return getAllManagers().remove(managerId) != null;
    }


    /// //////////////////////
    /// ///// HOTEL /////////
    /// /////////////////////
    @Transactional
    public Hotel createHotel(Hotel hotel) {

        if (hotel.getManagerId() == null) {
            throw new IllegalArgumentException("Manager ID is required");
        }

        UUID managerId = hotel.getManagerId();

        // --- get manager entity for JPA relation ---
        // from DB
        HotelManager managerEntity = em.find(HotelManager.class, managerId);
        if (managerEntity == null) {
            throw new IllegalArgumentException("Manager not found in DB: " + managerId);
        }

        // from in-memory map (for your existing logic)
        HotelManager managerInMemory = managers.get(managerId);
        if (managerInMemory == null) {
            // fall back to the managed entity if it’s not in the map
            managerInMemory = managerEntity;
            managers.put(managerId, managerInMemory);
        }

        if (hotel.getHotelId() == null) {
            hotel.setHotelId(UUID.randomUUID());
        }

        hotel.publish();

        // set JPA relation (also keeps managerId in sync)
        hotel.setManager(managerEntity);

        // persist to DB
        em.persist(hotel);

        // keep old in-memory behaviour
        hotels.put(hotel.getHotelId(), hotel);
        managerInMemory.addHotel(hotel);

        return hotel;
    }


    // ApplicationResource.java
    public Map<UUID, Hotel> getAllHotels() {
        // 1) start with anything we already have in memory
        Map<UUID, Hotel> all = new HashMap<>(hotels);

        // 2) load all hotels from the DB
        var dbHotels = em.createQuery("SELECT h FROM Hotel h", Hotel.class)
                .getResultList();

        // 3) add DB hotels that aren't in the map yet
        for (Hotel h : dbHotels) {
            all.putIfAbsent(h.getHotelId(), h);
        }

        // 4) refresh the internal cache so everything stays consistent
        hotels.clear();
        hotels.putAll(all);

        return all;
    }


    public Hotel getHotel(UUID id) {
        if (id == null) {
            return null;
        }

        // 1) Try in-memory cache
        Hotel h = hotels.get(id);
        if (h != null) {
            return h;
        }

        // 2) Fallback to DB
        h = em.find(Hotel.class, id);
        if (h != null) {
            hotels.put(id, h);   // cache for later
        }

        return h;
    }

    public List<Hotel> getHotelsForManager(UUID managerId) {
        if (managerId == null) {
            return Collections.emptyList();
        }

        // 1) DB hotels for this manager
        List<Hotel> dbHotels = em.createQuery(
                        "SELECT h FROM Hotel h WHERE h.manager.uuid = :mid",
                        Hotel.class)
                .setParameter("mid", managerId)
                .getResultList();

        Map<UUID, Hotel> merged = new LinkedHashMap<>();
        for (Hotel h : dbHotels) {
            merged.put(h.getHotelId(), h);
        }

        // 2) Fallback: in-memory hotels map
        for (Hotel h : hotels.values()) {
            UUID mid = h.getManagerId();
            if (mid == null && h.getManager() != null) {
                mid = h.getManager().getId();
            }

            if (managerId.equals(mid)) {
                merged.putIfAbsent(h.getHotelId(), h);
            }
        }

        return new ArrayList<>(merged.values());
    }


    @Transactional
    public Hotel updateHotel(UUID id, Hotel updatedHotel) {
        if (id == null || updatedHotel == null) {
            return null;
        }

        // Load the managed entity from the DB
        Hotel managed = em.find(Hotel.class, id);
        if (managed == null) {
            return null;
        }

        // Copy over editable fields
        managed.setName(updatedHotel.getName());
        managed.setDescription(updatedHotel.getDescription());
        managed.setCity(updatedHotel.getCity());
        managed.setCountry(updatedHotel.getCountry());
        managed.setAddress(updatedHotel.getAddress());
        managed.setNightPrice(updatedHotel.getNightPrice());
        managed.setImageUrl(updatedHotel.getImageUrl());
        // DO NOT touch `published` here → it stays whatever it was

        // If you handle amenities/photos/rooms via API, copy them here too

        // Persist changes
        em.merge(managed);

        // Keep cache in sync
        hotels.put(id, managed);

        return managed;
    }


    public boolean deleteHotel(UUID id) {
        return hotels.remove(id) != null;
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
                manager.getId(),                // fills the transient managerId
                name,
                description,
                city,
                country,
                address,
                new java.math.BigDecimal(price)
        );

        hotel.publish();
        hotel.setImageUrl(imageUrl);

        // 🔹 in-memory only – do NOT call createHotel()
        hotels.put(hotelId, hotel);
        manager.addHotel(hotel);

        return hotel;
    }


    /// //////////////////////
    /// ///// BOOKING /////////
    /// /////////////////////
    public Booking getBooking(UUID bookingId) {
        if (bookingId == null) {
            return null;
        }

        // 1) in-memory cache
        Booking b = bookings.get(bookingId);
        if (b != null) {
            return b;
        }

        // 2) DB lookup
        b = em.find(Booking.class, bookingId);
        if (b != null) {
            bookings.put(b.getBookingId(), b);  // cache for later
        }

        return b;
    }

    public Map<UUID, Booking> getBookings() {
        // 1) start with anything we already have in memory
        Map<UUID, Booking> all = new HashMap<>(bookings);

        // 2) load all bookings from the DB
        List<Booking> dbBookings = em.createQuery(
                        "SELECT b FROM Booking b", Booking.class)
                .getResultList();

        // 3) add DB bookings that aren't in the map yet
        for (Booking b : dbBookings) {
            all.putIfAbsent(b.getBookingId(), b);
        }

        // 4) refresh the internal cache so everything stays consistent
        bookings.clear();
        bookings.putAll(all);

        // 5) return an unmodifiable view
        return Collections.unmodifiableMap(bookings);
    }

    @Transactional
    public void saveBooking(Booking booking) {
        if (booking == null) {
            return;
        }

        booking.setUpdatedAt(Instant.now());

        // persist changes to DB
        em.merge(booking);

        // keep in-memory cache in sync
        bookings.put(booking.getBookingId(), booking);
    }


    @Transactional
    public Booking createBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }

        // Generate id + timestamps if missing
        if (booking.getBookingId() == null) {
            booking.setBookingId(UUID.randomUUID());
        }
        if (booking.getCreatedAt() == null) {
            booking.setCreatedAt(Instant.now());
        }
        booking.setUpdatedAt(Instant.now());

        // Link to Guest (from cache or DB) so domain stays consistent
        Guest g = guests.get(booking.getUserId());
        if (g == null && booking.getUserId() != null) {
            g = em.find(Guest.class, booking.getUserId());
            if (g != null) {
                guests.put(g.getId(), g);   // cache it
            }
        }
        if (g != null) {
            g.addBooking(booking);       // updates Guest’s transient map
            booking.setGuest(g);         // if your Booking entity has this
        }

        // Persist + cache
        em.persist(booking);
        bookings.put(booking.getBookingId(), booking);

        return booking;
    }

//    @Transactional
//    public Booking createBooking(UUID hotelId, UUID guestId, UUID roomTypeId) {
//        Booking booking = new Booking(
//                UUID.randomUUID(),
//                hotelId,
//                guestId,
//                roomTypeId
//        );
//        return createBooking(booking);  // delegate to the main method
//    }

    public List<Booking> getBookingsForGuest(UUID guestId) {
        if (guestId == null) {
            return Collections.emptyList();
        }

        // Load from DB
        List<Booking> dbBookings = em.createQuery(
                        "SELECT b FROM Booking b WHERE b.userId = :uid",
                        Booking.class)
                .setParameter("uid", guestId)
                .getResultList();

        // Refresh cache
        for (Booking b : dbBookings) {
            bookings.put(b.getBookingId(), b);
        }

        return dbBookings;
    }

    public List<Booking> getPendingBookingsForManager(UUID managerId) {
        if (managerId == null) {
            return Collections.emptyList();
        }

        // 1) DB bookings for this manager
        List<Booking> dbResult = em.createQuery(
                        "SELECT b FROM Booking b " +
                                "WHERE b.status = :status " +
                                "  AND b.hotelId IN (" +
                                "      SELECT h.hotelId FROM Hotel h " +
                                "      WHERE h.manager.uuid = :mid" +
                                "  )",
                        Booking.class)
                .setParameter("mid", managerId)
                .setParameter("status", BookingStatus.PENDING)
                .getResultList();

        // 2) Merge into a map to avoid duplicates
        Map<UUID, Booking> merged = new LinkedHashMap<>();
        for (Booking b : dbResult) {
            merged.put(b.getBookingId(), b);
        }

        // 3) Fallback: also look at in-memory bookings map
        for (Booking b : bookings.values()) {
            if (b.getStatus() != BookingStatus.PENDING) {
                continue;
            }

            // find the hotel + its manager
            Hotel h = hotels.get(b.getHotelId());
            if (h == null) {
                continue;
            }

            UUID mid = h.getManagerId();
            if (mid == null && h.getManager() != null) {
                mid = h.getManager().getId();
            }

            if (managerId.equals(mid)) {
                merged.putIfAbsent(b.getBookingId(), b);
            }
        }

        return new ArrayList<>(merged.values());
    }


    @Transactional
    public boolean deleteBooking(UUID bookingId) {
        if (bookingId == null) {
            return false;
        }

        // Remove from DB
        Booking managed = em.find(Booking.class, bookingId);
        if (managed != null) {
            em.remove(managed);
        } else {
            // nothing in DB, maybe only in cache
            if (!bookings.containsKey(bookingId)) {
                return false;
            }
        }

        // Remove from in-memory cache
        bookings.remove(bookingId);
        return true;
    }

    /// //////////////////////////////
    /// ///// AUTHENTICATION /////////
    /// //////////////////////////////
    public UUID authenticateGuest(String email, String password) {
        // 1) Check in-memory seed data / runtime cache
        for (Guest g : guests.values()) {
            if (g.getEmail().equals(email) && g.getPassword().equals(password)) {
                return g.getId();
            }
        }

        // 2) Check database
        var result = em.createQuery(
                        "SELECT g FROM Guest g WHERE g.email = :email AND g.password = :pw",
                        Guest.class)
                .setParameter("email", email)
                .setParameter("pw", password)
                .getResultList();

        if (!result.isEmpty()) {
            return result.get(0).getId();
        }
        return null;
    }

    public UUID authenticateManager(String email, String password) {
        // 1) Check in-memory seed data / runtime cache
        for (HotelManager m : managers.values()) {
            if (m.getEmail().equals(email) && m.getPassword().equals(password)) {
                return m.getId();
            }
        }

        // 2) Check database
        var result = em.createQuery(
                        "SELECT m FROM HotelManager m WHERE m.email = :email AND m.password = :pw",
                        HotelManager.class)
                .setParameter("email", email)
                .setParameter("pw", password)
                .getResultList();

        if (!result.isEmpty()) {
            return result.get(0).getId();
        }
        return null;
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




}
