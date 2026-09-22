package com.demoblaze.automation.data;

/**
 * Shared constants. Product names and buyer details are not hardcoded here:
 * each Scenario Outline example loads them from data/purchases.csv and data/buyers.json.
 */
public final class TestData {

    public static final String BASE_URL = "https://www.demoblaze.com/";

    public static final String ACTOR_NAME = "Customer";

    public static final String THANK_YOU_MESSAGE = "Thank you for your purchase!";

    private TestData() {
        // constants holder
    }
}
