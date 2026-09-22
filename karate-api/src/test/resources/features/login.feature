@login @api
Feature: DemoBlaze Login API
  As a registered customer
  I want to authenticate through the Login REST API
  So that I receive an auth token

  Examples are read from data/login-cases.json.
  expectedKey points at messages.* in karate-config.js.
  A fresh user is created first, except for the unknown-user case.

  Background:
    * url baseUrl
    * header Content-Type = 'application/json'
    * header Accept = 'application/json'
    * def password = defaultPassword

  @smoke
  Scenario Outline: <caseId> - <description>
    * def stamp = java.lang.System.currentTimeMillis() + '_' + java.util.concurrent.ThreadLocalRandom.current().nextInt(100000)
    * def username = (flow == 'unknown_user') ? ('missing_' + stamp) : ('login_' + stamp)
    * def passwordUsed = (flow == 'wrong_password') ? 'WrongPass999!' : password
    * def expected = expectedKey == 'NONE' ? null : messages[expectedKey]
    * def prepared = (flow == 'unknown_user') ? null : karate.call('classpath:helpers/create-user.feature', { username: username, password: password })
    Given url baseUrl
    And path 'login'
    And header Content-Type = 'application/json'
    And request { username: '#(username)', password: '#(passwordUsed)' }
    When method post
    Then status 200
    * if (flow == 'valid') karate.match(response, '#regex (?s).*Auth_token:\\s*\\S+.*')
    * if (flow != 'valid') karate.match(response.errorMessage, expected)
    * print 'INPUT username=', username, 'flow=', flow
    * print 'OUTPUT', response

    Examples:
      | read('classpath:data/login-cases.json') |
