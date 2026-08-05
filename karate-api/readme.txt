================================================================================
 DemoBlaze API Automation — Karate Framework
================================================================================

Project : demoblaze-karate-api
Base URL: https://api.demoblaze.com
APIs    : POST /signup , POST /login

--------------------------------------------------------------------------------
1. PROJECT STRUCTURE
--------------------------------------------------------------------------------

  karate-api/
  ├── pom.xml
  ├── readme.txt
  ├── conclusiones.txt
  ├── .gitignore
  └── src/test/
      ├── java/com/demoblaze/api/runners/
      │     ├── DemoBlazeApiTest.java   ← main suite runner (JUnit 5)
      │     ├── SignupRunner.java      ← IDE-friendly signup only
      │     └── LoginRunner.java       ← IDE-friendly login only
      └── resources/
            ├── karate-config.js
            └── features/
                  ├── signup.feature
                  └── login.feature

--------------------------------------------------------------------------------
2. PREREQUISITES
--------------------------------------------------------------------------------

  - JDK 17 or higher (project targets Java 17)
  - Apache Maven 3.9+  (or use parent project Maven Wrapper from repo root)
  - Internet access to reach https://api.demoblaze.com

  Verify:

    java -version
    mvn -version

--------------------------------------------------------------------------------
3. HOW TO RUN THE TESTS
--------------------------------------------------------------------------------

  From this folder (karate-api):

    mvn clean test

  From the parent repository root (if Maven Wrapper is available there):

    cd karate-api
    ..\mvnw.cmd clean test

  PowerShell — set JAVA_HOME if needed:

    $env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.20.8-hotspot"
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
    mvn clean test

  Run by tags / profiles:

    mvn test -Psmoke          # @smoke scenarios only
    mvn test -Psignup         # @signup only
    mvn test -Plogin          # @login only

  Or via Karate options:

    mvn test "-Dkarate.options=--tags @negative"
    mvn test "-Dkarate.options=--tags @positive"

--------------------------------------------------------------------------------
4. SCENARIOS COVERED
--------------------------------------------------------------------------------

  1) Signup  — successfully create a new user              (@smoke @positive)
  2) Signup  — user already exists                         (@negative)
  3) Login   — correct username and password               (@smoke @positive)
  4) Login   — incorrect password                          (@negative)
  5) Login   — incorrect / unknown username (bonus)        (@negative)

  Usernames are generated dynamically (timestamp + random) so re-runs never
  collide with previously registered accounts.

--------------------------------------------------------------------------------
5. HOW TO VIEW THE REPORTS
--------------------------------------------------------------------------------

  After a run, open the Karate HTML report:

    target/karate-reports/karate-summary.html

  PowerShell:

    Invoke-Item target\karate-reports\karate-summary.html

  Additional outputs:

    target/karate-reports/          ← Karate HTML (timeline, request/response)
    target/surefire-reports/        ← JUnit / Surefire XML + text
    target/karate-reports/*.json    ← Cucumber JSON (if enabled)

--------------------------------------------------------------------------------
6. NOTES ABOUT THE API UNDER TEST
--------------------------------------------------------------------------------

  DemoBlaze returns HTTP 200 for both success and business failures.
  Assertions therefore validate the response BODY:

    - Successful signup  → empty body
    - Duplicate signup   → { "errorMessage": "This user already exist." }
    - Successful login   → string containing "Auth_token: ..."
    - Wrong password     → { "errorMessage": "Wrong password." }
    - Unknown user       → { "errorMessage": "User does not exist." }

================================================================================
