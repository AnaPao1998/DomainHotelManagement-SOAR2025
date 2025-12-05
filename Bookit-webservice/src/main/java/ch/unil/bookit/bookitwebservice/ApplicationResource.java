package ch.unil.bookit.bookitwebservice;

import ch.unil.bookit.domain.Guest;
import ch.unil.bookit.domain.Hotel;
import ch.unil.bookit.domain.HotelManager;
import ch.unil.bookit.domain.booking.Booking;
import ch.unil.bookit.domain.booking.BookingStatus;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@ApplicationScoped
public class ApplicationResource {

    private EntityManagerFactory emf;

    // IN-MEMORY STORAGE FOR HOTELS & BOOKINGS (Hybrid Approach)
    private Map<UUID, Hotel> hotels = new HashMap<>();
    private Map<UUID, Booking> bookings = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            // 1. Connect to Database for Users
            emf = Persistence.createEntityManagerFactory("bookitPU");

            // 2. Populate Data (Robust Seeding)
            populateApplicationState();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("CRITICAL: Failed to create EntityManagerFactory: " + e.getMessage());
        }
    }

    private EntityManager getEntityManagerSafe() {
        if (emf != null) {
            return emf.createEntityManager();
        }
        return null;
    }

    // ==========================================
    //      DATABASE OPERATIONS (GUEST)
    // ==========================================

    public Guest createGuest(Guest guest) {
        EntityManager em = getEntityManagerSafe();
        if (em != null) {
            try {
                em.getTransaction().begin();
                if (guest.getId() == null) guest.setuuid(UUID.randomUUID());
                em.persist(guest);
                em.getTransaction().commit();
            } catch (Exception e) {
                if(em.getTransaction().isActive()) em.getTransaction().rollback();
                e.printStackTrace();
            } finally {
                em.close();
            }
        }
        return guest;
    }

    public Guest getGuest(UUID id) {
        EntityManager em = getEntityManagerSafe();
        if (em != null) {
            try { return em.find(Guest.class, id); } finally { em.close(); }
        }
        return null;
    }

    // Returns Map<UUID, Guest> to satisfy GuestResource (.values())
    public Map<UUID, Guest> getAllGuests() {
        EntityManager em = getEntityManagerSafe();
        Map<UUID, Guest> map = new HashMap<>();
        if (em != null) {
            try {
                List<Guest> list = em.createQuery("SELECT g FROM Guest g", Guest.class).getResultList();
                for (Guest g : list) {
                    map.put(g.getId(), g);
                }
            } finally {
                em.close();
            }
        }
        return map;
    }

    public Guest updateGuest(UUID id, Guest updatedInfo) {
        EntityManager em = getEntityManagerSafe();
        if (em != null) {
            try {
                em.getTransaction().begin();
                Guest guest = em.find(Guest.class, id);
                if (guest != null) {
                    // only update fields if they are NOT null
                    if (updatedInfo.getFirstName() != null) guest.setFirstName(updatedInfo.getFirstName());
                    if (updatedInfo.getLastName() != null)  guest.setLastName(updatedInfo.getLastName());
                    if (updatedInfo.getEmail() != null)     guest.setEmail(updatedInfo.getEmail());
                    if (updatedInfo.getPassword() != null)  guest.setPassword(updatedInfo.getPassword());

                    // balance is an int (primitive), so we can't check for null
                    if (updatedInfo.getBalance() != 0) guest.setBalance(updatedInfo.getBalance());

                    em.merge(guest);
                    em.getTransaction().commit();
                }
                return guest;
            } catch (Exception e) {
                if(em.getTransaction().isActive()) em.getTransaction().rollback();
                e.printStackTrace();
            } finally {
                em.close();
            }
        }
        return null;
    }

    public boolean deleteGuest(UUID id) {
        EntityManager em = getEntityManagerSafe();
        if (em != null) {
            try {
                em.getTransaction().begin();
                Guest guest = em.find(Guest.class, id);
                if (guest != null) {
                    em.remove(guest);
                    em.getTransaction().commit();
                    return true;
                }
            } finally {
                em.close();
            }
        }
        return false;
    }

    // ==========================================
    //      DATABASE OPERATIONS (MANAGER)
    // ==========================================

    public HotelManager createManager(HotelManager manager) {
        EntityManager em = getEntityManagerSafe();
        if (em != null) {
            try {
                em.getTransaction().begin();
                if (manager.getId() == null) manager.setuuid(UUID.randomUUID());
                em.persist(manager);
                em.getTransaction().commit();
            } catch (Exception e) {
                if(em.getTransaction().isActive()) em.getTransaction().rollback();
                e.printStackTrace();
            } finally {
                em.close();
            }
        }
        return manager;
    }

    public HotelManager getManager(UUID id) {
        EntityManager em = getEntityManagerSafe();
        if (em != null) {
            try { return em.find(HotelManager.class, id); } finally { em.close(); }
        }
        return null;
    }

    // FIXED: Returns Map to satisfy ManagerResource
    public Map<UUID, HotelManager> getAllManagers() {
        EntityManager em = getEntityManagerSafe();
        Map<UUID, HotelManager> map = new HashMap<>();
        if (em != null) {
            try {
                List<HotelManager> list = em.createQuery("SELECT m FROM HotelManager m", HotelManager.class).getResultList();
                for (HotelManager m : list) {
                    map.put(m.getId(), m);
                }
            } finally {
                em.close();
            }
        }
        return map;
    }

    public HotelManager updateManager(UUID id, HotelManager updatedInfo) {
        EntityManager em = getEntityManagerSafe();
        if (em != null) {
            try {
                em.getTransaction().begin();
                HotelManager manager = em.find(HotelManager.class, id);
                if (manager != null) {
                    // FIXED: Only update fields if they are NOT null
                    if (updatedInfo.getFirstName() != null) manager.setFirstName(updatedInfo.getFirstName());
                    if (updatedInfo.getLastName() != null)  manager.setLastName(updatedInfo.getLastName());
                    if (updatedInfo.getEmail() != null)     manager.setEmail(updatedInfo.getEmail());
                    if (updatedInfo.getPassword() != null)  manager.setPassword(updatedInfo.getPassword());

                    em.merge(manager);
                    em.getTransaction().commit();
                }
                return manager;
            } catch (Exception e) {
                if(em.getTransaction().isActive()) em.getTransaction().rollback();
                e.printStackTrace();
            } finally {
                em.close();
            }
        }
        return null;
    }

    public boolean deleteManager(UUID id) {
        EntityManager em = getEntityManagerSafe();
        if (em != null) {
            try {
                em.getTransaction().begin();
                HotelManager manager = em.find(HotelManager.class, id);
                if (manager != null) {
                    em.remove(manager);
                    em.getTransaction().commit();
                    return true;
                }
            } finally {
                em.close();
            }
        }
        return false;
    }

    // --- Authentication (DB) ---
    public UUID authenticateGuest(String email, String password) {
        EntityManager em = getEntityManagerSafe();
        if (em != null) {
            try {
                TypedQuery<Guest> q = em.createQuery(
                        "SELECT g FROM Guest g WHERE g.email = :email AND g.password = :password",
                        Guest.class
                );
                q.setParameter("email", email);
                q.setParameter("password", password);

                Guest g = q.getSingleResult();
                return g.getId();   // UUID from the entity

            } catch (NoResultException ex) {
                return null; // Login failed (Not found in DB)
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                em.close();
            }
        }
        return null;
    }

    public UUID authenticateManager(String email, String password) {
        EntityManager em = getEntityManagerSafe();
        if (em != null) {
            try {
                TypedQuery<HotelManager> q = em.createQuery(
                        "SELECT m FROM HotelManager m WHERE m.email = :e AND m.password = :p", HotelManager.class);
                q.setParameter("e", email);
                q.setParameter("p", password);
                return q.getSingleResult().getId();
            } catch (Exception e) {
                return null;
            } finally {
                em.close();
            }
        }
        return null;
    }

    // --- Wallet (DB) ---

    public Guest depositToGuestWallet(UUID guestId, int amount) {
        EntityManager em = getEntityManagerSafe();
        if (em != null) {
            try {
                em.getTransaction().begin();
                Guest guest = em.find(Guest.class, guestId);
                if (guest != null) {
                    guest.deposit(amount);
                    em.getTransaction().commit();
                }
                return guest;
            } finally {
                em.close();
            }
        }
        return null;
    }

    public Guest withdrawFromGuestWallet(UUID guestId, int amount) {
        EntityManager em = getEntityManagerSafe();
        if (em != null) {
            try {
                em.getTransaction().begin();
                Guest guest = em.find(Guest.class, guestId);
                if (guest != null) {
                    guest.withdraw(amount);
                    em.getTransaction().commit();
                }
                return guest;
            } finally {
                em.close();
            }
        }
        return null;
    }

    // ==========================================
    //      HASHMAP OPERATIONS (HOTEL/BOOKING)
    // ==========================================

    public Hotel createHotel(Hotel hotel) {
        UUID managerId = hotel.getManagerId();

        if (managerId == null) throw new IllegalArgumentException("Manager ID required");

        HotelManager manager = getManager(managerId);
        if (manager == null) throw new IllegalArgumentException("Manager not found in DB");

        if (hotel.getHotelId() == null) {
            hotel.setHotelId(UUID.randomUUID());
        }

        manager.addHotel(hotel);
        hotels.put(hotel.getHotelId(), hotel);
        return hotel;
    }

    public Hotel getHotel(UUID id) { return hotels.get(id); }

    public Map<UUID, Hotel> getAllHotels() { return hotels; }

    public Hotel updateHotel(UUID id, Hotel updatedInfo) {
        if (hotels.containsKey(id)) {
            updatedInfo.setHotelId(id);
            if (updatedInfo.getManagerId() == null) {
                updatedInfo.setManagerId(hotels.get(id).getManagerId());
            }
            hotels.put(id, updatedInfo);
            return updatedInfo;
        }
        return null;
    }

    public boolean deleteHotel(UUID id) { return hotels.remove(id) != null; }

    // --- Bookings (HashMap) ---

    public Booking createBooking(UUID hotelId, UUID guestId, UUID roomTypeId) {
        Guest guest = getGuest(guestId); // From DB
        Hotel hotel = hotels.get(hotelId); // From Map

        if (guest == null || hotel == null) {
            throw new IllegalArgumentException("Invalid Guest ID or Hotel ID");
        }

        UUID bookingId = UUID.randomUUID();
        Booking booking = new Booking(bookingId, hotelId, guestId, roomTypeId);

        booking.setStatus(BookingStatus.PENDING);
        bookings.put(bookingId, booking);

        guest.addBooking(booking);
        return booking;
    }

    public Booking getBooking(UUID id) { return bookings.get(id); }

    public void saveBooking(Booking booking) { bookings.put(booking.getBookingId(), booking); }

    public List<Booking> getBookingsForGuest(UUID guestId) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings.values()) {
            if (b.getUserId().equals(guestId)) {
                result.add(b);
            }
        }
        return result;
    }

    public Map<UUID, Booking> getBookings() { return bookings; }

    public List<Booking> getPendingBookingsForManager(UUID managerId) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings.values()) {
            if (b.getStatus() == BookingStatus.PENDING) {
                Hotel h = hotels.get(b.getHotelId());
                if (h != null && h.getManagerId().equals(managerId)) {
                    result.add(b);
                }
            }
        }
        return result;
    }

    // ==========================================
    //      HYBRID POPULATION (The Logic Fix)
    // ==========================================
    private void populateApplicationState() {
        EntityManager em = getEntityManagerSafe();
        if (em == null) return;

        try {
            // 1. Ensure Manager Exists
            HotelManager manager = null;
            List<HotelManager> managers = em.createQuery(
                            "SELECT m FROM HotelManager m WHERE m.email = 'manager@bookit.com'", HotelManager.class)
                    .getResultList();

            if (managers.isEmpty()) {
                em.getTransaction().begin();
                manager = new HotelManager(UUID.randomUUID(), "manager@bookit.com", "pass123", "Marta", "Keller");
                em.persist(manager);
                em.getTransaction().commit();
            } else {
                manager = managers.get(0);
            }

            // 2. Ensure Guest Exists
            List<Guest> guests = em.createQuery(
                            "SELECT g FROM Guest g WHERE g.email = 'guest1@bookit.com'", Guest.class)
                    .getResultList();

            if (guests.isEmpty()) {
                em.getTransaction().begin();
                Guest guest1 = new Guest(UUID.randomUUID(), "guest1@bookit.com", "pass123", "Ana", "Montero");
                guest1.deposit(500);
                em.persist(guest1);
                em.getTransaction().commit();
            }

            // 3. Create In-Memory Hotels
            if (hotels.isEmpty()) {
                createHotel(new Hotel(UUID.randomUUID(), manager.getId(), "Bookit Inn", "Cozy place", "Lausanne", "Switzerland", "Rue de la Paix 10", new BigDecimal("120.00")));
                createHotel(new Hotel(UUID.randomUUID(), manager.getId(), "Alpine Retreat", "Mountain view", "Zermatt", "Switzerland", "Matterhornstrasse 5", new BigDecimal("230.00")));

                for(Hotel h : hotels.values()) {
                    if(h.getName().equals("Bookit Inn")) h.setImageUrl("hotel1.jpg");
                    if(h.getName().equals("Alpine Retreat")) h.setImageUrl("hotel2.jpg");
                    h.publish();
                }
            }

        } catch (Exception e) {
            if(em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}