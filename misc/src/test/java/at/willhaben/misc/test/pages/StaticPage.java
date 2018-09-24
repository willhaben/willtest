package at.willhaben.misc.test.pages;

import at.willhaben.misc.test.util.FindTestId;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ISelect;

import java.util.List;

import static at.willhaben.misc.test.util.StaticResourceHtmlUtil.resourceHtmlFilePath;

public class StaticPage extends AbstractTestingPage {

    @FindTestId(value = "check-this-id")
    private WebElement divElementText;

    @FindTestId(value = "check-this-id", tagName = "span")
    private WebElement spanElementText;

    @FindTestId("select-id")
    private ISelect select;

    @FindTestId("select-id-list")
    private List<ISelect> selectList;

    protected StaticPage(WebDriver driver) {
        super(driver);
    }

    public static StaticPage open(WebDriver driver) {
        driver.navigate().to(resourceHtmlFilePath("static"));
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

    public List<ISelect> getSelectList() {
        return selectList;
    }
}
