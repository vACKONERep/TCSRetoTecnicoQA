package com.demoblaze.automation.tasks;

import com.demoblaze.automation.models.PurchaseData;
import com.demoblaze.automation.ui.OrderModal;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * High-level business task: open place-order, fill form, submit purchase.
 * Composes smaller tasks (Open/Closed and Single Responsibility).
 */
public class CompletePurchase implements Task {

    private final PurchaseData purchaseData;

    public CompletePurchase(PurchaseData purchaseData) {
        this.purchaseData = purchaseData;
    }

    public static CompletePurchase with(PurchaseData purchaseData) {
        return instrumented(CompletePurchase.class, purchaseData);
    }

    @Override
    @Step("{0} completes the purchase")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                PlaceOrder.now(),                 // opens modal + waits until Name is interactable
                FillPurchaseForm.with(purchaseData), // Name → Country → City → Card → Month → Year
                WaitUntil.the(OrderModal.PURCHASE_BUTTON, isClickable())
                        .forNoMoreThan(10).seconds(),
                Click.on(OrderModal.PURCHASE_BUTTON),
                WaitUntil.the(OrderModal.CONFIRMATION_TITLE, isVisible())
                        .forNoMoreThan(15).seconds()
        );
    }
}
