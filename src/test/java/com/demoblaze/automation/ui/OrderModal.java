package com.demoblaze.automation.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

/**
 * UI map for the Place Order form and the SweetAlert purchase confirmation.
 * <p>
 * Form fields are scoped under {@code #orderModal} so we never type into a
 * hidden/detached input while the Bootstrap modal is animating open.
 */
public final class OrderModal {

    public static final Target MODAL = Target.the("Place Order modal")
            .located(By.id("orderModal"));

    /** Modal only after Bootstrap has finished showing it (class "show"). */
    public static final Target MODAL_SHOWN = Target.the("fully visible Place Order modal")
            .located(By.cssSelector("#orderModal.show"));

    public static final Target MODAL_TITLE = Target.the("Place Order modal title")
            .located(By.id("orderModalLabel"));

    public static final Target NAME_FIELD = Target.the("Name field")
            .located(By.cssSelector("#orderModal input#name"));

    public static final Target COUNTRY_FIELD = Target.the("Country field")
            .located(By.cssSelector("#orderModal input#country"));

    public static final Target CITY_FIELD = Target.the("City field")
            .located(By.cssSelector("#orderModal input#city"));

    public static final Target CREDIT_CARD_FIELD = Target.the("Credit card field")
            .located(By.cssSelector("#orderModal input#card"));

    public static final Target MONTH_FIELD = Target.the("Month field")
            .located(By.cssSelector("#orderModal input#month"));

    public static final Target YEAR_FIELD = Target.the("Year field")
            .located(By.cssSelector("#orderModal input#year"));

    public static final Target PURCHASE_BUTTON = Target.the("Purchase button")
            .located(By.cssSelector("#orderModal button.btn.btn-primary"));

    public static final Target CLOSE_BUTTON = Target.the("Close place-order modal button")
            .located(By.cssSelector("#orderModal button.btn.btn-secondary"));

    // SweetAlert confirmation after successful purchase
    public static final Target CONFIRMATION_POPUP = Target.the("purchase confirmation popup")
            .located(By.cssSelector("div.sweet-alert.showSweetAlert.visible"));

    public static final Target CONFIRMATION_TITLE = Target.the("confirmation thank-you title")
            .located(By.cssSelector("div.sweet-alert.showSweetAlert.visible h2"));

    public static final Target CONFIRMATION_TEXT = Target.the("confirmation order details")
            .located(By.cssSelector("div.sweet-alert.showSweetAlert.visible p.lead.text-muted"));

    public static final Target CONFIRMATION_OK_BUTTON = Target.the("OK button on confirmation")
            .located(By.cssSelector("div.sweet-alert.showSweetAlert.visible button.confirm"));

    private OrderModal() {
        // UI map — not instantiable
    }
}
