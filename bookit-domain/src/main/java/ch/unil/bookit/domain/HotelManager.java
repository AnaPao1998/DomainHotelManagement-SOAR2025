package ch.unil.bookit.domain;

import ch.unil.bookit.domain.services.CurrencyConverter;
import ch.unil.bookit.domain.services.EmailService;

import java.util.*;

public class HotelManager extends User {
    private List<Hotel> hotels;
    private List<String> roomTypes = new ArrayList<>();
    private Map<String, Double> roomPrices = new HashMap<>();

    private EmailService emailService = new EmailService();
    private CurrencyConverter currencyConverter = new CurrencyConverter();

    // main constructor
    public HotelManager(UUID uuid, String email, String password, String firstName, String lastName) {
        super(uuid, email, password, firstName, lastName);
        hotels = new ArrayList<>();
    }

    public void addHotel(Hotel hotel) {
        if (hotel != null && !hotels.contains(hotel)) {
            hotels.add(hotel);
        }
    }
    public List<Hotel> getHotels() {
        return Collections.unmodifiableList(hotels);
    }

    // define a list of room types
    public void defineRoomTypes(List<String> types) {
        this.roomTypes = types;
    }

    // define prices for each room type
    public void setRoomPrices(String roomType, double price) {
        if (!roomTypes.contains(roomType)) {
            throw new IllegalArgumentException("Room type " + roomType + " does not exist.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price must be non-negative.");
        }
        roomPrices.put(roomType, price);
    }

    // 🔹 simple FX conversion method
    public double getConvertedPrice(String roomType, String targetCurrency) {
        double basePrice = roomPrices.getOrDefault(roomType, 0.0);
        return currencyConverter.convert("USD", targetCurrency, basePrice); // assuming base is USD
    }
    // approve booking
    public void approveBooking(Booking booking,Map<UUID, Guest> guests) {
        if (booking.getStatus() == null) {
            throw new IllegalStateException("Booking status cannot be null.");
        }
        if (booking.getStatus() != Booking.bookingStatus.PENDING) {
            throw new IllegalStateException("Only PENDING bookings can be approved.");
        }
        booking.setStatus(Booking.bookingStatus.CONFIRMED);
        System.out.println("Booking has been confirmed.");
        // 🔹 Send confirmation email
        emailService.sendEmail(
                booking.getGuest(guests).getEmail(),
                "Booking Confirmed",
                "Your booking has been approved! See you soon."
        );
    }
    // cancel booking
    public void cancelBooking(Booking booking,Map<UUID, Guest> guests) {
        if (booking.getStatus() == null) {
            throw new IllegalStateException("Booking status cannot be null.");
        }
        if (booking.getStatus() != Booking.bookingStatus.PENDING) {
            throw new IllegalStateException("Only PENDING bookings can be cancelled.");
        }
        booking.setStatus(Booking.bookingStatus.CANCELLED);

        // 🔹 Send cancellation email
        System.out.println("Booking has been cancelled.");
        emailService.sendEmail(
                booking.getGuest(guests).getEmail(),
                "Booking Cancelled",
                "Your booking has been cancelled. Please contact us for further details."
        );
    }









}
