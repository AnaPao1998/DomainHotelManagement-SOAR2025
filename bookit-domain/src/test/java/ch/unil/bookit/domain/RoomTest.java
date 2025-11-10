package ch.unil.bookit.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.UUID;

public class RoomTest {
    @Test
    public void testRoomCreation() {
        UUID roomId = UUID.randomUUID();
        UUID hotelId = UUID.randomUUID();
        Room room = new Room("Hotel de Test", "Ville", "Pays", "Adresse", roomId, hotelId, Room.RoomType.SINGLE, "Description", new BigDecimal("50.0"), "photo.jpg", "Description");

        assertEquals(roomId, room.getRoomId());
        assertEquals(Room.RoomType.SINGLE, room.getRoomType());
        assertEquals(new BigDecimal("50.00"), room.getPricePerNight());
    }
}
