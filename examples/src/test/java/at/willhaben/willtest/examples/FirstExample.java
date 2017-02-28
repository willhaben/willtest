package at.willhaben.willtest.examples;

import at.willhaben.willtest.config.DefaultFirefoxBinaryProvider;
import at.willhaben.willtest.misc.rule.SeleniumRule;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

public class FirstExample {
    private static final String REPO_HEADER_LOCATOR = "div.repohead-details-container h1";
    private static final String WILLTEST_GITHUB_PAGE = "https://github.com/willhaben/willtest";

    @BeforeClass
    public static void beforeClass() {
        assumeThat(
                "Please define the path to your firefox executable using " +
                        DefaultFirefoxBinaryProvider.FIREFOX_BINARY_LOCATION_SYSTEM_PROPERTY_KEY + " system property! " +
                        "This is just an assumption to keep our build green.",
                System.getProperty(DefaultFirefoxBinaryProvider.FIREFOX_BINARY_LOCATION_SYSTEM_PROPERTY_KEY),
                is(notNullValue()));
    }

    @Rule
    public SeleniumRule seleniumRule = new SeleniumRule();

    @Test
    public void openPage() {
        WebDriver webDriver = seleniumRule.getWebDriver();
        webDriver.get(WILLTEST_GITHUB_PAGE);
        WebElement element = webDriver.findElement(By.cssSelector(REPO_HEADER_LOCATOR));
        assertThat(element.getText(),is("willhaben/willtest"));
    }

    @Test
    public void buggyTest() {
        WebDriver webDriver = seleniumRule.getWebDriver();
        webDriver.get(WILLTEST_GITHUB_PAGE);
        WebElement element = webDriver.findElement(By.cssSelector(REPO_HEADER_LOCATOR));
        assertThat(element.getText(),is("fooooo"));
    }
}
