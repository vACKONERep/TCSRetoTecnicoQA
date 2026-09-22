Feature: Helper to create a DemoBlaze user

  # Called from the signup and login outlines. The suite runner only scans
  # classpath:features, so this file is not a standalone test.

  Scenario: Create user
    Given url baseUrl
    And path 'signup'
    And header Content-Type = 'application/json'
    And header Accept = 'application/json'
    And request { username: '#(username)', password: '#(password)' }
    When method post
    Then status 200
    * def errorMessage = karate.get('response.errorMessage')
    * match errorMessage == '#null'
