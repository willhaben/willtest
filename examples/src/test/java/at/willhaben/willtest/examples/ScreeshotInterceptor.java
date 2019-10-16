package at.willhaben.willtest.examples;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

class ScreeshotInterceptor extends AbstractTestConfig {

    private static final String WILLTEST_GITHUB_PAGE = "https://github.com/willhaben/willtest";
    private static final String REPO_HEADER_LOCATOR = "div.repohead-details-container h1";
    private String compareValue = "willhaben/willtest";

    @Test
    void testCreateDriverWithExtension(WebDriver driver) {
        driver.get(WILLTEST_GITHUB_PAGE);
        WebElement element = driver.findElement(By.cssSelector(REPO_HEADER_LOCATOR));
        assertThat(element.getText(), is(compareValue));
        fail();
    }
}
