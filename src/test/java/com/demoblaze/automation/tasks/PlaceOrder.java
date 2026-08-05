package com.demoblaze.automation.tasks;

import com.demoblaze.automation.ui.CartPage;
import com.demoblaze.automation.ui.OrderModal;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isEnabled;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Clicks "Place Order" and waits until the Bootstrap modal is fully shown
 * and the Name field is ready for input (no Thread.sleep).
 */
public class PlaceOrder implements Task {

    public static PlaceOrder now() {
        return instrumented(PlaceOrder.class);
    }

    @Override
    @Step("{0} clicks Place Order")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                WaitUntil.the(CartPage.PLACE_ORDER_BUTTON, isClickable())
                        .forNoMoreThan(10).seconds(),
                Click.on(CartPage.PLACE_ORDER_BUTTON),
                // Wait for Bootstrap fade-in to complete before typing
                WaitUntil.the(OrderModal.MODAL_SHOWN, isVisible())
                        .forNoMoreThan(10).seconds(),
                WaitUntil.the(OrderModal.MODAL_TITLE, isVisible())
                        .forNoMoreThan(10).seconds(),
                WaitUntil.the(OrderModal.NAME_FIELD, isVisible())
                        .forNoMoreThan(10).seconds(),
                WaitUntil.the(OrderModal.NAME_FIELD, isEnabled())
                        .forNoMoreThan(10).seconds(),
                WaitUntil.the(OrderModal.NAME_FIELD, isClickable())
                        .forNoMoreThan(10).seconds()
        );
    }
}
