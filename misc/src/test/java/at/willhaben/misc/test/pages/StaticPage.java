package at.willhaben.misc.test.pages;

import at.willhaben.willtest.misc.pages.find.FindTestId;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import static at.willhaben.misc.test.util.StaticResourceHtmlUtil.getAbsolutePath;

public class StaticPage extends AbstractTestingPage {

    @FindTestId(value = "check-this-id")
    private WebElement divElementText;

    @FindTestId(value = "check-this-id", tagName = "span")
    private WebElement spanElementText;

    @FindTestId("select-id")
    private Select select;

    protected StaticPage(WebDriver driver) {
        super(driver);
    }

    public static StaticPage open(WebDriver driver) {
        driver.navigate().to("file://" + getAbsolutePath("static"));
        return new StaticPage(driver);
    }

    public String getTextOfDivElement() {
        return divElementText.getText().trim();
    }

    public String getTextOfSpanElement() {
        return spanElementText.getText().trim();
    }

    public void select(String value) {
        select.selectByVisibleText(value);
    }

    public String getSelectedValue() {
        return select.getFirstSelectedOption().getText().trim();
    }
}
