package ch.unil.bookit.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HotelTest {

    @Test
    public void testHotelCreation() {
        UUID hotelId = UUID.randomUUID();
        UUID managerId = UUID.randomUUID();

        Hotel hotel = new Hotel(
                hotelId,
                managerId,
                "Hotel de Test",
                "",
                "City",
                "Country",
                "Addr",
                BigDecimal.ONE
        );

        assertEquals(hotelId, hotel.getHotelId());
        assertEquals("Hotel de Test", hotel.getName());
        assertEquals(managerId, hotel.getManagerId());
    }

    @Test
    public void testPublish() {
        UUID managerId = UUID.randomUUID();
        Hotel hotel = new Hotel(
                UUID.randomUUID(),
                managerId,
                "Test",
                "",
                "City",
                "Country",
                "Addr",
                BigDecimal.ONE
        );
        hotel.publish();
        assertTrue(hotel.isPublished());
    }
}

