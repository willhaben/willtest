package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.WebDriverProvider;
import org.junit.runner.Description;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;

/**
 * Adds the text of a possible alert to the normal test failure as suppressed exception.<b/>
 * The reason is that if an alert happens, there is usually a timeout from a {@link WebDriver#findElement(By)} call,
 * which is caused actually by the alert which hangs the javascript processing. Alerts are unfortunately not visible
 * on screenshots, they do not result in javascript console or webdriver log entries. So the only possibility to get
 * this information is to try to fetch a possible alert message. This helps then the investigation.
 *
 * Created by liptak on 2016.09.26..
 */
public class JavascriptAlert extends AbstractRule {
    private final WebDriverProvider webDriverProvider;

    public JavascriptAlert(WebDriverProvider webDriverProvider) {
        this.webDriverProvider = webDriverProvider;
    }

    @Override
    protected void onError(Description description, Throwable testFailure) throws Throwable {
        super.onError(description, testFailure);
        WebDriver webDriver = webDriverProvider.getWebDriver();
        try {
            Alert alert = webDriver.switchTo().alert();
            testFailure.addSuppressed( new RuntimeException( "Unexpected alert with text: '" + alert.getText() + "'!" ));
        }
        catch (NoAlertPresentException ignored ) {
        }
        finally {
            webDriver.switchTo().defaultContent();
        }
    }
}
