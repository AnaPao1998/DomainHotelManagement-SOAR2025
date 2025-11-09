package ch.unil.bookit.domain;

import java.util.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Hotel {
    // identity
    private UUID hotelId;
    private String name;
    private String description;
    private String city;
    private String country;
    private String address;

    public Hotel() {
        this.published = false;
    }

    // pricing
    private BigDecimal nightPrice; // price per night at the hotel

    private boolean published;

    // List of all rooms in the hotel
    private final List<Room> rooms = new ArrayList<>();

    // List for hotel photos (URL or file paths)
    private final List<String> photos = new ArrayList<>();

    // List of hotel amenities (e.g. wifi, parking, etc.)
    private final List<String> amenities = new ArrayList<>();


    public Hotel(UUID hotelId,
                 String name,
                 String description,
                 String city,
                 String country,
                 String address,
                 BigDecimal nightPrice) {
        this.hotelId = Objects.requireNonNull(hotelId, "hotelId");
        this.name = requireNonBlank(name, "name");
        this.description = (description == null) ? "" : description;  // the description is optional, if it's not fill in it will not cause any errors
        this.city = requireNonBlank(city, "city");
        this.country = requireNonBlank(country, "country");
        this.address = requireNonBlank(address, "address");
        this.nightPrice = requirePositive(nightPrice, "nightPrice");
        this.published = false;
    }

    // Commands
    public void publish() {
        this.published = true;
    }

    public void unpublish() {
        this.published = false;
    }

    // Updates
    public void updateContent(String name, String city, String country, String address) {
        this.name = requireNonBlank(name, "name");
        this.description = (description == null) ? "" : description;
        this.city = requireNonBlank(city, "city");
        this.country = requireNonBlank(country, "country");
        this.address = requireNonBlank(address, "address");
    }

    public void setNightPrice(BigDecimal nightPrice) {
        this.nightPrice = requirePositive(nightPrice, "nightPrice");
    }

    // Add a Room to the hotel's list of rooms
    public void addRoom(Room room) {
        this.rooms.add(Objects.requireNonNull(room, "room"));
    }

    // Get the list of rooms (read-only)
    public List<Room> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    // Add a photo URL/path to the hotel
    public void addPhoto(String photoUrl) {
        this.photos.add(requireNonBlank(photoUrl, "photoUrl"));
    }

    // Get the list of photos (read-only)
    public List<String> getPhotos() {
        return Collections.unmodifiableList(photos);
    }

    // Add an amenity to the hotel
    public void addAmenity(String amenity) {
        this.amenities.add(requireNonBlank(amenity, "amenity"));
    }

    // Get the list of amenities (read-only)
    public List<String> getAmenities() {
        return Collections.unmodifiableList(amenities);
    }

    // Getters
    public UUID getHotelId() { return hotelId; }
    public void setHotelId(UUID hotelId) { this.hotelId = hotelId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getAddress() { return address; }
    public BigDecimal getNightPrice() { return nightPrice; }
    public boolean isPublished() { return published; }

    // Helpers
    private static String requireNonBlank(String s, String field) {
        if (s == null || s.trim().isEmpty())
            throw new IllegalArgumentException(field + " is required");
        return s;
    }

    private static BigDecimal requirePositive(BigDecimal v, String field) {
        if (v == null || v.signum() <= 0)
            throw new IllegalArgumentException(field + " must be > 0");
        return v;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "hotelId=" + hotelId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", address='" + address + '\'' +
                ", nightPrice=" + nightPrice +
                ", published=" + published +
                ", rooms=" + rooms.size() +
                ", photos=" + photos.size() +
                ", amenities=" + amenities +
                '}';
    }
}
