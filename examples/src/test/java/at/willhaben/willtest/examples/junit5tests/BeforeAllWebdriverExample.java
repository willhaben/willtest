package at.willhaben.willtest.examples.junit5tests;

import at.willhaben.willtest.junit5.extensions.DriverParameterResolver;
import at.willhaben.willtest.junit5.extensions.ScreenshotExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

@ExtendWith({
        DriverParameterResolver.class,
        ScreenshotExtension.class
})
class BeforeAllWebdriverExample {

    @BeforeAll
    //A new webdriver is created here and is closed after all tests are finished
    static void setup(WebDriver driver) {
        driver.get("https://www.google.at");
    }

    @BeforeEach
    //The same webdriver as in the test is used
    void before(WebDriver driver) {
        driver.get("https://www.stackoverflow.com");
    }

    @Test
    void testCreateDriverWithExtension(WebDriver driver) {
        driver.get("https://github.com/willhaben/willtest");
    }
}
