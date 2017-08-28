package at.willhaben.willtest.examples;

import at.willhaben.willtest.misc.rule.SeleniumRule;
import at.willhaben.willtest.util.FixedTopBarShootingStrategy;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.AShot;

import static org.junit.Assert.fail;

public class ScreenshotProviderExampleTest {

    @Rule
    public SeleniumRule rule = new SeleniumRule()
            .setScreenshotProvider(webDriver -> new AShot()
                    .shootingStrategy(
                            new FixedTopBarShootingStrategy(By.cssSelector("div#topnav")))
                    .takeScreenshot(webDriver)
                    .getImage());

    @Test
    public void testCustomScreenshotProviderOnError() {
        WebDriver webDriver = rule.getWebDriver();
        webDriver.get("https://www.w3schools.com/howto/howto_css_alert_buttons.asp");

        fail();
    }
}
