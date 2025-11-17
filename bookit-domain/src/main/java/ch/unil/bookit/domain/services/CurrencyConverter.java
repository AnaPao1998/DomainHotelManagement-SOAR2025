
package ch.unil.bookit.domain.services;

public class CurrencyConverter {
    public double convert(String fromCurrency, String toCurrency, double amount) {
        if (fromCurrency.equals(toCurrency)) return amount;

        double rate = switch (toCurrency) {
            case "EUR" -> 0.9;
            case "CHF" -> 0.95;
            case "GBP" -> 0.8;
            default -> 1.0;
        };
        return amount * rate;
    }
}