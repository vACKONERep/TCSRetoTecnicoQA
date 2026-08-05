package com.demoblaze.automation.tasks;

import com.demoblaze.automation.ui.CartPage;
import com.demoblaze.automation.ui.HomePage;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Navigates to the shopping cart and waits until cart content is available.
 */
public class OpenCart implements Task {

    public static OpenCart page() {
        return instrumented(OpenCart.class);
    }

    @Override
    @Step("{0} opens the shopping cart")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                WaitUntil.the(HomePage.CART_NAV, isClickable())
                        .forNoMoreThan(10).seconds(),
                Click.on(HomePage.CART_NAV),
                WaitUntil.the(CartPage.PLACE_ORDER_BUTTON, isVisible())
                        .forNoMoreThan(10).seconds(),
                WaitUntil.the(CartPage.CART_PRODUCT_ROWS, isVisible())
                        .forNoMoreThan(10).seconds()
        );
    }
}