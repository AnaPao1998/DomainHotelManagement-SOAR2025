package ch.unil.bookit.domain;

import java.math.BigDecimal;
import java.util.*;
import java.util.Objects;
import java.util.UUID;

public class Room {
    private final UUID roomId;
    private final UUID hotelId;

    public enum RoomType {
        SINGLE,
        DOUBLE,
        DELUXE
    }

    private RoomType roomType; // Type of the room (Single, Double, Deluxe)
    private String description;
    private BigDecimal pricePerNight;
    private boolean available;

    private String photoUrl;

    public Room(UUID roomId, UUID hotelId, RoomType roomType, String description, BigDecimal pricePerNight, String photoUrl) {
        this.roomId = Objects.requireNonNull(roomId, "roomId");
        this.hotelId = Objects.requireNonNull(hotelId, "hotelId");
        this.roomType = Objects.requireNonNull(roomType, "roomType");
        this.description = (description == null) ? "" : description;
        this.pricePerNight = requirePositive(pricePerNight, "pricePerNight");
        this.photoUrl = (photoUrl == null) ? "" : photoUrl;
        this.available = true;
    }

    public UUID getRoomId() { return roomId; }
    public UUID getHotelId() { return hotelId; }
    public RoomType getRoomType() { return roomType; }
    public String getDescription() { return description; }
    public BigDecimal getPricePerNight() { return pricePerNight; }
    public boolean isAvailable() { return available; }
    public String getPhotoUrl() { return photoUrl; }

    public void setRoomType(RoomType roomType) {
        this.roomType = Objects.requireNonNull(roomType, "roomType");
    }
    public void setDescription(String description) {
        this.description = (description == null) ? "" : description;
    }
    public void setPricePerNight(BigDecimal pricePerNight) {
        this.pricePerNight = requirePositive(pricePerNight, "pricePerNight");
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = (photoUrl == null) ? "" : photoUrl;
    }

    private static BigDecimal requirePositive(BigDecimal v, String field) {
        if (v == null || v.signum() <= 0)
            throw new IllegalArgumentException(field + " must be > 0");
        return v;
    }

    @Override
    public String toString() {
        return "Room{" +
                "roomId=" + roomId +
                ", hotelId=" + hotelId +
                ", roomType=" + roomType +
                ", description='" + description + '\'' +
                ", pricePerNight=" + pricePerNight +
                ", available=" + available +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}