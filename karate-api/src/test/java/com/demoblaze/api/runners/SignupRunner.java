package com.demoblaze.api.runners;

import com.intuit.karate.junit5.Karate;

/**
 * Focused JUnit 5 runner for Signup feature only.
 * Useful for local debugging: run this class from the IDE.
 */
class SignupRunner {

    @Karate.Test
    Karate testSignup() {
        return Karate.run("classpath:features/signup.feature")
                .relativeTo(getClass());
    }
}
