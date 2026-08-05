package com.demoblaze.automation.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

/**
 * UI map for the DemoBlaze home page (product catalogue and global navigation).
 */
public final class HomePage {

    public static final Target LOGO = Target.the("DemoBlaze logo")
            .located(By.id("nava"));

    public static final Target HOME_NAV = Target.the("Home navigation link")
            .located(By.cssSelector("a.nav-link[href='index.html']"));

    public static final Target CART_NAV = Target.the("Cart navigation link")
            .located(By.id("cartur"));

    public static final Target PRODUCT_LINK = Target.the("product link named '{0}'")
            .locatedBy("//a[@class='hrefch' and normalize-space()='{0}']");

    public static final Target PRODUCT_CARDS = Target.the("product cards on the catalogue")
            .located(By.cssSelector("#tbodyid .card"));

    private HomePage() {
        // UI map — not instantiable
    }
}
