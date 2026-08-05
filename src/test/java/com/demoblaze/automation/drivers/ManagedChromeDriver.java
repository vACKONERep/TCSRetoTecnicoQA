package com.demoblaze.automation.drivers;

import io.github.bonigarcia.wdm.WebDriverManager;
import net.thucydides.core.webdriver.DriverSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Chrome driver source powered by WebDriverManager.
 * <p>
 * Serenity uses this when {@code webdriver.driver=provided} and the provided type
 * points to this class. Centralising browser setup keeps capabilities consistent
 * and satisfies the WebDriverManager requirement without scattered setup code.
 */
public class ManagedChromeDriver implements DriverSource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedChromeDriver.class);

    @Override
    public WebDriver newDriver() {
        LOGGER.info("Setting up ChromeDriver via WebDriverManager");
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--start-maximized",
                "--disable-notifications",
                "--disable-popup-blocking",
                "--remote-allow-origins=*",
                "--disable-search-engine-choice-screen"
        );
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        if (isHeadless()) {
            LOGGER.info("Headless mode enabled");
            options.addArguments("--headless=new", "--window-size=1920,1080", "--disable-gpu");
        }

        return new ChromeDriver(options);
    }

    @Override
    public boolean takesScreenshots() {
        return true;
    }

    private static boolean isHeadless() {
        String headless = System.getProperty("headless.mode",
                System.getenv().getOrDefault("HEADLESS_MODE", "false"));
        return Boolean.parseBoolean(headless);
    }
}
