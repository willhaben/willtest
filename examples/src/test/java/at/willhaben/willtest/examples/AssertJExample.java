package at.willhaben.willtest.examples;

import at.willhaben.willtest.junit5.BrowserUtil;
import at.willhaben.willtest.junit5.extensions.DriverParameterResolverExtension;
import at.willhaben.willtest.junit5.extensions.ScreenshotProvider;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

@ExtendWith({
        DriverParameterResolverExtension.class,
})
@BrowserUtil({ScreenshotProvider.class})
class AssertJExample {

    private SoftAssertions softAssertions;

    @BeforeEach
    void setup() {
        softAssertions = new SoftAssertions();
    }

    @Test
    void testAssertJSoftAssertions(WebDriver driver) {
        driver.get("https://www.google.com");
        softAssertions.assertThat("True").isEqualTo("False");
    }

    @AfterEach
    void tearDown() {
        softAssertions.assertAll();
    }
}
