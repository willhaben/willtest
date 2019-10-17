package at.willhaben.willtest.browserstack;

import at.willhaben.willtest.browserstack.exception.BrowserstackEnvironmentException;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static at.willhaben.willtest.browserstack.BrowserstackSystemProperties.*;

public class BrowserstackEnvironment {

    public static final String CAPABILITY_OS = "os";
    public static final String CAPABILITY_OS_VERSION = "os_version";

    public static final String CAPABILITY_BROWSER = "browser";
    public static final String CAPABILITY_BROWSER_VERSION = "browser_version";

    public static final String CAPABILITY_DISPLAY_RESOLUTION = "resolution";

    public static final String CAPABILITY_BROWSERSTACK_LOCAL = "browserstack.local";

    public static final String CAPABILITY_TESTNAME = "name";
    public static final String CAPABILITY_BUILD = "build";
    public static final String CAPABILITY_PROJECT = "project";

    private final DateTimeFormatter BUILD_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");


    private String os;
    private String osVersion;
    private String browser;
    private String browserVersion;
    private String displayResolution;
    private String local;

    private BrowserstackEnvironment(String os, String osVersion, String browser, String browserVersion, String displayResolution, String local) {
        this.os = os;
        this.osVersion = osVersion;
        this.browser = browser;
        this.browserVersion = browserVersion;
        this.displayResolution = displayResolution;
        this.local = local;
    }

    public static List<BrowserstackEnvironment> parseFromSystemProperties() {
        List<String> platforms = parseFromCommaSeparatedString(getBrowserstackPlatform());
        List<String> platformsVersions = parseFromCommaSeparatedString(getBrowserstackPlatformVersion());
        List<String> browsers = parseFromCommaSeparatedString(getBrowserstackBrowser());
        List<String> browsersVersions = parseFromCommaSeparatedString(getBrowserstackBrowserVersion());
        List<String> displayResolutions = parseFromCommaSeparatedString(getBrowserstackDisplayResolution());
        String local = getBrowserstackLocal();
        if((platforms.size() != platformsVersions.size()) || (platforms.size() != browsers.size()) ||
                (platforms.size() != browsersVersions.size())) {
            throw new BrowserstackEnvironmentException("If you use comma separated environments for Browserstack, every config" +
                    " must be set so all lists have the same length except the '" + BROWSERSTACK_HUB_LOCAL + "' property.");
        }
        ArrayList<BrowserstackEnvironment> browserstackEnvironments = new ArrayList<>();
        for (int i = 0; i < platforms.size(); i++) {
            browserstackEnvironments.add(new BrowserstackEnvironment(
                    platforms.get(i),
                    platformsVersions.get(i),
                    browsers.get(i),
                    browsersVersions.get(i),
                    displayResolutions.get(i),
                    local));
        }
        return browserstackEnvironments;
    }

    private static List<String> parseFromCommaSeparatedString(String optionString) {
        return Arrays.stream(optionString.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public <T extends MutableCapabilities> T addToCapabilities(T capabilities, String testName) {
        capabilities.setCapability(CAPABILITY_OS, os);
        capabilities.setCapability(CAPABILITY_OS_VERSION, osVersion);
        capabilities.setCapability(CAPABILITY_BROWSER, browser);
        capabilities.setCapability(CAPABILITY_BROWSER_VERSION, browserVersion);
        capabilities.setCapability(CAPABILITY_DISPLAY_RESOLUTION, displayResolution);
        capabilities.setCapability(CAPABILITY_BROWSERSTACK_LOCAL, local);
        capabilities.setCapability(CAPABILITY_TESTNAME, testName);
        String buildName = getBuildName().orElse(BUILD_DATE_FORMAT.format(ZonedDateTime.now()));
        capabilities.setCapability(CAPABILITY_BUILD, buildName);
        getProjectName().ifPresent(projectName -> capabilities.setCapability(CAPABILITY_PROJECT, projectName));
        return capabilities;
    }
}
