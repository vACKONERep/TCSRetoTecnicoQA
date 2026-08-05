package com.demoblaze.automation.data;

import com.demoblaze.automation.models.PurchaseData;

/**
 * Centralised realistic test data constants.
 * Prefer this over hard-coding values inside step definitions or tasks.
 */
public final class TestData {

    public static final String BASE_URL = "https://www.demoblaze.com/";

    public static final String PRODUCT_SAMSUNG_GALAXY_S6 = "Samsung galaxy s6";
    public static final String PRODUCT_NOKIA_LUMIA_1520 = "Nokia lumia 1520";

    public static final String ACTOR_NAME = "Customer";

    public static final String THANK_YOU_MESSAGE = "Thank you for your purchase!";

    /** Default valid purchase form payload used by the happy-path scenario. */
    public static final PurchaseData DEFAULT_PURCHASE = new PurchaseData(
            "Juan Perez",
            "Ecuador",
            "Quito",
            "4111111111111111",
            "12",
            "2028"
    );

    private TestData() {
        // constants holder
    }
}
