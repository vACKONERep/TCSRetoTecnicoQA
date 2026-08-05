package com.demoblaze.automation.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

/**
 * UI map for the product detail page.
 */
public final class ProductPage {

    public static final Target PRODUCT_TITLE = Target.the("product title on detail page")
            .located(By.cssSelector("h2.name"));

    public static final Target ADD_TO_CART_BUTTON = Target.the("Add to cart button")
            .located(By.cssSelector("a.btn.btn-success"));

    private ProductPage() {
        // UI map — not instantiable
    }
}
