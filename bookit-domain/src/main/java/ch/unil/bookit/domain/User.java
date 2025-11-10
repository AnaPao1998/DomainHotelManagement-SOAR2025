package ch.unil.bookit.domain;

import java.util.UUID;

public class User {

    private UUID uuid;
    /// changing this to UUID because it lets us use the unique ID package
    private String email;
    private String password;
    private String firstName;
    private String lastName;
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

    // get and set userids
    // always get first and set next
    public UUID getUUID() {
        return uuid;
    }

    public void setuuid(UUID uuid) {
        this.uuid = uuid;
    }

    // email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // first name
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // last name
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // set balance - not as basic as just get and set because you need to be able to
    // both withdraw and deposit so it's not always a value that is just called
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
