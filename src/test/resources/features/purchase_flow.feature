@purchase @e2e 
Feature: End-to-end product purchase on DemoBlaze
  As a DemoBlaze customer
  I want to add products to the cart and complete a purchase
  So that I can buy products successfully online

  Background:
    Given the customer opens the DemoBlaze home page

  Scenario: Successful purchase of two different products
    When the customer adds "Samsung galaxy s6" to the cart
    And the customer adds "Nokia lumia 1520" to the cart
    And the customer opens the cart
    Then the cart should contain the products:
      | product            |
      | Samsung galaxy s6  |
      | Nokia lumia 1520   |
    When the customer places the order with valid purchase data
    Then the purchase should be confirmed successfully
    And the order confirmation should display an order id
    When the customer closes the purchase confirmation
    Then the confirmation modal should no longer be visible