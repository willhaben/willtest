package at.willhaben.willtest.misc.pages;

import at.willhaben.willtest.misc.utils.XPathBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static at.willhaben.willtest.misc.pages.PageObject.DEFAULT_WAIT_TIMEOUT;
import static java.util.Arrays.asList;

public class RequireBuilder {

    private final PageObject pageObject;

    RequireBuilder(PageObject pageObject) {
        this.pageObject = pageObject;
    }

    protected RequireBuilder clickable(WebElement... elements) {
        clickable(DEFAULT_WAIT_TIMEOUT, elements);
        return this;
    }

    protected RequireBuilder clickable(long timeout, WebElement... elements) {
        requireElements(asList(elements), ExpectedConditions::elementToBeClickable, timeout);
        return this;
    }

    protected RequireBuilder clickable(String... xPathOrCss) {
        clickable(DEFAULT_WAIT_TIMEOUT, xPathOrCss);
        return this;
    }

    protected RequireBuilder clickable(long timeout, String... xPathOrCss) {
        requireBy(createLocators(xPathOrCss), ExpectedConditions::elementToBeClickable, timeout);
        return this;
    }

    protected RequireBuilder clickable(By... by) {
        clickable(DEFAULT_WAIT_TIMEOUT, by);
        return this;
    }

    protected RequireBuilder clickable(long timeout, By... by) {
        requireBy(asList(by), ExpectedConditions::elementToBeClickable, timeout);
        return this;
    }

    protected WebElement clickable(WebElement element) {
        return pageObject.getWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    protected WebElement clickable(XPathBuilder builder) {
        return pageObject.getWait().until(ExpectedConditions.elementToBeClickable(builder.build()));
    }

    protected WebElement clickable(String xPathOrCss) {
        return pageObject.getWait().until(ExpectedConditions.elementToBeClickable(locatorMapper().apply(xPathOrCss)));
    }

    protected WebElement clickable(By by) {
        return pageObject.getWait().until(ExpectedConditions.elementToBeClickable(by));
    }



    protected RequireBuilder visible(WebElement... elements) {
        visible(DEFAULT_WAIT_TIMEOUT, elements);
        return this;
    }

    protected RequireBuilder visible(long timeout, WebElement... elements) {
        requireElements(asList(elements), ExpectedConditions::visibilityOf, timeout);
        return this;
    }

    protected RequireBuilder visible(String... xPathOrCss) {
        visible(DEFAULT_WAIT_TIMEOUT, xPathOrCss);
        return this;
    }

    protected RequireBuilder visible(long timeout, String... xPathOrCss) {
        requireBy(createLocators(xPathOrCss), ExpectedConditions::visibilityOfElementLocated, timeout);
        return this;
    }

    protected RequireBuilder visible(By... by) {
        visible(DEFAULT_WAIT_TIMEOUT, by);
        return this;
    }

    protected RequireBuilder visible(long timeout, By... by) {
        requireBy(asList(by), ExpectedConditions::visibilityOfAllElementsLocatedBy, timeout);
        return this;
    }

    protected WebElement visible(WebElement element) {
        return pageObject.getWait().until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement visible(XPathBuilder builder) {
        return pageObject.getWait().until(ExpectedConditions.visibilityOfElementLocated(builder.build()));
    }

    protected WebElement visible(String xPathOrCss) {
        return pageObject.getWait().until(ExpectedConditions.visibilityOfElementLocated(locatorMapper().apply(xPathOrCss)));
    }

    protected WebElement visible(By by) {
        return pageObject.getWait().until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    private List<By> createLocators(String... xPathOrCss) {
        return Arrays.stream(xPathOrCss)
                .map(locatorMapper())
                .collect(Collectors.toList());
    }

    private Function<String, By> locatorMapper() {
        return locator -> {
            if (locator.startsWith("/")) {
                return By.xpath(locator);
            } else {
                return By.cssSelector(locator);
            }
        };
    }

    private void requireBy(List<By> locators,
                           Function<By, ExpectedCondition<?>> conditionCreator,
                           long timeout) {
        ExpectedCondition<?>[] conditions = locators.stream()
                .map(conditionCreator)
                .toArray(ExpectedCondition<?>[]::new);

        pageObject.getWait(timeout).until(ExpectedConditions.and(conditions));
    }

    private void requireElements(List<WebElement> elements,
                                 Function<WebElement, ExpectedCondition<WebElement>> conditionCreator,
                                 long timeout) {
        ExpectedCondition<?>[] conditions = elements.stream()
                .map(conditionCreator)
                .toArray(ExpectedCondition<?>[]::new);

        pageObject.getWait(timeout).until(ExpectedConditions.and(conditions));
    }
}
