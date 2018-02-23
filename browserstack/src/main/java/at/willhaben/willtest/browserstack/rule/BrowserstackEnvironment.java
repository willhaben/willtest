package at.willhaben.willtest.browserstack.rule;

import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BrowserstackEnvironment {

    public static final String CAPABILITY_OS = "os";
    public static final String CAPABILITY_OS_VERSION = "os_version";
    public static final String CAPABILITY_BROWSER = "browser";
    public static final String CAPABILITY_BROWSER_VERSION = "browser_version";
    public static final String CAPABILITY_DISPLAY_RESOLUTION = "resolution";

    private String os;
    private String osVersion;
    private String browser;
    private String browserVersion;
    private String displayResolution;

    private BrowserstackEnvironment(String os, String osVersion, String browser, String browserVersion, String displayResolution) {
        this.os = os;
        this.osVersion = osVersion;
        this.browser = browser;
        this.browserVersion = browserVersion;
        this.displayResolution = displayResolution;
    }

    public static List<BrowserstackEnvironment> parseEnvironmentsFromStrings(String platormsString,
                                                                             String platformsVersionsString,
                                                                             String browsersString,
                                                                             String browsersVersionsString,
                                                                             String displayResolutionString) {
        List<String> platforms = parseFromCommaSeparatedString(platormsString);
        List<String> platformsVersions = parseFromCommaSeparatedString(platformsVersionsString);
        List<String> browsers = parseFromCommaSeparatedString(browsersString);
        List<String> browsersVersions = parseFromCommaSeparatedString(browsersVersionsString);
        List<String> displayResolutions = parseFromCommaSeparatedString(displayResolutionString);
        if((platforms.size() != platformsVersions.size()) || (platforms.size() != browsers.size()) ||
                (platforms.size() != browsersVersions.size())) {
            throw new IllegalArgumentException("If you use comma separated environments for Browserstack, every config" +
                    " must be set so all lists have the same length.");
        }
        ArrayList<BrowserstackEnvironment> browserstackEnvironments = new ArrayList<>();
        for (int i = 0; i < platforms.size(); i++) {
            browserstackEnvironments.add(new BrowserstackEnvironment(platforms.get(i),
                    platformsVersions.get(i),
                    browsers.get(i),
                    browsersVersions.get(i),
                    displayResolutions.get(i)));
        }
        return browserstackEnvironments;
    }

    private static List<String> parseFromCommaSeparatedString(String platormsString) {
        return Arrays.stream(platormsString.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public DesiredCapabilities addToCapabilities(DesiredCapabilities capabilities) {
        capabilities.setCapability(CAPABILITY_OS, os);
        capabilities.setCapability(CAPABILITY_OS_VERSION, osVersion);
        capabilities.setCapability(CAPABILITY_BROWSER, browser);
        capabilities.setCapability(CAPABILITY_BROWSER_VERSION, browserVersion);
        capabilities.setCapability(CAPABILITY_DISPLAY_RESOLUTION, displayResolution);
        return capabilities;
    }
}
