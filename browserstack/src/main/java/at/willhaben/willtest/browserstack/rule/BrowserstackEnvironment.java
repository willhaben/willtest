package at.willhaben.willtest.browserstack.rule;

import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BrowserstackEnvironment {

    private String os;
    private String osVersion;
    private String browser;
    private String browserVersion;

    private BrowserstackEnvironment(String os, String osVersion, String browser, String browserVersion) {
        this.os = os;
        this.osVersion = osVersion;
        this.browser = browser;
        this.browserVersion = browserVersion;
    }

    public static List<BrowserstackEnvironment> parseEnvironmentsFromStrings(String platormsString,
                                                                             String platformsVersionsString,
                                                                             String browsersString,
                                                                             String browsersVersionsString) {
        List<String> platforms = parseFromCommaSeparatedString(platormsString);
        List<String> platformsVersions = parseFromCommaSeparatedString(platformsVersionsString);
        List<String> browsers = parseFromCommaSeparatedString(browsersString);
        List<String> browsersVersions = parseFromCommaSeparatedString(browsersVersionsString);
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
                    browsersVersions.get(i)));
        }
        return browserstackEnvironments;
    }

    private static List<String> parseFromCommaSeparatedString(String platormsString) {
        return Arrays.stream(platormsString.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public DesiredCapabilities addToCapabilities(DesiredCapabilities capabilities) {
        capabilities.setCapability("os", os);
        capabilities.setCapability("os_version", osVersion);
        capabilities.setCapability("browser", browser);
        capabilities.setCapability("browser_version", browserVersion);
        return capabilities;
    }
}
