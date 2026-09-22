@purchase @e2e
Feature: End-to-end product purchase on DemoBlaze
  As a DemoBlaze customer
  I want to add two products to the cart and complete checkout
  So that I receive an order confirmation

  Product pairs come from data/purchases.csv.
  The Place Order form comes from data/buyers.json, linked by buyerId.
  The Examples table only chooses which case id to run.

  Background:
    Given the customer opens the DemoBlaze home page

  @outline
  Scenario Outline: Successful purchase for case <caseId>
    When the customer adds the two products defined for case "<caseId>"
    And the customer opens the cart
    Then the cart should contain the two products defined for case "<caseId>"
    When the customer places the order with the buyer data for case "<caseId>"
    Then the purchase should be confirmed successfully
    And the order confirmation should display an order id
    When the customer closes the purchase confirmation
    Then the confirmation modal should no longer be visible

    Examples:
      | caseId    |
      | PHONES-01 |
      | PHONES-02 |
