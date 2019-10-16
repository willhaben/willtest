package at.willhaben.misc.test;

import at.willhaben.willtest.misc.pages.PageObject;
import at.willhaben.willtest.misc.utils.WhFluentWait;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CustomFluentWaitTest {

    private static final String ERROR_MESSAGE = "This is an error message";
    private static final String WEBSITE_TITLE = "Website Title";

    private WebDriver driver;

    @Before
    public void before() {
        driver = Mockito.mock(WebDriver.class);
        Mockito.when(driver.getTitle()).thenReturn(WEBSITE_TITLE);
    }

    @Test(expected = AssertionError.class)
    public void testOnError() {
        createAndApplyWaitFunction();
    }

    @Test(expected = AssertionError.class)
    public void testOnErrorPageObject() {
        new DummyPage(driver).waitError();
    }

    @Test(expected = AssertionError.class)
    public void testOnErrorOwnWaitBuilder() {
        new DummyPage(driver).waitErrorCustomWaitBuilder();
    }

    @Test
    public void testErrorMessage() {
        try {
            createAndApplyWaitFunction();
        } catch (AssertionError e) {
            assertThat(e.getMessage(), is(ERROR_MESSAGE));
        }
    }

    @Test(expected = TimeoutException.class)
    public void testEmptyErrorMessageSupplied() {
        WhFluentWait<String> waiter = (WhFluentWait<String>) new WhFluentWait<>("This is just a test string.")
                .withTimeout(Duration.ofMillis(10L))
                .pollingEvery(Duration.ofMillis(1L));
        waiter.until("", "Invald test string"::equals);
    }

    @Test
    public void testValid() {
        new DummyPage(driver).waitForValidTitle();
    }

    private void createAndApplyWaitFunction() {
        WhFluentWait<String> waiter = (WhFluentWait<String>) new WhFluentWait<>("This is just a test string.")
                .withTimeout(Duration.ofMillis(10L))
                .pollingEvery(Duration.ofMillis(1L));
        waiter.until(ERROR_MESSAGE, "Invald test string"::equals);
    }

    private class DummyPage extends PageObject {

        protected DummyPage(WebDriver driver) {
            super(driver);
        }

        public void waitError() {
            getShortWaiter()
                    .until(ERROR_MESSAGE, ExpectedConditions.titleIs("Invalid Website Title"));
        }

        private WhFluentWait<WebDriver> getShortWaiter() {
            return getWait(Duration.ofMillis(10L), Duration.ofMillis(1L));
        }

        public void waitErrorCustomWaitBuilder() {
            WhFluentWait<WebDriver> waiter = getShortWaiter();
            require("#id").withErrorMessage(ERROR_MESSAGE).clickable(waiter);
        }

        public void waitForValidTitle() {
            getShortWaiter().until("Should never be thrown.", ExpectedConditions.titleIs(WEBSITE_TITLE));
        }
    }
}
