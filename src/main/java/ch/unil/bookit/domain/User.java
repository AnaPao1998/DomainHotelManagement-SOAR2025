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
        this.balance = 0;
    }

    public void mergeWith(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null");
        }
        if (user.uuid == null) {
            this.uuid = user.uuid;
        }
        if (user.email != null) {
            this.email = user.email;
        }
        if (user.password != null) {
            this.password = user.password;
        }
        if (user.firstName != null) {
            this.firstName = user.firstName;
        }
        if (user.lastName != null) {
            this.lastName = user.lastName;
        }
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
        this.balance += amount;
    }

    public void withdraw(int amount) {
        if (this.balance < amount) {
            throw new IllegalStateException("Not enough money ):" + (amount + this.balance) + "are needed to BookIt!");
        }
        this.balance -= amount;
    }
}
