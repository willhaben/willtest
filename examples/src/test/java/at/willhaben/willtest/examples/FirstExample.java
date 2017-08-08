package at.willhaben.willtest.examples;

import at.willhaben.willtest.misc.rule.SeleniumRule;
import at.willhaben.willtest.util.FixedTopBarShootingStrategy;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FirstExample {
    private static final String REPO_HEADER_LOCATOR = "div.repohead-details-container h1";
    private static final String WILLTEST_GITHUB_PAGE = "https://github.com/willhaben/willtest";

    @BeforeClass
    public static void beforeClass() {
        Utils.assumeHavingFirefoxConfigured();
    }

    @Rule
    public final SeleniumRule seleniumRule = new SeleniumRule()
            .useScreenshotShootingStrategy(new FixedTopBarShootingStrategy(200, 80));

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

    @Test
    public void willTest() {
        WebDriver webDriver = seleniumRule.getWebDriver();
        webDriver.get("https://mobile-uat.willhaben.at");
        WebElement bapLink = webDriver.findElement(By.cssSelector("#bap div"));
        bapLink.click();
        assertThat(true, is(false));
    }
}
