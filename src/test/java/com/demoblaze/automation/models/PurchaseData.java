package com.demoblaze.automation.models;

import java.util.Objects;

/**
 * Immutable value object representing the purchase form data.
 * Keeps personal/payment details out of step definitions (Single Responsibility).
 */
public final class PurchaseData {

    private final String name;
    private final String country;
    private final String city;
    private final String creditCard;
    private final String month;
    private final String year;

    public PurchaseData(String name, String country, String city,
                        String creditCard, String month, String year) {
        this.name = Objects.requireNonNull(name, "name is required");
        this.country = Objects.requireNonNull(country, "country is required");
        this.city = Objects.requireNonNull(city, "city is required");
        this.creditCard = Objects.requireNonNull(creditCard, "creditCard is required");
        this.month = Objects.requireNonNull(month, "month is required");
        this.year = Objects.requireNonNull(year, "year is required");
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getCreditCard() {
        return creditCard;
    }

    public String getMonth() {
        return month;
    }

    public String getYear() {
        return year;
    }

    @Override
    public String toString() {
        return "PurchaseData{name='" + name + "', country='" + country
                + "', city='" + city + "', card=****" + lastFour(creditCard)
                + ", month='" + month + "', year='" + year + "'}";
    }

    private static String lastFour(String card) {
        if (card == null || card.length() < 4) {
            return "????";
        }
        return card.substring(card.length() - 4);
    }
}