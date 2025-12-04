package ch.unil.bookit.domain;

import jakarta.persistence.*;
import java.util.UUID;


@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")

public class User {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false, length = 36)
    private UUID uuid;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name= "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private int balance;

    public User() {
        this(null, null, null, null, null);
    }

    public User(UUID uuid, String email, String password, String firstName, String lastName) {
        this.uuid = uuid;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = 0; /// testing
    }

    // Generate a UUID automatically if it's null when persisting
    @PrePersist
    private void ensureId() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public UUID getId() {
        return uuid;
    }

    public void setuuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void deposit(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance += amount;
    }

    public void withdraw(int amount) {
        if (amount <= 0) {
            // cant withdraw negatve
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (this.balance < amount) {
            // show how much more is needed
            int shortage = amount - this.balance;
            throw new IllegalStateException("Not enough money ): " + shortage + " more needed to BookIt!");
        }
        this.balance -= amount;
    }
}
