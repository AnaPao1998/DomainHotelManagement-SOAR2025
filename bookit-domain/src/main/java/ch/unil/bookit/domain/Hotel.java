package ch.unil.bookit.domain;

import java.math.BigDecimal;
import java.util.*;
import java.util.Objects;

public class Hotel {
    // identity
    private UUID hotelId;
    private UUID managerId;
    private String name;
    private String description;
    private String city;
    private String country;
    private String address;

    // pricing
    private BigDecimal nightPrice;

    private boolean published;

    private final List<Room> rooms = new ArrayList<>();

    private final List<String> photos = new ArrayList<>();

    private final List<String> amenities = new ArrayList<>();

    public Hotel() {
    }

    public Hotel(UUID hotelId,
                 UUID managerId,
                 String name,
                 String description,
                 String city,
                 String country,
                 String address,
                 BigDecimal nightPrice) {
        this.hotelId = Objects.requireNonNull(hotelId, "hotelId");
        this.managerId = Objects.requireNonNull(managerId, "managerId");
        this.name = requireNonBlank(name, "name");
        this.description = (description == null) ? "" : description;
        this.city = requireNonBlank(city, "city");
        this.country = requireNonBlank(country, "country");
        this.address = requireNonBlank(address, "address");
        this.nightPrice = requirePositive(nightPrice, "nightPrice");
        this.published = false;
    }

    public UUID getManagerId() { return managerId; }
    public void setManagerId(UUID managerId) { this.managerId = managerId; }

    public void publish() { this.published = true; }
    public void unpublish() { this.published = false; }

    public void updateContent(String name, String city, String country, String address) {
        this.name = requireNonBlank(name, "name");
        this.city = requireNonBlank(city, "city");
        this.country = requireNonBlank(country, "country");
        this.address = requireNonBlank(address, "address");
    }

    public void setNightPrice(BigDecimal nightPrice) {
        this.nightPrice = requirePositive(nightPrice, "nightPrice");
    }

    public void addRoom(Room room) {
        this.rooms.add(Objects.requireNonNull(room, "room"));
    }

    public List<Room> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    public void addPhoto(String photoUrl) {
        this.photos.add(requireNonBlank(photoUrl, "photoUrl"));
    }

    public List<String> getPhotos() {
        return Collections.unmodifiableList(photos);
    }

    public void addAmenity(String amenity) {
        this.amenities.add(requireNonBlank(amenity, "amenity"));
    }

    public List<String> getAmenities() {
        return Collections.unmodifiableList(amenities);
    }

    public UUID getHotelId() { return hotelId; }
    public void setHotelId(UUID hotelId) { this.hotelId = hotelId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getAddress() { return address; }
    public BigDecimal getNightPrice() { return nightPrice; }
    public boolean isPublished() { return published; }

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
