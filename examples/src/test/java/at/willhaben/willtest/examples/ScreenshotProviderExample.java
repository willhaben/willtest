package at.willhaben.willtest.examples;

import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.ScreenshotInterceptor;
import at.willhaben.willtest.junit5.extensions.DriverParameterResolverExtension;
import at.willhaben.willtest.junit5.extensions.ScreenshotProvider;
import at.willhaben.willtest.util.FixedTopBarShootingStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.ashot.screentaker.ShootingStrategy;

import static org.junit.Assert.fail;

@ExtendWith(DriverParameterResolverExtension.class)
@BrowserUtil({
        ScreenshotProvider.class,
        ScreenshotProviderExample.RemoveFixedHeaderShootingStrategy.class
})
class ScreenshotProviderExample {

    @Test
    void testCustomScreenshotProviderOnError(WebDriver driver) {
        driver.get("https://www.w3schools.com/howto/howto_css_alert_buttons.asp");
        fail();
    }

    public static class RemoveFixedHeaderShootingStrategy implements ScreenshotInterceptor {
        @Override
        public ShootingStrategy provideShootingStrategy() {
            return new FixedTopBarShootingStrategy(By.cssSelector("div#topnav"));
        }
    }
}
