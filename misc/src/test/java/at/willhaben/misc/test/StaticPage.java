package at.willhaben.misc.test;

import at.willhaben.willtest.misc.pages.PageObject;
import at.willhaben.willtest.misc.pages.find.FindWh;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static at.willhaben.misc.test.StaticResourceHtmlUtil.getAbsolutePath;

public class StaticPage extends PageObject {

    @FindWh(dataTestId = "check-this-id")
    private WebElement testElementText;

    protected StaticPage(WebDriver driver) {
        super(driver);
    }

    public static StaticPage open(WebDriver driver) {
        driver.navigate().to("file://" + getAbsolutePath("static"));
        return new StaticPage(driver);
    }

    public String getTextOfTestElement() {
        return testElementText.getText().trim();
    }
}
