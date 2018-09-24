package at.willhaben.misc.test.pages;

import at.willhaben.misc.test.util.SelectComponent;
import at.willhaben.willtest.misc.pages.PageObject;
import at.willhaben.willtest.misc.pages.find.WhFieldDecorator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;

public class AbstractTestingPage extends PageObject {

    protected AbstractTestingPage(WebDriver driver) {
        super(driver);
    }

    @Override
    protected void initElements() {
        WhFieldDecorator fieldDecorator = new WhFieldDecorator(new DefaultElementLocatorFactory(getWebDriver()));
        fieldDecorator.addCustomUiComponent(new SelectComponent());

        PageFactory.initElements(fieldDecorator, this);
    }
}
