package at.willhaben.willtest.util;

public class BrowserSelectionUtils {

    private static final String DEFAULT_FIREFOX = "firefox";
    private static final String DEFAULT_CHROME = "chrome";
    private static final String DEFAULT_IE = "ie";
    private static final String DEFAULT_EDGE = "edge";

    public static String getBrowser() {
        return Environment.getValue("browser", DEFAULT_FIREFOX).toLowerCase();
    }

    public static boolean isFirefox() {
        return getBrowser().equals(DEFAULT_FIREFOX);
    }

    public static boolean isChrome() {
        return getBrowser().equals(DEFAULT_CHROME);
    }

    public static boolean isIE() {
        return getBrowser().equals(DEFAULT_IE);
    }

    public static boolean isEdge() {
        return getBrowser().equals(DEFAULT_EDGE);
    }
}
