package ch.unil.bookit.domain;

import java.util.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Hotel {
    // identity
    private final UUID hotelId;
    private String name;
    private String description;
    private String city;
    private String country;
    private String address;

    // pricing
    private BigDecimal nightPrice; // price per night

    private boolean published;

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

    // Getters
    public UUID getHotelId() { return hotelId; }
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
                '}';
    }
}
