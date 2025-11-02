package ch.unil.bookit.domain;

import java.util.*;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.EnumSet;
import java.util.UUID;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Hotel {
    // identity
    private final UUID hotelId;
    private String name;
    private String description;
    private String city;
    private String country;
    private String address;

    // pricing
    private BigDecimal basePrice;

    // rating
    private OptionalDouble rating;

    // Collections
    private final Map<String, RoomType> roomTypes;
    private final List<URI> photos;
    private final EnumSet<Amenity> amenities;

    private boolean published;

    public Hotel(UUID hotelId,
                 String name,
                 String description,
                 String city,
                 String country,
                 String address,
                 BigDecimal basePrice) {
        this.hotelId   = Objects.requireNonNull(hotelId, "hotelId");
        this.name      = requireNonBlank(name, "name");
        this.description = (description == null) ? "" : description;
        this.city      = requireNonBlank(city, "city");
        this.country   = requireNonBlank(country, "country");
        this.address   = requireNonBlank(address, "address");
        this.basePrice = requirePositive(basePrice, "basePrice");

        this.rating    = OptionalDouble.empty();
        this.roomTypes = new LinkedHashMap<>();
        this.photos    = new ArrayList<>();
        this.amenities = EnumSet.noneOf(Amenity.class);
        this.published = false;
    }
    public void publish()   { this.published = true; }
    public void unpublish() { this.published = false; }

    public void updateContent(String name, String description, String city, String country, String address) {
        this.name = requireNonBlank(name, "name");
        this.description = (description == null) ? "" : description;
        this.city = requireNonBlank(city, "city");
        this.country = requireNonBlank(country, "country");
        this.address = requireNonBlank(address, "address");
    }

    public void setBasePrice(BigDecimal newBasePrice) {
        this.basePrice = requirePositive(newBasePrice, "basePrice");
    }

    public void setRating(OptionalDouble rating) {
        validateRating(Objects.requireNonNull(rating, "rating"));
        this.rating = rating;
    }

    public void upsertRoomType(String key, RoomType roomType) {
        roomTypes.put(normalizeKey(key), Objects.requireNonNull(roomType));
    }

    public void removeRoomType(String key) {
        roomTypes.remove(normalizeKey(key));
    }

    public void addPhoto(URI uri) { photos.add(Objects.requireNonNull(uri, "photo uri")); }

    public boolean removePhoto(URI uri) { return photos.remove(uri); }

    public void addAmenity(Amenity amenity) { amenities.add(Objects.requireNonNull(amenity, "amenity")); }

    public void removeAmenity(Amenity amenity) { amenities.remove(Objects.requireNonNull(amenity, "amenity")); }

    // calculator without taxes or fees
    public BigDecimal quote(String roomTypeKey, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null || !checkIn.isBefore(checkOut))
            throw new IllegalArgumentException("Invalid dates: checkIn must be before checkOut");

        RoomType rt = roomTypes.get(normalizeKey(roomTypeKey));
        if (rt == null) throw new NoSuchElementException("Unknown room type: " + roomTypeKey);

        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal nightly = rt.pricePerNight().orElse(basePrice);
        return nightly.multiply(BigDecimal.valueOf(nights));
    }

    // Getters
    public UUID getHotelId() { return hotelId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getAddress() { return address; }          // NEW
    public BigDecimal getBasePrice() { return basePrice; }
    public OptionalDouble getRating() { return rating; }
    public Map<String, RoomType> getRoomTypes() { return Collections.unmodifiableMap(roomTypes); }
    public List<URI> getPhotos() { return Collections.unmodifiableList(photos); }
    public EnumSet<Amenity> getAmenities() { return amenities.clone(); }
    public boolean isPublished() { return published; }

    // internal types
    public enum Amenity { WIFI, PARKING, KITCHEN, BREAKFAST, POOL, GYM, AIR_CONDITIONING }

    public static final class RoomType {
        private final UUID id;
        private final String name;
        private final int capacity;
        private final Optional<BigDecimal> pricePerNight;
        private final List<URI> photos;

        private RoomType(UUID id, String name, int capacity,
                         Optional<BigDecimal> pricePerNight, List<URI> photos) {
            this.id = Objects.requireNonNull(id, "id");
            this.name = requireNonBlank(name, "name");
            if (capacity <= 0) throw new IllegalArgumentException("capacity must be > 0");
            this.capacity = capacity;
            this.pricePerNight = Objects.requireNonNull(pricePerNight, "pricePerNight");
            pricePerNight.ifPresent(p -> requirePositive(p, "pricePerNight"));
            this.photos = List.copyOf(photos == null ? List.of() : photos);
        }

        public static RoomType of(UUID id, String name, int capacity) {
            return new RoomType(id, name, capacity, Optional.empty(), List.of());
        }

        public static RoomType of(UUID id, String name, int capacity,
                                  BigDecimal pricePerNight, List<URI> photos) {
            return new RoomType(id, name, capacity, Optional.ofNullable(pricePerNight), photos);
        }

        public UUID id() { return id; }
        public String name() { return name; }
        public int capacity() { return capacity; }
        public Optional<BigDecimal> pricePerNight() { return pricePerNight; }
        public List<URI> photos() { return photos; }
    }

    // Helpers
    private static String normalizeKey(String key) {
        String k = requireNonBlank(key, "roomType key").trim().toLowerCase(Locale.ROOT);
        return k.replace(' ', '-');
    }

    private static String requireNonBlank(String s, String field) {
        if (s == null || s.trim().isEmpty())
            throw new IllegalArgumentException(field + " is required");
        return s;
    }

    private static BigDecimal requirePositive(BigDecimal value, String field) {
        if (value == null || value.signum() <= 0)
            throw new IllegalArgumentException(field + " must be > 0");
        return value;
    }

    private static void validateRating(OptionalDouble rating) {
        if (rating.isPresent()) {
            double r = rating.getAsDouble();
            if (r < 0.0 || r > 5.0) throw new IllegalArgumentException("rating must be in [0,5]");
        }
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Hotel)) return false;
        return hotelId.equals(((Hotel) o).hotelId);
    }

    @Override public int hashCode() { return Objects.hash(hotelId); }

    @Override public String toString() {
        return "Hotel{" +
                "hotelId=" + hotelId +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", address='" + address + '\'' +
                ", published=" + published +
                '}';
    }

}
