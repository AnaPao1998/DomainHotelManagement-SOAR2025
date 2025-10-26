package ch.unil.bookit.domain;

// Money
package com.bookit.domain.shared;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public final class Money {
    private final BigDecimal amount;
    private final Currency currency;

    public Money(BigDecimal amount, Currency currency) {
        if (amount == null || currency == null) throw new IllegalArgumentException("Null money");
        if (amount.scale() > 2 || amount.signum() < 0) throw new IllegalArgumentException("Invalid amount");
        this.amount = amount;
        this.currency = currency;
    }
    public BigDecimal amount() { return amount; }
    public Currency currency() { return currency; }
    public Money add(Money other) { requireSameCurrency(other); return new Money(amount.add(other.amount), currency); }
    public Money multiply(int n) { return new Money(amount.multiply(BigDecimal.valueOf(n)), currency); }
    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) throw new IllegalArgumentException("Currency mismatch");
    }
}

// DateRange
import java.time.LocalDate;

public record DateRange(LocalDate checkIn, LocalDate checkOut) {
    public DateRange {
        if (checkIn == null || checkOut == null) throw new IllegalArgumentException("Dates required");
        if (!checkIn.isBefore(checkOut)) throw new IllegalArgumentException("checkIn < checkOut required");
    }
    public int nights() { return (int) (checkOut.toEpochDay() - checkIn.toEpochDay()); }
}

// BookingStatus
public enum BookingStatus { PENDING, CONFIRMED, CANCELLED, COMPLETED }

// PaymentStatus
package com.bookit.domain.shared;
public enum PaymentStatus { AUTHORIZED, CAPTURED, CANCELLED, FAILED }
