package at.willhaben.willtest.misc.pages;

import at.willhaben.willtest.misc.utils.ConditionType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class WaitForBuilder extends AbstractWaitingBuilder<WebElement> {

    WaitForBuilder(PageObject pageObject, WebElement webElement) {
        super(pageObject, webElement);
    }

    WaitForBuilder(PageObject pageObject, By by) {
        super(pageObject, by);
    }

    @Override
    public WebElement clickable(long timeout) {
        return getPageObject().getWait().until(generateCondition(ConditionType.CLICKABLE));
    }

    @Override
    public WebElement visible(long timeout) {
        return getPageObject().getWait().until(generateCondition(ConditionType.VISIBLE));
    }

    public void visibleAfterClick(WebElement clickElement, WebElement visibleElement){
        getPageObject().getWait().until(driver -> {
            clickElement.click();
            return visibleElement.isDisplayed();
        });
    }
}
