package at.willhaben.willtest.examples.junit5tests;

import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.extensions.DriverParameterResolver;
import at.willhaben.willtest.junit5.extensions.PageSourceExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(DriverParameterResolver.class)
@BrowserUtil(PageSourceExtension.class)
class PageSourceExample {

    @Test
    void testFailingTest(WebDriver driver) {
        driver.get("https://www.google.at");
        fail();
    }
}
