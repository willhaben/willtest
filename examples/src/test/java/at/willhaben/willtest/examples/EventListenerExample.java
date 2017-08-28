package at.willhaben.willtest.examples;

import at.willhaben.willtest.log4j.SeleniumEventListener;
import at.willhaben.willtest.misc.rule.SeleniumRule;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EventListenerExample {

    @Rule
    public final SeleniumRule rule = createRuleWithDefaultEventListener();

    private static SeleniumRule createRuleWithDefaultEventListener() {
        return new SeleniumRule()
                .addWebDriverEventListener(new SeleniumEventListener());
    }

    @Test
    public void testEventListenerWithError() {
        WebDriver webDriver = rule.getWebDriver();
        webDriver.get("https://github.com");
        WebElement searchInput = webDriver.findElement(By.cssSelector(".header-search-input"));
        searchInput.sendKeys("will", "test");
        searchInput.sendKeys(Keys.ENTER);
        String searchKeyword = webDriver.findElement(By.cssSelector(".header-search-input")).getAttribute("value");
        assertThat(searchKeyword, is("foooooo"));
    }
}
