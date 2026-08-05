package com.demoblaze.api.runners;

import com.intuit.karate.Results;
import com.intuit.karate.Runner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit 5 entry point for the DemoBlaze Karate API suite.
 * <p>
 * Runs all features under {@code classpath:features} and fails the build
 * if any scenario fails. Karate HTML reports are written under {@code target/karate-reports}.
 */
class DemoBlazeApiTest {

    @Test
    void testAllApiScenarios() {
        Results results = Runner.path("classpath:features")
                .outputCucumberJson(true)
                .parallel(1);

        assertEquals(
                0,
                results.getFailCount(),
                results.getErrorMessages()
        );
    }
}
