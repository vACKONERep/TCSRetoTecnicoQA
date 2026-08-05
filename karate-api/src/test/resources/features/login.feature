@login @api
Feature: DemoBlaze Login API
  As a registered customer
  I want to authenticate through the Login REST API
  So that I receive an auth token to use the application

  Background:
    * url baseUrl
    * header Content-Type = 'application/json'
    * header Accept = 'application/json'
    * def timestamp = java.lang.System.currentTimeMillis()
    * def random = Math.floor(Math.random() * 100000)
    * def newUsername = 'login_user_' + timestamp + '_' + random
    * def password = defaultPassword

  @smoke @positive
  Scenario: Login with correct username and password
    # Arrange: ensure a fresh user exists before login
    Given path 'signup'
    And request { username: '#(newUsername)', password: '#(password)' }
    When method post
    Then status 200

    # Act: login with the same credentials
    Given path 'login'
    And request { username: '#(newUsername)', password: '#(password)' }
    When method post
    Then status 200
    # DemoBlaze returns a string token, e.g. "Auth_token: <base64>"
    And match response == '#string'
    And match response contains 'Auth_token'
    And match response == '#regex (?s).*Auth_token:\\s*\\S+.*'
    * print 'Login successful for:', newUsername
    * print 'Token response:', response

  @negative
  Scenario: Login with incorrect password
    # Arrange: create a valid user first
    Given path 'signup'
    And request { username: '#(newUsername)', password: '#(password)' }
    When method post
    Then status 200

    # Act: same username, wrong password
    Given path 'login'
    And request { username: '#(newUsername)', password: 'WrongPass999!' }
    When method post
    Then status 200
    And match response == { errorMessage: '#(messages.wrongPassword)' }
    And match response.errorMessage == 'Wrong password.'
    * print 'Login correctly rejected for wrong password. User:', newUsername

  @negative
  Scenario: Login with incorrect username
    Given path 'login'
    And request { username: 'user_does_not_exist_x_999', password: 'whatever' }
    When method post
    Then status 200
    And match response == { errorMessage: '#(messages.userDoesNotExist)' }
    And match response.errorMessage contains 'does not exist'
    * print 'Login correctly rejected for unknown user'
