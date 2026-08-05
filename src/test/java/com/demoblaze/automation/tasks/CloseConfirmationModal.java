package com.demoblaze.automation.tasks;

import com.demoblaze.automation.ui.OrderModal;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Closes the SweetAlert purchase confirmation by clicking OK.
 * <p>
 * DemoBlaze often reloads the page right after OK. Waiting for the modal to
 * disappear (or polling the DOM) can surface {@code invalid session id}.
 * This task therefore only dismisses the dialog and stops interacting.
 */
public class CloseConfirmationModal implements Task {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloseConfirmationModal.class);

    public static CloseConfirmationModal now() {
        return instrumented(CloseConfirmationModal.class);
    }

    @Override
    @Step("{0} closes the purchase confirmation modal")
    public <T extends Actor> void performAs(T actor) {
        try {
            if (!isDriverSessionAlive(actor)) {
                LOGGER.warn("WebDriver session already closed — skipping confirmation dismiss");
                return;
            }

            // Prefer a short visibility wait; OK is usually already on screen after purchase asserts
            actor.attemptsTo(
                    WaitUntil.the(OrderModal.CONFIRMATION_OK_BUTTON, isVisible())
                            .forNoMoreThan(5).seconds(),
                    WaitUntil.the(OrderModal.CONFIRMATION_OK_BUTTON, isClickable())
                            .forNoMoreThan(5).seconds()
            );

            // JS click is more reliable on SweetAlert than a native click during unload
            clickOkSafely(actor);

            LOGGER.info("Purchase confirmation OK clicked — no further modal polling (DemoBlaze may reload)");
            // Intentionally do NOT wait for CONFIRMATION_POPUP to become invisible.
        } catch (WebDriverException ex) {
            if (isInvalidSession(ex)) {
                LOGGER.warn("Session became invalid while dismissing confirmation (treated as closed): {}",
                        ex.getMessage());
                return;
            }
            // Modal may already be gone after a fast page reload
            if (isStaleOrMissing(ex)) {
                LOGGER.warn("Confirmation modal already gone: {}", ex.getMessage());
                return;
            }
            throw ex;
        }
    }

    private static <T extends Actor> void clickOkSafely(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        try {
            actor.attemptsTo(Click.on(OrderModal.CONFIRMATION_OK_BUTTON));
        } catch (WebDriverException clickError) {
            if (isInvalidSession(clickError)) {
                throw clickError;
            }
            LOGGER.info("Native click on OK failed ({}), trying JavaScript click", clickError.getMessage());
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                    "var btn = document.querySelector('div.sweet-alert.showSweetAlert.visible button.confirm');"
                            + "if (btn) { btn.click(); }"
            );
        }
    }

    private static <T extends Actor> boolean isDriverSessionAlive(T actor) {
        try {
            WebDriver driver = BrowseTheWeb.as(actor).getDriver();
            if (driver == null) {
                return false;
            }
            driver.getWindowHandle();
            return true;
        } catch (WebDriverException ex) {
            return false;
        }
    }

    private static boolean isInvalidSession(Throwable error) {
        String message = String.valueOf(error.getMessage()).toLowerCase();
        Throwable cause = error.getCause();
        String causeMessage = cause != null ? String.valueOf(cause.getMessage()).toLowerCase() : "";
        return message.contains("invalid session id")
                || causeMessage.contains("invalid session id")
                || message.contains("session deleted")
                || message.contains("no such window");
    }

    private static boolean isStaleOrMissing(Throwable error) {
        String message = String.valueOf(error.getMessage()).toLowerCase();
        return message.contains("stale element")
                || message.contains("no such element")
                || message.contains("element not found")
                || message.contains("not clickable")
                || message.contains("waiting for");
    }
}
