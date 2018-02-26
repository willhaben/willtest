package at.willhaben.willtest.misc.pages;

import at.willhaben.willtest.misc.utils.ConditionType;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Optional;
import java.util.function.Function;

public class IsAvailableBuilder extends AbstractWaitingBuilder<Optional<WebElement>> {

    IsAvailableBuilder(PageObject pageObject, WebElement webElement) {
        super(pageObject, webElement);
    }

    IsAvailableBuilder(PageObject pageObject, By by) {
        super(pageObject, by);
    }

    @Override
    public Optional<WebElement> clickable(long timeout) {
        return waitFor(generateCondition(ConditionType.CLICKABLE), timeout);
    }

    @Override
    public Optional<WebElement> visible(long timeout) {
        return waitFor(generateCondition(ConditionType.VISIBLE), timeout);
    }

    private <T> Optional<T> waitFor(Function<? super WebDriver, T> findFunction, long timeout) {
        try {
            return Optional.of(getPageObject().getWait(timeout).until(findFunction));
        } catch (TimeoutException e) {
            return Optional.empty();
        }
    }
}
