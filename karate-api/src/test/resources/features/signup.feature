@signup @api
Feature: DemoBlaze Signup API
  As a new customer
  I want to register through the Signup REST API
  So that I can create an account on DemoBlaze

  Each row of data/signup-cases.csv is one Scenario Outline example.
  expectedKey points at messages.* in karate-config.js.
  The username is generated per example so a rerun does not collide with a previous user.

  Background:
    * url baseUrl
    * header Content-Type = 'application/json'
    * header Accept = 'application/json'
    * def password = defaultPassword

  @smoke
  Scenario Outline: <caseId> - <description>
    * def stamp = java.lang.System.currentTimeMillis() + '_' + java.util.concurrent.ThreadLocalRandom.current().nextInt(100000)
    * def username = 'signup_' + stamp
    * def expected = expectedKey == 'NONE' ? null : messages[expectedKey]
    * def prepared = (flow == 'duplicate') ? karate.call('classpath:helpers/create-user.feature', { username: username, password: password }) : null
    Given url baseUrl
    And path 'signup'
    And header Content-Type = 'application/json'
    And request { username: '#(username)', password: '#(password)' }
    When method post
    Then status 200
    * def actualError = karate.get('response.errorMessage')
    * match actualError == expected
    * print 'INPUT username=', username, 'flow=', flow
    * print 'OUTPUT', response

    Examples:
      | read('classpath:data/signup-cases.csv') |
