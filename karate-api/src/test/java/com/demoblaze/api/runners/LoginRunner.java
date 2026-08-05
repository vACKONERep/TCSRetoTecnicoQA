package com.demoblaze.api.runners;

import com.intuit.karate.junit5.Karate;

/**
 * Focused JUnit 5 runner for Login feature only.
 * Useful for local debugging: run this class from the IDE.
 */
class LoginRunner {

    @Karate.Test
    Karate testLogin() {
        return Karate.run("classpath:features/login.feature")
                .relativeTo(getClass());
    }
}
