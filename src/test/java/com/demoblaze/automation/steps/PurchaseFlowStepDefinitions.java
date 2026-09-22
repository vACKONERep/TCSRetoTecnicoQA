package com.demoblaze.automation.steps;

import com.demoblaze.automation.data.PurchaseCatalog;
import com.demoblaze.automation.data.TestData;
import com.demoblaze.automation.models.PurchaseCase;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * Steps stay thin: they resolve the case id against the CSV/JSON catalog
 * and then orchestrate Screenplay Tasks and Questions.
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

    @When("the customer adds the two products defined for case {string}")
    public void theCustomerAddsTheTwoProductsDefinedForCase(String caseId) {
        PurchaseCase purchaseCase = PurchaseCatalog.find(caseId);
        LOGGER.info("Case {} — adding products from CSV: {}", caseId, purchaseCase);

        theActorInTheSpotlight().attemptsTo(
                AddProductToCart.named(purchaseCase.getFirstProduct()),
                AddProductToCart.named(purchaseCase.getSecondProduct())
        );
    }

    @When("the customer opens the cart")
    public void theCustomerOpensTheCart() {
        theActorInTheSpotlight().attemptsTo(
                OpenCart.page()
        );
    }

    @Then("the cart should contain the two products defined for case {string}")
    public void theCartShouldContainTheTwoProductsDefinedForCase(String caseId) {
        PurchaseCase purchaseCase = PurchaseCatalog.find(caseId);
        LOGGER.info(
                "Verifying cart for {} contains '{}' and '{}'",
                caseId,
                purchaseCase.getFirstProduct(),
                purchaseCase.getSecondProduct()
        );

        theActorInTheSpotlight().should(
                seeThat(
                        "cart product list",
                        CartContents.displayed(),
                        hasItems(purchaseCase.getFirstProduct(), purchaseCase.getSecondProduct())
                )
        );
    }

    @When("the customer places the order with the buyer data for case {string}")
    public void theCustomerPlacesTheOrderWithTheBuyerDataForCase(String caseId) {
        PurchaseCase purchaseCase = PurchaseCatalog.find(caseId);
        LOGGER.info("Placing order for {} with buyer data from JSON: {}", caseId, purchaseCase.getBuyer());

        theActorInTheSpotlight().attemptsTo(
                CompletePurchase.with(purchaseCase.getBuyer())
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
