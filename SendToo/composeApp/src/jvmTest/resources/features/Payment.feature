Feature: Payment creation and transaction listing

  Scenario: Create a payment from JSON payload
    Given Valid payment request data
    When I create a payment object:
      """
      {
        "amount": 49.99,
        "recipientEmail": "alice@example.com",
        "currency": "USD"
      }
      """
    Then I should see 1 transaction with amount 49.99

  Scenario: Reject payment with unsupported currency
    Given Valid payment request data
    When I create a payment object:
      """
      {
        "amount": 10.00,
        "recipientEmail": "alice@example.com",
        "currency": "JPY"
      }
      """
    Then I should get an UnsupportedCurrency error

  Scenario: Reject payment for unknown recipient
    Given Valid payment request data
    When I create a payment object:
      """
      {
        "amount": 25.50,
        "recipientEmail": "charlie@example.com",
        "currency": "USD"
      }
      """
    Then I should get a RecipientNotFound error

  Scenario: Reject payment due to insufficient funds
    Given Valid payment request data
    When I create a payment object:
      """
      {
        "amount": 5000.00,
        "recipientEmail": "alice@example.com",
        "currency": "USD"
      }
      """
    Then I should get an InsufficientFunds error