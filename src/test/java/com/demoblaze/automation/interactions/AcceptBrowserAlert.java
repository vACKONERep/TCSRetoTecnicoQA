package com.demoblaze.automation.interactions;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Waits for a native JavaScript alert (used by DemoBlaze after Add to cart)
 * and accepts it. Avoids Thread.sleep by using an explicit WebDriverWait.
 */
public class AcceptBrowserAlert implements Interaction {

    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptBrowserAlert.class);
    private static final Duration ALERT_TIMEOUT = Duration.ofSeconds(10);

    public static AcceptBrowserAlert andContinue() {
        return instrumented(AcceptBrowserAlert.class);
    }

    @Override
    @Step("{0} accepts the browser alert")
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        WebDriverWait wait = new WebDriverWait(driver, ALERT_TIMEOUT);
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String message = alert.getText();
        LOGGER.info("Accepting browser alert with message: '{}'", message);
        alert.accept();
    }
}
