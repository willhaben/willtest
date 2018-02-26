package at.willhaben.willtest.misc.pages;

import at.willhaben.willtest.misc.utils.ConditionType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Objects;

public abstract class AbstractWaitingBuilder<T> {

    private final PageObject pageObject;
    private By by;
    private WebElement webElement;

    AbstractWaitingBuilder(PageObject pageObject, WebElement webElement) {
        this.pageObject = pageObject;
        this.webElement = webElement;
    }

    AbstractWaitingBuilder(PageObject pageObject, By by) {
        this.pageObject = pageObject;
        this.by = by;
    }

    public PageObject getPageObject() {
        return pageObject;
    }

    public abstract T clickable(long timeout);

    public abstract T visible(long timeout);

    public T clickable() {
        return clickable(PageObject.DEFAULT_WAIT_TIMEOUT);
    }

    public T visible() {
        return visible(PageObject.DEFAULT_WAIT_TIMEOUT);
    }

    protected ExpectedCondition<WebElement> generateCondition(ConditionType conditionType) {
        switch (conditionType) {
            case CLICKABLE:
                return generateClickableCondition();
            case VISIBLE:
                return generateVisibleCondition();
            default:
                throw new IllegalArgumentException();
        }
    }

    private ExpectedCondition<WebElement> generateClickableCondition() {
        if(Objects.nonNull(webElement)) {
            return ExpectedConditions.elementToBeClickable(webElement);
        } else if(Objects.nonNull(by)) {
            return ExpectedConditions.elementToBeClickable(by);
        }
        throw new IllegalStateException();
    }

    private ExpectedCondition<WebElement> generateVisibleCondition() {
        if(Objects.nonNull(webElement)) {
            return ExpectedConditions.visibilityOf(webElement);
        } else if(Objects.nonNull(by)) {
            return ExpectedConditions.visibilityOfElementLocated(by);
        }
        throw new IllegalStateException();
    }
}
