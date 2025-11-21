package ch.unil.bookit.domain;

import ch.unil.bookit.domain.booking.Booking;
import ch.unil.bookit.domain.booking.BookingStatus;
import ch.unil.bookit.domain.services.CurrencyConverter;
import ch.unil.bookit.domain.services.EmailService;

import java.util.*;

public class HotelManager extends User {

    private final List<Hotel> hotels = new ArrayList<>();
    private List<String> roomTypes = new ArrayList<>();
    private final Map<String, Double> roomPrices = new HashMap<>();

    private final EmailService emailService = new EmailService();
    private final CurrencyConverter currencyConverter = new CurrencyConverter();

    public HotelManager() {
        super();
    }

    public HotelManager(UUID uuid, String email, String password, String firstName, String lastName) {
        super(uuid, email, password, firstName, lastName);
    }

    public void addHotel(Hotel hotel) {
        if (hotel != null && !hotels.contains(hotel)) {
            hotel.setManagerId(this.getId());   // updated
            hotels.add(hotel);
        }
    }

    public List<Hotel> getHotels() {
        return Collections.unmodifiableList(hotels);
    }

    public void defineRoomTypes(List<String> types) {
        this.roomTypes = (types == null) ? new ArrayList<>() : new ArrayList<>(types);
    }

    public void setRoomPrices(String roomType, double price) {
        if (!roomTypes.contains(roomType)) {
            throw new IllegalArgumentException("Room type " + roomType + " does not exist.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price must be non-negative.");
        }
        roomPrices.put(roomType, price);
    }

    public double getConvertedPrice(String roomType, String targetCurrency) {
        double basePrice = roomPrices.getOrDefault(roomType, 0.0);
        return currencyConverter.convert("USD", targetCurrency, basePrice);
    }

    public void approveBooking(Booking booking, Map<UUID, Guest> guests) {

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only PENDING bookings can be approved.");
        }

        booking.markConfirmed();  // consistent domain method
        System.out.println("Booking has been confirmed.");

        Guest guest = booking.resolveGuest(guests);

        emailService.sendEmail(
                guest.getEmail(),
                "Booking Confirmed",
                "Your booking " + booking.getBookingId() + " has been approved! See you soon."
        );
    }

    public void cancelBooking(Booking booking, Map<UUID, Guest> guests) {

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalStateException("Only PENDING bookings can be cancelled.");
        }

        booking.markCancelled();  // consistent domain method
        System.out.println("Booking has been cancelled.");

        Guest guest = booking.resolveGuest(guests);

        emailService.sendEmail(
                guest.getEmail(),
                "Booking Cancelled",
                "Your booking has been cancelled. Please contact us for further details."
        );
    }
}
