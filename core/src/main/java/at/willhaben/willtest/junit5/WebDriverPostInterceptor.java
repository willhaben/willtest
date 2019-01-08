package at.willhaben.willtest.junit5;

import org.openqa.selenium.WebDriver;

public interface WebDriverPostInterceptor extends BrowserUtilExtension {
    void postProcessWebDriver(WebDriver driver);
}
