package ch.unil.bookit.domain;

import java.math.BigDecimal;
import java.util.*;

public class Room extends Hotel {
    public Room(UUID hotelId,
                String name,
                String description,
                String city,
                String country,
                String address,
                BigDecimal nightPrice) {
        super(hotelId, name, description, city, country, address, nightPrice); //calls the parent User constructor
    }
}
