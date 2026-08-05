package com.demoblaze.automation.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

/**
 * UI map for the shopping cart page.
 */
public final class CartPage {

    public static final Target CART_TABLE = Target.the("cart products table")
            .located(By.id("tbodyid"));

    public static final Target CART_PRODUCT_ROWS = Target.the("rows of products in the cart")
            .located(By.cssSelector("#tbodyid tr"));

    public static final Target CART_PRODUCT_BY_NAME = Target.the("cart product '{0}'")
            .locatedBy("//tbody[@id='tbodyid']//td[normalize-space()='{0}']");

    public static final Target PLACE_ORDER_BUTTON = Target.the("Place Order button")
            .located(By.cssSelector("button.btn.btn-success"));

    public static final Target TOTAL_PRICE = Target.the("cart total price")
            .located(By.id("totalp"));

    private CartPage() {
        // UI map — not instantiable
    }
}
