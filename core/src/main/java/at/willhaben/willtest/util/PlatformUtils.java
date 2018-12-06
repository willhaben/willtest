package at.willhaben.willtest.util;

import at.willhaben.willtest.util.Environment;

import java.net.MalformedURLException;
import java.net.URL;

public class PlatformUtils {

    private static final String DEFAULT_PLATFORM_ANDROID = "Android";
    private static final String DEFAULT_PLATFORM_ANDROID_HUB = "Android-Hub";

    private static final String DEFAULT_PLATFORM_IOS = "IOS";

    private static final String DEFAULT_PLATFORM_DESKTOP = "Linux";
    private static final String DEFAULT_PLATFORM_WINDOWS = "Windows";
    private static final String SELENIUM_HUB_SYSTEM_PROPERTY_KEY = "seleniumHub";
    private static final String DEFAULT_PLATFORM_LINUX = "Linux";

    public static String getPlatform() {
        return Environment.getValue("platform", DEFAULT_PLATFORM_DESKTOP);
    }

    public static boolean isAndroid() {
        String platform = getPlatform();
        return platform.equals(DEFAULT_PLATFORM_ANDROID) || platform.equals(DEFAULT_PLATFORM_ANDROID_HUB);
    }

    public static boolean isIOS() {
        return getPlatform().equals(DEFAULT_PLATFORM_IOS);
    }

    public static boolean isLinux() {
        return getPlatform().equals(DEFAULT_PLATFORM_LINUX);
    }

    public static boolean isWindows() {
        return getPlatform().equals(DEFAULT_PLATFORM_WINDOWS);
    }

}
