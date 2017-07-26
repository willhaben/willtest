package at.willhaben.willtest.examples;

import at.willhaben.willtest.config.SeleniumProvider;
import at.willhaben.willtest.config.WebDriverConfigurationParticipant;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import static org.mockito.Mockito.mock;

public class MockedSeleniumProvider implements SeleniumProvider<MockedSeleniumProvider,WebDriver>, TestRule {
    @Override
    public WebDriver getWebDriver() {
        return mock(WebDriver.class);
    }

    @Override
    public MockedSeleniumProvider addWebDriverConfigurationParticipant(
            WebDriverConfigurationParticipant<WebDriver> webDriverConfigurationParticipant) {
        return this;
    }

    @Override
    public MockedSeleniumProvider addWebDriverEventListener(WebDriverEventListener listener) {
        return this;
    }

    @Override
    public MockedSeleniumProvider getThis() {
        return this;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return base;
    }
}
