package com.demoblaze.automation.tasks;

import com.demoblaze.automation.interactions.AcceptBrowserAlert;
import com.demoblaze.automation.ui.HomePage;
import com.demoblaze.automation.ui.ProductPage;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Composite task: open a product from the catalogue, add it to the cart,
 * accept the confirmation alert, and return to the home page for the next action.
 */
public class AddProductToCart implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddProductToCart.class);

    private final String productName;

    public AddProductToCart(String productName) {
        this.productName = productName;
    }

    public static AddProductToCart named(String productName) {
        return instrumented(AddProductToCart.class, productName);
    }

    @Override
    @Step("{0} adds the product '#productName' to the cart")
    public <T extends Actor> void performAs(T actor) {
        LOGGER.info("Adding product '{}' to the cart", productName);

        actor.attemptsTo(
                WaitUntil.the(HomePage.PRODUCT_LINK.of(productName), isClickable())
                        .forNoMoreThan(15).seconds(),
                Click.on(HomePage.PRODUCT_LINK.of(productName)),
                WaitUntil.the(ProductPage.PRODUCT_TITLE, isVisible())
                        .forNoMoreThan(10).seconds(),
                WaitUntil.the(ProductPage.ADD_TO_CART_BUTTON, isClickable())
                        .forNoMoreThan(10).seconds(),
                Click.on(ProductPage.ADD_TO_CART_BUTTON),
                AcceptBrowserAlert.andContinue(),
                // Return to catalogue so another product can be selected
                Click.on(HomePage.HOME_NAV),
                WaitUntil.the(HomePage.PRODUCT_CARDS, isVisible())
                        .forNoMoreThan(15).seconds()
        );
    }
}
