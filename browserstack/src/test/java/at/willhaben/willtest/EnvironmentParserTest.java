package at.willhaben.willtest;

import at.willhaben.willtest.browserstack.rule.BrowserstackEnvironment;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EnvironmentParserTest {

    private static final String platforms = "windows, mac OS";
    private static final String platformVersions = "10, Sierra";
    private static final String browserVersions = "56.0, 50.0";
    private static final String resolutions = "1920x1080, 1024x768";

    @Test
    public void testValidEnvironments() {
        String browsers = "firefox, chrome";
        List<BrowserstackEnvironment> environments = BrowserstackEnvironment.parseEnvironmentsFromStrings(platforms,
                platformVersions,
                browsers,
                browserVersions,
                resolutions);
        DesiredCapabilities capabilities = environments.get(0).addToCapabilities(new DesiredCapabilities());
        assertThat(capabilities.getCapability(BrowserstackEnvironment.CAPABILITY_OS), is("windows"));
        assertThat(capabilities.getCapability(BrowserstackEnvironment.CAPABILITY_BROWSER_VERSION), is("56.0"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidEnvironments() {
        String browsers = "firefox";    // one parameter missing
        BrowserstackEnvironment.parseEnvironmentsFromStrings(platforms,
                platformVersions,
                browsers,
                browserVersions,
                resolutions);
    }
}
