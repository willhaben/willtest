package at.willhaben.willtest.rule;

import at.willhaben.willtest.config.WebDriverProvider;
import org.junit.runner.Description;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;

/**
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
        catch (NoAlertPresentException thisIsNotInterestingSimplyIgnoring ) {
        }
        finally {
            webDriver.switchTo().defaultContent();
        }
    }
}
