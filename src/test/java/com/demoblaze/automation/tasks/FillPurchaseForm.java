package com.demoblaze.automation.tasks;

import com.demoblaze.automation.models.PurchaseData;
import com.demoblaze.automation.ui.OrderModal;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Clear;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.questions.Value;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isEnabled;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Fills the Place Order form with realistic purchase data.
 * <p>
 * Order: Name → Country → City → Credit Card → Month → Year.
 * Each field is focused, cleared, then typed via Screenplay {@link Enter}.
 * Name is verified and re-typed once if the first attempt did not stick
 * (common when the Bootstrap modal is still animating).
 */
public class FillPurchaseForm implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(FillPurchaseForm.class);

    private final PurchaseData purchaseData;

    public FillPurchaseForm(PurchaseData purchaseData) {
        this.purchaseData = purchaseData;
    }

    public static FillPurchaseForm with(PurchaseData purchaseData) {
        return instrumented(FillPurchaseForm.class, purchaseData);
    }

    @Override
    @Step("{0} fills the purchase form with valid data")
    public <T extends Actor> void performAs(T actor) {
        LOGGER.info("Filling purchase form: {}", purchaseData);

        // Extra safety: modal must be fully shown before any SendKeys
        actor.attemptsTo(
                WaitUntil.the(OrderModal.MODAL_SHOWN, isVisible())
                        .forNoMoreThan(10).seconds(),
                WaitUntil.the(OrderModal.NAME_FIELD, isClickable())
                        .forNoMoreThan(10).seconds()
        );

        // Required order — Name first (DemoBlaze validates Name + Card for purchase)
        typeIntoField(actor, OrderModal.NAME_FIELD, purchaseData.getName(), true);
        typeIntoField(actor, OrderModal.COUNTRY_FIELD, purchaseData.getCountry(), false);
        typeIntoField(actor, OrderModal.CITY_FIELD, purchaseData.getCity(), false);
        typeIntoField(actor, OrderModal.CREDIT_CARD_FIELD, purchaseData.getCreditCard(), true);
        typeIntoField(actor, OrderModal.MONTH_FIELD, purchaseData.getMonth(), false);
        typeIntoField(actor, OrderModal.YEAR_FIELD, purchaseData.getYear(), false);

        LOGGER.info("Purchase form filled — Name='{}'", readValue(actor, OrderModal.NAME_FIELD));
    }

    /**
     * Click → Clear → Enter.theValue(...).into(...) with waits.
     * When {@code verify} is true, re-attempt once if the value did not stick.
     */
    private static <T extends Actor> void typeIntoField(
            T actor,
            Target field,
            String value,
            boolean verify
    ) {
        actor.attemptsTo(
                WaitUntil.the(field, isVisible()).forNoMoreThan(10).seconds(),
                WaitUntil.the(field, isEnabled()).forNoMoreThan(10).seconds(),
                WaitUntil.the(field, isClickable()).forNoMoreThan(10).seconds(),
                Click.on(field),
                Clear.field(field),
                Enter.theValue(value).into(field)
        );

        if (!verify) {
            return;
        }

        String actual = readValue(actor, field);
        if (value.equals(actual)) {
            LOGGER.info("Field '{}' accepted value '{}'", field.getName(), value);
            return;
        }

        LOGGER.warn(
                "Field '{}' expected '{}' but was '{}' — retrying clear + type",
                field.getName(),
                value,
                actual
        );

        actor.attemptsTo(
                Click.on(field),
                Clear.field(field),
                Enter.theValue(value).into(field)
        );

        String afterRetry = readValue(actor, field);
        LOGGER.info("Field '{}' after retry: '{}'", field.getName(), afterRetry);
    }

    private static String readValue(Actor actor, Target field) {
        try {
            String value = Value.of(field).answeredBy(actor);
            return value == null ? "" : value.trim();
        } catch (RuntimeException ex) {
            LOGGER.warn("Could not read value of '{}': {}", field.getName(), ex.getMessage());
            return "";
        }
    }
}
