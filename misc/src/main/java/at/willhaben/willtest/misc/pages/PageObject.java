package at.willhaben.willtest.misc.pages;

import at.willhaben.willtest.misc.utils.WhFluentWait;
import at.willhaben.willtest.misc.utils.XPathOrCssUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Base class for Pageobjects with some common functions.
 */
public abstract class PageObject {

    public static final long DEFAULT_WAIT_TIMEOUT = 30L;

    private final WebDriver driver;

    protected PageObject(WebDriver driver) {
        this.driver = driver;
        initElements();
        initPage();
    }

    /**
     * @return current {@link WebDriver}
     */
    public WebDriver getWebDriver() {
        return driver;
    }

    /**
     * This method calls the {@link PageFactory#initElements(WebDriver, Object)} method to init every annotated
     * {@link WebElement} in the pageobject. It is automatically called on pageobject creation.
     */
    protected void initElements() {
        PageFactory.initElements(this.driver, this);
    }

    /**
     * Called in the constructor {@link #PageObject(WebDriver)}. Can be
     * overridden to wait for some conditions to become true to ensure the page is fully loaded.
     */
    public void initPage() {}

    /**
     * Moves one page back.
     */
    public void goBack() {
        driver.navigate().back();
    }

    /**
     * Refreshes the current page.
     */
    public void refresh() {
        driver.navigate().refresh();
    }

    /**
     * Returns a random element of a given list.
     * @param elements list of elements
     * @param <T> type of elements
     * @return a random element of the list
     */
    public <T> T getRandomElement(List<T> elements) {
        return getRandomElement(0, elements);
    }

    /**
     * Returns a random element of a given list.
     * @param lowerBound minimal index for the calculation of the random element
     * @param elements list of elements
     * @param <T> type of elements
     * @return a random element of the list
     */
    public <T> T getRandomElement(int lowerBound, List<T> elements) {
        int randomIndex = ThreadLocalRandom.current().nextInt(elements.size() - lowerBound) + lowerBound;
        return elements.get(randomIndex);
    }

    /**
     * Clicks on a random {@link WebElement} in given list.
     * @param elements list of {@link WebElement}
     */
    public void clickRandomWebElement(List<WebElement> elements) {
        getRandomElement(elements).click();
    }

    /**
     * Clicks on a random {@link WebElement} in given list.
     * @param lowerBound minimal index for the calculation of the random element
     * @param elements list of {@link WebElement}
     */
    public void clickRandomWebElement(int lowerBound, List<WebElement> elements) {
        getRandomElement(lowerBound, elements).click();
    }

    /**
     * Waiting on a specific {@link WebElement}.
     * @param element to wait for
     * @return Builder for waiting for a specific element.
     */
    public WaitForBuilder waitFor(WebElement element) {
        return new WaitForBuilder(this, element);
    }

    /**
     * Waiting on a specific {@link WebElement} identified by an XPath expression or a CSS selector.
     * @param xPathOrCss to wait for
     * @return Builder for waiting for a specific element.
     */
    public WaitForBuilder waitFor(String xPathOrCss) {
        return new WaitForBuilder(this, XPathOrCssUtil.mapToBy(xPathOrCss));
    }

    /**
     * Waiting on a specific {@link WebElement} identified by a {@link By}.
     * @param by to wait for
     * @return Builder for waiting for a specific element.
     */
    public WaitForBuilder waitFor(By by) {
        return new WaitForBuilder(this, by);
    }

    /**
     * Waiting on a set of elements.
     * @param elements to wait for
     * @return Builder to create different waiting conditions.
     */
    public RequireBuilder require(WebElement... elements) {
        return new RequireBuilder(this, new RequireType(elements));
    }

    /**
     * Waiting on a set of elements.
     * @param xPathOrCss XPath or CSS locators to wait for
     * @return Builder to create different waiting conditions.
     */
    public RequireBuilder require(String... xPathOrCss) {
        return new RequireBuilder(this, new RequireType(Arrays.stream(xPathOrCss)
                .map(XPathOrCssUtil::mapToBy)
                .toArray(By[]::new)));
    }

    /**
     * Waiting on a set of elements.
     * @param bys locators to wait for
     * @return Builder to create different waiting conditions.
     */
    public RequireBuilder require(By... bys) {
        return new RequireBuilder(this, new RequireType(bys));
    }

    /**
     * Used to check if an element is available or visible.
     * @param webElement element to check
     * @return Builder for checking appearance of element.
     */
    public IsAvailableBuilder is(WebElement webElement) {
        return new IsAvailableBuilder(this, webElement);
    }

    /**
     * Used to check if an element is available or visible.
     * @param xPathOrCss XPath or CSS locator of element
     * @return Builder for checking appearance of element.
     */
    public IsAvailableBuilder is(String xPathOrCss) {
        return new IsAvailableBuilder(this, XPathOrCssUtil.mapToBy(xPathOrCss));
    }

    /**
     * Used to check if an element is available or visible.
     * @param by locator of the element to check
     * @return Builder for checking appearance of element.
     */
    public IsAvailableBuilder is(By by) {
        return new IsAvailableBuilder(this, by);
    }

    /**
     * Same as {@link #getWait(long)} with a default wait of {@value DEFAULT_WAIT_TIMEOUT} seconds.
     * @return
     */
    protected WhFluentWait<WebDriver> getWait() {
        return getWait(DEFAULT_WAIT_TIMEOUT);
    }

    /**
     * Generates a default {@link FluentWait} which ignores {@link NoSuchElementException} and
     * {@link StaleElementReferenceException}. Polls every 250 milliseconds.
     * @param timeout Timeout in seconds
     * @return Waiter
     */
    protected WhFluentWait<WebDriver> getWait(long timeout) {
        return getWait(Duration.ofSeconds(timeout), Duration.ofMillis(250L));
    }

    protected WhFluentWait<WebDriver> getWait(Duration waitFor, Duration polling) {
        return (WhFluentWait<WebDriver>) new WhFluentWait<>(driver)
                .withTimeout(waitFor)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .pollingEvery(polling);
    }

    public  <T> Optional<T> waitFor(Function<? super WebDriver, T> findFunction, long timeout) {
        try {
            return Optional.of(getWait(timeout).until(findFunction));
        } catch (TimeoutException e) {
            return Optional.empty();
        }
    }

    protected Optional<WebElement> findWithFilter(By selector, Predicate<WebElement> predicate) {
        return findWithFilter(driver, selector, predicate);
    }

    protected <T> Optional<T> findWithFilter(List<T> elements, Predicate<T> predicate) {
        return elements.stream()
                .filter(predicate)
                .findFirst();
    }

    protected Optional<WebElement> findWithFilter(SearchContext searchContext, By selector, Predicate<WebElement> predicate) {
        return searchContext.findElements(selector).stream()
                .filter(predicate)
                .findFirst();
    }
}
