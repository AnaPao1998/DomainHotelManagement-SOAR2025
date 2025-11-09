
package ch.unil.bookit.domain.services;

public class CurrencyConverter {
    public double convert(String fromCurrency, String toCurrency, double amount) {
        // Simple stub for now — in production use a real API call or library
        if (fromCurrency.equals(toCurrency)) return amount;

        // Simulate conversion rate
        double rate = switch (toCurrency) {
            case "EUR" -> 0.9;
            case "CHF" -> 0.95;
            case "GBP" -> 0.8;
            default -> 1.0;
        };
        return amount * rate;
    }
}