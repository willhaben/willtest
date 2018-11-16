package at.willhaben.willtest.examples.junit5tests;

import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.DefaultBrowserOptionInterceptor;
import at.willhaben.willtest.junit5.extensions.DriverParameterResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@ExtendWith({
        DriverParameterResolver.class,
})
@BrowserUtil(DefaultBrowserOptionInterceptor.class)
public class CreateDriverWithExtensionTest {

    private static final String WILLTEST_GITHUB_PAGE = "https://github.com/willhaben/willtest";
    private static final String REPO_HEADER_LOCATOR = "div.repohead-details-container h1";
    private String compareValue = "willhaben/willtest";

    @Test
    public void testCreateDriverWithExtension(WebDriver driver) {
        driver.get(WILLTEST_GITHUB_PAGE);
        WebElement element = driver.findElement(By.cssSelector(REPO_HEADER_LOCATOR));
        assertThat(element.getText(), is(compareValue));
    }

    @Test
    @BrowserUtil(BrowserSetup.class)
    public void testCreateChromeDriver(WebDriver driver){
        driver.get(WILLTEST_GITHUB_PAGE);
        WebElement element = driver.findElement(By.cssSelector(REPO_HEADER_LOCATOR));
        assertThat(element.getText(), is(compareValue));
    }
}
