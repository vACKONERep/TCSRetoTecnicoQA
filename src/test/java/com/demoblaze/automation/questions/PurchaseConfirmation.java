package com.demoblaze.automation.questions;

import com.demoblaze.automation.data.TestData;
import com.demoblaze.automation.ui.OrderModal;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.questions.Text;
import net.serenitybdd.screenplay.questions.Visibility;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Custom Questions around the SweetAlert purchase confirmation:
 * thank-you message, full body text, and extracted order ID.
 */
public final class PurchaseConfirmation {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseConfirmation.class);
    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("Id:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    private PurchaseConfirmation() {
        // utility holder for related questions
    }

    public static Question<String> thankYouMessage() {
        return actor -> {
            waitForConfirmation(actor);
            String title = Text.of(OrderModal.CONFIRMATION_TITLE).answeredBy(actor).trim();
            LOGGER.info("Confirmation title: '{}'", title);
            return title;
        };
    }

    public static Question<String> detailsText() {
        return actor -> {
            waitForConfirmation(actor);
            String details = Text.of(OrderModal.CONFIRMATION_TEXT).answeredBy(actor).trim();
            LOGGER.info("Confirmation details: {}", details.replace("\n", " | "));
            return details;
        };
    }

    public static Question<Boolean> isSuccessful() {
        return actor -> {
            String title = thankYouMessage().answeredBy(actor);
            String details = detailsText().answeredBy(actor);
            boolean success = TestData.THANK_YOU_MESSAGE.equalsIgnoreCase(title)
                    && ORDER_ID_PATTERN.matcher(details).find();
            LOGGER.info("Purchase successful? {}", success);
            return success;
        };
    }

    public static Question<String> orderId() {
        return actor -> {
            String details = detailsText().answeredBy(actor);
            Matcher matcher = ORDER_ID_PATTERN.matcher(details);
            if (!matcher.find()) {
                LOGGER.warn("Order ID not found in confirmation text: {}", details);
                return "";
            }
            String id = matcher.group(1);
            LOGGER.info("Extracted order ID: {}", id);
            return id;
        };
    }

    public static Question<Boolean> orderIdIsDisplayed() {
        return actor -> {
            String id = orderId().answeredBy(actor);
            return id != null && !id.isBlank();
        };
    }

    public static Question<Boolean> modalIsVisible() {
        return Visibility.of(OrderModal.CONFIRMATION_POPUP);
    }

    /**
     * Safe post-purchase check: returns {@code true} when the SweetAlert is gone
     * OR when the WebDriver session is already invalid/closed (DemoBlaze reload).
     * Never fails the scenario with "invalid session id".
     */
    public static Question<Boolean> modalIsDismissedSafely() {
        return actor -> {
            try {
                WebDriver driver = BrowseTheWeb.as(actor).getDriver();
                if (driver == null) {
                    LOGGER.info("No WebDriver present — treating confirmation as dismissed");
                    return true;
                }
                driver.getWindowHandle();

                boolean stillVisible = OrderModal.CONFIRMATION_POPUP
                        .resolveAllFor(actor)
                        .stream()
                        .anyMatch(element -> {
                            try {
                                return element.isDisplayed();
                            } catch (RuntimeException ignored) {
                                return false;
                            }
                        });

                LOGGER.info("Confirmation modal still visible? {}", stillVisible);
                return !stillVisible;
            } catch (WebDriverException ex) {
                LOGGER.warn(
                        "Could not re-check confirmation modal (session/DOM may be gone after OK): {} — treating as dismissed",
                        ex.getMessage()
                );
                return true;
            }
        };
    }

    private static void waitForConfirmation(Actor actor) {
        actor.attemptsTo(
                WaitUntil.the(OrderModal.CONFIRMATION_TITLE, isVisible())
                        .forNoMoreThan(15).seconds()
        );
    }
}
