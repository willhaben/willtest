package at.willhaben.willtest.misc.pages;

import at.willhaben.willtest.config.SeleniumProvider;
import at.willhaben.willtest.misc.utils.XPathBuilder;
import at.willhaben.willtest.misc.utils.XPathOrCssUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public abstract class PageObject {

    static final long DEFAULT_WAIT_TIMEOUT = 30L;

    private final WebDriver driver;

    protected PageObject(WebDriver driver) {
        this.driver = driver;
        initElements();
        initPage();
    }

    protected PageObject(SeleniumProvider provider) {
        this.driver = provider.getWebDriver();
        initElements();
        initPage();
    }

    public WebDriver getWebDriver() {
        return driver;
    }

    protected final void initElements() {
        PageFactory.initElements(this.driver, this);
    }

    public void initPage() {}

    public void goBack() {
        driver.navigate().back();
    }

    public void refresh() {
        driver.navigate().refresh();
    }

    public <T> T getRandomElement(List<T> elements) {
        return getRandomElement(0, elements);
    }

    public <T> T getRandomElement(int lowerBound, List<T> elements) {
        int randomIndex = ThreadLocalRandom.current().nextInt(elements.size() - lowerBound) + lowerBound;
        return elements.get(randomIndex);
    }

    public void clickRandomWebElement(List<WebElement> elements) {
        getRandomElement(elements).click();
    }

    public void clickRandomWebElement(int lowerBound, List<WebElement> elements) {
        getRandomElement(lowerBound, elements).click();
    }

    public WaitForBuilder waitFor(WebElement elements) {
        return new WaitForBuilder(this, elements);
    }

    public WaitForBuilder waitFor(String xPathOrCss) {
        return new WaitForBuilder(this, XPathOrCssUtil.mapToBy(xPathOrCss));
    }

    public WaitForBuilder waitFor(By by) {
        return new WaitForBuilder(this, by);
    }

    public RequireBuilder require(WebElement... elements) {
        return new RequireBuilder(this, RequireType.require(elements));
    }

    public RequireBuilder require(String... xPathOrCss) {
        return new RequireBuilder(this, RequireType.require(Arrays.stream(xPathOrCss)
                .map(XPathOrCssUtil::mapToBy)
                .toArray(By[]::new)));
    }

    public RequireBuilder require(By... bys) {
        return new RequireBuilder(this, RequireType.require(bys));
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
    protected FluentWait<WebDriver> getWait() {
        return getWait(DEFAULT_WAIT_TIMEOUT);
    }

    /**
     * Generates a default {@link FluentWait} which ignores {@link NoSuchElementException} and
     * {@link StaleElementReferenceException}. Polls every 250 milliseconds.
     * @param timeout Timeout in seconds
     * @return Waiter
     */
    protected FluentWait<WebDriver> getWait(long timeout) {
        return new FluentWait<>(driver)
                .withTimeout(timeout, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .pollingEvery(250L, TimeUnit.MILLISECONDS);
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
