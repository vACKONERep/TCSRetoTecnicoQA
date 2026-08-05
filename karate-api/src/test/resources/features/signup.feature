@signup @api
Feature: DemoBlaze Signup API
  As a new customer
  I want to register through the Signup REST API
  So that I can create an account to use DemoBlaze

  Background:
    * url baseUrl
    * path 'signup'
    * header Content-Type = 'application/json'
    * header Accept = 'application/json'
    # Dynamic unique username — avoids collisions between test runs
    * def timestamp = java.lang.System.currentTimeMillis()
    * def random = Math.floor(Math.random() * 100000)
    * def newUsername = 'auto_user_' + timestamp + '_' + random
    * def password = defaultPassword

  @smoke @positive
  Scenario: Successfully create a new user
    Given request { username: '#(newUsername)', password: '#(password)' }
    When method post
    Then status 200
    # DemoBlaze returns HTTP 200 with an empty body on success (no errorMessage)
    * def errorMessage = karate.get('response.errorMessage')
    * match errorMessage == '#null'
    * print 'Created user:', newUsername

  @negative
  Scenario: Attempt to create a user that already exists
    # Arrange: create the user once so the second call is a true negative case
    Given request { username: '#(newUsername)', password: '#(password)' }
    When method post
    Then status 200

    # Act: try to sign up again with the same credentials
    Given path 'signup'
    And request { username: '#(newUsername)', password: '#(password)' }
    When method post
    Then status 200
    # Assert: business error is returned in the body (HTTP stays 200)
    And match response == { errorMessage: '#(messages.userAlreadyExists)' }
    And match response.errorMessage contains 'already exist'
    * print 'Duplicate signup correctly rejected for:', newUsername
