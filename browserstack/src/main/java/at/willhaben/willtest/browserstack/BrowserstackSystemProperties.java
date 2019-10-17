package at.willhaben.willtest.browserstack;

import java.util.Optional;

import static java.lang.System.getProperty;

public class BrowserstackSystemProperties {

    public static final String BROWSERSTACK_HUB_LOCAL = "browserstack.local";
    public static final String BROWSERSTACK_PLATFORM = "platform";
    public static final String BROWSERSTACK_PLATFORM_VERSION = "platform.version";
    public static final String BROWSERSTACK_BROWSER = "browser";
    public static final String BROWSERSTACK_BROWSER_VERSION = "browser.version";
    public static final String BROWSERSTACK_DISPLAY_RESOLUTION = "display.resolution";

    public static final String BROWSERSTACK_BUILD = "browserstack.build";
    public static final String BROWSERSTACK_PROJECT = "browserstack.project";

    public static String getBrowserstackLocal() {
        return getProperty(BROWSERSTACK_HUB_LOCAL, "false");
    }

    public static String getBrowserstackPlatform() {
        return getProperty(BROWSERSTACK_PLATFORM, "linux");
    }

    public static String getBrowserstackPlatformVersion() {
        return getProperty(BROWSERSTACK_PLATFORM_VERSION, "asdf");
    }

    public static String getBrowserstackBrowser() {
        return getProperty(BROWSERSTACK_BROWSER, "firefox");
    }

    public static String getBrowserstackBrowserVersion() {
        return getProperty(BROWSERSTACK_BROWSER_VERSION, "asdf");
    }

    public static String getBrowserstackDisplayResolution() {
        return getProperty(BROWSERSTACK_DISPLAY_RESOLUTION, "1920x1080");
    }

    public static Optional<String> getBuildName() {
        return Optional.ofNullable(getProperty(BROWSERSTACK_BUILD));
    }

    public static Optional<String> getProjectName() {
        return Optional.ofNullable(getProperty(BROWSERSTACK_PROJECT));
    }
}
