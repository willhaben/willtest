package at.willhaben.willtest.config;

import com.google.common.base.Function;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Timeouts;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Adjusts timeouts available by using <code>webDriver.manage().timeouts()</code>.
 * Please note, that if some of the 3 timeouts are not defined, then it will use the defaults settings of the given
 * {@link WebDriver} implementation.<br/>
 * If all timeouts are set through this class, then there is a possibility to get the current timeout settings of the
 * {@link WebDriver}, which is not possible out of the box in Selenium.
 */
public class TimeoutsConfigurationParticipant<D extends WebDriver> implements WebDriverConfigurationParticipant<D> {
    private Duration implicitWait;
    private Duration scriptTimeout;
    private Duration pageLoadTimeout;

    private static final long CONDITION_POLL_INTERVAL = 200;

    @Override
    public void postConstruct(D webDriver) {
        Timeouts timeouts = webDriver.manage().timeouts();
        if (implicitWait != null) {
            timeouts.implicitlyWait(implicitWait.getNano(), TimeUnit.NANOSECONDS);
        }
        if (scriptTimeout != null) {
            timeouts.setScriptTimeout(scriptTimeout.getNano(), TimeUnit.NANOSECONDS);
        }
        if (pageLoadTimeout != null) {
            timeouts.pageLoadTimeout(pageLoadTimeout.getNano(), TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Returns the current implicit wait in milliseconds. If some code changes the timeout directly, this value can be
     * incorrect. Always set timeouts using this class.
     *
     * @return
     */
    public Optional<Duration> getImplicitWait() {
        return Optional.ofNullable(implicitWait);
    }

    /**
     * Returns the current script timeout in milliseconds. If some code changes the timeout directly, this value can be
     * incorrect. Always set timeouts using this class.
     *
     * @return
     */
    public Optional<Duration> getScriptTimeout() {
        return Optional.ofNullable(scriptTimeout);
    }

    /**
     * Returns the current page load timeout in milliseconds. If some code changes the timeout directly, this value can be
     * incorrect. Always set timeouts using this class.
     *
     * @return
     */
    public Optional<Duration> getPageLoadTimeout() {
        return Optional.ofNullable(pageLoadTimeout);
    }

    /**
     * If there is an implicit wait set, then you might need to wait for a shorter period. This
     * method makes it possible with setting the implicit wait temporarily to 0.
     *
     * @param webDriver
     * @param seconds
     * @return a {@link Wait} instance, which set the implicit wait temporarily to 0.
     */
    public Wait<WebDriver> waitOverridingImplicitWait(WebDriver webDriver, int seconds) {
        //API comes from selenium -> cannot migrate guava to java 8
        //noinspection Guava
        return new Wait<WebDriver>() {
            @Override
            public <T> T until(Function<? super WebDriver, T> function) {
                Timeouts timeouts = webDriver.manage().timeouts();
                try {
                    timeouts.implicitlyWait(0, TimeUnit.SECONDS);
                    return new WebDriverWait(webDriver, seconds, CONDITION_POLL_INTERVAL).until(function);
                } finally {
                    timeouts.implicitlyWait(implicitWait.getNano(), TimeUnit.NANOSECONDS);
                }
            }
        };
    }

    /**
     * Adds this configurator to the given {@link SeleniumProvider}
     *
     * @param seleniumProvider
     * @param <T>
     * @return <code>seleniumProvider</code> to enable method chaining
     */
    public <T extends SeleniumProvider<T,D>> T addTo(T seleniumProvider) {
        seleniumProvider.addWebDriverConfigurationParticipant(this);
        return seleniumProvider;
    }

    /**
     * See {@link Timeouts#implicitlyWait(long, TimeUnit)}
     *
     * @param implicitWait
     * @return
     */
    public TimeoutsConfigurationParticipant<D> withImplicitWait(Duration implicitWait) {
        Objects.requireNonNull(implicitWait);
        this.implicitWait = implicitWait;
        return this;
    }

    /**
     * See {@link Timeouts#setScriptTimeout(long, TimeUnit)}
     *
     * @param scriptTimeout
     * @return
     */
    public TimeoutsConfigurationParticipant<D> withScriptTimeout(Duration scriptTimeout) {
        Objects.requireNonNull(scriptTimeout);
        this.scriptTimeout = scriptTimeout;
        return this;
    }

    /**
     * See {@link Timeouts#pageLoadTimeout(long, TimeUnit)}
     *
     * @param pageLoadTimeout
     * @return
     */
    public TimeoutsConfigurationParticipant<D> withPageLoadTimeout(Duration pageLoadTimeout) {
        Objects.requireNonNull(pageLoadTimeout);
        this.pageLoadTimeout = pageLoadTimeout;
        return this;
    }

    /**
     * This will reset implicit wait to the {@link WebDriver} implementation default
     *
     * @return
     */
    public TimeoutsConfigurationParticipant<D> withoutImplicitWait() {
        this.implicitWait = null;
        return this;
    }

    /**
     * This will reset page load timeout to the {@link WebDriver} implementation default
     *
     * @return
     */
    public TimeoutsConfigurationParticipant<D> withoutPageLoadTimeout() {
        this.pageLoadTimeout = null;
        return this;
    }

    /**
     * This will reset script timeout to the {@link WebDriver} implementation default
     *
     * @return
     */
    public TimeoutsConfigurationParticipant<D> withoutScriptTimeout() {
        this.scriptTimeout = null;
        return this;
    }
}