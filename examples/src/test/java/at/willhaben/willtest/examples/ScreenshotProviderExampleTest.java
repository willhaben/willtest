package at.willhaben.willtest.examples;

import at.willhaben.willtest.misc.rule.SeleniumRule;
import at.willhaben.willtest.util.FixedTopBarShootingStrategy;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.AShot;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class ScreenshotProviderExampleTest {

    @Rule
    public SeleniumRule rule = new SeleniumRule()
            .setScreenshotProvider(webDriver -> new AShot()
                    .shootingStrategy(
                            new FixedTopBarShootingStrategy(200, By.cssSelector("div#topnav")))
                    .takeScreenshot(webDriver)
                    .getImage());

    @Test
    public void testCustomScreenshotProvider() {
        WebDriver webDriver = rule.getWebDriver();
        webDriver.get("https://www.w3schools.com/bootstrap/bootstrap_navbar.asp");
        assertThat(true, is(false));
    }
}
