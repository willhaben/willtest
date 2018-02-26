package at.willhaben.willtest.misc.utils;

import org.openqa.selenium.By;

public class XPathOrCssUtil {

    /**
     * Generates a {@link By}. Checks for the first character in the string. A leading '/' generates a
     * {@link By#xpath(String)} otherwise a {@link By#cssSelector(String)}.
     * @param xPathOrCss locator string
     * @return locator for selenium
     */
    public static By mapToBy(String xPathOrCss) {
        if (xPathOrCss.startsWith("/")) {
            return By.xpath(xPathOrCss);
        } else {
            return By.cssSelector(xPathOrCss);
        }
    }
}
