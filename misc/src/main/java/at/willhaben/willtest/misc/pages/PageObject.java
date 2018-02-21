package at.willhaben.willtest.misc.pages;

import at.willhaben.willtest.config.SeleniumProvider;
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

    private static final long DEFAULT_WAIT_TIMEOUT = 30L;

    private final WebDriver driver;

    protected PageObject(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(this.driver, this);
        initPage();
    }

    protected PageObject(SeleniumProvider provider) {
        this.driver = provider.getWebDriver();
        PageFactory.initElements(this.driver, this);
        initPage();
    }

    public WebDriver getWebDriver() {
        return driver;
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

    protected void requireClickable(WebElement... elements) {
        requireClickable(DEFAULT_WAIT_TIMEOUT, elements);
    }

    protected void requireClickable(long timeout, WebElement... elements) {
        requireElements(asList(elements), ExpectedConditions::elementToBeClickable, timeout);
    }

    protected void requireVisible(WebElement... elements) {
        requireVisible(DEFAULT_WAIT_TIMEOUT, elements);
    }

    protected void requireVisible(long timeout, WebElement... elements) {
        requireElements(asList(elements), ExpectedConditions::visibilityOf, timeout);
    }

    protected void requireClickable(String... xPathOrCss) {
        requireBy(createLocators(xPathOrCss), ExpectedConditions::elementToBeClickable, DEFAULT_WAIT_TIMEOUT);
    }

    protected void requireVisible(String... xPathOrCss) {
        requireBy(createLocators(xPathOrCss), ExpectedConditions::visibilityOfElementLocated, DEFAULT_WAIT_TIMEOUT);
    }

    private List<By> createLocators(String... xPathOrCss) {
        return Arrays.stream(xPathOrCss)
                .map(locator -> {
                    if (locator.startsWith("/")) {
                        return By.xpath(locator);
                    } else {
                        return By.cssSelector(locator);
                    }
                })
                .collect(Collectors.toList());
    }

    private void requireBy(List<By> locators,
                         Function<By, ExpectedCondition<WebElement>> conditionCreator,
                         long timeout) {
        ExpectedCondition<?>[] conditions = locators.stream()
                .map(conditionCreator)
                .toArray(ExpectedCondition<?>[]::new);

        getWait(timeout).until(ExpectedConditions.and(conditions));
    }

    private void requireElements(List<WebElement> elements,
                                 Function<WebElement, ExpectedCondition<WebElement>> conditionCreator,
                                 long timeout) {
        ExpectedCondition<?>[] conditions = elements.stream()
                .map(conditionCreator)
                .toArray(ExpectedCondition<?>[]::new);

        getWait(timeout).until(ExpectedConditions.and(conditions));
    }

    protected FluentWait<WebDriver> getWait() {
        return getWait(DEFAULT_WAIT_TIMEOUT);
    }

    protected FluentWait<WebDriver> getWait(long timeout) {
        // michael.weisgrab@willhaben.at
        // Styria23IT
        return new FluentWait<>(driver)
                .withTimeout(timeout, TimeUnit.SECONDS)
                .pollingEvery(250L, TimeUnit.MILLISECONDS);
    }

    public Optional<WebElement> isClickable(By locator) {
        return isClickable(locator, DEFAULT_WAIT_TIMEOUT);
    }

    public Optional<WebElement> isClickable(By locator, long timeout) {
        return waitFor(ExpectedConditions.elementToBeClickable(locator), timeout);
    }

    public Optional<List<WebElement>> isAllVisible(By locator) {
        return isAllVisible(locator, DEFAULT_WAIT_TIMEOUT);
    }

    public Optional<List<WebElement>> isAllVisible(By locator, long timeout) {
        return waitFor(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator), timeout);
    }

    public Optional<WebElement> isVisible(By locator) {
        return isVisible(locator, DEFAULT_WAIT_TIMEOUT);
    }

    public Optional<WebElement> isVisible(By locator, long timeout) {
        return waitFor(ExpectedConditions.visibilityOfElementLocated(locator), timeout);
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
