package at.willhaben.misc.test.pages;

import at.willhaben.willtest.misc.pages.PageObject;
import at.willhaben.willtest.misc.pages.find.CustomFieldDecorator;
import at.willhaben.willtest.misc.pages.find.WhElementLocatorFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class AbstractTestingPage extends PageObject {

    protected AbstractTestingPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initElements() {
        WhElementLocatorFactory factory = new WhElementLocatorFactory(getWebDriver());
        factory.addComponent(new FindTestIdComponent());
        PageFactory.initElements(new CustomFieldDecorator(factory), this);
    }
}
