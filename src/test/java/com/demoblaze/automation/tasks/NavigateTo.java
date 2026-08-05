package com.demoblaze.automation.tasks;

import com.demoblaze.automation.data.TestData;
import com.demoblaze.automation.ui.HomePage;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.waits.WaitUntil;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;

/**
 * Opens the DemoBlaze application and waits until the catalogue is ready.
 */
public class NavigateTo implements Task {

    private final String url;

    public NavigateTo(String url) {
        this.url = url;
    }

    public static NavigateTo demoBlazeHomePage() {
        return instrumented(NavigateTo.class, TestData.BASE_URL);
    }

    public static NavigateTo url(String url) {
        return instrumented(NavigateTo.class, url);
    }

    @Override
    @Step("{0} navigates to #url")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Open.url(url),
                WaitUntil.the(HomePage.PRODUCT_CARDS, isVisible())
                        .forNoMoreThan(15).seconds()
        );
    }
}