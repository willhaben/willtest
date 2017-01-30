package at.willhaben.willtest.config;

import com.google.common.base.Function;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Adjusts timeouts available by using <code>webDriver.manage().timeouts()</code>.
 * Please note, that if some of the 3 timeouts are not defined, then it will use the defaults settings of the given
 * {@link WebDriver} implementation.<br/>
 * If all timouts are set through this class, then there is a possibility to get the current timeout settings of the
 * {@link WebDriver}, which is not possible out of the box in Selenium.
 */
public class TimeoutsConfigurationParticipant implements WebDriverConfigurationParticipant {
    private Long implicitWait;
    private Long scriptTimeout;
    private Long pageLoadTimeout;

    private static final long CONDITION_POLL_INTERVAL = 200;

    @Override
    public void postConstruct(WebDriver webDriver) {
        Timeouts timeouts = webDriver.manage().timeouts();
        if (implicitWait != null) {
            timeouts.implicitlyWait(implicitWait,TimeUnit.MILLISECONDS);
        }
        if (scriptTimeout != null) {
            timeouts.setScriptTimeout(scriptTimeout,TimeUnit.MILLISECONDS);
        }
        if (pageLoadTimeout != null) {
            timeouts.pageLoadTimeout(pageLoadTimeout,TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Returns the current implicit wait in milliseconds. If some code changes the timeout directly, this value can be
     * incorrect. Always set timeouts using this class.
     * @return
     */
    public Optional<Long> getImplicitWaitInMilliSeconds() {
        return Optional.ofNullable(implicitWait);
    }

    /**
     * Returns the current script timeout in milliseconds. If some code changes the timeout directly, this value can be
     * incorrect. Always set timeouts using this class.
     * @return
     */
    public Optional<Long> getScriptTimeoutInMilliSeconds() {
        return Optional.ofNullable(scriptTimeout);
    }

    /**
     * Returns the current page load timeout in milliseconds. If some code changes the timeout directly, this value can be
     * incorrect. Always set timeouts using this class.
     * @return
     */
    public Optional<Long> getPageLoadTimeoutInMilliSeconds() {
        return Optional.ofNullable(pageLoadTimeout);
    }

    /**
     * If there is an implicit wait set, then you might need to wait for a shorter period. This
     * method makes it possible with setting the implicit wait temporarly to 0.
     * @param webDriver
     * @param seconds
     * @return a {@link Wait} instance, which set the implicit wait temporarly to 0.
     */
    public Wait<WebDriver> waitOverridingImplicitWait(WebDriver webDriver, int seconds) {
        return new Wait<WebDriver>() {
            @Override
            public <T> T until(Function<? super WebDriver, T> function) {
                Timeouts timeouts = webDriver.manage().timeouts();
                try {
                    timeouts.implicitlyWait(0, TimeUnit.SECONDS);
                    return new WebDriverWait(webDriver, seconds, CONDITION_POLL_INTERVAL).until(function);
                } finally {
                    timeouts.implicitlyWait(implicitWait, TimeUnit.MILLISECONDS);
                }
            }
        };
    }

    /**
     * Adds this configurator to the given {@link WebDriverProvider}
     * @param webDriverProvider
     * @param <T>
     * @return <code>webDriverProvider</code> to enable method chaining
     */
    public <T extends WebDriverProvider> T addTo(T webDriverProvider) {
        webDriverProvider.addWebDriverConfigurationParticipant(this);
        return webDriverProvider;
    }

    /**
     * See {@link Timeouts#implicitlyWait(long, TimeUnit)}
     * @param timeout
     * @param timeUnit
     * @return
     */
    public TimeoutsConfigurationParticipant withImplicitWait(Long timeout, TimeUnit timeUnit) {
        Objects.requireNonNull(timeout);
        Objects.requireNonNull(timeUnit);
        this.implicitWait = TimeUnit.MILLISECONDS.convert(timeout,timeUnit);
        return this;
    }

    /**
     * See {@link Timeouts#setScriptTimeout(long, TimeUnit)}
     * @param timeout
     * @param timeUnit
     * @return
     */
    public TimeoutsConfigurationParticipant withScriptTimeout(Long timeout, TimeUnit timeUnit) {
        Objects.requireNonNull(timeout);
        Objects.requireNonNull(timeUnit);
        this.scriptTimeout = TimeUnit.MILLISECONDS.convert(timeout,timeUnit);
        return this;
    }

    /**
     * See {@link Timeouts#pageLoadTimeout(long, TimeUnit)}
     * @param timeout
     * @param timeUnit
     * @return
     */
    public TimeoutsConfigurationParticipant withPageLoadTimeout(Long timeout, TimeUnit timeUnit) {
        Objects.requireNonNull(timeout);
        Objects.requireNonNull(timeUnit);
        this.pageLoadTimeout = TimeUnit.MILLISECONDS.convert(timeout,timeUnit);
        return this;
    }

    /**
     * This will reset implicit wait to the {@link WebDriver} implementation default
     * @return
     */
    public TimeoutsConfigurationParticipant withoutImplicitWait() {
        this.implicitWait = null;
        return this;
    }

    /**
     * This will reset page load timeout to the {@link WebDriver} implementation default
     * @return
     */
    public TimeoutsConfigurationParticipant withoutPageLoadTimeout() {
        this.pageLoadTimeout = null;
        return this;
    }

    /**
     * This will reset script timeout to the {@link WebDriver} implementation default
     * @return
     */
    public TimeoutsConfigurationParticipant withoutScriptTimeout() {
        this.scriptTimeout = null;
        return this;
    }
}