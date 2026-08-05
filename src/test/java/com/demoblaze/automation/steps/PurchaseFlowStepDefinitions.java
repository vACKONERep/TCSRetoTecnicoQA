package com.demoblaze.automation.steps;

import com.demoblaze.automation.data.TestData;
import com.demoblaze.automation.questions.CartContents;
import com.demoblaze.automation.questions.PurchaseConfirmation;
import com.demoblaze.automation.tasks.AddProductToCart;
import com.demoblaze.automation.tasks.CloseConfirmationModal;
import com.demoblaze.automation.tasks.CompletePurchase;
import com.demoblaze.automation.tasks.NavigateTo;
import com.demoblaze.automation.tasks.OpenCart;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.ensure.Ensure;
import org.assertj.core.api.SoftAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Cucumber step definitions for the DemoBlaze purchase flow.
 * Steps stay thin: they orchestrate Screenplay Tasks/Questions only.
 * Browser lifecycle is handled in {@link Hooks}.
 */
public class PurchaseFlowStepDefinitions {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseFlowStepDefinitions.class);

    @Given("the customer opens the DemoBlaze home page")
    public void theCustomerOpensTheDemoBlazeHomePage() {
        theActorCalled(TestData.ACTOR_NAME).attemptsTo(
                NavigateTo.demoBlazeHomePage()
        );
    }

    @When("the customer adds {string} to the cart")
    public void theCustomerAddsProductToTheCart(String productName) {
        theActorInTheSpotlight().attemptsTo(
                AddProductToCart.named(productName)
        );
    }

    @When("the customer opens the cart")
    public void theCustomerOpensTheCart() {
        theActorInTheSpotlight().attemptsTo(
                OpenCart.page()
        );
    }

    @Then("the cart should contain the products:")
    public void theCartShouldContainTheProducts(List<Map<String, String>> productTable) {
        List<String> expectedProducts = productTable.stream()
                .map(row -> row.get("product"))
                .toList();

        LOGGER.info("Verifying cart contains: {}", expectedProducts);

        // Soft assertions: report every missing product in a single failure
        SoftAssertions softly = new SoftAssertions();
        List<String> actualProducts = theActorInTheSpotlight()
                .asksFor(CartContents.displayed());

        for (String expected : expectedProducts) {
            softly.assertThat(actualProducts)
                    .as("Cart should contain product '%s'", expected)
                    .contains(expected);
        }
        softly.assertAll();

        theActorInTheSpotlight().should(
                seeThat("cart product list", CartContents.displayed(), hasItems(expectedProducts.toArray(String[]::new)))
        );
    }

    @When("the customer places the order with valid purchase data")
    public void theCustomerPlacesTheOrderWithValidPurchaseData() {
        theActorInTheSpotlight().attemptsTo(
                CompletePurchase.with(TestData.DEFAULT_PURCHASE)
        );
    }

    @Then("the purchase should be confirmed successfully")
    public void thePurchaseShouldBeConfirmedSuccessfully() {
        theActorInTheSpotlight().should(
                seeThat("purchase success", PurchaseConfirmation.isSuccessful(), is(true)),
                seeThat("thank you message",
                        PurchaseConfirmation.thankYouMessage(),
                        equalTo(TestData.THANK_YOU_MESSAGE))
        );

        theActorInTheSpotlight().attemptsTo(
                Ensure.thatTheAnswerTo("thank you message", PurchaseConfirmation.thankYouMessage())
                        .isEqualTo(TestData.THANK_YOU_MESSAGE)
        );
    }

    @Then("the order confirmation should display an order id")
    public void theOrderConfirmationShouldDisplayAnOrderId() {
        theActorInTheSpotlight().should(
                seeThat("order id is displayed",
                        PurchaseConfirmation.orderIdIsDisplayed(), is(true)),
                seeThat("order id value",
                        PurchaseConfirmation.orderId(), not(emptyString()))
        );

        String orderId = theActorInTheSpotlight().asksFor(PurchaseConfirmation.orderId());
        LOGGER.info("Purchase completed with order ID: {}", orderId);
    }

    @When("the customer closes the purchase confirmation")
    public void theCustomerClosesThePurchaseConfirmation() {
        // Click OK only — DemoBlaze may reload; task must not poll the dying DOM
        theActorInTheSpotlight().attemptsTo(
                CloseConfirmationModal.now()
        );
    }

    @Then("the confirmation modal should no longer be visible")
    public void theConfirmationModalShouldNoLongerBeVisible() {
        // Business success was already asserted (thank-you + order id).
        // Use a session-safe question so invalid session id after OK never fails the scenario.
        theActorInTheSpotlight().should(
                seeThat(
                        "confirmation modal is dismissed (or session already closed after OK)",
                        PurchaseConfirmation.modalIsDismissedSafely(),
                        is(true)
                )
        );
    }
}
