package ch.unil.bookit.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
public class GuestTest {

    private Guest testGuest;
    private Booking testBooking;
    private UUID testGuestId;


    @BeforeEach
    void setUp()
    {
        testGuestId = UUID.randomUUID();
        testGuest = new Guest(testGuestId, "test@test.com","pass123", "Test", "Guest");
        //create booking
        testBooking = new Booking(UUID.randomUUID(),UUID.randomUUID(), testGuestId, UUID.randomUUID());
    }

    @Test
    void testGuestInheritsUserBalance(){
        assertEquals(0, testGuest.getBalance());
        testGuest.deposit(120);
        assertEquals(120, testGuest.getBalance());

        testGuest.withdraw(20);
        assertEquals(100, testGuest.getBalance());
    }

    @Test
    void testWithdrawInsufficent(){
        testGuest.deposit(50);
        assertEquals(50, testGuest.getBalance());

        assertThrows(IllegalStateException.class, () -> {
            testGuest.withdraw(100);
        });

        assertEquals(50, testGuest.getBalance());
    }
    @Test
    void testGuestAddAndGetBooking(){
        testGuest.addBooking(testBooking);

        assertEquals(1, testGuest.getBooking().size());
        assertEquals(testBooking, testGuest.getBooking(testBooking.getBookingId()));
    }

    @Test
    void testAddExistingBookingFails(){
        testGuest.addBooking(testBooking);
        assertThrows(IllegalArgumentException.class, () -> {
                    testGuest.addBooking(testBooking);
        });

        assertEquals(1, testGuest.getBooking().size());
    }
}
