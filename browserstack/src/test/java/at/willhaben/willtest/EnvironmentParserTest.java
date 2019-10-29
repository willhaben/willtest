package at.willhaben.willtest;

import at.willhaben.willtest.browserstack.BrowserstackEnvironment;
import at.willhaben.willtest.browserstack.exception.BrowserstackEnvironmentException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

import static at.willhaben.willtest.browserstack.BrowserstackSystemProperties.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class EnvironmentParserTest {

    static {
        System.setProperty(BROWSERSTACK_PLATFORM, "windows, mac OS");
        System.setProperty(BROWSERSTACK_PLATFORM_VERSION, "10, Sierra");
        System.setProperty(BROWSERSTACK_BROWSER_VERSION, "56.0, 50.0");
        System.setProperty(BROWSERSTACK_DISPLAY_RESOLUTION, "1920x1080, 1024x768");
    }

    @Test
    void testValidEnvironments() {
        String browsers = "firefox, chrome";
        System.setProperty(BROWSERSTACK_BROWSER, browsers);
        List<BrowserstackEnvironment> environments = BrowserstackEnvironment.parseFromSystemProperties();
        DesiredCapabilities capabilities = environments.get(0)
                .addToCapabilities(new DesiredCapabilities(), "testname");
        assertThat(capabilities.getCapability(BrowserstackEnvironment.CAPABILITY_OS), is("windows"));
        assertThat(capabilities.getCapability(BrowserstackEnvironment.CAPABILITY_BROWSER_VERSION), is("56.0"));
    }

    @Test()
    void testInvalidEnvironments() {
        Assertions.assertThrows(BrowserstackEnvironmentException.class, () -> {
            String browsers = "firefox";    // one parameter missing
            System.setProperty(BROWSERSTACK_BROWSER, browsers);
            BrowserstackEnvironment.parseFromSystemProperties();
        });
    }
}
