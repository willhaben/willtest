package at.willhaben.willtest.examples.junit5tests;

import at.willhaben.willtest.junit5.extensions.DriverParameterResolver;
import at.willhaben.willtest.junit5.extensions.ScreenshotExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({ScreenshotExtension.class, DriverParameterResolver.class})
class ScreenshotTest {

    @Test
    void testTakeScreenshot(WebDriver driver) {
        assertThrows(TimeoutException.class, () ->  {
            driver.get("https://www.google.com");
            driver.findElement(By.id("this-is-an-invalid-id"));
        });
    }
}
