package com.demoblaze.automation.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber lifecycle hooks.
 * <p>
 * Browser is closed ONLY in {@link After} — never mid-scenario / mid-step.
 */
public class Hooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hooks.class);

    @Before
    public void setTheStage(Scenario scenario) {
        OnStage.setTheStage(new OnlineCast());
        LOGGER.info("Starting scenario: '{}' — Screenplay stage ready", scenario.getName());
    }

    /**
     * Runs after every step of the scenario has finished (pass or fail).
     * Does not run between steps, so it cannot cause "invalid session id" mid-flow.
     */
    @After
    public void closeBrowserAndDrawCurtain(Scenario scenario) {
        LOGGER.info(
                "Finishing scenario: '{}' [{}] — closing browser",
                scenario.getName(),
                scenario.getStatus()
        );

        try {
            WebDriver driver = Serenity.getDriver();
            if (driver != null && isSessionAlive(driver)) {
                driver.quit();
                LOGGER.info("Browser closed successfully");
            } else {
                LOGGER.info("Browser session already closed — nothing to quit");
            }
        } catch (Throwable error) {
            LOGGER.warn("Browser quit skipped or failed: {}", error.getMessage());
        } finally {
            try {
                OnStage.drawTheCurtain();
            } catch (Throwable ignored) {
                // stage may already be empty
            }
            LOGGER.info("Screenplay stage cleared");
        }
    }

    private static boolean isSessionAlive(WebDriver driver) {
        try {
            driver.getWindowHandle();
            return true;
        } catch (WebDriverException ex) {
            return false;
        }
    }
}
