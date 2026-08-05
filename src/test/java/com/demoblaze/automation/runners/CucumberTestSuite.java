package com.demoblaze.automation.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * JUnit Platform suite that discovers and runs Cucumber features
 * through Serenity BDD.
 * <p>
 * The SerenityReporterParallel plugin registers Serenity's BaseStepListener
 * so Screenplay actors, screenshots and living documentation work correctly.
 * <p>
 * Execute with: {@code mvn clean verify}
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("/features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.demoblaze.automation.steps")
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "net.serenitybdd.cucumber.core.plugin.SerenityReporterParallel,pretty,timeline:target/cucumber-timeline"
)
public class CucumberTestSuite {
    // Entry point only — configuration is annotation-driven
}
