package at.willhaben.willtest.examples;

import at.willhaben.willtest.junit5.WebDriverPostInterceptor;
import org.openqa.selenium.WebDriver;

public class PostProcessSetup implements WebDriverPostInterceptor {
    @Override
    public void postProcessWebDriver(WebDriver driver) {
        driver.manage().window().maximize();
    }
}
