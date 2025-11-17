package ch.unil.bookit.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testGetAndSetUUID() {
        // create new user for test
        User user = new User();

        // create uuid
        UUID newId = UUID.randomUUID();

        // setter
        user.setuuid(newId);

        // getter and verify it's the same
        assertEquals(newId, user.getUUID());
    }

    @Test
    void testGetAndSetEmail() {
        User user = new User();
        String testEmail = "test@test.com";

        user.setEmail(testEmail);
        assertEquals(testEmail, user.getEmail());
    }


    @Test
    void testGetAndSetPassword() {
        User user = new User();
        String testPassword = "testPassword";

        user.setPassword(testPassword);
        assertEquals(testPassword, user.getPassword());
    }

    @Test
    void testGetAndSetName() {
        User user = new User();
        String testName = "testName";

        user.setFirstName(testName);
        assertEquals(testName, user.getFirstName());
    }

    @Test
    void testGetAndSetLastName() {
        User user = new User();
        String testLastName = "testLastName";

        user.setLastName(testLastName);
        assertEquals(testLastName, user.getLastName());
    }

    @Test
    void testGetAndSetBalance() {
        User user = new User();

        user.setBalance(100);

        // make sure balance is actually 100
        assertEquals(100, user.getBalance());
    }

    @Test
    void testDepositIncreasesBalance() {
        User user = new User();
        user.setBalance(50);
        // should allow an add of a positive amount
        user.deposit(25);
        assertEquals(75, user.getBalance());
    }

    @Test
    void testDepositRejectsNegativeAmount() {
        User user = new User();
        // ensures that it rejects if a negative number is given
        assertThrows(IllegalArgumentException.class, () -> user.deposit(-10));
    }

    @Test
    void testWithdrawDecreasesBalance() {
        User user = new User();
        user.setBalance(100);

        user.withdraw(40);

        assertEquals(60, user.getBalance());
    }

    @Test
    void testWithdrawRejectsNegativeAmount() {
        User user = new User();
        user.setBalance(100);
        assertThrows(IllegalArgumentException.class, () -> user.withdraw(-50));
    }
}