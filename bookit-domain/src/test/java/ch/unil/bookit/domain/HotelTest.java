package ch.unil.bookit.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.UUID;

public class HotelTest {
    @Test
    public void testHotelCreation() {
        UUID hotelId = UUID.randomUUID();
        Hotel hotel = new Hotel(hotelId, "Hotel de Test", "Description", "Ville", "Pays", "Adresse");

        assertEquals(hotelId, hotel.getHotelId());
        assertEquals("Hotel de Test", hotel.getName());
    }

    @Test
    public void testPublish() {
        Hotel hotel = new Hotel(UUID.randomUUID(), "Test", "", "City", "Country", "Addr");
        hotel.publish();
        assertTrue(hotel.isPublished());
    }
}
